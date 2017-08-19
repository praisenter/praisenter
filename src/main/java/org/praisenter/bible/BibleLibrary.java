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
package org.praisenter.bible;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
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
import org.apache.lucene.search.TermQuery;
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
import org.praisenter.UnknownFormatException;
import org.praisenter.json.JsonIO;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * A collection of bibles that has been loaded into a specific location and converted
 * into the praisenter format.
 * <p>
 * Obtain a {@link BibleLibrary} instance by calling the {@link #open(Path)}
 * static method. Only one instance should be created for each path. Multiple instances
 * modifying the same path can have unexpected results and can show different sets of bibles.
 * <p>
 * This class is intended to be thread safe within this application but can still contend
 * with other programs during disk operations.
 * <p>
 * The bibles contained in the specified folder will be added to a lucene index for searching.
 * Opening a bible library will initiate a process to update the index to ensure the latest
 * information is contained in the index. This process can take some time as it must read
 * each bible file and update it in the index.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleLibrary {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The sub folder in the zip to store bibles */
	private static final String ZIP_DIR = "bibles";

	// lucene

	/** The relative path to the directory containing the lucene index */
	private static final String INDEX_DIR = "_index";
	
	/** The lucene field to store the bible's path */
	private static final String FIELD_PATH = "path";
	
	/** The lucene field to store the bible's unique identifier */
	private static final String FIELD_BIBLE_ID = "bibleid";
	
	/** The lucene field to store the book number as a searchable value */
	private static final String FIELD_BOOK_ID = "bookid";
	
	/** The lucene field to store the book number */
	private static final String FIELD_BOOK_NUMBER = "booknumber";
	
	/** The lucene field to store the chapter number */
	private static final String FIELD_VERSE_CHAPTER = "chapter";
	
	/** The lucene field to store the verse number */
	private static final String FIELD_VERSE_NUMBER = "verse";
	
	/** The lucene field that contains all the bible searchable text */
	private static final String FIELD_TEXT = "text";
	
	// location
	
	/** The path to the bible library */
	private final Path path;
	
	/** The path to the bible library's index */
	private final Path indexPath;
	
	// searching
	
	/** The file-system index */
	private Directory directory;
	
	/** The analyzer for the index */
	private Analyzer analyzer;
	
	// loaded
	
	/** The bibles */
	private final Map<UUID, FileData<Bible>> bibles;
	
	// locks
	
	/** The mutex locks */
	private final LockMap<String> locks;
	
	/**
	 * Sets up a new {@link BibleLibrary} at the given path.
	 * @param path the root path to the bible library
	 * @return {@link BibleLibrary}
	 * @throws IOException if an IO error occurs
	 */
	public static final BibleLibrary open(Path path) throws IOException {
		BibleLibrary bl = new BibleLibrary(path);
		bl.initialize();
		return bl;
	}
	
	/**
	 * Full constructor.
	 * @param path the path to maintain the bible library
	 */
	private BibleLibrary(Path path) {
		this.path = path;
		this.indexPath = this.path.resolve(INDEX_DIR);
		this.bibles = new ConcurrentHashMap<UUID, FileData<Bible>>();
		this.locks = new LockMap<String>();
	}
	
	/**
	 * Performs the initialization required by the bible library.
	 * @throws IOException if an IO error occurs
	 */
	private void initialize() throws IOException {
		LOGGER.debug("Initializing bible library at '{}'.", this.path);
		
		// verify paths exist
		Files.createDirectories(this.path);
		Files.createDirectories(this.indexPath);
		
		// load and update the index
		LOGGER.debug("Initializing bible library lucene index.");
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
									// read in the bible
									Bible bible = JsonIO.read(is, Bible.class);

									// once the bible has been loaded successfully
									// and added to the lucene index successfully
									// then we'll add it to the bible map
									this.bibles.put(bible.getId(), new FileData<Bible>(bible, file));
								} catch (Exception e) {
									// make sure its not in the index
									// we don't want to be able to find the bible
									// if we failed to load it
									LOGGER.warn("Failed to load bible '" + file.toAbsolutePath().toString() + "'", e);
									writer.deleteDocuments(new Term(FIELD_PATH, file.toAbsolutePath().toString()));
								}
							} catch (Exception ex) {
								// make sure its not in the index
								// we don't want to be able to find the bible
								// if we failed to load it
								LOGGER.warn("Failed to load bible '" + file.toAbsolutePath().toString() + "'", ex);
								writer.deleteDocuments(new Term(FIELD_PATH, file.toAbsolutePath().toString()));
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns a list of lucene documents that contains the fields for the given bible.
	 * @param bible the bible
	 */
	private List<Document> createDocuments(FileData<Bible> fileData) {
		List<Document> documents = new ArrayList<Document>();
		Bible bible = fileData.getData();
		// books
		if (bible.books != null) {
			for (Book book : bible.books) {
				if (book.chapters != null) {
					for (Chapter chapter : book.chapters) {
						for (Verse verse : chapter.verses) {
							Document document = new Document();
							
							Field pathField = new StringField(FIELD_PATH, fileData.getPath().toAbsolutePath().toString(), Field.Store.YES);
							document.add(pathField);
							
							// allow filtering by the bible id
							Field bibleField = new StringField(FIELD_BIBLE_ID, bible.getId().toString(), Field.Store.YES);
							document.add(bibleField);
							
							// allow filtering by the book number
							Field bookIdField = new IntPoint(FIELD_BOOK_ID, book.number);
							document.add(bookIdField);
							
							// stored data so we can look up the verse
							
							Field bookField = new StoredField(FIELD_BOOK_NUMBER, book.number);
							document.add(bookField);
							
							Field vChapterField = new StoredField(FIELD_VERSE_CHAPTER, chapter.number);
							document.add(vChapterField);
							
							Field vNumberField = new StoredField(FIELD_VERSE_NUMBER, verse.number);
							document.add(vNumberField);
							
							if (!StringManipulator.isNullOrEmpty(verse.text)) {
								Field textField = new TextField(FIELD_TEXT, verse.text, Field.Store.YES);
								document.add(textField);
							}
							
							documents.add(document);
						}
					}
				}
			}
		}
		
		return documents;
	}
	
	/**
	 * Returns the lock for the index.
	 * @return Object
	 */
	private Object getIndexLock() {
		return this.locks.get("INDEX");
	}
	
	/**
	 * Returns a lock for the given bible.
	 * @param bible the bible
	 * @return Object
	 */
	private Object getBibleLock(Bible bible) {
		return this.locks.get(bible.getId().toString());
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
	 * Returns the bible for the given id or null if not found.
	 * @param id the bible id
	 * @return {@link Bible}
	 */
	public Bible get(UUID id) {
		if (id == null) return null;
		if (!this.bibles.containsKey(id)) return null;
		return this.bibles.get(id).getData();
	}
	
	/**
	 * Returns all the bibles in this bible library.
	 * @return List&lt;{@link Bible}&gt;
	 */
	public List<Bible> all() {
		return this.bibles.values().stream().map(f -> f.getData()).collect(Collectors.toList());
	}
	
	/**
	 * Returns the number of bibles in the library.
	 * @return int
	 */
	public int size() {
		return this.bibles.size();
	}
	
	/**
	 * Saves the given bible (either new or existing) to the bible library.
	 * @param bible the bible to save
	 * @throws JsonMappingException if an error occurs while mapping the object to JSON
	 * @throws JsonGenerationException  if an error occurs while building the JSON
	 * @throws IOException if an IO error occurs
	 */
	public void save(Bible bible) throws JsonGenerationException, JsonMappingException, IOException {
		// update the last modified date
		bible.setModifiedDate(Instant.now());
		
		// calling this method could indicate one of the following:
		// 1. New
		// 2. Save Existing
		// 3. Save Existing + Rename
		
		FileData<Bible> fileData = null;
		
		// obtain the lock on the bible
		synchronized (this.getBibleLock(bible)) {
			LOGGER.debug("Saving bible '{}'.", bible.getName());
			
			// get the current file reference
			fileData = this.bibles.get(bible.getId());
			
			// generate the file name and path
			String name = StringManipulator.toFileName(bible.name, bible.getId());
			Path path = this.path.resolve(name + Constants.BIBLE_FILE_EXTENSION);
			Path uuid = this.path.resolve(StringManipulator.toFileName(bible.getId()) + Constants.BIBLE_FILE_EXTENSION);
			
			// check for operation
			if (fileData == null) {
				LOGGER.debug("Adding bible '{}'.", bible.getName());
				// then its a new
				synchronized (this.getPathLock(path)) {
					// check if the path exists once we obtain the lock
					if (Files.exists(path)) {
						// just use the UUID (which shouldn't need a lock since it's unique)
						path = uuid;
					}
					JsonIO.write(path, bible);
					fileData = new FileData<Bible>(bible, path);
					LOGGER.debug("Bible '{}' saved to '{}'.", bible.getName(), path);
				}
			} else {
				LOGGER.debug("Updating bible '{}'.", bible.getName());
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
								JsonIO.write(original, bible);
							} else {
								// if the path already exists and the current path isn't the uuid path
								// then we know that this was a rename to a different name that already exists
								LOGGER.warn("Unable to rename bible '{}' to '{}' because a file with that name already exists.", bible.getName(), path.getFileName());
								throw new FileAlreadyExistsException(path.getFileName().toString());
							}
						} else {
							LOGGER.debug("Renaming bible '{}' to '{}'.", bible.getName(), path.getFileName());
							// otherwise rename the file
							Files.move(original, path);
							// update the path
							fileData = new FileData<Bible>(bible, path);
							JsonIO.write(path, bible);
						}
					}
				} else {
					// it's a normal save
					JsonIO.write(original, bible);
				}
			}
			
			// update the bible map (it may have changed)
			this.bibles.put(bible.getId(), fileData);
		}
		
		// add to/update the lucene index
		synchronized (this.getIndexLock()) {
			LOGGER.debug("Updating lucene index for bible '{}'.", bible.getName());
			IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			try (IndexWriter writer = new IndexWriter(this.directory, config)) {
				// update the fields
				List<Document> documents = createDocuments(fileData);
				
				// update the document
				writer.updateDocuments(new Term(FIELD_BIBLE_ID, bible.getId().toString()), documents);
			} catch (Exception ex) {
				// if this happens, the user should really just execute a reindex
				// we don't know what to back out at this point
				LOGGER.warn("Failed to update the lucene index for bible '" + bible.getName() + "'. Please initiate a reindex.", ex);
			}
		}
	}
	
	/**
	 * Removes the given bible from the bible library, deletes the file on
	 * the file system, and removes it's data from the lucene index.
	 * @param bible the bible to remove
	 * @throws IOException if an IO error occurs
	 */
	public void remove(Bible bible) throws IOException {
		if (bible == null) return;
		
		UUID id = bible.getId();
		if (id == null) return;
		
		synchronized (this.getBibleLock(bible)) {
			FileData<Bible> fileData = this.bibles.get(bible.getId());
			LOGGER.debug("Removing bible '{}'.", bible.getName());
			// delete the file
			if (fileData != null) {
				Files.deleteIfExists(fileData.getPath());
			}
			// remove it from the map
			this.bibles.remove(id);
		}
		
		synchronized (this.getIndexLock()) {
			LOGGER.debug("Removing lucene indexing for bible '{}'.", bible.getName());
			// remove from the lucene index so it can't be found
			// in searches any more
			IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
			config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			try (IndexWriter writer = new IndexWriter(this.directory, config)) {
				// update the document
				writer.deleteDocuments(new Term(FIELD_BIBLE_ID, id.toString()));
			} catch (Exception ex) {
				// if this happens, the user should really just execute a reindex
				// we don't know what to back out at this point
				LOGGER.warn("Failed to remove the lucene indexing for bible '" + bible.getName() + "'. Please initiate a reindex.", ex);
			}
		}
	}
	
	/**
	 * Exports the given bibles to the given file.
	 * @param path the file
	 * @param bibles the bibles to export
	 * @throws IOException if an IO error occurs
	 */
	public void exportBibles(Path path, List<Bible> bibles) throws IOException {
		final PraisenterBibleExporter exporter = new PraisenterBibleExporter();
		exporter.execute(path, bibles);
	}

	/**
	 * Exports the given bibles to the given file.
	 * @param stream the zip stream to export to
	 * @param bibles the bibles to export
	 * @throws IOException if an IO error occurs
	 */
	public void exportBibles(ZipOutputStream stream, List<Bible> bibles) throws IOException {
		final PraisenterBibleExporter exporter = new PraisenterBibleExporter();
		exporter.execute(stream, ZIP_DIR, bibles);
	}
	
	/**
	 * Imports the given bibles into the library.
	 * @param path the path to a zip file
	 * @return List&lt;{@link Bible}&gt;
	 * @throws FileNotFoundException if the given path is not found
	 * @throws InvalidFormatException if the file wasn't in the format expected
	 * @throws UnknownFormatException if the format of the file couldn't be determined
	 * @throws IOException if an IO error occurs
	 */
	public List<Bible> importBibles(Path path) throws FileNotFoundException, IOException, InvalidFormatException, UnknownFormatException {
		BibleFormatDetector importer = new BibleFormatDetector();
		List<Bible> bibles = importer.execute(path);
		
		LOGGER.debug("'{}' bibles found in '{}'.", bibles.size(), path);
		Iterator<Bible> it = bibles.iterator();
		while (it.hasNext()) {
			Bible bible = it.next();
			try {
				this.save(bible);
			} catch (Exception ex) {
				LOGGER.error("Failed to save the bible '" + bible.getName() + "'", ex);
				it.remove();
			}
		}
		return bibles;
	}
	
	/**
	 * Re-indexes all bibles.
	 * @throws IOException if an IO error occurs
	 */
	public void reindex() throws IOException {
		synchronized (this.getIndexLock()) {
			LOGGER.debug("Re-indexing lucene based on current bibles.");
			IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
			config.setOpenMode(OpenMode.CREATE);
			try (IndexWriter writer = new IndexWriter(this.directory, config)) {
				for (FileData<Bible> fileData : this.bibles.values()) {
					Bible bible = fileData.getData();
					try {
						// add the data to the document
						List<Document> documents = createDocuments(fileData);
						// update the document
						writer.updateDocuments(new Term(FIELD_BIBLE_ID, bible.getId().toString()), documents);
					} catch (Exception e) {
						// make sure its not in the index
						LOGGER.warn("Failed to update the lucene index for bible '" + bible.getName() + "'.", e);
					}
				}
			}
		}
	}
	
	// searching
	
	/**
	 * Searches this bible library for the given criteria.
	 * @param criteria the search criteria
	 * @return List&lt;{@link BibleSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 */
	public List<BibleSearchResult> search(BibleSearchCriteria criteria) throws IOException {
		// verify text
		if (criteria == null || criteria.getText() == null || criteria.getText().length() == 0) {
			return Collections.emptyList();
		}
		
		// tokenize
		List<String> tokens = this.getTokens(criteria.getText(), FIELD_TEXT);
		
		// build query
		Query query = getQueryForTokens(criteria.getBibleId(), criteria.getBookNumber(), FIELD_TEXT, tokens, criteria.getType());
		
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
	 * @param bibleId the id of the bible to search; or null to search all
	 * @param bookNumber the book number of the book to search; or null to search all
	 * @param field the lucene field to search
	 * @param tokens the tokens to search for
	 * @param type the type of search
	 * @return Query
	 */
	private Query getQueryForTokens(UUID bibleId, Short bookNumber, String field, List<String> tokens, SearchType type) {
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
		
		// further filter on any other criteria
		// check if the bible id was supplied
		if (bibleId != null) {
			LOGGER.debug("Adding bible id '{}' to filter.", bibleId);
			// then filter by the bible
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			builder.add(query, Occur.MUST);
			builder.add(new TermQuery(new Term(FIELD_BIBLE_ID, bibleId.toString())), Occur.FILTER);
			//check if the book number was supplied
			if (bookNumber != null) {
				LOGGER.debug("Adding book number '{}' to filter.", bookNumber);
				// then filter by the book number too
				builder.add(IntPoint.newExactQuery(FIELD_BOOK_ID, bookNumber), Occur.FILTER);
			}
			query = builder.build();
		}
		
		return query;
	}
	
	/**
	 * Runs the given lucene query and returns a list of bible search results.
	 * @param query the lucene query to execute
	 * @param maxResults the maximum results to return
	 * @return List&lt;{@link BibleSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 * @see <a href="http://stackoverflow.com/questions/25814445/accessing-words-around-a-positional-match-in-lucene">Accessing words around a positional match in Lucene</a>
	 * @see BibleSearchResult
	 */
	private List<BibleSearchResult> search(Query query, int maxResults) throws IOException {
		List<BibleSearchResult> results = new ArrayList<BibleSearchResult>();
		
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
				
				FileData<Bible> fileData = this.bibles.get(UUID.fromString(document.get(FIELD_BIBLE_ID)));
				if (fileData == null) {
					LOGGER.warn("Unable to find bible '{}'. A re-index might fix this problem.", document.get(FIELD_BIBLE_ID));
					continue;
				}
				
				// get the bible
				Bible bible = fileData.getData();
				short bookNumber = document.getField(FIELD_BOOK_NUMBER).numericValue().shortValue();
				short chapterNumber = document.getField(FIELD_VERSE_CHAPTER).numericValue().shortValue();
				short verseNumber = document.getField(FIELD_VERSE_NUMBER).numericValue().shortValue();
				
				LocatedVerse verse = null;
				if (bible != null) {
					verse = bible.getVerse(bookNumber, chapterNumber, verseNumber);
				}
				
				// just continue if its not found
				if (verse == null) {
					LOGGER.warn("Unable to find {} {}:{} in '{}'. A re-index might fix this problem.", bookNumber, chapterNumber, verseNumber, bible != null ? bible.getName() : "null");
					continue;
				}
				
				// get the text around the match
				List<BibleSearchMatch> matches = new ArrayList<BibleSearchMatch>();
				String[] items = document.getValues(FIELD_TEXT);
				for (String item : items) {
					try {
						String text = highlighter.getBestFragment(analyzer, FIELD_TEXT, item);
						if (text != null) {
							matches.add(new BibleSearchMatch(FIELD_TEXT, item, text));
						}
					} catch (Exception e) {
						LOGGER.warn("Failed to find matching text for value " + item + " due to unexpected exception.", e);
					}
				}
				
				BibleSearchResult match = new BibleSearchResult(doc.score, verse.getBible(), verse.getBook(), verse.getChapter(), verse.getVerse(), matches);
				results.add(match);
			}
		}
		
		return results;
	}
}
