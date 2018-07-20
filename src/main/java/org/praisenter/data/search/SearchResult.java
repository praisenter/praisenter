package org.praisenter.data.search;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.lucene.document.Document;

public class SearchResult {
	/** The matched document */
	private final Document document;
	
	/** The matched text */
	private final List<SearchTextMatch> matches;
	
	/** The matching score */
	private final float score;
	
	public SearchResult(Document document, List<SearchTextMatch> matches, float score) {
		this.document = document;
		this.matches = matches;
		this.score = score;
	}

	public Document getDocument() {
		return document;
	}
	
	public List<SearchTextMatch> getMatches() {
		return Collections.unmodifiableList(matches);
	}

	public float getScore() {
		return score;
	}
	
	public UUID getMatchId() {
		return UUID.fromString(this.document.get(Indexable.FIELD_ID));
	}
	
	public String getMatchText() {
		return this.document.get(Indexable.FIELD_TEXT);
	}
	
	public String getMatchDataType() {
		return this.document.get(Indexable.FIELD_TYPE);
	}
}
