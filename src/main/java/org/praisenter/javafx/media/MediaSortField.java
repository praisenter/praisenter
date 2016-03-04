package org.praisenter.javafx.media;

// TODO translate
public enum MediaSortField {
	NAME("Name"),
	TYPE("Type"),
	ADDED_DATE("Date Added");
	
	private final String name;
	
	private MediaSortField(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
