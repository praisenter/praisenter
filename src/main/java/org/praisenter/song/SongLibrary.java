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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;

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
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.praisenter.Constants;
import org.praisenter.SearchType;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;
import org.praisenter.xml.XmlIO;

// FIXME update for multi-threading and other features added to slide and bible libraries
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
	
	/** The extension to use for the song files */
	private static final String EXTENSION = ".xml";
	
	// lucene
	
	/** The lucene field to store the song's unique identifier */
	private static final String FIELD_ID = "id";
	
	/** The lucene field to store the song's path */
	private static final String FIELD_PATH = "path";
	
	/** The lucene field that contains all the song searchable text */
	private static final String FIELD_TEXT = "text";
	
	/** The relative path to the directory containing the lucene index */
	private static final String INDEX_DIR = "_index";
	
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
	private final Map<UUID, Song> songs;
	
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
		
		this.songs = new HashMap<UUID, Song>();
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
						// only open xml files
						if (MimeType.XML.check(file)) {
							try (InputStream is = Files.newInputStream(file)) {
								try {
									// read in the xml
									Song song = XmlIO.read(is, Song.class);
									song.path = file;

									// once the song has been loaded successfully
									// and added to the lucene index successfully
									// then we'll add it to the song map
									this.songs.put(song.getId(), song);
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
	private Document createDocument(Song song) {
		Document document = new Document();
		// we store the path and id so we can lookup up songs by either
		
		// store the path so we know where to get the song
		Field pathField = new StringField(FIELD_PATH, song.path.toAbsolutePath().toString(), Field.Store.YES);
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
				String text = verse.getOutput(SongOutputType.TEXT);
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
	public synchronized void reindex() throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			for (Song song : this.songs.values()) {
				try {
					// add the data to the document
					Document document = createDocument(song);
					// update the document
					writer.updateDocument(new Term(FIELD_ID, song.getId().toString()), document);
				} catch (Exception e) {
					// make sure its not in the index
					LOGGER.warn("Failed to update the song in the lucene index '" + song.path.toAbsolutePath().toString() + "'", e);
				}
			}
		}
	}
	
	/**
	 * Returns the song for the given id or null if not found.
	 * @param id the song id
	 * @return {@link Song}
	 */
	public synchronized Song get(UUID id) {
		if (id == null) return null;
		return this.songs.get(id);
	}
	
	/**
	 * Returns all the songs in this song library.
	 * @return List&lt;{@link Song}&gt;
	 */
	public synchronized List<Song> all() {
		return new ArrayList<Song>(this.songs.values());
	}
	
	/**
	 * Returns the number of songs in the library.
	 * @return int
	 */
	public synchronized int size() {
		return this.songs.size();
	}
	
	/**
	 * Returns true if the given id is in the song library.
	 * @param id the song id
	 * @return boolean
	 */
	public synchronized boolean contains(UUID id) {
		if (id == null) return false;
		return this.songs.containsKey(id);
	}
	
	/**
	 * Returns true if the given song is in the song library.
	 * @param song the song
	 * @return boolean
	 */
	public synchronized boolean contains(Song song) {
		if (song == null || song.getId() == null) return false;
		return this.songs.containsKey(song.getId());
	}
	
	/**
	 * Saves the given song (either new or existing) to the song library.
	 * @param song the song to save
	 * @throws JAXBException if an error occurs while writing the song to XML
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void save(Song song) throws JAXBException, IOException {
		if (this.songs.containsKey(song.getId())) {
			// technically an update
			song.path = this.songs.get(song.getId()).path;
		}
		
		if (song.path == null) {
			String name = createFileName(song);
			Path path = this.path.resolve(name + EXTENSION);
			// verify there doesn't exist a song with this name already
			if (Files.exists(path)) {
				// just use the guid
				path = this.path.resolve(song.getId().toString().replaceAll("-", "") + EXTENSION);
			}
			song.path = path;
		}
		
		// save the song		
		XmlIO.save(song.path, song);
		
		// add to/update the lucene index
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			// update the fields
			Document document = createDocument(song);
			
			// update the document
			writer.updateDocument(new Term(FIELD_ID, song.getId().toString()), document);
		}
		
		this.songs.put(song.getId(), song);
	}
	
	/**
	 * Removes the given song id from the song library and deletes the file on
	 * the file system.
	 * @param id the id of the song
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void remove(UUID id) throws IOException {
		if (id == null) return;
		// remove from the lucene index so it can't be found
		// in searches any more
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			// update the document
			writer.deleteDocuments(new Term(FIELD_ID, id.toString()));
		}
		
		// remove it from the map
		Song song = this.songs.remove(id);
		
		// delete the file
		if (song != null) {
			Files.deleteIfExists(song.path);
		}
	}
	
	/**
	 * Removes the given song from the song library and deletes the file on
	 * the file system.
	 * @param song the song to remove
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void remove(Song song) throws IOException {
		if (song == null || song.getId() == null) return;
		remove(song.getId());
	}
	
	/**
	 * Creates a file name for the given song based off the
	 * title, variant and author for use as a file name.
	 * @param song the song
	 * @return String
	 */
	public static final String createFileName(Song song) {
		String title = song.getDefaultTitle();
		String variant = song.getVariant();
		Author author = song.getDefaultAuthor();
		
		StringBuilder sb = new StringBuilder();
		if (title != null) {
			sb.append(title);
		}
		if (variant != null && variant.length() != 0) {
			sb.append(variant);
		}
		if (author != null && author.name != null && author.name.length() != 0) {
			sb.append(author.name);
		}
		String name = sb.toString();
		
//		// truncate the name to certain length
//		int max = Constants.MAX_FILE_NAME_CODEPOINTS - EXTENSION.length();
//		if (name.length() > max) {
//			LOGGER.warn("File name too long '{}', truncating.", name);
//			name = name.substring(0, Math.min(name.length() - 1, max));
//		}
		
		return StringManipulator.toFileName(name, song.getId());
	}
	
	// searching
	
	/**
	 * Searches this song library for the given text using the given search type.
	 * @param text the search text
	 * @param type the search type
	 * @return List&lt;{@link SongSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 */
	public List<SongSearchResult> search(String text, SearchType type) throws IOException {
		// verify text
		if (text == null || text.length() == 0) {
			return Collections.emptyList();
		}
		
//		// check for wildcard characters for non-wildcard searches
//		if (type != SearchType.ALL_WILDCARD && type != SearchType.ANY_WILDCARD && !text.contains(Character.toString(WildcardQuery.WILDCARD_CHAR))) {
//			// take the wildcard characters out
//			text = text.replaceAll("\\" + WildcardQuery.WILDCARD_CHAR, "");
//		}
		
		// tokenize
		List<String> tokens = this.getTokens(text, FIELD_TEXT);
		
		// search
		return this.search(getQueryForTokens(FIELD_TEXT, tokens, type));
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
		
		TokenStream stream = this.analyzer.tokenStream(field, text);
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();

		while (stream.incrementToken()) {
			tokens.add(attr.toString());
		}
		
		stream.end();
		stream.close();
		
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
		final String[] temp = new String[0];
		if (tokens.size() == 0) return null;
		if (tokens.size() == 1) {
			String token = tokens.get(0);
//			if (type == SearchType.ALL_WILDCARD || type == SearchType.ANY_WILDCARD) {
				// check for wildcard character
				if (!token.contains(Character.toString(WildcardQuery.WILDCARD_CHAR))) {
					token = WildcardQuery.WILDCARD_CHAR + token + WildcardQuery.WILDCARD_CHAR;
				}
				return new WildcardQuery(new Term(field, token));
//			} else {
//				return new TermQuery(new Term(field, token));
//			}
		// PHRASE
		} else if (type == SearchType.PHRASE) {
			return new PhraseQuery(2, field, tokens.toArray(temp));
		// ALL_WILDCARD, ANY_WILDCARD
//		} else if (type == SearchType.ALL_WILDCARD || type == SearchType.ANY_WILDCARD) {
//			BooleanQuery.Builder builder = new BooleanQuery.Builder();
//			for (String token : tokens) {
//				if (!token.contains(Character.toString(WildcardQuery.WILDCARD_CHAR))) {
//					token = WildcardQuery.WILDCARD_CHAR + token + WildcardQuery.WILDCARD_CHAR;
//				}
//				builder.add(new WildcardQuery(new Term(field, token)), type == SearchType.ALL_WILDCARD ? Occur.MUST : Occur.SHOULD);
//			}
//			return builder.build();
		// ALL_WORDS, ANY_WORD, LOCATION
		} else {
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (String token : tokens) {
				builder.add(new TermQuery(new Term(field, token)), type == SearchType.ALL_WORDS ? Occur.MUST : Occur.SHOULD);
			}
			return builder.build();
		}
	}
	
	/**
	 * Runs the given lucene query and returns a list of song search results.
	 * @param query the lucene query to execute
	 * @return List&lt;{@link SongSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 * @see <a href="http://stackoverflow.com/questions/25814445/accessing-words-around-a-positional-match-in-lucene">Accessing words around a positional match in Lucene</a>
	 * @see SongSearchResult
	 */
	private List<SongSearchResult> search(Query query) throws IOException {
		List<SongSearchResult> results = new ArrayList<SongSearchResult>();
		
		try (IndexReader reader = DirectoryReader.open(this.directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);
			
			TopDocs result = searcher.search(query, 25);
			ScoreDoc[] docs = result.scoreDocs;
			
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(scorer);
			
			for (ScoreDoc doc : docs) {
				Document document = searcher.doc(doc.doc);
				
				// get the song
				Song song = this.songs.get(UUID.fromString(document.get(FIELD_ID)));
				
				// just continue if its not found
				if (song == null) {
					continue;
				}
				
				// get the text around the match
				List<SongSearchMatch> matches = new ArrayList<SongSearchMatch>();
				String[] items = document.getValues(FIELD_TEXT);
				for (String item : items) {
					try {
						String text = highlighter.getBestFragment(analyzer, FIELD_TEXT, item);
						if (text != null) {
							matches.add(new SongSearchMatch(FIELD_TEXT, item, text));
						}
					} catch (Exception e) {
						LOGGER.warn("Failed to find matching text for value " + item + " due to unexpected exception.", e);
					}
				}
				
				SongSearchResult match = new SongSearchResult(song, matches);
				results.add(match);
			}
		}
		
		return results;
	}
}
