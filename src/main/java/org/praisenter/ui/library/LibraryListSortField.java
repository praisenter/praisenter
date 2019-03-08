package org.praisenter.ui.library;

import org.praisenter.ui.translations.Translations;

enum LibraryListSortField {
	NAME(Translations.get("item.name")),
	TYPE(Translations.get("item.type")),
	MODIFIED_ON(Translations.get("item.modified")),
	CREATED_ON(Translations.get("item.created"));
	
	private final String name;
	
	private LibraryListSortField(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
