package org.praisenter.data.song;

final class SearchableText {
	int songId;
	String part;
	String text;
	String language;
	String translit;
	
	@Override
	public String toString() {
		return text;
	}
}
