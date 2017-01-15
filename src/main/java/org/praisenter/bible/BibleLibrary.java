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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import org.praisenter.SearchType;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.StringManipulator;
import org.praisenter.xml.XmlIO;

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
	
	/** The extension to use for the bible files */
	private static final String EXTENSION = ".xml";
	
	// lucene

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
	
	/** The relative path to the directory containing the lucene index */
	private static final String INDEX_DIR = "_index";
	
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
	private final Map<UUID, Bible> bibles;
	
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
		this.bibles = new ConcurrentHashMap<UUID, Bible>();
	}
	
	/**
	 * Performs the initialization required by the bible library.
	 * @throws IOException if an IO error occurs
	 */
	private synchronized void initialize() throws IOException {
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
									Bible bible = XmlIO.read(is, Bible.class);
									bible.path = file;

									// once the bible has been loaded successfully
									// and added to the lucene index successfully
									// then we'll add it to the bible map
									this.bibles.put(bible.getId(), bible);
								} catch (Exception e) {
									// make sure its not in the index
									// we don't want to be able to find the bible
									// if we failed to load it
									LOGGER.warn("Failed to load bible '" + file.toAbsolutePath().toString() + "'", e);
									writer.deleteDocuments(new Term(FIELD_PATH, file.toAbsolutePath().toString()));
								}
							} catch (IOException ex) {
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
	private List<Document> createDocuments(Bible bible) {
		List<Document> documents = new ArrayList<Document>();
		// books
		if (bible.books != null) {
			for (Book book : bible.books) {
				if (book.chapters != null) {
					for (Chapter chapter : book.chapters) {
						for (Verse verse : chapter.verses) {
							Document document = new Document();
							
							Field pathField = new StringField(FIELD_PATH, bible.path.toAbsolutePath().toString(), Field.Store.YES);
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
	 * Re-indexes all bibles.
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void reindex() throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			for (Bible bible : this.bibles.values()) {
				try {
					// add the data to the document
					List<Document> documents = createDocuments(bible);
					// update the document
					writer.updateDocuments(new Term(FIELD_BIBLE_ID, bible.getId().toString()), documents);
				} catch (Exception e) {
					// make sure its not in the index
					LOGGER.warn("Failed to update the bible in the lucene index '" + bible.path.toAbsolutePath().toString() + "'", e);
				}
			}
		}
	}
	
	/**
	 * Returns the bible for the given id or null if not found.
	 * @param id the bible id
	 * @return {@link Bible}
	 */
	public Bible get(UUID id) {
		if (id == null) return null;
		return this.bibles.get(id);
	}
	
	/**
	 * Returns all the bibles in this bible library.
	 * @return List&lt;{@link Bible}&gt;
	 */
	public List<Bible> all() {
		return new ArrayList<Bible>(this.bibles.values());
	}
	
	/**
	 * Returns the number of bibles in the library.
	 * @return int
	 */
	public int size() {
		return this.bibles.size();
	}
	
	/**
	 * Returns true if the given id is in the bible library.
	 * @param id the bible id
	 * @return boolean
	 */
	public boolean contains(UUID id) {
		if (id == null) return false;
		return this.bibles.containsKey(id);
	}
	
	/**
	 * Returns true if the given bible is in the bible library.
	 * @param bible the bible
	 * @return boolean
	 */
	public boolean contains(Bible bible) {
		if (bible == null || bible.getId() == null) return false;
		return this.bibles.containsKey(bible.getId());
	}
	
	/**
	 * Saves the given bible (either new or existing) to the bible library.
	 * @param bible the bible to save
	 * @throws JAXBException if an error occurs while writing the bible to XML
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void save(Bible bible) throws JAXBException, IOException {
		// update the last modified date
		bible.lastModifiedDate = Instant.now();
		
		// check for old bible data
		Bible old = this.bibles.get(bible.getId());
		if (old != null) {
			// then its an update
			bible.path = old.path;
		}
		
		// generate the file name and path
		String name = createFileName(bible);
		Path path = this.path.resolve(name + EXTENSION);
		
		// check if the old path was given
		if (bible.path == null) {
			// this indicates that we need to save a new one so
			// verify there doesn't exist a bible with this name already
			if (Files.exists(path)) {
				// just use the UUID
				path = this.path.resolve(StringManipulator.toFileName(bible.getId()) + EXTENSION);
			}
		} else if (!bible.path.equals(path)) {
			// this indicates that we need to rename the file
			Files.move(bible.path, path);
		}
		
		// set the path
		bible.path = path;
		
		// save the bible		
		XmlIO.save(bible.path, bible);
		
		// add to/update the lucene index
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			// update the fields
			List<Document> documents = createDocuments(bible);
			
			// update the document
			writer.updateDocuments(new Term(FIELD_BIBLE_ID, bible.getId().toString()), documents);
		}
		
		this.bibles.put(bible.getId(), bible);
	}
	
	/**
	 * Removes the given bible id from the bible library and deletes the file on
	 * the file system.
	 * @param id the id of the bible
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
			writer.deleteDocuments(new Term(FIELD_BIBLE_ID, id.toString()));
		}
		
		// remove it from the map
		Bible bible = this.bibles.remove(id);
		
		// delete the file
		if (bible != null) {
			Files.deleteIfExists(bible.path);
		}
	}
	
	/**
	 * Removes the given bible from the bible library and deletes the file on
	 * the file system.
	 * @param bible the bible to remove
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void remove(Bible bible) throws IOException {
		if (bible == null || bible.getId() == null) return;
		remove(bible.getId());
	}
	
	/**
	 * Creates a file name for the given bible based off the name.
	 * @param bible the bible
	 * @return String
	 */
	public static final String createFileName(Bible bible) {
		StringBuilder sb = new StringBuilder();
		if (bible != null) {
			String name = bible.name;
			String ttl = StringManipulator.toFileName(name == null ? "" : name);
			if (ttl.length() == 0) {
				ttl = "Untitled";
			}
			sb.append(ttl);
		}
		
		String name = sb.toString();
		
		// truncate the name to certain length
		int max = Constants.MAX_FILE_NAME_CODEPOINTS - EXTENSION.length();
		if (name.length() > max) {
			LOGGER.warn("File name too long '{}', truncating.", name);
			name = name.substring(0, Math.min(name.length() - 1, max));
		}
		
		return name;
	}
	
	// searching
	
	/**
	 * Searches this bible library for the given text using the given search type.
	 * @param bibleId the id of the bible to search; or null to search all
	 * @param bookNumber the book number of the book to search; or null to search all
	 * @param text the search text
	 * @param type the search type
	 * @return List&lt;{@link BibleSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 */
	public List<BibleSearchResult> search(UUID bibleId, Short bookNumber, String text, SearchType type) throws IOException {
		// verify text
		if (text == null || text.length() == 0) {
			return Collections.emptyList();
		}
		
		// tokenize
		List<String> tokens = this.getTokens(text, FIELD_TEXT);
		
		// search
		return this.search(getQueryForTokens(bibleId, bookNumber, FIELD_TEXT, tokens, type));
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
	 * @param bibleId the id of the bible to search; or null to search all
	 * @param bookNumber the book number of the book to search; or null to search all
	 * @param field the lucene field to search
	 * @param tokens the tokens to search for
	 * @param type the type of search
	 * @return Query
	 */
	private Query getQueryForTokens(UUID bibleId, Short bookNumber, String field, List<String> tokens, SearchType type) {
		Query query = null;
		
		if (tokens.size() == 0) return null;
		if (tokens.size() == 1) {
			// single term, just do a fuzzy query on it with a larger max edit distance
			String token = tokens.get(0);
			query = new FuzzyQuery(new Term(field, token));
		// PHRASE
		} else if (type == SearchType.PHRASE) {
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
			// then filter by the bible
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			builder.add(query, Occur.MUST);
			builder.add(new TermQuery(new Term(FIELD_BIBLE_ID, bibleId.toString())), Occur.FILTER);
			//check if the book number was supplied
			if (bookNumber != null) {
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
	 * @return List&lt;{@link BibleSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 * @see <a href="http://stackoverflow.com/questions/25814445/accessing-words-around-a-positional-match-in-lucene">Accessing words around a positional match in Lucene</a>
	 * @see BibleSearchResult
	 */
	private synchronized List<BibleSearchResult> search(Query query) throws IOException {
		List<BibleSearchResult> results = new ArrayList<BibleSearchResult>();
		
		try (IndexReader reader = DirectoryReader.open(this.directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);
			
			TopDocs result = searcher.search(query, 250);
			ScoreDoc[] docs = result.scoreDocs;
			
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(scorer);
			
			for (ScoreDoc doc : docs) {
				Document document = searcher.doc(doc.doc);
				
				// get the bible
				Bible bible = this.bibles.get(UUID.fromString(document.get(FIELD_BIBLE_ID)));
				short bookNumber = document.getField(FIELD_BOOK_NUMBER).numericValue().shortValue();
				short chapterNumber = document.getField(FIELD_VERSE_CHAPTER).numericValue().shortValue();
				short verseNumber = document.getField(FIELD_VERSE_NUMBER).numericValue().shortValue();
				
				LocatedVerse verse = null;
				if (bible != null) {
					verse = bible.getVerse(bookNumber, chapterNumber, verseNumber);
				}
				
				// just continue if its not found
				if (verse == null) {
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
