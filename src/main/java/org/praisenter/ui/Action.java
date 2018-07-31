package org.praisenter.ui;

public enum Action {
	EDIT,
	CLOSE,
	SAVE,
	SAVE_AS,
	RENAME,
	DELETE,
	
	UNDO,
	REDO,
	
	COPY,
	CUT,
	PASTE,
	
	SELECT_ALL,
	SELECT_NONE,
	SELECT_INVERT,

	IMPORT,
	EXPORT,
	
	// application
	
	PREFERENCES,
	PRESENT,
	MANAGE_MEDIA,
	MANAGE_BIBLES,
	MANAGE_SLIDES,
	MANAGE_SHOWS,
	MANAGE_SONGS,
	REINDEX,
	ABOUT,
	LOGS,
	EXIT,
	
	// bible
	
	NEW_BIBLE,
	NEW_BOOK,
	NEW_CHAPTER,
	NEW_VERSE,
	RENUMBER,
	REORDER,
	
	// slide
	
	NEW_SLIDE,
	NEW_SLIDE_SHOW
}
