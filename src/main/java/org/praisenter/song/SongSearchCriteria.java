package org.praisenter.song;

import org.praisenter.SearchType;

public final class SongSearchCriteria {
	private final String text;
	private final SearchType type;
	private final int maximumResults;
	
	public SongSearchCriteria(String text, SearchType type, int maximumResults) {
		super();
		this.text = text;
		this.type = type;
		this.maximumResults = maximumResults;
	}
	public String getText() {
		return text;
	}
	public SearchType getType() {
		return type;
	}
	public int getMaximumResults() {
		return maximumResults;
	}
}
