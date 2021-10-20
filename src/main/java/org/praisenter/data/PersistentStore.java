package org.praisenter.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.zip.ZipOutputStream;

import org.praisenter.async.AsyncHelper;
import org.praisenter.data.search.SearchIndex;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class PersistentStore<T extends Persistable> {
	private final PersistAdapter<T> adapter;
	private final SearchIndex index;
	
	private final ObservableList<T> items;
	private final ObservableList<T> itemsReadOnly;
	
	public PersistentStore(PersistAdapter<T> adapter, SearchIndex index) {
		this.adapter = adapter;
		this.index = index;
		
		this.items = FXCollections.observableArrayList();
		this.itemsReadOnly = FXCollections.unmodifiableObservableList(this.items);
	}
	
	public CompletableFuture<List<T>> initialize() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				this.adapter.initialize();
				return this.adapter.load();
			} catch (Exception ex) {
				throw new CompletionException(ex);
			}
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((items) -> {
			this.items.addAll(items);
			return items;
		}));
	}
	
	private void throwIfNotJavaFXThread() {
		if (!Platform.isFxApplicationThread()) {
			throw new IllegalStateException("The getItem method must be called on the Java FX UI thread.");
		}
	}
	
	public ObservableList<T> getItemsUnmodifiable() {
		this.throwIfNotJavaFXThread();
		return this.itemsReadOnly;
	}
	
	public T getItem(UUID id) {
		this.throwIfNotJavaFXThread();
		for (int i = 0; i < this.items.size(); i++) {
			T item = this.items.get(i);
			if (id.equals(item.getId())) {
				return item;
			}
		}
		return null;
	}
	
	public CompletableFuture<Void> create(T item) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.adapter.create(item);
				this.index.create(item);
			} catch (Exception ex) {
				throw new CompletionException(ex);
			}
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.items.add(item);
		}));
	}
	
	public CompletableFuture<Void> update(T item) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.adapter.update(item);
				this.index.update(item);
			} catch (Exception ex) {
				throw new CompletionException(ex);
			}
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			int index = -1;
			for (int i = 0; i < this.items.size(); i++) {
				T test = this.items.get(i);
				if (test.getId().equals(item.getId())) {
					index = i;
				}
			}
			if (index >= 0 && index < this.items.size()) {
				this.items.set(index, item);
			}
		}));
	}
	
	public CompletableFuture<Void> delete(T item) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.adapter.delete(item);
				this.index.delete(item);
			} catch (Exception ex) {
				throw new CompletionException(ex);
			}
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			this.items.removeIf(i -> i.getId().equals(item.getId()));
		}));
	}
	
	public CompletableFuture<DataImportResult<T>> importData(Path path, boolean isTypeKnown) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				DataImportResult<T> result = this.adapter.importData(path);
				if (result != null) {
					for (T item : result.getCreated()) {
						this.index.create(item);
					}
					for (T item : result.getUpdated()) {
						this.index.update(item);
					}
				}
				return result;
			} catch (Exception ex) {
				if (isTypeKnown) {
					throw new CompletionException(ex);
				} else {
					return null;
				}
			}
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((result) -> {
			if (result != null) {
				for (T item : result.getUpdated()) {
					int index = this.items.indexOf(item);
					if (index < 0 || index >= this.items.size()) {
						this.items.add(item);
					} else {
						this.items.set(index, item);
					}
				}
				this.items.addAll(result.getCreated());
			}
			return result;
		}));
	}
	
	public void exportData(KnownFormat format, ZipOutputStream stream, List<T> items) throws IOException {
		this.adapter.exportData(format, stream, items);
	}
	
	public CompletableFuture<Void> exportData(KnownFormat format, Path path, T item) {
		return CompletableFuture.runAsync(() -> {
			try {
				this.adapter.exportData(format, path, item);
			} catch (Exception ex) {
				throw new CompletionException(ex);
			}
		});
	}
	
	public Path getFilePath(T item) {
		return this.adapter.getFilePath(item);
	}
}
