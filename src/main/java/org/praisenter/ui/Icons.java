package org.praisenter.ui;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public final class Icons {
	
	public static final String ERROR = "p-icon-error";
	public static final String SUCCESS = "p-icon-success";
	public static final String PENDING = "p-icon-pending";
	public static final String BOOK = "p-icon-book";
	public static final String BOOKMARK = "p-icon-bookmark";
	public static final String MUSIC = "p-icon-music";
	public static final String USER = "p-icon-user";
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
	public static final String LAYERS = "p-icon-layers";
	public static final String PENCIL = "p-icon-pencil";
	public static final String GEAR = "p-icon-gear";
	public static final String TIMER = "p-icon-timer";
	public static final String CALENDAR = "p-icon-calendar";
	public static final String MEDIA = "p-icon-media";
	public static final String TEXT = "p-icon-text";
	public static final String PLACEHOLDER = "p-icon-placeholder";
	public static final String HISTORY = "p-icon-history";
	public static final String VERSE = "p-icon-verse";
	public static final String UPDATE = "p-icon-update";
	public static final String INFO = "p-icon-info";
	

	public static final Region getIcon(String icon) {
		Region node = new Region();
		node.getStyleClass().add(icon);
		StackPane sp = new StackPane(node);
		sp.getStyleClass().add("p-icon-container");
		return sp;
	}
}
