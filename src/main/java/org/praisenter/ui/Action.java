package org.praisenter.ui;

import java.util.function.Supplier;

//import org.controlsfx.glyphfont.Glyph;
import org.praisenter.utility.RuntimeProperties;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

public enum Action {
	SAVE("action.save", new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.SAVE)),
	// TODO save all
	SAVE_ALL("action.saveall", new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.SAVE)),
//			() -> {
//		StackPane stack = new StackPane();
//		StackPane pane = new StackPane(Glyphs.SAVE.duplicate());
//		pane.setTranslateX(3);
//		pane.setTranslateY(1);
//		
//		stack.setTranslateX(-3);
//		stack.setTranslateY(-1);
//		
//		SVGPath path = new SVGPath();
//		path.setContent("M0,0 L10,0 L10,1 L1,1 L1,12 L0,12 Z");
//		path.getStyleClass().add("p-save-all-adder");
//
//		stack.getChildren().addAll(path, pane);
//		return stack;
//	}),
	RENAME("action.rename", new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.RENAME)),
	DELETE("action.delete", new KeyCodeCombination(KeyCode.DELETE), getGraphicSupplier(Icons.DELETE)),
	
	OPEN("action.open"),
	UNDO("action.undo", new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.UNDO)),
	REDO("action.redo", RuntimeProperties.IS_WINDOWS_OS
			// windows
			? new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN)
			// mac/ubuntu
			: new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN), getGraphicSupplier(Icons.REDO)),
	
	COPY("action.copy", new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.COPY)),
	CUT("action.cut", new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.CUT)),
	PASTE("action.paste", new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.PASTE)),
	
	SELECT_ALL("action.select.all", new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), getGraphicSupplier(Icons.SELECT_ALL)),
	SELECT_NONE("action.select.none", getGraphicSupplier(Icons.SELECT_NONE)),
	SELECT_INVERT("action.select.invert", getGraphicSupplier(Icons.SELECT_INVERT)),

	IMPORT("action.import", getGraphicSupplier(Icons.IMPORT)),
	EXPORT("action.export", getGraphicSupplier(Icons.EXPORT)),
	
	BULK_EDIT_BEGIN("action.edit.bulk"),
	
	// application
	
	REINDEX("action.workspace.reindex"),
	ABOUT("action.about", getGraphicSupplier(Icons.INFO)),
	INCREASE_FONT_SIZE("action.font.increase", new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.SHORTCUT_DOWN)),
	DECREASE_FONT_SIZE("action.font.decrease", new KeyCodeCombination(KeyCode.MINUS, KeyCombination.SHORTCUT_DOWN)),
	RESET_FONT_SIZE("action.font.reset", new KeyCodeCombination(KeyCode.NUMPAD0, KeyCombination.SHORTCUT_DOWN)),
	APPLICATION_LOGS("action.application.logs"),
	WORKSPACE_LOGS("action.workspace.logs"),
	EXIT("action.exit"),
	RESTART("action.restart"),
	CHECK_FOR_UPDATE("action.update.check", getGraphicSupplier(Icons.UPDATE)),
	
	// bible
	
	NEW_BIBLE("action.new.bible", new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN), getGraphicSupplier(Icons.BOOK)),
	NEW_BOOK("action.new.bible.book", getGraphicSupplier(Icons.BOOK)),
	NEW_CHAPTER("action.new.bible.chapter", getGraphicSupplier(Icons.BOOKMARK)),
	NEW_VERSE("action.new.bible.verse", getGraphicSupplier(Icons.VERSE)),
	RENUMBER("action.renumber", getGraphicSupplier(Icons.RENUMBER)),
	REORDER("action.reorder", getGraphicSupplier(Icons.REORDER)),
	
	// slide
	
	NEW_SLIDE("action.new.slide", new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN), getGraphicSupplier(Icons.DESKTOP)),
	NEW_SLIDE_TEXT_COMPONENT("action.new.slide.component.text", getGraphicSupplier(Icons.TEXT)),
	NEW_SLIDE_MEDIA_COMPONENT("action.new.slide.component.media", getGraphicSupplier(Icons.MEDIA)),
	NEW_SLIDE_DATETIME_COMPONENT("action.new.slide.component.datetime", getGraphicSupplier(Icons.CALENDAR)),
	NEW_SLIDE_PLACEHOLDER_COMPONENT("action.new.slide.component.placeholder", getGraphicSupplier(Icons.PLACEHOLDER)),
	NEW_SLIDE_COUNTDOWN_COMPONENT("action.new.slide.component.countdown", getGraphicSupplier(Icons.TIMER)),
	
	// TODO change to use theme colors
	SLIDE_COMPONENT_MOVE_BACK("action.stacking.back", new KeyCodeCombination(KeyCode.CLOSE_BRACKET, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), () -> {
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
	SLIDE_COMPONENT_MOVE_FRONT("action.stacking.front", new KeyCodeCombination(KeyCode.OPEN_BRACKET, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), () -> {
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
	SLIDE_COMPONENT_MOVE_UP("action.stacking.up", new KeyCodeCombination(KeyCode.OPEN_BRACKET, KeyCombination.SHORTCUT_DOWN), () -> {
		Rectangle upr1 = new Rectangle(1, 1, 10, 10);
		Rectangle upr2 = new Rectangle(5, 5, 10, 10);
		upr1.setFill(Color.GRAY);
		upr2.setFill(Color.BLUE);
		upr1.setSmooth(false);
		return new Pane(upr1, upr2);
	}),
	SLIDE_COMPONENT_MOVE_DOWN("action.stacking.down", new KeyCodeCombination(KeyCode.CLOSE_BRACKET, KeyCombination.SHORTCUT_DOWN), () -> {
		Rectangle downr1 = new Rectangle(1, 1, 10, 10);
		Rectangle downr2 = new Rectangle(5, 5, 10, 10);
		downr1.setFill(Color.BLUE);
		downr2.setFill(Color.GRAY);
		downr2.setSmooth(false);
		return new Pane(downr1, downr2);
	}),
	
	// song
	
	NEW_SONG("action.new.song", new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN, KeyCombination.ALT_DOWN), getGraphicSupplier(Icons.MUSIC)),
	NEW_LYRICS("action.new.song.lyrics", getGraphicSupplier(Icons.MUSIC)),
	NEW_AUTHOR("action.new.song.author", getGraphicSupplier(Icons.USER)),
	NEW_SONGBOOK("action.new.song.songbook", getGraphicSupplier(Icons.BOOK)),
	NEW_SECTION("action.new.song.section", getGraphicSupplier(Icons.VERSE)),
	
	// other
	DOWNLOAD_ZEFANIA_BIBLES("action.download.zefania"),
	DOWNLOAD_UNBOUND_BIBLES("action.download.unbound"),
	DOWNLOAD_OPENSONG_BIBLES("action.download.opensong"),
	
	;
	
	private static final Supplier<Node> getGraphicSupplier(String icon) {
		return () -> {
			return Icons.getIcon(icon);
		};
	}
	
//	private static final Supplier<Node> getGraphicSupplierForNew(Glyph glyph) {
//		return () ->
//		{
//			StackPane stack = new StackPane();
////			BorderStroke stroke1 = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null);
////			BorderStroke stroke2 = new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null);
//			Glyph plus = Glyphs.NEW.duplicate().size(6).color(Color.BLACK);
////			plus.setBorder(new Border(stroke2));
////			plus.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
//			plus.getStyleClass().add("p-new-adder");
//			plus.setPadding(new Insets(1, 2, 1, 2));
////			plus.setShape(new Circle(Math.max(plus.getWidth() * 0.5, 24)));
//			plus.setTranslateX(3);
//			
////			Region backing = new Region(); 
////			backing.setShape(new Circle(4));
////			backing.setMaxSize(9, 9);
////			
////			backing.setBorder(new Border(stroke2));
////			backing.setBackground(new Background(new BackgroundFill(Color.LIME, null, null)));
////			backing.setTranslateX(7);
////			backing.setTranslateY(-5);
//			
//			stack.getChildren().addAll(glyph.duplicate(), plus);
//			StackPane.setAlignment(plus, Pos.TOP_RIGHT);
//			return stack;
//		};
//	}
	
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
}
