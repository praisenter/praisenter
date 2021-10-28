package org.praisenter.data.workspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.KnownFormat;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.Persistable;
import org.praisenter.data.PersistentStore;
import org.praisenter.data.Tag;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BiblePersistAdapter;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaPersistAdapter;
import org.praisenter.data.search.Indexable;
import org.praisenter.data.search.SearchCriteria;
import org.praisenter.data.search.SearchIndex;
import org.praisenter.data.search.SearchResults;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlidePersistAdapter;
import org.praisenter.data.slide.SlideRenderer;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.SongPersistAdapter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class WorkspaceManager {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final WorkspacePathResolver pathResolver;
	private final WorkspaceConfiguration workspaceConfiguration;
	private final SearchIndex searchIndex;
	private final Set<Path> otherWorkspaces;
	private final boolean newWorkspace;
	
	private final ConcurrentMap<Class<?>, PersistentStore<?>> adapters;
	
	private final Map<UUID, Persistable> itemLookup;
	
	private final ObservableList<Persistable> items;
	private final ObservableList<Persistable> itemsReadOnly;
	
	private final ObservableSet<Tag> tags;
	private final ObservableSet<Tag> tagsReadOnly;
	
	private WorkspaceManager(
			WorkspacePathResolver pathResolver,
			WorkspaceConfiguration workspaceConfiguration,
			SearchIndex searchIndex,
			Set<Path> otherWorkspaces,
			boolean isNewWorkspace) {
		this.pathResolver = pathResolver;
		this.workspaceConfiguration = workspaceConfiguration;
		this.searchIndex = searchIndex;
		this.otherWorkspaces = otherWorkspaces;
		this.newWorkspace = isNewWorkspace;
		
		this.adapters = new ConcurrentHashMap<>();
		
		this.itemLookup = new HashMap<>();
		
		this.items = FXCollections.observableArrayList();
		this.itemsReadOnly = FXCollections.unmodifiableObservableList(this.items);
		
		this.tags = FXCollections.observableSet(new HashSet<>());
		this.tagsReadOnly = FXCollections.unmodifiableObservableSet(this.tags);
	}
	
	public static WorkspaceManager open(Path basePath, Set<Path> otherWorkspaces) throws IOException {
		WorkspacePathResolver pathResolver = new WorkspacePathResolver(basePath);
		boolean isNewWorkspace = false;
		
		// create all the paths for the workspace
		LOGGER.info("Initializing workspace folders...");
		Path[] paths = new Path[] {
			pathResolver.getBasePath(),
			pathResolver.getLogsPath(),
			pathResolver.getBiblesPath(),
			pathResolver.getConfigurationPath(),
			pathResolver.getMediaPath(),
			pathResolver.getSearchIndexPath(),
			pathResolver.getSlidesPath(),
			pathResolver.getSongsPath()
		};
		
		for (Path path : paths) {
			try {
				Files.createDirectories(path);
			} catch (Exception ex) {
				LOGGER.warn("Failed to create the directory '" + path.toAbsolutePath() + "': " + ex.getMessage(), ex);
				throw ex;
			}
		}
		
		// setup the configuration
		LOGGER.info("Loading workspace configuration...");
		WorkspaceConfiguration workspaceConfiguration = null;
		Path configFilePath = pathResolver.getConfigurationFilePath();
		if (Files.exists(configFilePath)) {
			// read the file
			try {
				workspaceConfiguration = JsonIO.read(configFilePath, WorkspaceConfiguration.class);
			} catch (Exception ex) {
				LOGGER.error("Failed to read the file '" + configFilePath.toAbsolutePath() + "' as '" + WorkspaceConfiguration.class + "': " + ex.getMessage(), ex);
				throw ex;
			}
		} else {
			// generate the file
			workspaceConfiguration = new WorkspaceConfiguration();
			isNewWorkspace = true;
			try {
				JsonIO.write(configFilePath, workspaceConfiguration);
			} catch (Exception ex) {
				LOGGER.warn("Failed to save the file '" + configFilePath.toAbsolutePath() + "': " + ex.getMessage(), ex);
			}
		}

		// setup the search index
		LOGGER.info("Setting up search index...");
		SearchIndex searchIndex = null;
		try {
			final FSDirectory directory = FSDirectory.open(pathResolver.getSearchIndexPath());
			final Analyzer analyzer = new StandardAnalyzer(new CharArraySet(1, false));
			searchIndex = new SearchIndex(directory, analyzer);
		} catch (Exception ex) {
			LOGGER.warn("Failed to initialize the search index at '" + pathResolver.getSearchIndexPath() + "': " + ex.getMessage(), ex);
			throw ex;
		}
		
		return new WorkspaceManager(
				pathResolver,
				workspaceConfiguration,
				searchIndex,
				Collections.unmodifiableSet(otherWorkspaces),
				isNewWorkspace);
	}
	
	public WorkspacePathResolver getWorkspacePathResolver() {
		return this.pathResolver;
	}
	
	public WorkspaceConfiguration getWorkspaceConfiguration() {
		return this.workspaceConfiguration;
	}
	
	public CompletableFuture<Void> saveWorkspaceConfiguration() {
		return CompletableFuture.runAsync(() -> {
			try {
				JsonIO.write(this.pathResolver.getConfigurationFilePath(), this.workspaceConfiguration);
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		});
	}
	
	public Set<Path> getOtherWorkspaces() {
		return this.otherWorkspaces;
	}
	
	public boolean isNewWorkspace() {
		return this.newWorkspace;
	}
	
	public CompletableFuture<Void> registerBiblePersistAdapter() {
		return this.registerPersistAdapter(Bible.class, new BiblePersistAdapter(this.pathResolver.getBiblesPath()));
	}

	public CompletableFuture<Void> registerSongPersistAdapter() {
		return this.registerPersistAdapter(Song.class, new SongPersistAdapter(this.pathResolver.getSongsPath()));
	}
	
	public CompletableFuture<Void> registerMediaPersistAdapter() {
		return this.registerPersistAdapter(Media.class, new MediaPersistAdapter(this.pathResolver.getMediaPath(), this.workspaceConfiguration));
	}
	
	public CompletableFuture<Void> registerSlidePersistAdapter(SlideRenderer slideRenderer) {
		return this.registerPersistAdapter(Slide.class, new SlidePersistAdapter(this.pathResolver.getSlidesPath(), slideRenderer, this.workspaceConfiguration));
	}
	
	public <T extends Persistable> CompletableFuture<Void> registerPersistAdapter(Class<T> clazz, PersistAdapter<T> adapter) {
		PersistentStore<T> store = new PersistentStore<T>(adapter, this.searchIndex);
		return store.initialize().thenApply((items) -> {
			this.adapters.put(clazz, store);
			return items;
		}).thenCompose(AsyncHelper.onJavaFXThreadAndWait((items) -> {
			// add all items to the lookup and
			// initialize the set of all saved tags
			for (T item : items) {
				this.itemLookup.put(item.getId(), item);
				Set<Tag> tags = item.getTagsUnmodifiable();
				if (tags != null && !tags.isEmpty()) {
					this.tags.addAll(tags);
				}
			}
			
			// add all items to the full list
			this.items.addAll(items);
		}));
	}

	public ObservableList<Persistable> getItemsUnmodifiable() {
		return this.itemsReadOnly;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> ObservableList<T> getItemsUnmodifiable(Class<T> clazz) {
		PersistentStore<?> adapter = this.adapters.get(clazz);
		if (adapter == null) return null;
		return (ObservableList<T>) adapter.getItemsUnmodifiable();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> T getItem(Class<T> clazz, UUID id) {
//		PersistentStore<?> adapter = this.adapters.get(clazz);
//		if (adapter == null) return null;
//		return (T)adapter.getItem(id);
		Persistable o = this.getPersistableById(id);
		if (o != null) {
			if (o.getClass() == clazz) {
				return (T)o;
			} else {
				// this shouldn't happen, but lets throw just in case
				throw new ClassCastException("The type requested was '" + clazz.getName() + "' but the object with id '" + id + "' was of type '" + o.getClass().getName() + "'.");
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> create(T item) {
		Class<?> clazz = item.getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.create(item).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			// add to lookup
			this.itemLookup.put(item.getId(), item);
			
			// add to the main list
			this.items.add(item);
			
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
			// update the main list
			this.updateListItem(item);
			// make sure any new tags are added to the main set
			this.addItemTags(item);
		}));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<Void> delete(T item) {
		Class<?> clazz = item.getClass();
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.delete(item).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			// remove from the lookup
			this.itemLookup.remove(item.getId());
			
			// remove from the main list
			this.items.removeIf(i -> i.getId().equals(item.getId()));
		}));
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Persistable> CompletableFuture<DataImportResult<T>> importData(Path path, Class<T> clazz) {
		PersistentStore<T> store = (PersistentStore<T>)this.adapters.get(clazz);
		if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
		return store.importData(path, true).thenCompose(AsyncHelper.onJavaFXThreadAndWait((result) -> {
			// add created lookups
			for (Persistable item : result.getCreated()) {
				this.itemLookup.put(item.getId(), item);
			}
			
			// add created
			this.items.addAll(result.getCreated());
			
			// update updated
			for (Persistable item : result.getUpdated()) {
				this.updateListItem(item);
			}
			
			// make sure we capture any new tags from the import
			this.addDataImportResultTags(result);
			return result;
		}));
	}
	
	public CompletableFuture<Void> importData(Path path, Class<?>... classes) {
		final List<CompletableFuture<DataImportResult<? extends Persistable>>> futures = new ArrayList<>();
		
		for (Class<?> clazz : classes) {
			PersistentStore<?> store = this.adapters.get(clazz);
			if (store == null) throw new UnsupportedOperationException("A persistence adapter was not found for class '" + clazz + "'.");
			futures.add(store.importData(path, false).thenApply((l) -> (DataImportResult<? extends Persistable>)l));
		}
		
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenCompose(AsyncHelper.onJavaFXThreadAndWait(() -> {
			// process the results
			int numberImported = 0;
			
			for (CompletableFuture<DataImportResult<? extends Persistable>> future : futures) {
				DataImportResult<? extends Persistable> result = future.get();

				// check for null result (couldn't interpret it)
				if (result == null) {
					continue;
				}
				
				// add created lookups
				for (Persistable item : result.getCreated()) {
					this.itemLookup.put(item.getId(), item);
					numberImported++;
				}
				
				// add created
				this.items.addAll(result.getCreated());
				
				// update updated
				for (Persistable item : result.getUpdated()) {
					this.updateListItem(item);
					numberImported++;
				}
				
				// make sure we capture any new tags from the import
				this.addDataImportResultTags(result);
			}
			
			if (numberImported == 0) {
				throw new CompletionException(new Exception("Failed to import path '" + path + "' it does not match any supported format of media, bible, song, or slide."));
			}
		}));
	}
	
	private void updateListItem(Persistable item) {
		this.itemLookup.put(item.getId(), item);
		
		int index = -1;
		for (int i = 0; i < this.items.size(); i++) {
			Persistable test = this.items.get(i);
			if (test.getId().equals(item.getId())) {
				index = i;
			}
		}
		if (index >= 0 && index < this.items.size()) {
			this.items.set(index, item);
		}
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
	
	public CompletableFuture<SearchResults> search(SearchCriteria criteria) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.searchIndex.search(criteria);
			} catch (Exception ex) {
				throw new CompletionException(ex);
			}
		});
	}
	
	public CompletableFuture<Void> reindex() {
		List<? extends Indexable> items = new ArrayList<Persistable>(this.items);
		return CompletableFuture.runAsync(() -> {
			try {
				this.searchIndex.reindex(items);
			} catch (IOException e) {
				throw new CompletionException(e);
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
		this.throwIfNotJavaFXThread();
		return this.itemLookup.get(id);
	}
	
	public ObservableSet<Tag> getTagsUmodifiable() {
		return this.tagsReadOnly;
	}

	private void throwIfNotJavaFXThread() {
		if (!Platform.isFxApplicationThread()) {
			throw new IllegalStateException("The getItem method must be called on the Java FX UI thread.");
		}
	}
}
