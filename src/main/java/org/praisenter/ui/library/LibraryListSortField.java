package org.praisenter.ui.library;

import org.praisenter.ui.translations.Translations;

enum LibraryListSortField {
	NAME(Translations.get("list.sort.name")),
	TYPE(Translations.get("list.sort.type")),
	MODIFIED_ON(Translations.get("list.sort.modified")),
	CREATED_ON(Translations.get("list.sort.created"));
	
	private final String name;
	
	private LibraryListSortField(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
