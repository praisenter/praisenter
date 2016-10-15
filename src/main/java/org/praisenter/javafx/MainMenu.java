package org.praisenter.javafx;

import java.util.Deque;
import java.util.LinkedList;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class MainMenu extends VBox implements EventHandler<ActionEvent> {

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final ApplicationPane defaults = new DefaultApplicationPane();
	
	private final Node rootNode;
	private final MenuBar menu;
	private final HBox toolbar;

	private final ObjectProperty<Node> focusOwner = new SimpleObjectProperty<Node>();
	private final ObjectProperty<Node> appPane = new SimpleObjectProperty<Node>();
	
	public MainMenu(Node rootNode) {
		this.rootNode = rootNode;
		
		// MENU
		
		this.menu = new MenuBar();
		this.menu.setUseSystemMenuBar(true);
		
		// top-level menus
		Menu file = new Menu("File");
		Menu edit = new Menu("Edit");
		Menu media = new Menu("Media");
		Menu songs = new Menu("Songs");
		Menu bibles = new Menu("Bibles");
		Menu slides = new Menu("Slides");
		Menu help = new Menu("Help");
		
		this.menu.getMenus().addAll(file, edit, media, songs, bibles, slides, help);
		
		// File
		Menu fNew = new Menu("New");
		MenuItem fNewSlide = new MenuItem("Slide");
		MenuItem fNewSlideShow = new MenuItem("Slide Show");
		MenuItem fNewSong = new MenuItem("Song");
		MenuItem fNewBible = new MenuItem("Bible");
		fNew.getItems().addAll(fNewSlide, fNewSlideShow, fNewSong, fNewBible);
		
		Menu fImport = new Menu("Import");
		MenuItem fImportSlides = new MenuItem("Slides");
		MenuItem fImportSongs = new MenuItem("Songs");
		MenuItem fImportBibles = new MenuItem("Bibles");
		fImport.getItems().addAll(fImportSlides, fImportSongs, fImportBibles);
		
		MenuItem fSave = createMenuItem("Save", FONT_AWESOME.create(FontAwesome.Glyph.SAVE), new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), ApplicationAction.SAVE);
		MenuItem fSaveAs = createMenuItem("Save As...", FONT_AWESOME.create(FontAwesome.Glyph.SAVE), new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN), ApplicationAction.SAVE_AS);
		MenuItem fSetup = createMenuItem("Preferences", FONT_AWESOME.create(FontAwesome.Glyph.GEAR), null, ApplicationAction.PREFERENCES);
		MenuItem fExit = createMenuItem("Exit", null, null, ApplicationAction.EXIT);
		file.getItems().addAll(fNew, new SeparatorMenuItem(), fSave, fSaveAs, new SeparatorMenuItem(), fImport, new SeparatorMenuItem(), fSetup, new SeparatorMenuItem(), fExit);
		
		// Edit
		MenuItem fCopy = createMenuItem("Copy", FONT_AWESOME.create(FontAwesome.Glyph.COPY), new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), ApplicationAction.COPY);
		MenuItem fCut = createMenuItem("Cut", FONT_AWESOME.create(FontAwesome.Glyph.CUT), new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN), ApplicationAction.CUT);
		MenuItem fPaste = createMenuItem("Paste", FONT_AWESOME.create(FontAwesome.Glyph.PASTE), new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN), ApplicationAction.PASTE);
		MenuItem fRename = createMenuItem("Rename", FONT_AWESOME.create(FontAwesome.Glyph.TERMINAL), new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN), ApplicationAction.RENAME);
		MenuItem fDelete = createMenuItem("Delete", FONT_AWESOME.create(FontAwesome.Glyph.CLOSE), new KeyCodeCombination(KeyCode.DELETE), ApplicationAction.DELETE);
		MenuItem fSelectAll = createMenuItem("Select All", null, new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN), ApplicationAction.SELECT_ALL);
		edit.getItems().addAll(fCopy, fCut, fPaste, new SeparatorMenuItem(), fRename, fDelete, fSelectAll);
		
		// Media
		MenuItem mManage = createMenuItem("Manage media", null, null, ApplicationAction.MANAGE_MEDIA);
		media.getItems().addAll(mManage);
		
		// Songs
		// ----

		// Slides
		MenuItem slManage = createMenuItem("Manage slides", null, null, ApplicationAction.MANAGE_SLIDES);
		slides.getItems().addAll(slManage);
		
		// Bibles
		MenuItem blManage = createMenuItem("Manage bibles", null, null, ApplicationAction.MANAGE_BIBLES);
		bibles.getItems().addAll(blManage);
		
		// Help
		MenuItem hAbout = createMenuItem("About", null, null, ApplicationAction.ABOUT);
		help.getItems().addAll(hAbout);
		
		// TOOLBAR
		
		this.toolbar = new HBox();
		
		// EVENTS
		
		// auto bind when the scene changes
		this.sceneProperty().addListener((obs, ov, nv) -> {
			focusOwner.unbind();
			if (nv != null) {
				focusOwner.bind(nv.focusOwnerProperty());
			}
		});
		
		// call methods when focus owner changes
		this.focusOwner.addListener((obs, ov, nv) -> {
			Node appPane = getApplicationPane(nv);
			this.appPane.set(appPane);
		});
		
		this.appPane.addListener((obs, ov, nv) -> {
			if (ov != null) {
				ov.removeEventHandler(ApplicationPaneEvent.STATE_CHANGED, MainMenu.this::handleStateChanged);
			}
			
			if (nv == null) {
				updateMenuState(defaults);
			} else {
				ApplicationPane pane = (ApplicationPane)nv;
				// recursively go through the menu updating disabled and visibility states
				updateMenuState(pane);
				nv.addEventHandler(ApplicationPaneEvent.STATE_CHANGED, MainMenu.this::handleStateChanged);
			}
		});
		
		// LAYOUT
		
		this.getChildren().addAll(this.menu, this.toolbar);
		
		// INITIALIZATION
		
		updateMenuState(defaults);
	}
	
	private MenuItem createMenuItem(String label, Node graphic, KeyCombination accelerator, ApplicationAction action) {
		MenuItem item = new MenuItem(label, graphic);
		item.setAccelerator(accelerator);
		item.setUserData(action);
		item.setOnAction(this);
		return item;
	}
	
	private void handleStateChanged(ApplicationPaneEvent event) {
		ApplicationPane pane = event.getApplicationPane();
		if (pane != null) {
			updateMenuState(pane);
		}
	}
	
	private void updateMenuState(ApplicationPane pane) {
		Deque<MenuItem> menus = new LinkedList<MenuItem>();
		// seed with the menu bar's menus
		menus.addAll(this.menu.getMenus());
		while (menus.size() > 0) {
			MenuItem menu = menus.pop();
			
			// process this item
			Object data = menu.getUserData();
			if (data != null && data instanceof ApplicationAction) {
				ApplicationAction action = (ApplicationAction)data;
				boolean disabled = !pane.isApplicationActionEnabled(action);
				boolean visible = pane.isApplicationActionVisible(action);
				menu.setDisable(disabled);
				menu.setVisible(visible);
			}
			
			// add children
			if (menu instanceof Menu) {
				menus.addAll(((Menu)menu).getItems());
			}
		}
	}

	private Node getApplicationPane(Node focused) {
		while (focused != null) {
			if (focused instanceof ApplicationPane) {
				return focused;
			}
			focused = focused.getParent();
		}
		return null;
	}
	
	@Override
	public void handle(ActionEvent event) {
		Node focused = this.focusOwner.get();
		if (focused == null) {
			focused = this.rootNode;
		}
		Object source = event.getSource();
		if (source != null && source instanceof MenuItem) {
			Object data = ((MenuItem)source).getUserData();
			if (data != null && data instanceof ApplicationAction) {
				ApplicationAction action = (ApplicationAction)data;
				focused.fireEvent(new ApplicationEvent(event.getSource(), event.getTarget(), ApplicationEvent.ALL, action));
			}
		}
	}
	
	private class DefaultApplicationPane implements ApplicationPane {
		public boolean isApplicationActionEnabled(ApplicationAction action) {
	    	switch (action) {
				case ABOUT:
				case EXIT:
				case IMPORT_BIBLES:
				case IMPORT_SLIDES:
				case IMPORT_SONGS:
				case MANAGE_BIBLES:
				case MANAGE_MEDIA:
				case MANAGE_SLIDES:
				case MANAGE_SONGS:
				case NEW_BIBLE:
				case NEW_SLIDE:
				case NEW_SLIDE_SHOW:
				case NEW_SONG:
				case PREFERENCES:
					return true;
				default:
					return false;
			}
		}
		public boolean isApplicationActionVisible(ApplicationAction action) {
			return true;
		}
	}
}
