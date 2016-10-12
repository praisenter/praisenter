package org.praisenter.javafx;

import java.sql.SQLException;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.praisenter.javafx.bible.BibleLibraryPane;
import org.praisenter.javafx.bible.BiblePane;
import org.praisenter.javafx.media.MediaLibraryPane;
import org.praisenter.javafx.slide.SlideLibraryPane;
import org.praisenter.javafx.slide.editor.SlideEditorPane;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class MainPane extends BorderPane {

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final PraisenterContext context;
	
	private final BooleanProperty showSave = new SimpleBooleanProperty(true);
	private final BooleanProperty showSaveAs = new SimpleBooleanProperty(true);
	private final BooleanProperty showRename = new SimpleBooleanProperty(true);
	private final BooleanProperty showCopy = new SimpleBooleanProperty(true);
	private final BooleanProperty showCut = new SimpleBooleanProperty(true);
	private final BooleanProperty showPaste = new SimpleBooleanProperty(true);
	
	public MainPane(PraisenterContext context) {
		this.context = context;
		
		this.setTop(createMenus());
	}
	
	// TODO finish all menu items
	// TODO determine menu item availability
	
	// TODO translate
	private MenuBar createMenus() {
		Rectangle blank = new Rectangle(0,0,15,15);
		blank.setFill(Color.TRANSPARENT);
		
		MenuBar menu = new MenuBar();
		menu.setUseSystemMenuBar(true);
		
		Menu file = new Menu("File");
		Menu edit = new Menu("Edit");
		Menu media = new Menu("Media");
		Menu songs = new Menu("Songs");
		Menu bibles = new Menu("Bibles");
		Menu slides = new Menu("Slides");
		Menu help = new Menu("Help");
		
		menu.getMenus().addAll(file, edit, media, songs, bibles, slides, help);
		
		Menu fNew = new Menu("New");
		MenuItem fNewSlide = new MenuItem("Slide", blank);
		MenuItem fNewSlideShow = new MenuItem("Slide Show");
		MenuItem fNewSong = new MenuItem("Song");
		MenuItem fNewBible = new MenuItem("Bible");
		fNew.getItems().addAll(fNewSlide, fNewSlideShow, fNewSong, fNewBible);
		
		Menu fImport = new Menu("Import");
		MenuItem fImportSlides = new MenuItem("Slides", blank);
		MenuItem fImportSongs = new MenuItem("Songs");
		MenuItem fImportBibles = new MenuItem("Bibles");
		fImport.getItems().addAll(fImportSlides, fImportSongs, fImportBibles);
		
		MenuItem fSave = new MenuItem("Save", FONT_AWESOME.create(FontAwesome.Glyph.SAVE));
		MenuItem fSaveAs = new MenuItem("Save As...", FONT_AWESOME.create(FontAwesome.Glyph.SAVE));
		MenuItem fRename = new MenuItem("Rename", FONT_AWESOME.create(FontAwesome.Glyph.TERMINAL));
		
		MenuItem fSetup = new MenuItem("Preferences", FONT_AWESOME.create(FontAwesome.Glyph.GEAR));
		MenuItem fExit = new MenuItem("Exit");
		
		fSave.disableProperty().bind(showSave);
		fSaveAs.disableProperty().bind(showSaveAs);
		fRename.disableProperty().bind(showRename);
		
		fNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
		fSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
		fSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
		fRename.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
		
		file.getItems().addAll(fNew, new SeparatorMenuItem(), fSave, fSaveAs, fRename, new SeparatorMenuItem(), fImport, new SeparatorMenuItem(), fSetup, new SeparatorMenuItem(), fExit);
		
		MenuItem fCopy = new MenuItem("Copy", FONT_AWESOME.create(FontAwesome.Glyph.COPY));
		MenuItem fCut = new MenuItem("Cut", FONT_AWESOME.create(FontAwesome.Glyph.CUT));
		MenuItem fPaste = new MenuItem("Paste", FONT_AWESOME.create(FontAwesome.Glyph.PASTE));
		
		fCopy.disableProperty().bind(showCopy);
		fCut.disableProperty().bind(showCut);
		fPaste.disableProperty().bind(showPaste);
		
		fCopy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
		fCut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));
		fPaste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));
		
		edit.getItems().addAll(fCopy, fCut, fPaste);
		
		MenuItem mManage = new MenuItem("Manage media");
		MenuItem mImport = new MenuItem("Import media");
		media.getItems().addAll(mManage, mImport);
		
		// add/edit
		MenuItem soManage = new MenuItem("Manage songs");
		MenuItem soImport = new MenuItem("Import songs");
		MenuItem soNew = new MenuItem("Create a new song");
		// manage
		songs.getItems().addAll(soManage, soImport, soNew);

		MenuItem slManage = new MenuItem("Manage slides");
		MenuItem slNew = new MenuItem("Create a new slide");
		slides.getItems().addAll(slManage, slNew);
		
		MenuItem blManage = new MenuItem("Manage bibles");
		MenuItem blImport = new MenuItem("Import bibles");
		bibles.getItems().addAll(blManage, blImport);
		
		MenuItem hAbout = new MenuItem("About");
		help.getItems().addAll(hAbout);
		
		BreadCrumbBar<Object> bar = new BreadCrumbBar<>();
		
		
		// panes
		
		SetupPane sp = new SetupPane(context.getConfiguration());
		BibleLibraryPane blp = new BibleLibraryPane(context);
		MediaLibraryPane mlp = new MediaLibraryPane(context, Orientation.HORIZONTAL);
		SlideLibraryPane slp = new SlideLibraryPane(context);
		
		// menu actions

		fSetup.setOnAction((e) -> {
			setCenter(sp);
		});
		
		blManage.setOnAction((e) -> {
			setCenter(blp);
		});
		
		mManage.setOnAction((e) -> {
			setCenter(mlp);
		});
		
		slManage.setOnAction((e) -> {
			setCenter(slp);
		});
		
		fExit.setOnAction(e -> {
			context.getJavaFXContext().getStage().close();
		});
		
		return menu;
	}
	
	// TODO slide show pane
	// TODO song pane
	// TODO slide pane
	
	private void setMainPane(Node node) {
		Node old = getCenter();
		// undo any hooks
		setCenter(node);
		// add any hooks
		// update the menu
	}
}
