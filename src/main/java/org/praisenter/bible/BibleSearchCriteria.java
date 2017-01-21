package org.praisenter.bible;

import java.util.UUID;

import org.praisenter.SearchType;

public final class BibleSearchCriteria {
	private final UUID bibleId;
	private final Short bookNumber;
	private final String text; 
	private final SearchType type;
	
	public BibleSearchCriteria(String text, SearchType type) {
		this(null, null, text, type);
	}
	
	public BibleSearchCriteria(UUID bibleId, String text, SearchType type) {
		this(bibleId, null, text, type);
	}
	
	public BibleSearchCriteria(UUID bibleId, Short bookNumber, String text, SearchType type) {
		this.bibleId = bibleId;
		this.bookNumber = bookNumber;
		this.text = text;
		this.type = type;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Text: '").append(text).append("'");
		sb.append(" SearchType: ").append(this.type);
		if (this.bibleId != null) {
			sb.append(" BibleId: ").append(this.bibleId);
		}
		if (this.bookNumber != null) {
			sb.append(" Book: ").append(this.bookNumber);
		}
		return sb.toString();
	}
	
	public UUID getBibleId() {
		return bibleId;
	}
	public Short getBookNumber() {
		return bookNumber;
	}
	public String getText() {
		return text;
	}
	public SearchType getType() {
		return type;
	}
}
