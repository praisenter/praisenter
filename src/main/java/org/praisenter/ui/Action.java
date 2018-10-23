package org.praisenter.ui;

import java.util.function.Supplier;

import org.praisenter.javafx.ApplicationGlyphs;
import org.praisenter.utility.RuntimeProperties;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public enum Action {
//	EDIT("action.edit"),
//	CLOSE("action.close", () -> ApplicationGlyphs.MENU_CLOSE.duplicate()),
	
	SAVE("action.save", new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.MENU_SAVE.duplicate()),
//	SAVE_AS("action.saveas", () -> {
//		StackPane stack = new StackPane();
//		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.GREEN);
//		plus.setTranslateX(3);
//		stack.getChildren().addAll(Glyphs.MENU_SAVE_AS.duplicate(), plus);
//		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
//		return stack;
//	}),
	SAVE_ALL("action.saveall", new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), () -> {
		StackPane stack = new StackPane();
		StackPane pane = new StackPane(Glyphs.MENU_SAVE_ALL.duplicate().size(12));
		pane.setTranslateX(3);
		pane.setTranslateY(3);
		stack.getChildren().addAll(Glyphs.MENU_SAVE_ALL.duplicate().size(12), pane);
		return stack;	
	}),
	RENAME("action.rename", new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.MENU_RENAME.duplicate()),
	DELETE("action.delete", new KeyCodeCombination(KeyCode.DELETE), () -> Glyphs.MENU_DELETE.duplicate()),
	
	UNDO("action.undo", new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.MENU_UNDO.duplicate()),
	REDO("action.redo", RuntimeProperties.IS_WINDOWS_OS
			// windows
			? new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)
			// mac/ubuntu
			: new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN), () -> Glyphs.MENU_REDO.duplicate()),
	
	COPY("action.copy", new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.MENU_COPY.duplicate()),
	CUT("action.cut", new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.MENU_CUT.duplicate()),
	PASTE("action.paste", new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.MENU_PASTE.duplicate()),
	
	SELECT_ALL("action.select.all", new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.SELECT_ALL.duplicate()),
	SELECT_NONE("action.select.none", () -> Glyphs.SELECT_NONE.duplicate()),
	SELECT_INVERT("action.select.invert", () -> Glyphs.SELECT_INVERT.duplicate()),

	IMPORT("action.import", () -> Glyphs.MENU_IMPORT.duplicate()),
	EXPORT("action.export", () -> Glyphs.MENU_EXPORT.duplicate()),
	
	NEW("action.new", () -> Glyphs.NEW.duplicate()),
	
	// application
	
	PREFERENCES("action.preferences", () -> Glyphs.MENU_PREFERENCES.duplicate()),
//	MANAGE_MEDIA("action.media"),
//	MANAGE_BIBLES("action.bibles"),
//	MANAGE_SLIDES("action.slides"),
//	MANAGE_SHOWS("action.shows"),
//	MANAGE_SONGS("action.songs"),
	REINDEX("action.reindex"),
	ABOUT("action.about", () -> Glyphs.MENU_ABOUT.duplicate()),
	LOGS("action.logs"),
	EXIT("action.exit"),
	
	// bible
	
//	NEW_BIBLE("action.new.bible"),
//	NEW_BOOK("action.new.book"),
//	NEW_CHAPTER("action.new.chapter"),
//	NEW_VERSE("action.new.verse"),
	RENUMBER("action.renumber", () -> Glyphs.RENUMBER.duplicate()),
	REORDER("action.reorder", () -> Glyphs.REORDER.duplicate())
	
	// slide
	
//	NEW_SLIDE("action.new.slide"),
//	NEW_SLIDE_SHOW("action.new.show"),
	
	// song
	
//	NEW_SONG("action.new.song")
	;
	
	private final String messageKey;
	private final KeyCombination accelerator;
	private final Supplier<Node> graphicSupplier;
	
	private Action(String messageKey) {
		this(messageKey, null, null);
	}

	private Action(String messageKey, KeyCombination accelerator) {
		this(messageKey, accelerator, null);
	}
	
	private Action(String messageKey, Supplier<Node> graphicSupplier) {
		this(messageKey, null, graphicSupplier);
	}
	
	private Action(String messageKey, KeyCombination accelerator, Supplier<Node> graphicSupplier) {
		this.messageKey = messageKey;
		this.accelerator = accelerator;
		this.graphicSupplier = graphicSupplier;
	}
	
	public KeyCombination getAccelerator() {
		return this.accelerator;
	}
	
	public String getMessageKey() {
		return this.messageKey;
	}
	
	public Supplier<Node> getGraphicSupplier() {
		return this.graphicSupplier;
	}
	
//	public MenuItem createMenuItem() {
//		return this.createMenuItem(null, null, null);
//	}
//	
//	public MenuItem createMenuItem(String label) {
//		return this.createMenuItem(label, null, null);
//	}
//	
//	public MenuItem createMenuItem(String label, KeyCombination accelerator, Node graphic) {
//		MenuItem mi = new MenuItem(label != null ? label : Translations.get(this.messageKey));
//		
//		if (accelerator != null) mi.setAccelerator(accelerator);
//		else if (this.accelerator != null) mi.setAccelerator(this.accelerator);
//		
//		if (graphic != null) mi.setGraphic(graphic);
//		else if (this.graphicSupplier != null) mi.setGraphic(this.graphicSupplier.get());
//		
//		mi.setUserData(this);
//		
//		return mi;
//	}
//	
//	public Button createButton() {
//		return this.createButton(null, null);
//	}
//	
//	public Button createButton(String label) {
//		return this.createButton(label, null);
//	}
//	
//	public Button createButton(String label, Node graphic) {
//		Button btn = new Button(label != null ? label : Translations.get(this.messageKey));
//		
//		if (graphic != null) btn.setGraphic(graphic);
//		else if (this.graphicSupplier != null) btn.setGraphic(this.graphicSupplier.get());
//		
//		btn.setUserData(this);
//		
//		return btn;
//	}
}
