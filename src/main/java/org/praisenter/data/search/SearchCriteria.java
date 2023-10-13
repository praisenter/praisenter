package org.praisenter.data.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.queries.spans.SpanNearQuery;
import org.apache.lucene.queries.spans.SpanQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

public class SearchCriteria {
	private final String field;
	private final String terms;
	private final SearchType type;
	private final int maxResults;
	private final boolean fuzzy;
	
	public SearchCriteria(String field, String terms, SearchType type, boolean fuzzy, int maxResults) {
		this.field = field;
		this.terms = terms;
		this.type = type;
		this.maxResults = maxResults;
		this.fuzzy = fuzzy;
	}
	
	public String getField() {
		return this.field;
	}
	
	public String getTerms() {
		return this.terms;
	}
	
	public SearchType getType() {
		return this.type;
	}
	
	public boolean isFuzzy() {
		return this.fuzzy;
	}
	
	public int getMaxResults() {
		return this.maxResults;
	}

	/**
	 * Uses the lucene analyzer to tokenize the given text for the given lucene field.
	 * @param text the text to tokenize
	 * @return List&lt;String&gt;
	 * @throws IOException
	 */
	protected final List<String> getTokens(Analyzer analyzer) throws IOException {
		List<String> tokens = new ArrayList<String>();
		
		TokenStream stream = analyzer.tokenStream(this.field, this.terms);
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
			query = convertTermToQuery(token);//new FuzzyQuery(new Term(this.field, token));
		// PHRASE
		} else if (this.type == SearchType.PHRASE) {
			if (this.fuzzy) {
				// for phrase, do a span-near-fuzzy query since we 
				// care if the words are close to each other
				SpanQuery[] sqs = new SpanQuery[tokens.size()];
				for (int i = 0; i < tokens.size(); i++) {
					sqs[i] = new SpanMultiTermQueryWrapper<FuzzyQuery>(new FuzzyQuery(new Term(this.field, tokens.get(i))));
				}
				// the terms should be within 3 terms of each other
				query = new SpanNearQuery(sqs, 3, false);
			} else {
				query = new PhraseQuery(3, this.field, tokens.toArray(new String[0]));
			}
		// ALL_WORDS, ANY_WORD
		} else {
			// do an and/or combination of fuzzy queries
			BooleanQuery.Builder builder = new BooleanQuery.Builder();
			for (String token : tokens) {
				builder.add(convertTermToQuery(token), this.type == SearchType.ALL_WORDS ? Occur.MUST : Occur.SHOULD);
			}
			query = builder.build();
		}
		// ALL_WILDCARD, ANY_WILDCARD (not available as an option)
		
		return query;
	}
	
	private Query convertTermToQuery(String token) {
		Term term = new Term(this.field, token);
		if (this.fuzzy) {
			return new FuzzyQuery(term);
		} else {
			return new TermQuery(term);
		}
	}
	
	public Query createQuery(Analyzer analyzer) throws IOException {
		return this.createBasicQuery(analyzer);
	}
}
