package org.praisenter.javafx;

import java.util.Deque;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

class MainMenu extends VBox implements EventHandler<ActionEvent> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final MainPane mainPane;
	private final MenuBar menu;
	private final HBox toolbar;

	private final BooleanProperty windowFocused = new SimpleBooleanProperty();
	private final ObjectProperty<Node> focusOwner = new SimpleObjectProperty<Node>();
	private final ObjectProperty<Node> appPane = new SimpleObjectProperty<Node>();
	
	public MainMenu(MainPane mainPane) {
		this.mainPane = mainPane;
		this.appPane.set(mainPane);
		
		EventHandler<ApplicationPaneEvent> paneStateChanged = new EventHandler<ApplicationPaneEvent>() {
			@Override
			public void handle(ApplicationPaneEvent event) {
				ApplicationPane pane = event.getApplicationPane();
				if (pane != null) {
					updateMenuState(focusOwner.get(), pane, event.getReason());
				}
			}
		};
		ChangeListener<String> textSelectionChanged = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				updateMenuState("Text Selection Changed");
			}
		};
		
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
		MenuItem fNewSlide = createMenuItem(ApplicationAction.NEW_SLIDE);
		MenuItem fNewSlideShow = new MenuItem("Slide Show");
		MenuItem fNewSong = new MenuItem("Song");
		Menu fNewBibleRoot = new Menu("Bible");
		MenuItem fNewBible = createMenuItem(ApplicationAction.NEW_BIBLE);
		MenuItem fNewBook = createMenuItem(ApplicationAction.NEW_BOOK);
		MenuItem fNewChapter = createMenuItem(ApplicationAction.NEW_CHAPTER);
		MenuItem fNewVerse = createMenuItem(ApplicationAction.NEW_VERSE);
		fNewBibleRoot.getItems().addAll(fNewBible, new SeparatorMenuItem(), fNewBook, fNewChapter, fNewVerse);
		fNew.getItems().addAll(fNewSlide, fNewSlideShow, fNewSong, fNewBibleRoot);
		
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
		MenuItem fReorder = createMenuItem(ApplicationAction.REORDER);
		MenuItem fRenumber = createMenuItem(ApplicationAction.RENUMBER);
		MenuItem fSelectAll = createMenuItem(ApplicationAction.SELECT_ALL);
		MenuItem fSelectNone = createMenuItem(ApplicationAction.SELECT_NONE);
		MenuItem fSelectInvert = createMenuItem(ApplicationAction.SELECT_INVERT);
		edit.getItems().addAll(fOpen, new SeparatorMenuItem(), fCopy, fCut, fPaste, new SeparatorMenuItem(), fReorder, fRenumber, new SeparatorMenuItem(), fRename, fDelete, new SeparatorMenuItem(), fSelectAll, fSelectNone, fSelectInvert);
		
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
		MenuItem blReindex = createMenuItem(ApplicationAction.REINDEX_BIBLES);
		bibles.getItems().addAll(blManage, new SeparatorMenuItem(), blReindex);
		
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
			LOGGER.debug("Focus changed from {} to {}", ov, nv);
			
			Node appPane = getClosestApplicationPane(nv);
			this.appPane.set(appPane);
			
			// attach/detach selection change event handler if text input
			if (ov != null && ov instanceof TextInputControl) {
				((TextInputControl)ov).selectedTextProperty().removeListener(textSelectionChanged);
			}
			if (nv != null && nv instanceof TextInputControl) {
				((TextInputControl)nv).selectedTextProperty().addListener(textSelectionChanged);
			}
			
			updateMenuState("Focus Changed");
		});
		
		
		this.appPane.addListener((obs, ov, nv) -> {
			// attach/detach state changed event handler
			if (ov != null) {
				ov.removeEventHandler(ApplicationPaneEvent.STATE_CHANGED, paneStateChanged);
			}
			if (nv != null) {
				nv.addEventHandler(ApplicationPaneEvent.STATE_CHANGED, paneStateChanged);
			}
		});
		
		// we need to re-evaluate the menu state when the focus between menus changes
		// primarily for re-evaluating the content in the clipboard.
		// this allows us to disable an item if the user goes to a different app and copies
		// something that isn't the expected type
		this.windowFocused.addListener((obs, ov, nv) -> {
			updateMenuState("Window Focus");
		});
		
		// LAYOUT
		
		this.getChildren().addAll(this.menu, this.toolbar);
		
		// INITIALIZATION
		
		updateMenuState("Initialization");
	}
	
	private MenuItem createMenuItem(ApplicationAction action) {
		MenuItem item = action.toMenuItem();
		item.setOnAction(this);
		return item;
	}
	
	private void updateMenuState(String reason) {
		Node node = this.appPane.get();
		if (node == null) {
			node = this.mainPane;
		}
		if (node != null && node instanceof ApplicationPane) {
			this.updateMenuState(this.focusOwner.get(), (ApplicationPane)node, reason);
		}
	}
	
	private void updateMenuState(Node focused, ApplicationPane pane, String reason) {
		LOGGER.debug("Menu state updating for focused: {} application-pane: {} due to {}", focused, pane, reason);
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
				boolean disabled = !(this.mainPane.isApplicationActionEnabled(action) || this.isEnabledForFocused(focused, action) || pane.isApplicationActionEnabled(action));
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

	/**
	 * Some actions should be available to normal Java FX controls, namely the text input
	 * controls for copy/cut/paste/delete/select all.
	 * @param focused the true focus owner
	 * @param action the action
	 * @return boolean
	 */
	private boolean isEnabledForFocused(Node focused, ApplicationAction action) {
		switch (action) {
			case CUT:
			case DELETE:
			case COPY:
				if (focused instanceof TextInputControl) {
					String selection = ((TextInputControl)focused).getSelectedText();
					return selection != null && !selection.isEmpty();
				}
				break;
			case SELECT_ALL:
				if (focused instanceof TextInputControl) {
					return true;
				}
				break;
			case PASTE:
				Clipboard cb = Clipboard.getSystemClipboard();
				if (focused instanceof TextInputControl) {
					return cb.hasContent(DataFormat.PLAIN_TEXT);
				}
				break;
			default:
				break;
		}
		return false;
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
		Node focusOwner = this.focusOwner.get();
		Node focused = this.appPane.get();
		if (focused == null) {
			focused = this.mainPane;
		}
		Object source = event.getSource();
		if (source != null && source instanceof MenuItem) {
			Object data = ((MenuItem)source).getUserData();
			if (data != null && data instanceof ApplicationAction) {
				ApplicationAction action = (ApplicationAction)data;
				// check for text input handling
				if ((ApplicationAction.COPY == action ||
					 ApplicationAction.CUT == action ||
					 ApplicationAction.PASTE == action ||
					 ApplicationAction.DELETE == action ||
					 ApplicationAction.SELECT_ALL == action) && focusOwner != null && focusOwner instanceof TextInputControl) {
					LOGGER.debug("Node {} is TextInputControl. Bypassing default delegation.", focusOwner);
					// allow it to pass through and don't fire the default
					// application event
					TextInputControl control = (TextInputControl)focusOwner;
					if (ApplicationAction.COPY == action) {
						control.copy();
					} else if (ApplicationAction.CUT == action) {
						// JAVABUG 11/09/16 LOW [workaround] for java.lang.StringIndexOutOfBoundsException when only using control.cut();
						control.copy();
						IndexRange selection = control.getSelection();
						control.deselect();
						control.deleteText(selection);
					} else if (ApplicationAction.PASTE == action) {
						control.paste();
					} else if (ApplicationAction.DELETE == action) {
						// JAVABUG 11/09/16 LOW [workaround] workaround for java.lang.StringIndexOutOfBoundsException when only using control.deleteText(control.getSelection());
						IndexRange selection = control.getSelection();
						control.deselect();
						control.deleteText(selection);
					} else if (ApplicationAction.SELECT_ALL == action) {
						control.selectAll();
					}
				} else {
					LOGGER.debug("Delegating {} to {}.", action, focused);
					focused.fireEvent(new ApplicationEvent(event.getSource(), event.getTarget(), ApplicationEvent.ALL, action));
				}
			}
		}
		event.consume();
	}
}
