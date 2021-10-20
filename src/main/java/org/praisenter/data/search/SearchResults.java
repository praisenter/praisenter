package org.praisenter.data.search;

import java.util.List;

public final class SearchResults {
	private final SearchCriteria criteria;
	private final List<SearchResult> results;
	private final boolean hasMore;
	private final int numberOfResults;
	
	public SearchResults(SearchCriteria criteria, List<SearchResult> results) {
		this.criteria = criteria;
		this.results = results;
		
		int size = results.size();
		int max = criteria.getMaxResults();
		
		this.hasMore = size > max;
		
		if (hasMore) {
			results.remove(size - 1);
		}
		
		this.numberOfResults = size;
	}

	public SearchCriteria getCriteria() {
		return this.criteria;
	}

	public List<SearchResult> getResults() {
		return this.results;
	}

	public boolean hasMore() {
		return this.hasMore;
	}

	public int getNumberOfResults() {
		return this.numberOfResults;
	}
}
