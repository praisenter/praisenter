package org.praisenter.data.bible;

import java.io.IOException;
import java.util.UUID;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.praisenter.data.search.Indexable;
import org.praisenter.data.search.SearchCriteria;
import org.praisenter.data.search.SearchType;

public final class BibleSearchCriteria extends SearchCriteria {
	private final UUID bibleId;
	private final int bookNumber;
	
	public BibleSearchCriteria(String terms, SearchType type, int maxResults) {
		this(terms, type, maxResults, null, -1);
	}
	
	public BibleSearchCriteria(String terms, SearchType type, int maxResults, int bookNumber) {
		this(terms, type, maxResults, null, bookNumber);
	}
	
	public BibleSearchCriteria(String terms, SearchType type, int maxResults, UUID bibleId) {
		this(terms, type, maxResults, bibleId, -1);
	}
	
	public BibleSearchCriteria(String terms, SearchType type, int maxResults, UUID bibleId, int bookNumber) {
		super(terms, type, maxResults);
		this.bibleId = bibleId;
		this.bookNumber = bookNumber;
	}
	
	public UUID getBibleId() {
		return this.bibleId;
	}
	
	public int getBookNumber() {
		return this.bookNumber;
	}
	
	@Override
	public Query createQuery(Analyzer analyzer) throws IOException {
		Query query = super.createQuery(analyzer);
		
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(query, Occur.MUST);
		builder.add(new TermQuery(new Term(Indexable.FIELD_TYPE, Bible.DATA_TYPE_BIBLE)), Occur.FILTER);
		
		// further filter on any other criteria
		// check if the bible id was supplied
		if (this.bibleId != null) {
			// then filter by the bible
			builder.add(new TermQuery(new Term(Indexable.FIELD_ID, this.bibleId.toString())), Occur.FILTER);
			//check if the book number was supplied
			if (this.bookNumber > 0) {
				// then filter by the book number too
				builder.add(IntPoint.newExactQuery(Bible.FIELD_BOOK_ID, this.bookNumber), Occur.FILTER);
			}
		}
		
		return builder.build();
	}
}
