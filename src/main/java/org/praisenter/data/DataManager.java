package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.zip.ZipOutputStream;

import org.praisenter.data.search.SearchCriteria;
import org.praisenter.data.search.SearchIndex;
import org.praisenter.data.search.SearchResult;

import javafx.collections.ObservableList;

public final class DataManager {
	private final ConcurrentMap<Class<?>, PersistentStore<?>> adapters;
	private final SearchIndex index;
	
	public DataManager(SearchIndex index) {
		this.adapters = new ConcurrentHashMap<>();
		this.index = index;
	}
	
	public <T extends Persistable> CompletableFuture<Void> registerPersistAdapter(Class<T> clazz, PersistAdapter<T> adapter) {
		PersistentStore<T> store = new PersistentStore<T>(adapter, this.index);
		return store.initialize().thenRun(() -> {
			this.adapters.put(clazz, store);
		});
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> ObservableList<T> getItems(Class<T> clazz) {
		PersistentStore<?> adapter = this.adapters.get(clazz);
		if (adapter == null) return null;
		return (ObservableList<T>) adapter.getItems();
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
		return store.create(item);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> update(T item) {
		Class<?> clazz = item.getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.update(item);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> delete(T item) {
		Class<?> clazz = item.getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.delete(item);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<DataImportResult<T>> importData(Class<T> clazz, Path path) {
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.importData(path);
	}
	
	@SuppressWarnings("unchecked")
	public CompletableFuture<Void> importData(Path path, Class<Persistable>... classes) {
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (Class<Persistable> clazz : classes) {
			PersistentStore<?> store = this.adapters.get(clazz);
			if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
			futures.add(store.importData(path).thenApply((res) -> { return null; }));
		}
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> void exportData(KnownFormat format, ZipOutputStream stream, List<T> items) throws IOException {
		if (items == null || items.isEmpty()) return;
		Class<?> clazz = items.get(0).getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		store.exportData(format, stream, items);
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
}
