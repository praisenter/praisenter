package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipOutputStream;

import org.praisenter.async.AsyncHelper;
import org.praisenter.data.search.SearchCriteria;
import org.praisenter.data.search.SearchIndex;
import org.praisenter.data.search.SearchResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class DataManager {
	private final ConcurrentMap<Class<?>, PersistentStore<?>> adapters;
	private final SearchIndex index;
	private final ObservableSet<Tag> tags;
	private final ObservableSet<Tag> tagsReadOnly;
	
	public DataManager(SearchIndex index) {
		this.adapters = new ConcurrentHashMap<>();
		this.index = index;
		this.tags = FXCollections.observableSet(new HashSet<>());
		this.tagsReadOnly = FXCollections.unmodifiableObservableSet(this.tags);
	}
	
	public <T extends Persistable> CompletableFuture<Void> registerPersistAdapter(Class<T> clazz, PersistAdapter<T> adapter) {
		PersistentStore<T> store = new PersistentStore<T>(adapter, this.index);
		return store.initialize().thenApply((items) -> {
			this.adapters.put(clazz, store);
			return items;
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((items) -> {
			// initialize the set of all saved tags
			for (T item : items) {
				Set<Tag> tags = item.getTagsUnmodifiable();
				if (tags != null && !tags.isEmpty()) {
					this.tags.addAll(tags);
				}
			}
		}));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> ObservableList<T> getItemsUnmodifiable(Class<T> clazz) {
		PersistentStore<?> adapter = this.adapters.get(clazz);
		if (adapter == null) return null;
		return (ObservableList<T>) adapter.getItemsUnmodifiable();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> T getItem(Class<T> clazz, UUID id) {
		PersistentStore<?> adapter = this.adapters.get(clazz);
		if (adapter == null) return null;
		return (T)adapter.getItem(id);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> create(T item) {
		Class<?> clazz = item.getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.create(item).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			// make sure any new tags are added to the main set
			this.addItemTags(item);
		}));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> update(T item) {
		Class<?> clazz = item.getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.update(item).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			// make sure any new tags are added to the main set
			this.addItemTags(item);
		}));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> delete(T item) {
		Class<?> clazz = item.getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.delete(item);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<DataImportResult<T>> importData(Path path, Class<T> clazz) {
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.importData(path).thenCompose(AsyncHelper.onJavaFXThreadAndWait((result) -> {
			// make sure we capture any new tags from the import
			this.addDataImportResultTags(result);
			return result;
		}));
	}
	
	@SuppressWarnings("unchecked")
	public CompletableFuture<Void> importData(Path path, Class<Persistable>... classes) {
		final List<CompletableFuture<DataImportResult<?>>> futures = new ArrayList<>();
		
		for (Class<Persistable> clazz : classes) {
			PersistentStore<?> store = this.adapters.get(clazz);
			if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
			futures.add(store.importData(path).thenApply((l) -> (DataImportResult<?>)l));
		}
		
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			// make sure we capture any new tags from the import
			for (CompletableFuture<DataImportResult<?>> future : futures) {
				DataImportResult<?> result = future.get();
				this.addDataImportResultTags(result);
			}
		}));
	}
	
	private <T> void addDataImportResultTags(DataImportResult<T> result) {
		for (Object o : result.getCreated()) {
			if (o instanceof Persistable) {
				this.addItemTags((Persistable)o);
			}
		}
		for (Object o : result.getUpdated()) {
			if (o instanceof Persistable) {
				this.addItemTags((Persistable)o);
			}
		}
	}
	
	private <T extends Persistable> void addItemTags(T item) {
		Set<Tag> tags = item.getTagsUnmodifiable();
		if (tags != null && !tags.isEmpty()) {
			this.tags.addAll(tags);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> void exportData(KnownFormat format, ZipOutputStream stream, List<T> items) throws IOException {
		if (items == null || items.isEmpty()) return;
		
		// group by class
		Map<Class<?>, List<Persistable>> grouped = new HashMap<>();
		for (Persistable p : items) {
			Class<?> clazz = p.getClass();
			List<Persistable> group = grouped.get(clazz);
			if (group == null) {
				group = new ArrayList<>();
				grouped.put(clazz, group);
			}
			group.add(p);
		}
		
		// then iterate all the items exporting each set
		for (Class<?> clazz : grouped.keySet()) {
			PersistentStore<Persistable> store = (PersistentStore<Persistable>)this.adapters.get(clazz);
			if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
			store.exportData(format, stream, grouped.get(clazz));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> exportData(KnownFormat format, Path path, T item) {
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(item.getClass());
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + item.getClass() + "'.");
		return store.exportData(format, path, item);
	}
	
	public CompletableFuture<List<SearchResult>> search(SearchCriteria criteria) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.index.search(criteria);
			} catch (Exception ex) {
				throw new CompletionException(ex);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> Path getFilePath(T item) {
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(item.getClass());
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + item.getClass() + "'.");
		return store.getFilePath(item);
	}
	
	public Persistable getPersistableById(UUID id) {
		for (PersistentStore<?> store : this.adapters.values()) {
			Persistable item = store.getItem(id);
			if (item != null) {
				return item;
			}
		}
		return null;
	}
	
	public ObservableSet<Tag> getTagsUmodifiable() {
		return this.tagsReadOnly;
	}
}
