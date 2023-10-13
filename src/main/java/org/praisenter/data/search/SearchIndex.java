package org.praisenter.data.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.TokenSources;
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
			
			QueryScorer scorer = new QueryScorer(query);
			Highlighter highlighter = new Highlighter(scorer);
			StoredFields storedFields = searcher.storedFields();
			
			for (ScoreDoc doc : docs) {
				Document document = storedFields.document(doc.doc);
				
				// get the text
				String text = document.get(Indexable.FIELD_TEXT);
				
				// get the text around the match
				List<SearchTextMatch> matches = new ArrayList<SearchTextMatch>();
				
				try {
					TokenStream tokens = TokenSources.getTokenStream(Indexable.FIELD_TEXT, null, text, this.analyzer, -1);
					String[] fragments = highlighter.getBestFragments(tokens, text, 10);
					
					for (String fragment : fragments) {
						matches.add(new SearchTextMatch(Indexable.FIELD_TEXT, text, fragment));
					}
				} catch (IllegalArgumentException e) {
					// https://issues.apache.org/jira/browse/LUCENE-9568
					LOGGER.warn("Failed to get highlighted text for search '" + criteria.getTerms() + "': " + e.getMessage());
				} catch (Exception e) {
					LOGGER.error("Failed to get matching text for terms: '" + criteria.getTerms() + "'", e);
				}
				
				results.add(new SearchResult(document, matches, doc.score));
			}
		}
		
		return new SearchResults(criteria, results);
	}
}
