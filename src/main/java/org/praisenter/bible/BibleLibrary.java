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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
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
	
	/** The lucene field to store the bible's unique identifier */
	private static final String FIELD_BOOK_NUMBER = "bookid";
	
	/** The lucene field to store the bible's unique identifier */
	private static final String FIELD_VERSE_CHAPTER = "chapter";
	
	/** The lucene field to store the bible's unique identifier */
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
		this.bibles = new HashMap<UUID, Bible>();
	}
	
	/**
	 * Performs the initialization required by the bible library.
	 * @throws IOException if an IO error occurs
	 */
	private void initialize() throws IOException {
		// verify paths exist
		Files.createDirectories(this.path);
		Files.createDirectories(this.indexPath);
		
		FileTypeMap map = MimetypesFileTypeMap.getDefaultFileTypeMap();
		
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
						String mimeType = map.getContentType(file.toString());
						if (mimeType.equals("application/xml")) {
							try (InputStream is = Files.newInputStream(file)) {
								try {
									// read in the xml
									Bible bible = XmlIO.read(is, Bible.class);
									bible.path = file;

									// once the bible has been loaded successfully
									// and added to the lucene index successfully
									// then we'll add it to the bible map
									this.bibles.put(bible.id, bible);
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
							
							Field bibleField = new StringField(FIELD_BIBLE_ID, bible.id.toString(), Field.Store.YES);
							document.add(bibleField);
							
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
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			for (Bible bible : this.bibles.values()) {
				try {
					// add the data to the document
					List<Document> documents = createDocuments(bible);
					// update the document
					writer.updateDocuments(new Term(FIELD_BIBLE_ID, bible.id.toString()), documents);
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
	public synchronized Bible get(UUID id) {
		if (id == null) return null;
		return this.bibles.get(id);
	}
	
	/**
	 * Returns all the bibles in this bible library.
	 * @return List&lt;{@link Bible}&gt;
	 */
	public synchronized List<Bible> all() {
		return new ArrayList<Bible>(this.bibles.values());
	}
	
	/**
	 * Returns the number of bibles in the library.
	 * @return int
	 */
	public synchronized int size() {
		return this.bibles.size();
	}
	
	/**
	 * Returns true if the given id is in the bible library.
	 * @param id the bible id
	 * @return boolean
	 */
	public synchronized boolean contains(UUID id) {
		if (id == null) return false;
		return this.bibles.containsKey(id);
	}
	
	/**
	 * Returns true if the given bible is in the bible library.
	 * @param bible the bible
	 * @return boolean
	 */
	public synchronized boolean contains(Bible bible) {
		if (bible == null || bible.id == null) return false;
		return this.bibles.containsKey(bible.id);
	}
	
	/**
	 * Saves the given bible (either new or existing) to the bible library.
	 * @param bible the bible to save
	 * @throws JAXBException if an error occurs while writing the bible to XML
	 * @throws IOException if an IO error occurs
	 */
	public synchronized void save(Bible bible) throws JAXBException, IOException {
		if (this.bibles.containsKey(bible.id)) {
			// then its an update
			bible.path = this.bibles.get(bible.id).path;
			// update the last modified date
			bible.lastModifiedDate = new Date();
		}
		
		if (bible.path == null) {
			String name = createFileName(bible);
			Path path = this.path.resolve(name + EXTENSION);
			// verify there doesn't exist a bible with this name already
			if (Files.exists(path)) {
				// just use the guid
				path = this.path.resolve(bible.id.toString().replaceAll("-", "") + EXTENSION);
			}
			bible.path = path;
		}
		
		// save the bible		
		XmlIO.save(bible.path, bible);
		
		// add to/update the lucene index
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			// update the fields
			List<Document> documents = createDocuments(bible);
			
			// update the document
			writer.updateDocuments(new Term(FIELD_BIBLE_ID, bible.id.toString()), documents);
		}
		
		this.bibles.put(bible.id, bible);
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
		if (bible == null || bible.id == null) return;
		remove(bible.id);
	}
	
	/**
	 * Creates a file name for the given bible based off the name.
	 * @param bible the bible
	 * @return String
	 */
	private String createFileName(Bible bible) {
		StringBuilder sb = new StringBuilder();
		if (bible != null) {
			String ttl = StringManipulator.toFileName(bible.name == null ? "" : bible.name);
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
	 * @param text the search text
	 * @param type the search type
	 * @return List&lt;{@link BibleSearchResult}&gt;
	 * @throws IOException if an IO error occurs
	 */
	public List<BibleSearchResult> search(UUID bibleId, String text, SearchType type) throws IOException {
		// verify text
		if (text == null || text.length() == 0) {
			return Collections.emptyList();
		}
		
		// check for wildcard characters for non-wildcard searches
		if (type != SearchType.ALL_WILDCARD && type != SearchType.ANY_WILDCARD && !text.contains(Character.toString(WildcardQuery.WILDCARD_CHAR))) {
			// take the wildcard characters out
			text = text.replaceAll("\\" + WildcardQuery.WILDCARD_CHAR, "");
		}
		
		// tokenize
		List<String> tokens = this.getTokens(text, FIELD_TEXT);
		
		// search
		return this.search(getQueryForTokens(bibleId, FIELD_TEXT, tokens, type));
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
	 * @param field the lucene field to search
	 * @param tokens the tokens to search for
	 * @param type the type of search
	 * @return Query
	 */
	private Query getQueryForTokens(UUID bibleId, String field, List<String> tokens, SearchType type) {
		Query query = null;
		final String[] temp = new String[0];
		if (tokens.size() == 0) return null;
		if (tokens.size() == 1) {
			String token = tokens.get(0);
			if (type == SearchType.ALL_WILDCARD || type == SearchType.ANY_WILDCARD) {
				// check for wildcard character
				if (!token.contains(Character.toString(WildcardQuery.WILDCARD_CHAR))) {
					token = WildcardQuery.WILDCARD_CHAR + token + WildcardQuery.WILDCARD_CHAR;
				}
				query = new WildcardQuery(new Term(field, token));
			} else {
				query = new TermQuery(new Term(field, token));
			}
		// PHRASE
		} else if (type == SearchType.PHRASE) {
			query = new PhraseQuery(2, field, tokens.toArray(temp));
		// ALL_WILDCARD, ANY_WILDCARD
		} else if (type == SearchType.ALL_WILDCARD || type == SearchType.ANY_WILDCARD) {
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (String token : tokens) {
				if (!token.contains(Character.toString(WildcardQuery.WILDCARD_CHAR))) {
					token = WildcardQuery.WILDCARD_CHAR + token + WildcardQuery.WILDCARD_CHAR;
				}
				builder.add(new WildcardQuery(new Term(field, token)), type == SearchType.ALL_WILDCARD ? Occur.MUST : Occur.SHOULD);
			}
			query = builder.build();
		// ALL_WORDS, ANY_WORD, LOCATION
		} else {
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (String token : tokens) {
				builder.add(new TermQuery(new Term(field, token)), type == SearchType.ALL_WORDS ? Occur.MUST : Occur.SHOULD);
			}
			query = builder.build();
		}
		
		if (bibleId != null) {
			// TODO need to test this
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			builder.add(query, Occur.MUST);
			builder.add(new TermQuery(new Term(FIELD_BIBLE_ID, bibleId.toString())), Occur.FILTER);
			return builder.build();
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
	private List<BibleSearchResult> search(Query query) throws IOException {
		List<BibleSearchResult> results = new ArrayList<BibleSearchResult>();
		
		try (IndexReader reader = DirectoryReader.open(this.directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);
			
			TopDocs result = searcher.search(query, 25);
			ScoreDoc[] docs = result.scoreDocs;
			
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(scorer);
			
			for (ScoreDoc doc : docs) {
				Document document = searcher.doc(doc.doc);
				
				// get the bible
				Bible bible = this.bibles.get(UUID.fromString(document.get(FIELD_BIBLE_ID)));
				short bookNumber = document.getField(FIELD_BOOK_NUMBER).numericValue().shortValue();
				short chapterNumber = document.getField(FIELD_VERSE_CHAPTER).numericValue().shortValue();
				short number = document.getField(FIELD_VERSE_NUMBER).numericValue().shortValue();
				
				Book book = null;
				Chapter chapter = null;
				Verse verse = null;
				if (bible != null) {
					for (Book b : bible.books) {
						if (b.number == bookNumber) {
							book = b;
							break;
						}
					}
					
					if (book != null) {
						for (Chapter c : book.chapters) {
							if (c.number == chapterNumber) {
								chapter = c;
								break;
							}
						}
					}
					
					if (chapter != null) {
						for (Verse v : chapter.verses) {
							if (v.number == number) {
								verse = v;
								break;
							}
						}
					}
				}
				
				// just continue if its not found
				if (bible == null) {
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
				
				BibleSearchResult match = new BibleSearchResult(bible, book, chapter, verse, matches);
				results.add(match);
			}
		}
		
		return results;
	}
}
