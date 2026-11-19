package org.praisenter.data.search;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public final class SearchIndex implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();

	private final Path indexPath;
	private final Directory directory;
	private final Analyzer analyzer;
	private final IndexWriter indexWriter;
	private final SearcherManager searcherManager;
	private final AtomicBoolean closed;
	
	protected SearchIndex(
			Path path, 
			Directory directory, 
			Analyzer analyzer, 
			IndexWriter writer, 
			SearcherManager searcherManager) {
		this.indexPath = path;
		this.directory = directory;
		this.analyzer = analyzer;
		this.indexWriter = writer;
		this.searcherManager = searcherManager;
		this.closed = new AtomicBoolean(false);
	}

	public static final SearchIndex open(Path indexPath) throws IOException {
		FSDirectory directory = FSDirectory.open(indexPath);

		Analyzer analyzer = new StandardAnalyzer(new CharArraySet(1, true));
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);

		try {
			IndexWriter writer = new IndexWriter(directory, config);

			try {
				SearcherManager searcherManager = new SearcherManager(writer, true, true, null);
				return new SearchIndex(indexPath, directory, analyzer, writer, searcherManager);
			} catch (IOException e) {
				try { 
					writer.close();
				} catch (IOException ex) { 
					LOGGER.error("Failed to close writer after searcher manager creation failure", ex); 
				}
				throw e;
			}
		} catch (IOException e) {
			analyzer.close();
			try { 
				directory.close();
			} catch (IOException ex) { 
				LOGGER.error("Failed to close directory after analyzer creation failure", ex); 
			}
			throw e;
		}
	}
	
	public Path getIndexPath() {
		return this.indexPath;
	}

	public boolean isDisposed() {
		return this.closed.get();
	}

	private void throwIfDisposed() throws IOException {
		if (this.closed.get()) 
			throw new IOException("SearchIndex is disposed");
	}

	private void refreshSearchManager() {
		try {
			this.searcherManager.maybeRefresh();
		} catch (IOException e) {
			LOGGER.error("Failed to refresh searcher manager - will try again on the next search", e);
		}
	}

	public synchronized void create(Indexable item) throws IOException {
		this.throwIfDisposed();
		List<Document> docs = item.index();
		if (docs == null || docs.isEmpty()) return;
		this.indexWriter.updateDocuments(new Term(Indexable.FIELD_ID, item.getId().toString()), docs);
		this.indexWriter.commit();
		this.refreshSearchManager();
	}
	
	public synchronized void update(Indexable item) throws IOException {
		this.throwIfDisposed();
		List<Document> docs = item.index();
		Term id = new Term(Indexable.FIELD_ID, item.getId().toString());
		if (docs == null || docs.isEmpty()) {
			this.indexWriter.deleteDocuments(id);
		} else {
			this.indexWriter.updateDocuments(id, docs);
		}
		this.indexWriter.commit();
		this.refreshSearchManager();
	}
	
	public synchronized void delete(Indexable item) throws IOException {
		this.throwIfDisposed();
		this.indexWriter.deleteDocuments(new Term(Indexable.FIELD_ID, item.getId().toString()));
		this.indexWriter.commit();
		this.refreshSearchManager();
	}
	
	public synchronized void reindex(Iterable<? extends Indexable> items) throws IOException {
		this.throwIfDisposed();
		this.indexWriter.deleteAll();

		// re-add everything
		for (Indexable item : items) {
			LOGGER.debug("Indexing document {} {}", item.getClass().getName(), item.getName());
			List<Document> docs = item.index();
			if (docs == null || docs.isEmpty()) {
				continue;
			}
			this.indexWriter.updateDocuments(new Term(Indexable.FIELD_ID, item.getId().toString()), docs);	
		}

		this.indexWriter.commit();

		this.searcherManager.maybeRefreshBlocking();
	}
	
	public synchronized SearchResults search(SearchCriteria criteria) throws IOException {
		this.throwIfDisposed();

		Query query = criteria.createQuery(this.analyzer);
		if (query == null) 
			return new SearchResults(criteria, Collections.emptyList());
		
		this.refreshSearchManager();

		List<SearchResult> results = new ArrayList<SearchResult>();
		
		IndexSearcher searcher = this.searcherManager.acquire();
		try {
			TopDocs result = searcher.search(query, criteria.getMaxResults() + 1);
			ScoreDoc[] docs = result.scoreDocs;
			
			QueryScorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(scorer);
			highlighter.setTextFragmenter(new NullFragmenter());
			StoredFields storedFields = searcher.storedFields();
			
			for (ScoreDoc doc : docs) {
				Document document = storedFields.document(doc.doc);
				
				// get the text
				String text = document.get(Indexable.FIELD_TEXT);
				
				// get the text around the match
				List<SearchTextMatch> matches = new ArrayList<SearchTextMatch>();
				
				try (TokenStream tokens = TokenSources.getTokenStream(Indexable.FIELD_TEXT, null, text, this.analyzer, -1)) {
					String[] fragments = highlighter.getBestFragments(tokens, text, 10);
					
					for (String fragment : fragments) {
						matches.add(new SearchTextMatch(Indexable.FIELD_TEXT, text, fragment));
					}
				} catch (IllegalArgumentException e) {
					// https://github.com/apache/lucene/issues/10608
					LOGGER.warn("Failed to get highlighted text for search '" + criteria.getTerms() + "': " + e.getMessage());
				} catch (Exception e) {
					LOGGER.error("Failed to get matching text for terms: '" + criteria.getTerms() + "'", e);
				}
				
				results.add(new SearchResult(document, matches, doc.score));
			}
		} finally {
			this.searcherManager.release(searcher);
		}

		return new SearchResults(criteria, results);
	}
	
	@Override 
	public synchronized void close() throws IOException {
		if (!this.closed.compareAndSet(false, true))
			return;

		LOGGER.debug("Disposing search index");
		List<IOException> exceptions = new ArrayList<>();
		
		try {
			this.searcherManager.close();
		} catch (IOException e) {
			LOGGER.error("Failed to close searcher manager", e);
			exceptions.add(e);
		}

		try {
			this.indexWriter.close();
		} catch (IOException e) {
			LOGGER.error("Failed to close writer", e);
			exceptions.add(e);
		}

		this.analyzer.close();

		try {
			this.directory.close();
		} catch (IOException e) {
			LOGGER.error("Failed to close directory", e);
			exceptions.add(e);
		}

		// check if anything failed
		if (!exceptions.isEmpty()) {
			// collect the exceptions and throw a new one with the suppressed exceptions
			IOException e = new IOException("Failed to close search index");
			for (IOException ex : exceptions) {
				e.addSuppressed(ex);
			}
			throw e;
		}
	}
}
