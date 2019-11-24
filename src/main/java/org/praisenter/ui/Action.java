package org.praisenter.ui;

import java.util.function.Supplier;

import org.praisenter.utility.RuntimeProperties;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public enum Action {
	DIVIDER(""),
	
	SAVE("action.save", new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.SAVE.duplicate()),
	SAVE_ALL("action.saveall", new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), () -> {
		// TODO need better icon or set background color for second one
		StackPane stack = new StackPane();
		StackPane pane = new StackPane(Glyphs.SAVE.duplicate().size(12));
		pane.setTranslateX(3);
		pane.setTranslateY(3);
		stack.setTranslateX(-3);
		stack.setTranslateY(-3);
		stack.getChildren().addAll(Glyphs.SAVE.duplicate().size(12), pane);
		return stack;	
	}),
	RENAME("action.rename", new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.RENAME.duplicate()),
	DELETE("action.delete", new KeyCodeCombination(KeyCode.DELETE), () -> Glyphs.DELETE.duplicate()),
	
	UNDO("action.undo", new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.UNDO.duplicate()),
	REDO("action.redo", RuntimeProperties.IS_WINDOWS_OS
			// windows
			? new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)
			// mac/ubuntu
			: new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN), () -> Glyphs.REDO.duplicate()),
	
	COPY("action.copy", new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.COPY.duplicate()),
	CUT("action.cut", new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.CUT.duplicate()),
	PASTE("action.paste", new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.PASTE.duplicate()),
	
	SELECT_ALL("action.select.all", new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), () -> Glyphs.SELECT_ALL.duplicate()),
	SELECT_NONE("action.select.none", () -> Glyphs.SELECT_NONE.duplicate()),
	SELECT_INVERT("action.select.invert", () -> Glyphs.SELECT_INVERT.duplicate()),

	IMPORT("action.import", () -> Glyphs.IMPORT.duplicate()),
	EXPORT("action.export", () -> Glyphs.EXPORT.duplicate()),
	
	BULK_EDIT("action.edit.bulk"),
	
	// application
	
	PREFERENCES("action.preferences", () -> Glyphs.MENU_PREFERENCES.duplicate()),
	REINDEX("action.reindex"),
	ABOUT("action.about", () -> Glyphs.MENU_ABOUT.duplicate()),
	LOGS("action.logs"),
	EXIT("action.exit"),
	
	// bible
	
	NEW_BIBLE("action.new.bible"),
	NEW_BOOK("action.new.bible.book", () -> {
		StackPane stack = new StackPane();
		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.LIME);
		plus.setTranslateX(3);
		stack.getChildren().addAll(Glyphs.NEW_BOOK.duplicate(), plus);
		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
		return stack;
	}),
	NEW_CHAPTER("action.new.bible.chapter", () -> {
		StackPane stack = new StackPane();
		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.LIME);
		plus.setTranslateX(3);
		stack.getChildren().addAll(Glyphs.NEW_CHAPTER.duplicate(), plus);
		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
		return stack;
	}),
	NEW_VERSE("action.new.bible.verse", () -> {
		StackPane stack = new StackPane();
		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.LIME);
		plus.setTranslateX(3);
		stack.getChildren().addAll(Glyphs.NEW_VERSE.duplicate(), plus);
		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
		return stack;
	}),
	RENUMBER("action.renumber", () -> Glyphs.RENUMBER.duplicate()),
	REORDER("action.reorder", () -> Glyphs.REORDER.duplicate()),
	
	// slide
	
	NEW_SLIDE("action.new.slide"),
	NEW_SLIDE_TEXT_COMPONENT("action.new.slide.component.text"),
	NEW_SLIDE_MEDIA_COMPONENT("action.new.slide.component.media"),
	NEW_SLIDE_DATETIME_COMPONENT("action.new.slide.component.datetime"),
	NEW_SLIDE_PLACEHOLDER_COMPONENT("action.new.slide.component.placeholder"),
	NEW_SLIDE_COUNTDOWN_COMPONENT("action.new.slide.component.countdown"),
	SLIDE_COMPONENT_MOVE_BACK("action.stacking.back", () -> {
		Rectangle back1 = new Rectangle(5, 5, 10, 10);
		Rectangle back2 = new Rectangle(3, 3, 10, 10);
		Rectangle back3 = new Rectangle(1, 1, 10, 10);
		back1.setFill(Color.DARKGRAY);
		back2.setFill(Color.GRAY);
		back3.setFill(Color.BLUE);
		back1.setSmooth(false);
		back2.setSmooth(false);
		back3.setSmooth(false);
		return new Pane(back3, back2, back1);
	}),
	SLIDE_COMPONENT_MOVE_FRONT("action.stacking.front", () -> {
		Rectangle front1 = new Rectangle(1, 1, 10, 10);
		Rectangle front2 = new Rectangle(3, 3, 10, 10);
		Rectangle front3 = new Rectangle(5, 5, 10, 10);
		front1.setFill(Color.DARKGRAY);
		front2.setFill(Color.GRAY);
		front3.setFill(Color.BLUE);
		front1.setSmooth(false);
		front2.setSmooth(false);
		front3.setSmooth(false);
		return new Pane(front1, front2, front3);
	}),
	SLIDE_COMPONENT_MOVE_UP("action.stacking.up", () -> {
		Rectangle upr1 = new Rectangle(1, 1, 10, 10);
		Rectangle upr2 = new Rectangle(5, 5, 10, 10);
		upr1.setFill(Color.GRAY);
		upr2.setFill(Color.BLUE);
		upr1.setSmooth(false);
		return new Pane(upr1, upr2);
	}),
	SLIDE_COMPONENT_MOVE_DOWN("action.stacking.down", () -> {
		Rectangle downr1 = new Rectangle(1, 1, 10, 10);
		Rectangle downr2 = new Rectangle(5, 5, 10, 10);
		downr1.setFill(Color.BLUE);
		downr2.setFill(Color.GRAY);
		downr2.setSmooth(false);
		return new Pane(downr1, downr2);
	}),
	
	NEW_SLIDE_SHOW("action.new.show"),
	
	// song
	
	NEW_SONG("action.new.song"),
	NEW_LYRICS("action.new.song.lyrics", () -> {
		StackPane stack = new StackPane();
		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.LIME);
		plus.setTranslateX(3);
		stack.getChildren().addAll(Glyphs.NEW_LYRICS.duplicate(), plus);
		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
		return stack;
	}),
	NEW_AUTHOR("action.new.song.author", () -> {
		StackPane stack = new StackPane();
		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.LIME);
		plus.setTranslateX(3);
		stack.getChildren().addAll(Glyphs.NEW_AUTHOR.duplicate(), plus);
		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
		return stack;
	}),
	NEW_SONGBOOK("action.new.song.songbook", () -> {
		StackPane stack = new StackPane();
		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.LIME);
		plus.setTranslateX(3);
		stack.getChildren().addAll(Glyphs.NEW_SONGBOOK.duplicate(), plus);
		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
		return stack;
	}),
	NEW_SECTION("action.new.song.section", () -> {
		StackPane stack = new StackPane();
		Node plus = Glyphs.ASTERISK.duplicate().size(8).color(Color.LIME);
		plus.setTranslateX(3);
		stack.getChildren().addAll(Glyphs.NEW_SECTION.duplicate(), plus);
		StackPane.setAlignment(plus, Pos.TOP_RIGHT);
		return stack;
	}),
	
	// the "new" menu
	
	NEW("action.new", () -> Glyphs.NEW.duplicate(), 
			Action.NEW_SLIDE,
			Action.NEW_SLIDE_SHOW,
			Action.NEW_BIBLE,
			Action.NEW_SONG,
			Action.DIVIDER,
			// bible related sub creates
			Action.NEW_BOOK, 
			Action.NEW_CHAPTER, 
			Action.NEW_VERSE,
			// slide related sub creates
			Action.NEW_SLIDE_TEXT_COMPONENT,
			Action.NEW_SLIDE_MEDIA_COMPONENT,
			Action.NEW_SLIDE_DATETIME_COMPONENT,
			Action.NEW_SLIDE_PLACEHOLDER_COMPONENT,
			Action.NEW_SLIDE_COUNTDOWN_COMPONENT,
			// song related sub creates
			Action.NEW_LYRICS,
			Action.NEW_SECTION,
			Action.NEW_AUTHOR,
			Action.NEW_SONGBOOK)
	;
	
	private final String messageKey;
	private final KeyCombination accelerator;
	private final Supplier<Node> graphicSupplier;
	private final Action[] actions;
	
	private Action(String messageKey, Action... actions) {
		this(messageKey, null, null, actions);
	}

	private Action(String messageKey, KeyCombination accelerator, Action... actions) {
		this(messageKey, accelerator, null, actions);
	}
	
	private Action(String messageKey, Supplier<Node> graphicSupplier, Action... actions) {
		this(messageKey, null, graphicSupplier, actions);
	}
	
	private Action(String messageKey, KeyCombination accelerator, Supplier<Node> graphicSupplier, Action... actions) {
		this.messageKey = messageKey;
		this.accelerator = accelerator;
		this.graphicSupplier = graphicSupplier;
		this.actions = actions;
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
	
	public Action[] getActions() {
		return this.actions;
	}
}
