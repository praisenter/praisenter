package org.praisenter.data.song;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.praisenter.data.search.Indexable;
import org.praisenter.data.search.SearchCriteria;
import org.praisenter.data.search.SearchType;

public final class SongTextSearchCriteria extends SearchCriteria {
	public SongTextSearchCriteria(String terms, SearchType type, int maxResults) {
		super(Indexable.FIELD_TEXT, terms, type, maxResults);
	}
	
	@Override
	public Query createQuery(Analyzer analyzer) throws IOException {
		Query query = super.createQuery(analyzer);
		
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		builder.add(query, Occur.MUST);
		builder.add(new TermQuery(new Term(Indexable.FIELD_TYPE, Song.DATA_TYPE_SONG)), Occur.FILTER);
		
		return builder.build();
	}
}
