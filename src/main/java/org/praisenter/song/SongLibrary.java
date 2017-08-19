/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.song;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.praisenter.Constants;
import org.praisenter.FileData;
import org.praisenter.InvalidFormatException;
import org.praisenter.LockMap;
import org.praisenter.SearchType;
import org.praisenter.Tag;
import org.praisenter.UnknownFormatException;
import org.praisenter.json.JsonIO;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;

// FEATURE (M) Add duplicate detection and merge features

/**
 * A collection of songs that has been loaded into a specific location and converted
 * into the praisenter format.
 * <p>
 * Obtain a {@link SongLibrary} instance by calling the {@link #open(Path)}
 * static method. Only one instance should be created for each path. Multiple instances
 * modifying the same path can have unexpected results and can show different sets of songs.
 * <p>
 * This class is intended to be thread safe within this application but can still contend
 * with other programs during disk operations.
 * <p>
 * The songs contained in the specified folder will be added to a lucene index for searching.
 * Opening a song library will initiate a process to update the index to ensure the latest
 * information is contained in the index. This process can take some time as it must read
 * each song file and update it in the index.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SongLibrary {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The sub folder in the zip to store songs */
	private static final String ZIP_DIR = "songs";

	// lucene

	/** The relative path to the directory containing the lucene index */
	private static final String INDEX_DIR = "_index";
	
	/** The lucene field to store the song's unique identifier */
	private static final String FIELD_ID = "id";
	
	/** The lucene field to store the song's path */
	private static final String FIELD_PATH = "path";
	
	/** The lucene field that contains all the song searchable text */
	private static final String FIELD_TEXT = "text";
	
	// location
	
	/** The path to the song library */
	private final Path path;
	
	/** The path to the song library's index */
	private final Path indexPath;
	
	// searching
	
	/** The file-system index */
	private Directory directory;
	
	/** The analyzer for the index */
	private Analyzer analyzer;
	
	// loaded
	
	/** The songs */
	private final Map<UUID, FileData<Song>> songs;

	// locks
	
	/** The mutex locks */
	private final LockMap<String> locks;
	
	/**
	 * Sets up a new {@link SongLibrary} at the given path.
	 * @param path the root path to the song library
	 * @return {@link SongLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final SongLibrary open(Path path) throws IOException {
		SongLibrary sl = new SongLibrary(path);
		sl.initialize();
		return sl;
	}
	
	/**
	 * Full constructor.
	 * @param path the path to maintain the song library
	 */
	private SongLibrary(Path path) {
		this.path = path;
		this.indexPath = this.path.resolve(INDEX_DIR);
		this.songs = new HashMap<UUID, FileData<Song>>();
		this.locks = new LockMap<String>();
	}
	
	/**
	 * Performs the initialization required by the song library.
	 * @throws IOException if an IO error occurs
	 */
	private void initialize() throws IOException {
		// verify paths exist
		Files.createDirectories(this.path);
		Files.createDirectories(this.indexPath);
		
		// load and update the index
		this.directory = FSDirectory.open(this.indexPath);
		
		// don't exclude stop words!
		this.analyzer = new StandardAnalyzer(new CharArraySet(1, false));
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			// index existing documents
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.path)) {
				for (Path file : stream) {
					// only open files
					if (Files.isRegularFile(file)) {
						// only open json files
						if (MimeType.JSON.check(file)) {
							try (InputStream is = Files.newInputStream(file)) {
								try {
									// read in the xml
									Song song = JsonIO.read(is, Song.class);

									// once the song has been loaded successfully
									// and added to the lucene index successfully
									// then we'll add it to the song map
									this.songs.put(song.getId(), new FileData<Song>(song, file));
								} catch (Exception e) {
									// make sure its not in the index
									// we don't want to be able to find the song
									// if we failed to load it
									LOGGER.warn("Failed to load song '" + file.toAbsolutePath().toString() + "'", e);
									writer.deleteDocuments(new Term(FIELD_PATH, file.toAbsolutePath().toString()));
								}
							} catch (IOException ex) {
								// make sure its not in the index
								// we don't want to be able to find the song
								// if we failed to load it
								LOGGER.warn("Failed to load song '" + file.toAbsolutePath().toString() + "'", ex);
								writer.deleteDocuments(new Term(FIELD_PATH, file.toAbsolutePath().toString()));
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns a lucene document object that contains the fields for the given song.
	 * @param song the song
	 */
	private Document createDocument(FileData<Song> fileData) {
		Song song = fileData.getData();
		Document document = new Document();
		// we store the path and id so we can lookup up songs by either
		
		// store the path so we know where to get the song
		Field pathField = new StringField(FIELD_PATH, fileData.getPath().toAbsolutePath().toString(), Field.Store.YES);
		document.add(pathField);
		
		// store the id so we can lookup the song in the cache
		Field idField = new StringField(FIELD_ID, song.getId().toString(), Field.Store.YES);
		document.add(idField);
		
		// search on keywords too
		if (!StringManipulator.isNullOrEmpty(song.keywords)) {
			Field keywordsField = new StringField(FIELD_TEXT, song.keywords, Field.Store.YES);
			document.add(keywordsField);
		}
		
		// iterate the lyrics
		for (Lyrics lyrics : song.lyrics) {
			
			// title fields
			if (!StringManipulator.isNullOrEmpty(lyrics.title)) {
				Field titleField = new TextField(FIELD_TEXT, lyrics.title, Field.Store.YES);
				document.add(titleField);
			}
			
			// verse fields
			for (Verse verse : lyrics.verses) {
				String text = verse.getText();
				if (!StringManipulator.isNullOrEmpty(text)) {
					Field verseField = new TextField(FIELD_TEXT, text, Field.Store.YES);
					document.add(verseField);
				}
			}
		}
		
		return document;
	}
	
	/**
	 * Re-indexes all songs.
	 * @throws IOException if an IO error occurs
	 */
	public void reindex() throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			for (FileData<Song> fileData : this.songs.values()) {
				Song song = fileData.getData();
				try {
					// add the data to the document
					Document document = createDocument(fileData);
					// update the document
					writer.updateDocument(new Term(FIELD_ID, song.getId().toString()), document);
				} catch (Exception e) {
					// make sure its not in the index
					LOGGER.warn("Failed to update the song in the lucene index '" + fileData.getPath().toAbsolutePath().toString() + "'", e);
				}
			}
		}
	}

	/**
	 * Returns the lock for the index.
	 * @return Object
	 */
	private Object getIndexLock() {
		return this.locks.get("INDEX");
	}
	
	/**
	 * Returns a lock for the given song.
	 * @param song the song
	 * @return Object
	 */
	private Object getSongLock(Song song) {
		return this.locks.get(song.getId().toString());
	}

	/**
	 * Returns a lock for the given path file name.
	 * @param path the path
	 * @return Object
	 */
	private Object getPathLock(Path path) {
		return this.locks.get(path.getFileName().toString());
	}
	
	/**
	 * Returns the song for the given id or null if not found.
	 * @param id the song id
	 * @return {@link Song}
	 */
	public Song get(UUID id) {
		if (id == null) return null;
		if (!this.songs.containsKey(id)) return null;
		return this.songs.get(id).getData();
	}
	
	/**
	 * Returns all the songs in this song library.
	 * @return List&lt;{@link Song}&gt;
	 */
	public List<Song> all() {
		return this.songs.values().stream().map(f -> f.getData()).collect(Collectors.toList());
	}
	
	/**
	 * Returns the number of songs in the library.
	 * @return int
	 */
	public int size() {
		return this.songs.size();
	}
	
	/**
	 * Saves the given song (either new or existing) to the song library.
	 * @param song the song to save
	 * @throws IOException if an IO error occurs
	 */
	public void save(Song song) throws IOException {
		// update the last modified date
		song.setModifiedDate(Instant.now());
		
		// calling this method could indicate one of the following:
		// 1. New
		// 2. Save Existing
		// 3. Save Existing + Rename
		
		FileData<Song> fileData = null;
		String title = song.getDefaultTitle();
		
		// obtain the lock on the song
		synchronized (this.getSongLock(song)) {
			LOGGER.debug("Saving song '{}'.", title);
			
			// get the current file reference
			fileData = this.songs.get(song.getId());
			
			// generate the file name and path
			String name = StringManipulator.toFileName(title, song.getId());
			Path path = this.path.resolve(name + Constants.SONG_FILE_EXTENSION);
			Path uuid = this.path.resolve(StringManipulator.toFileName(song.getId()) + Constants.SONG_FILE_EXTENSION);
			
			// check for operation
			if (fileData == null) {
				LOGGER.debug("Adding song '{}'.", title);
				// then its a new
				synchronized (this.getPathLock(path)) {
					// check if the path exists once we obtain the lock
					if (Files.exists(path)) {
						// just use the UUID (which shouldn't need a lock since it's unique)
						path = uuid;
					}
					JsonIO.write(path, song);
					fileData = new FileData<Song>(song, path);
					LOGGER.debug("Song '{}' saved to '{}'.", title, path);
				}
			} else {
				LOGGER.debug("Updating song '{}'.", title);
				// it's an existing one
				Path original = fileData.getPath();
				if (!original.equals(path)) {
					// obtain the desired path lock
					synchronized (this.getPathLock(path)) {
						// check if the path exists once we obtain the lock
						if (Files.exists(path)) {
							// is the original path the UUID path (which indicates that when it was imported
							// it had a file name conflict)
							if (original.equals(uuid)) {
								// if so, this isn't really a rename, just save it
								JsonIO.write(original, song);
							} else {
								// if the path already exists and the current path isn't the uuid path
								// then we know that this was a rename to a different name that already exists
								LOGGER.warn("Unable to rename song '{}' to '{}' because a file with that name already exists.", title, path.getFileName());
								throw new FileAlreadyExistsException(path.getFileName().toString());
							}
						} else {
							LOGGER.debug("Renaming song '{}' to '{}'.", title, path.getFileName());
							// otherwise rename the file
							Files.move(original, path);
							// then save the changes
							JsonIO.write(path, song);
							// update the path
							fileData = new FileData<Song>(song, path);
						}
					}
				} else {
					// it's a normal save
					JsonIO.write(original, song);
				}
			}
			
			// update the song map (it may have changed)
			this.songs.put(song.getId(), fileData);
		}
		
		// add to/update the lucene index
		synchronized (this.getIndexLock()) {
			LOGGER.debug("Updating lucene index for song '{}'.", title);
			IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			try (IndexWriter writer = new IndexWriter(this.directory, config)) {
				// update the fields
				Document document = createDocument(fileData);
				// update the document
				writer.updateDocument(new Term(FIELD_ID, song.getId().toString()), document);
			} catch (Exception ex) {
				// if this happens, the user should really just execute a reindex
				// we don't know what to back out at this point
				LOGGER.warn("Failed to update the lucene index for song '" + title + "'. Please initiate a reindex.", ex);
			}
		}
	}
	
	/**
	 * Removes the given song from the song library and deletes the file on
	 * the file system.
	 * @param song the song
	 * @throws IOException if an IO error occurs
	 */
	public void remove(Song song) throws IOException {
		if (song == null) return;
		
		UUID id = song.getId();
		if (id == null) return;
		
		String title = song.getDefaultTitle();
		
		synchronized (this.getSongLock(song)) {
			FileData<Song> fileData = this.songs.get(song.getId());
			LOGGER.debug("Removing song '{}'.", title);
			// delete the file
			if (fileData != null) {
				Files.deleteIfExists(fileData.getPath());
			}
			// remove it from the map
			this.songs.remove(id);
		}
		
		synchronized (this.getIndexLock()) {
			LOGGER.debug("Removing lucene indexing for song '{}'.", title);
			// remove from the lucene index so it can't be found
			// in searches any more
			IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			try (IndexWriter writer = new IndexWriter(this.directory, config)) {
				// update the document
				writer.deleteDocuments(new Term(FIELD_ID, id.toString()));
			} catch (Exception ex) {
				// if this happens, the user should really just execute a reindex
				// we don't know what to back out at this point
				LOGGER.warn("Failed to remove the lucene indexing for song '" + title + "'. Please initiate a reindex.", ex);
			}
		}
	}
	
	// tags
	
	/**
	 * Adds the given tag to the given song and saves it.
	 * @param song the song
	 * @param tag the new tag
	 * @return boolean true if the tag was added successfully
	 * @throws IOException if an IO error occurs
	 */
	public boolean addTag(Song song, Tag tag) throws IOException {
		// obtain the lock for this song
		synchronized(this.getSongLock(song)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this song was deleted
			// or renamed. the song map will contain the latest 
			// object for us to update
			FileData<Song> fileData = this.songs.get(song.getId());
			Song latest = fileData.getData();
			// make sure the song wasn't removed
			if (latest != null) {
				LOGGER.debug("Adding tag '{}' to song '{}'.", tag, song.getDefaultTitle());
				// see if adding the tag really does add it...
				boolean added = latest.getTags().add(tag);
				if (added) {
					try {
						JsonIO.write(fileData.getPath(), song);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save song after adding tag '{}' to song '{}'.", tag, song.getDefaultTitle());
						// remove the tag due to not being able to save
						latest.getTags().remove(tag);
						// rethrow the exception
						throw ex;
					}
				}
				return added;
			}
			return false;			
		}
	}

	/**
	 * Adds the given tags to the given song and saves it.
	 * @param song the song
	 * @param tags the new tags
	 * @return boolean true if the tags were added successfully
	 * @throws IOException if an IO error occurs
	 */	
	public boolean addTags(Song song, Collection<Tag> tags) throws IOException {
		// obtain the lock for this song
		synchronized(this.getSongLock(song)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this song was deleted
			// or renamed. the song map will contain the latest 
			// song for us to update
			FileData<Song> fileData = this.songs.get(song.getId());
			Song latest = fileData.getData();
			// make sure the song wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Adding tags '{}' to song '{}'.", ts, song.getDefaultTitle());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.getTags());
				// attempt to add all of the tags
				boolean added = latest.getTags().addAll(tags);
				if (added) {
					try {
						JsonIO.write(fileData.getPath(), song);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save song after adding tags '{}' to song '{}'.", ts, song.getDefaultTitle());
						// reset to initial state
						latest.getTags().retainAll(old);
						// rethrow the exception
						throw ex;
					}
				}
				return added;
			}
			return false;
		}
	}
	
	/**
	 * Sets the given tags on the given song and saves it.
	 * @param song the song
	 * @param tags the new tags
	 * @return boolean true if the tags were set successfully
	 * @throws IOException if an IO error occurs
	 */	
	public boolean setTags(Song song, Collection<Tag> tags) throws IOException {
		// obtain the lock for this song
		synchronized(this.getSongLock(song)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this song was deleted
			// or renamed. the song map will contain the latest 
			// song for us to update
			FileData<Song> fileData = this.songs.get(song.getId());
			Song latest = fileData.getData();
			// make sure the song wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Setting tags '{}' on song '{}'.", ts, song.getDefaultTitle());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.getTags());
				// attempt to set the tags
				boolean changed = latest.getTags().addAll(tags);
				changed |= latest.getTags().retainAll(tags);
				if (changed) {
					try {
						// attempt to save
						JsonIO.write(fileData.getPath(), song);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save song after setting tags '{}' on song '{}'.", ts, song.getDefaultTitle());
						// reset to initial state
						latest.getTags().clear();
						latest.getTags().addAll(old);
						// rethrow the exception
						throw ex;
					}
				}
				return changed;
			}
			return false;
		}
	}
	
	/**
	 * Removes the given tag from the given song and saves it.
	 * @param song the song
	 * @param tag the tag to remove
	 * @return boolean true if the tag was removed successfully
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTag(Song song, Tag tag) throws IOException {
		// obtain the lock for this song
		synchronized(this.getSongLock(song)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this song was deleted
			// or renamed. the song map will contain the latest 
			// song for us to update
			FileData<Song> fileData = this.songs.get(song.getId());
			Song latest = fileData.getData();
			// make sure the song wasn't removed
			if (latest != null) {
				LOGGER.debug("Removing tag '{}' from song '{}'.", tag, song.getDefaultTitle());
				boolean removed = latest.getTags().remove(tag);
				if (removed) {
					try {
						JsonIO.write(fileData.getPath(), song);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save song after removing tag '{}' from song '{}'.", tag, song.getDefaultTitle());
						// reset to initial state
						latest.getTags().add(tag);
						// rethrow the exception
						throw ex;
					}
				}
				return removed;
			}
			return false;
		}
	}

	/**
	 * Removes the given tags from the given song and saves it.
	 * @param song the song
	 * @param tags the tags to remove
	 * @return boolean true if the tags were removed successfully
	 * @throws IOException if an IO error occurs
	 */	
	public boolean removeTags(Song song, Collection<Tag> tags) throws IOException {
		// obtain the lock for this song
		synchronized(this.getSongLock(song)) {
			// sanity check, it's possible that while this thread
			// was waiting for the lock, that this song was deleted
			// or renamed. the song map will contain the latest 
			// song for us to update
			FileData<Song> fileData = this.songs.get(song.getId());
			Song latest = fileData.getData();
			// make sure the song wasn't removed
			if (latest != null) {
				String ts = tags.stream().map(t -> t.getName()).collect(Collectors.joining(", "));
				LOGGER.debug("Removing tags '{}' from song '{}'.", ts, song.getDefaultTitle());
				// keep the old set just in case the new set fails to save
				TreeSet<Tag> old = new TreeSet<Tag>(latest.getTags());
				// attempt to set the tags
				boolean removed = latest.getTags().removeAll(tags);
				if (removed) {
					try {
						JsonIO.write(fileData.getPath(), song);
					} catch (Exception ex) {
						LOGGER.warn("Failed to save song after removing tags '{}' from song '{}'.", ts, song.getDefaultTitle());
						// reset to initial state
						latest.getTags().clear();
						latest.getTags().addAll(old);
						// rethrow the exception
						throw ex;
					}
				}
				return removed;
			}
			return false;
		}
	}

	// export / import
	
	/**
	 * Exports the given songs to the given file using the given exporter
	 * @param path the file
	 * @param songs the songs to export
	 * @param exporter the song exporter to use
	 * @throws IOException if an IO error occurs
	 */
	public void exportSongs(Path path, List<Song> songs, SongExporter exporter) throws IOException {
		exporter.execute(path, songs);
	}

	/**
	 * Exports the given songs to the given file using the given exporter.
	 * @param stream the zip stream to export to
	 * @param songs the songs to export
	 * @param exporter the song exporter to use
	 * @throws IOException if an IO error occurs
	 */
	public void exportSongs(ZipOutputStream stream, List<Song> songs, SongExporter exporter) throws IOException {
		exporter.execute(stream, ZIP_DIR, songs);
	}
	
	/**
	 * Imports the given songs into the library.
	 * @param path the path to a zip file
	 * @return List&lt;{@link Song}&gt;
	 * @throws FileNotFoundException if the given path is not found
	 * @throws InvalidFormatException if the file wasn't in the format expected
	 * @throws UnknownFormatException if the format of the file couldn't be determined
	 * @throws IOException if an IO error occurs
	 */
	public List<Song> importSongs(Path path) throws FileNotFoundException, IOException, InvalidFormatException, UnknownFormatException {
		SongFormatDetector importer = new SongFormatDetector();
		List<Song> songs = importer.execute(path);
		
		LOGGER.debug("'{}' songs found in '{}'.", songs.size(), path);
		Iterator<Song> it = songs.iterator();
		while (it.hasNext()) {
			Song song = it.next();
			try {
				this.save(song);
			} catch (Exception ex) {
				LOGGER.error("Failed to save the song '" + song.getDefaultTitle() + "'", ex);
				it.remove();
			}
		}
		return songs;
	}
	
	// searching
	
	/**
	 * Searches this song library for the given criteria.
	 * @param criteria the search criteria
	 * @return List&lt;{@link SongSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 */
	public List<SongSearchResult> search(SongSearchCriteria criteria) throws IOException {
		// verify text
		if (criteria == null || criteria.getText() == null || criteria.getText().length() == 0) {
			return Collections.emptyList();
		}
		
		// tokenize
		List<String> tokens = this.getTokens(criteria.getText(), FIELD_TEXT);
		
		// build query
		Query query = getQueryForTokens(FIELD_TEXT, tokens, criteria.getType());
		
		// search
		return this.search(query, criteria.getMaximumResults());
	}

	/**
	 * Uses the lucene analyzer to tokenize the given text for the given lucene field.
	 * @param text the text to tokenize
	 * @param field the lucene field the tokens will be searching
	 * @return List&lt;String&gt;
	 * @throws IOException
	 */
	private List<String> getTokens(String text, String field) throws IOException {
		List<String> tokens = new ArrayList<String>();
		
		LOGGER.debug("Tokenizing input '{}'.", text);
		TokenStream stream = this.analyzer.tokenStream(field, text);
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();

		while (stream.incrementToken()) {
			tokens.add(attr.toString());
		}
		
		stream.end();
		stream.close();
		
		LOGGER.debug("Input tokenized into: {}", String.join(", ", tokens));
		return tokens;
	}
	
	/**
	 * Builds a lucene query for the given lucene field, tokens and search type.
	 * @param field the lucene field to search
	 * @param tokens the tokens to search for
	 * @param type the type of search
	 * @return Query
	 */
	private Query getQueryForTokens(String field, List<String> tokens, SearchType type) {
		Query query = null;
		
		LOGGER.debug("Building lucene query based on search type '{}' and tokens.", type);
		if (tokens.size() == 0) return null;
		if (tokens.size() == 1) {
			LOGGER.debug("Using single token FuzzyQuery.");
			// single term, just do a fuzzy query on it with a larger max edit distance
			String token = tokens.get(0);
			query = new FuzzyQuery(new Term(field, token));
		// PHRASE
		} else if (type == SearchType.PHRASE) {
			LOGGER.debug("Using SpanMultiTermQuery with FuzzyQuery for each token.");
			// for phrase, do a span-near-fuzzy query since we 
			// care if the words are close to each other
			SpanQuery[] sqs = new SpanQuery[tokens.size()];
			for (int i = 0; i < tokens.size(); i++) {
				sqs[i] = new SpanMultiTermQueryWrapper<FuzzyQuery>(new FuzzyQuery(new Term(field, tokens.get(i))));
			}
			// the terms should be within 3 terms of each other
			query = new SpanNearQuery(sqs, 3, false);
		// ALL_WORDS, ANY_WORD
		} else {
			LOGGER.debug("Using BooleanQuery with FuzzyQuery for each token.");
			// do an and/or combination of fuzzy queries
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (String token : tokens) {
				builder.add(new FuzzyQuery(new Term(field, token)), type == SearchType.ALL_WORDS ? Occur.MUST : Occur.SHOULD);
			}
			query = builder.build();
		}
		// ALL_WILDCARD, ANY_WILDCARD (not available as an option)
		
		return query;
	}
	
	/**
	 * Runs the given lucene query and returns a list of song search results.
	 * @param query the lucene query to execute
	 * @return List&lt;{@link SongSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 * @see <a href="http://stackoverflow.com/questions/25814445/accessing-words-around-a-positional-match-in-lucene">Accessing words around a positional match in Lucene</a>
	 * @see SongSearchResult
	 */
	private List<SongSearchResult> search(Query query, int maxResults) throws IOException {
		List<SongSearchResult> results = new ArrayList<SongSearchResult>();
		
		// NOTE: this doesn't need to be synchronized with the index, it will use a snapshot
		// of the index at the time it's opened
		LOGGER.debug("Searching using constructed query.");
		try (IndexReader reader = DirectoryReader.open(this.directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);
			
			TopDocs result = searcher.search(query, maxResults + 1);
			ScoreDoc[] docs = result.scoreDocs;
			
			LOGGER.debug("Search found {} results.", docs.length);
			
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(scorer);
			
			for (ScoreDoc doc : docs) {
				Document document = searcher.doc(doc.doc);
				
				// get the song
				FileData<Song> fileData = this.songs.get(UUID.fromString(document.get(FIELD_ID)));
				if (fileData == null) {
					LOGGER.warn("Unable to find song '{}'. A re-index might fix this problem.", document.get(FIELD_ID));
					continue;
				}
				
				// get the song
				Song song = fileData.getData();
				
				// just continue if its not found
				if (song == null) {
					LOGGER.warn("Unable to find song '{}'. A re-index might fix this problem.", document.get(FIELD_ID));
					continue;
				}
				
				// get the text around the match
				List<SongSearchMatch> matches = new ArrayList<SongSearchMatch>();
				String[] items = document.getValues(FIELD_TEXT);
				for (String item : items) {
					try {
						String text = highlighter.getBestFragment(this.analyzer, FIELD_TEXT, item);
						if (text != null) {
							matches.add(new SongSearchMatch(FIELD_TEXT, item, text));
						}
					} catch (Exception e) {
						LOGGER.warn("Failed to find matching text for value " + item + " due to unexpected exception.", e);
					}
				}
				
				SongSearchResult match = new SongSearchResult(doc.score, song, matches);
				results.add(match);
			}
		}
		
		return results;
	}
}
