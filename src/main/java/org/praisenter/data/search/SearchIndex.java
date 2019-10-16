package org.praisenter.data.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;

public final class SearchIndex {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The file-system index */
	private Directory directory;
	
	/** The analyzer for the index */
	private Analyzer analyzer;
	
	public SearchIndex(Directory directory, Analyzer analyzer) {
		this.directory = directory;
		this.analyzer = analyzer;
	}
	
	public synchronized void create(Indexable item) throws IOException {
		List<Document> docs = item.index();
		if (docs == null || docs.isEmpty()) return;
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			writer.updateDocuments(new Term(Indexable.FIELD_ID, item.getId().toString()), docs);	
		}
	}
	
	public synchronized void update(Indexable item) throws IOException {
		List<Document> docs = item.index();
		if (docs == null || docs.isEmpty()) return;
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			writer.updateDocuments(new Term(Indexable.FIELD_ID, item.getId().toString()), docs);	
		}
	}
	
	public synchronized void delete(Indexable item) throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			writer.deleteDocuments(new Term(Indexable.FIELD_ID, item.getId().toString()));	
		}
	}
	
	public synchronized void reindex(Iterable<? extends Indexable> items) throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(this.analyzer);
		config.setOpenMode(OpenMode.CREATE);
		try (IndexWriter writer = new IndexWriter(this.directory, config)) {
			for (Indexable item : items) {
				LOGGER.debug("Indexing document {}", item.getName());
				List<Document> docs = item.index();
				if (docs == null || docs.isEmpty()) {
					continue;
				}
				writer.updateDocuments(new Term(Indexable.FIELD_ID, item.getId().toString()), docs);	
			}
		}
	}
	
	public SearchResults search(SearchCriteria criteria) throws IOException {
		Query query = criteria.createQuery(this.analyzer);
		if (query == null) 
			return new SearchResults(criteria, Collections.emptyList());
		
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		// NOTE: this doesn't need to be synchronized with the index, it will use a snapshot
		// of the index at the time it's opened
		try (IndexReader reader = DirectoryReader.open(this.directory)) {
			IndexSearcher searcher = new IndexSearcher(reader);
			
			TopDocs result = searcher.search(query, criteria.getMaxResults() + 1);
			ScoreDoc[] docs = result.scoreDocs;
			
			Scorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(scorer);
			
			for (ScoreDoc doc : docs) {
				Document document = searcher.doc(doc.doc);
				
				// get the text around the match
				List<SearchTextMatch> matches = new ArrayList<SearchTextMatch>();
				String[] terms = document.getValues(Indexable.FIELD_TEXT);
				for (String term : terms) {
					try {
						String text = highlighter.getBestFragment(this.analyzer, Indexable.FIELD_TEXT, term);
						if (text != null) {
							matches.add(new SearchTextMatch(Indexable.FIELD_TEXT, term, text));
						}
					} catch (Exception e) {
						LOGGER.warn("Failed to find matching text for value '" + term + "' due to an unexpected exception. The match was excluded.", e);
					}
				}
				
				results.add(new SearchResult(document, matches, doc.score));
			}
		}
		
		return new SearchResults(criteria, results);
	}
}
