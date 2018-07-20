package org.praisenter.data.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;

public class SearchCriteria {
	private final String terms;
	private final SearchType type;
	private final int maxResults;
	
	public SearchCriteria(String terms, SearchType type, int maxResults) {
		this.terms = terms;
		this.type = type;
		this.maxResults = maxResults;
	}
	
	public String getTerms() {
		return this.terms;
	}
	
	public SearchType getType() {
		return this.type;
	}
	
	public int getMaxResults() {
		return this.maxResults;
	}

	/**
	 * Uses the lucene analyzer to tokenize the given text for the given lucene field.
	 * @param text the text to tokenize
	 * @param field the lucene field the tokens will be searching
	 * @return List&lt;String&gt;
	 * @throws IOException
	 */
	protected final List<String> getTokens(Analyzer analyzer) throws IOException {
		List<String> tokens = new ArrayList<String>();
		
		TokenStream stream = analyzer.tokenStream(Indexable.FIELD_TEXT, this.terms);
		CharTermAttribute attr = stream.addAttribute(CharTermAttribute.class);
		stream.reset();

		while (stream.incrementToken()) {
			tokens.add(attr.toString());
		}
		
		stream.end();
		stream.close();
		
		return tokens;
	}
	
	protected final Query createBasicQuery(Analyzer analyzer) throws IOException {
		// get the tokens
		List<String> tokens = this.getTokens(analyzer);
		
		if (tokens == null || tokens.size() <= 0) return null;
		
		Query query = null;
		
		if (tokens.size() == 0) return null;
		if (tokens.size() == 1) {
			// single term, just do a fuzzy query on it with a larger max edit distance
			String token = tokens.get(0);
			query = new FuzzyQuery(new Term(Indexable.FIELD_TEXT, token));
		// PHRASE
		} else if (type == SearchType.PHRASE) {
			// for phrase, do a span-near-fuzzy query since we 
			// care if the words are close to each other
			SpanQuery[] sqs = new SpanQuery[tokens.size()];
			for (int i = 0; i < tokens.size(); i++) {
				sqs[i] = new SpanMultiTermQueryWrapper<FuzzyQuery>(new FuzzyQuery(new Term(Indexable.FIELD_TEXT, tokens.get(i))));
			}
			// the terms should be within 3 terms of each other
			query = new SpanNearQuery(sqs, 3, false);
		// ALL_WORDS, ANY_WORD
		} else {
			// do an and/or combination of fuzzy queries
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (String token : tokens) {
				builder.add(new FuzzyQuery(new Term(Indexable.FIELD_TEXT, token)), type == SearchType.ALL_WORDS ? Occur.MUST : Occur.SHOULD);
			}
			query = builder.build();
		}
		// ALL_WILDCARD, ANY_WILDCARD (not available as an option)
		
		return query;
	}
	
	public Query createQuery(Analyzer analyzer) throws IOException {
		return this.createBasicQuery(analyzer);
	}
}
