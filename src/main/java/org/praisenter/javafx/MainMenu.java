package org.praisenter.javafx;

import java.util.Deque;
import java.util.LinkedList;

import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// FIXME still issues with focus and current application pane (mainly with breadcrumb bar and progress button, they steal focus away)
class MainMenu extends VBox implements EventHandler<ActionEvent> {

	/** The font-awesome glyph-font pack */
	private static final GlyphFont FONT_AWESOME	= GlyphFontRegistry.font("FontAwesome");
	
	private final MainPane mainPane;
	private final MenuBar menu;
	private final HBox toolbar;

	private final BooleanProperty windowFocused = new SimpleBooleanProperty();
	private final ObjectProperty<Node> focusOwner = new SimpleObjectProperty<Node>();
	private final ObjectProperty<Node> appPane = new SimpleObjectProperty<Node>();
	
	public MainMenu(MainPane mainPane) {
		this.mainPane = mainPane;
		
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
		MenuItem fImportMedia = createMenuItem(ApplicationAction.IMPORT_MEDIA);
		MenuItem fImportSlides = createMenuItem(ApplicationAction.IMPORT_SLIDES);
		MenuItem fImportSongs = createMenuItem(ApplicationAction.IMPORT_SONGS);
		MenuItem fImportBibles = createMenuItem(ApplicationAction.IMPORT_BIBLES);
		fImport.getItems().addAll(fImportMedia, fImportSlides, fImportSongs, fImportBibles);
		
		MenuItem fExport = createMenuItem(ApplicationAction.EXPORT);
		
		MenuItem fSave = createMenuItem(ApplicationAction.SAVE);
		MenuItem fSaveAs = createMenuItem(ApplicationAction.SAVE_AS);
		MenuItem fSetup = createMenuItem(ApplicationAction.PREFERENCES);
		MenuItem fExit = createMenuItem(ApplicationAction.EXIT);
		file.getItems().addAll(fNew, new SeparatorMenuItem(), fSave, fSaveAs, new SeparatorMenuItem(), fImport, fExport, new SeparatorMenuItem(), fSetup, new SeparatorMenuItem(), fExit);
		
		// Edit
		MenuItem fOpen = createMenuItem(ApplicationAction.OPEN);
		MenuItem fCopy = createMenuItem(ApplicationAction.COPY);
		MenuItem fCut = createMenuItem(ApplicationAction.CUT);
		MenuItem fPaste = createMenuItem(ApplicationAction.PASTE);
		MenuItem fRename = createMenuItem(ApplicationAction.RENAME);
		MenuItem fDelete = createMenuItem(ApplicationAction.DELETE);
		MenuItem fSelectAll = createMenuItem(ApplicationAction.SELECT_ALL);
		MenuItem fSelectNone = createMenuItem(ApplicationAction.SELECT_NONE);
		MenuItem fSelectInvert = createMenuItem(ApplicationAction.SELECT_INVERT);
		edit.getItems().addAll(fOpen, new SeparatorMenuItem(), fCopy, fCut, fPaste, new SeparatorMenuItem(), fRename, fDelete, new SeparatorMenuItem(), fSelectAll, fSelectNone, fSelectInvert);
		
		// Media
		MenuItem mManage = createMenuItem(ApplicationAction.MANAGE_MEDIA);
		media.getItems().addAll(mManage);
		
		// Songs
		// ----

		// Slides
		MenuItem slManage = createMenuItem(ApplicationAction.MANAGE_SLIDES);
		slides.getItems().addAll(slManage);
		
		// Bibles
		MenuItem blManage = createMenuItem(ApplicationAction.MANAGE_BIBLES);
		bibles.getItems().addAll(blManage);
		
		// Help
		MenuItem hAbout = createMenuItem(ApplicationAction.ABOUT);
		MenuItem hLogs = createMenuItem(ApplicationAction.LOGS);
		help.getItems().addAll(hLogs, hAbout);
		
		// TOOLBAR
		
		this.toolbar = new HBox();
		
		// EVENTS
		
		// auto bind when the scene changes
		this.sceneProperty().addListener((obs, ov, nv) -> {
			focusOwner.unbind();
			windowFocused.unbind();
			if (nv != null) {
				focusOwner.bind(nv.focusOwnerProperty());
				windowFocused.bind(nv.getWindow().focusedProperty());
			}
		});
		
		// call methods when focus owner changes
		this.focusOwner.addListener((obs, ov, nv) -> {
			Node appPane = getClosestApplicationPane(nv);
			this.appPane.set(appPane);
		});
		
		this.appPane.addListener((obs, ov, nv) -> {
			if (ov != null) {
				ov.removeEventHandler(ApplicationPaneEvent.STATE_CHANGED, MainMenu.this::handleStateChanged);
			}
			
			if (nv == null) {
				updateMenuState(this.mainPane);
			} else {
				ApplicationPane pane = (ApplicationPane)nv;
				// recursively go through the menu updating disabled and visibility states
				updateMenuState(pane);
				nv.addEventHandler(ApplicationPaneEvent.STATE_CHANGED, MainMenu.this::handleStateChanged);
			}
		});
		
		// we need to re-evaluate the menu state when the focus between menus changes
		// primarily for re-evaluating the content in the clipboard.
		// this allows us to disable an item if the user goes to a different app and copies
		// something that isn't the expected type
		this.windowFocused.addListener((obs, ov, nv) -> {
			Node focused = this.appPane.get();
			if (focused == null) {
				updateMenuState(this.mainPane);
			} else {
				ApplicationPane pane = (ApplicationPane)focused;
				// recursively go through the menu updating disabled and visibility states
				updateMenuState(pane);
			}
		});
		
		// LAYOUT
		
		this.getChildren().addAll(this.menu, this.toolbar);
		
		// INITIALIZATION
		
		updateMenuState(this.mainPane);
	}
	
	private MenuItem createMenuItem(ApplicationAction action) {
		MenuItem item = action.toMenuItem();
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
				// an action is disabled or hidden as long as both the root
				// and the currently focused application pane don't handle it
				boolean disabled = !(this.mainPane.isApplicationActionEnabled(action) || pane.isApplicationActionEnabled(action));
				boolean visible = this.mainPane.isApplicationActionVisible(action) || pane.isApplicationActionVisible(action);
				menu.setDisable(disabled);
				menu.setVisible(visible);
			}
			
			// add children
			if (menu instanceof Menu) {
				menus.addAll(((Menu)menu).getItems());
			}
		}
	}

	private Node getClosestApplicationPane(Node focused) {
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
		Node focused = this.appPane.get();
		if (focused == null) {
			focused = this.mainPane;
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
}
