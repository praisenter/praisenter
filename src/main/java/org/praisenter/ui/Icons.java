package org.praisenter.ui;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public final class Icons {
	
	public static final String ERROR = "p-icon-error";
	public static final String SUCCESS = "p-icon-success";
	public static final String PENDING = "p-icon-pending";
	public static final String BIBLE = "p-icon-bible";
	public static final String BIBLE_ADD = "p-icon-bible-add";
	public static final String BOOK = "p-icon-book";
	public static final String BOOK_ADD = "p-icon-book-add";
	public static final String BOOKMARK = "p-icon-bookmark";
	public static final String BOOKMARK_ADD = "p-icon-bookmark-add";
	public static final String SONG = "p-icon-song";
	public static final String SONG_ADD = "p-icon-song-add";
	public static final String LYRICS = "p-icon-lyrics";
	public static final String LYRICS_ADD = "p-icon-lyrics-add";
	public static final String USER = "p-icon-user";
	public static final String USER_ADD = "p-icon-user-add";
	public static final String TAG = "p-icon-tag";
	public static final String TEXT_SIZE = "p-icon-textsize";
	public static final String TEXT_FORMAT = "p-icon-textformat";
	public static final String HORIZONTAL_ALIGN_LEFT = "p-icon-halignleft";
	public static final String HORIZONTAL_ALIGN_RIGHT = "p-icon-halignright";
	public static final String HORIZONTAL_ALIGN_CENTER = "p-icon-haligncenter";
	public static final String HORIZONTAL_ALIGN_JUSTIFY = "p-icon-halignjustify";
	public static final String VERTICAL_ALIGN_TOP = "p-icon-valigntop";
	public static final String VERTICAL_ALIGN_CENTER = "p-icon-valigncenter";
	public static final String VERTICAL_ALIGN_BOTTOM = "p-icon-valignbottom";
	public static final String SAVE = "p-icon-save";
	public static final String SAVE_ALL = "p-icon-saveall";
	public static final String RENAME = "p-icon-rename";
	public static final String DELETE = "p-icon-delete";
	public static final String COPY = "p-icon-copy";
	public static final String CUT = "p-icon-cut";
	public static final String PASTE = "p-icon-paste";
	public static final String UNDO = "p-icon-undo";
	public static final String REDO = "p-icon-redo";
	public static final String IMPORT = "p-icon-import";
	public static final String EXPORT = "p-icon-export";
	public static final String SORT_ASCENDING = "p-icon-sortasc";
	public static final String SORT_DESCENDING = "p-icon-sortdesc";
	public static final String RENUMBER = "p-icon-renumber";
	public static final String REORDER = "p-icon-reorder";
	public static final String SELECT_ALL = "p-icon-selectall";
	public static final String SELECT_NONE = "p-icon-selectnone";
	public static final String SELECT_INVERT = "p-icon-selectinvert";
	public static final String PLAY = "p-icon-play";
	public static final String PAUSE = "p-icon-pause";
	public static final String STOP = "p-icon-stop";
	public static final String MUTE = "p-icon-mute";
	public static final String VOLUME = "p-icon-volume";
	public static final String SEARCH = "p-icon-search";
	public static final String DESKTOP = "p-icon-desktop";
	public static final String DESKTOP_ADD = "p-icon-desktop-add";
	public static final String LAYERS = "p-icon-layers";
	public static final String PENCIL = "p-icon-pencil";
	public static final String GEAR = "p-icon-gear";
	public static final String TIMER = "p-icon-timer";
	public static final String TIMER_ADD = "p-icon-timer-add";
	public static final String CALENDAR = "p-icon-calendar";
	public static final String CALENDAR_ADD = "p-icon-calendar-add";
	public static final String MEDIA = "p-icon-media";
	public static final String MEDIA_ADD = "p-icon-media-add";
	public static final String TEXT = "p-icon-text";
	public static final String TEXT_ADD = "p-icon-text-add";
	public static final String PLACEHOLDER = "p-icon-placeholder";
	public static final String PLACEHOLDER_ADD = "p-icon-placeholder-add";
	public static final String HISTORY = "p-icon-history";
	public static final String VERSE = "p-icon-verse";
	public static final String VERSE_ADD = "p-icon-verse-add";
	public static final String UPDATE = "p-icon-update";
	public static final String INFO = "p-icon-info";
	public static final String NEW = "p-icon-new";
	public static final String FORWARD = "p-icon-forward";
	public static final String BACKWARD = "p-icon-backward";
	public static final String TO_BACK = "p-icon-to-back";
	public static final String TO_FRONT = "p-icon-to-front";
	public static final String CLOSE = "p-icon-close";
	
	public static final Region getIcon(String icon) {
		Region node = new Region();
		// NOTE: "font-icon" is here to inherit the color from font-icon in the atlantafx themes
		// this is done so that -fx-fill can be used as the -fx-background-color since the themes
		// aren't consistent with the hover/focused text colorings
		node.getStyleClass().addAll("p-icon", "font-icon", icon);
		StackPane sp = new StackPane(node);
		sp.getStyleClass().add("p-icon-container");
		return sp;
	}
}
