package org.praisenter.ui;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.async.AsyncHelper;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.controls.WindowHelper;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.upgrade.UpgradeChecker;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

final class MainMenu extends MenuBar {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String MAIN_MENU_CLASS = "p-main-menu";
	
	private final GlobalContext context;
	
	private Stage aboutDialog;

	public MainMenu(GlobalContext context) {
		super();
		this.getStyleClass().add(MAIN_MENU_CLASS);
		
		this.context = context;
		
		this.setUseSystemMenuBar(true);
		
		Menu workspacesMenu = new Menu(Translations.get("menu.workspace.switch"));
		for (Path path : context.getWorkspaceManager().getOtherWorkspaces()) {
			MenuItem mnuSelectWorkspace = new MenuItem(path.toAbsolutePath().toString());
			mnuSelectWorkspace.setOnAction(e -> {
				this.switchWorkspace(path);
			});
			workspacesMenu.getItems().add(mnuSelectWorkspace);
		}
		
		MenuItem mnuNewWorkspace = new MenuItem(Translations.get("menu.workspace.new"));
		mnuNewWorkspace.setOnAction(e -> {
			this.switchWorkspace(null);
		});
		workspacesMenu.getItems().add(mnuNewWorkspace);
		
		Menu mnuFile = new Menu(
				Translations.get("menu.file"), 
				null, 
//				new Menu(
//						Translations.get("menu.new"), 
//						null,
						this.createMenuItem(Action.NEW_SLIDE),
						this.createMenuItem(Action.NEW_BIBLE),
						this.createMenuItem(Action.NEW_SONG),
						new SeparatorMenuItem(),
						new Menu(
								Translations.get("menu.bible"),
								null,
								this.createMenuItem(Action.NEW_BOOK),
								this.createMenuItem(Action.NEW_CHAPTER),
								this.createMenuItem(Action.NEW_VERSE)),
						new Menu(
								Translations.get("menu.song"),
								null,
								this.createMenuItem(Action.NEW_LYRICS),
								this.createMenuItem(Action.NEW_AUTHOR),
								this.createMenuItem(Action.NEW_SONGBOOK),
								this.createMenuItem(Action.NEW_SECTION)),
						new Menu(
								Translations.get("menu.slide"),
								null,
								this.createMenuItem(Action.NEW_SLIDE_TEXT_COMPONENT),
								this.createMenuItem(Action.NEW_SLIDE_PLACEHOLDER_COMPONENT),
								this.createMenuItem(Action.NEW_SLIDE_DATETIME_COMPONENT),
								this.createMenuItem(Action.NEW_SLIDE_COUNTDOWN_COMPONENT),
								this.createMenuItem(Action.NEW_SLIDE_MEDIA_COMPONENT)),
//						),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.SAVE),
				this.createMenuItem(Action.SAVE_ALL),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.REINDEX),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.IMPORT),
				this.createMenuItem(Action.EXPORT),
				new SeparatorMenuItem(),
				workspacesMenu,
				this.createMenuItem(Action.RESTART),
				this.createMenuItem(Action.EXIT));
		
		Menu mnuEdit = new Menu(
				Translations.get("menu.edit"), 
				null, 
				this.createMenuItem(Action.UNDO),
				this.createMenuItem(Action.REDO),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.COPY),
				this.createMenuItem(Action.CUT),
				this.createMenuItem(Action.PASTE),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.SELECT_ALL),
				this.createMenuItem(Action.SELECT_INVERT),
				this.createMenuItem(Action.SELECT_NONE),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.BULK_EDIT_BEGIN),
				this.createMenuItem(Action.RENAME),
				this.createMenuItem(Action.DELETE),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.RENUMBER),
				this.createMenuItem(Action.REORDER),
				new SeparatorMenuItem(),
				this.createToggleMenuItem(Action.SLIDE_COMPONENT_SNAP_TO_GRID, context.snapToGridEnabledProperty()),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_BACK),
				this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_DOWN),
				this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_UP),
				this.createMenuItem(Action.SLIDE_COMPONENT_MOVE_FRONT));
		
		// Window
		Menu mnuWindow = new Menu(
				Translations.get("menu.window"),
				null,
				this.createMenuItem(Action.INCREASE_FONT_SIZE),
				this.createMenuItem(Action.DECREASE_FONT_SIZE),
				this.createMenuItem(Action.RESET_FONT_SIZE));
		
		// Help
		//	App logs
		//	workspace logs
		//	Help contents
		//	Attribution
		//		https://www.ffmpeg.org/ GPL
		//		https://linearicons.com/free CC
		//	check for update
		//	about
		Menu mnuHelp = new Menu(
				Translations.get("menu.help"),
				null,
				this.createMenuItem(Action.APPLICATION_LOGS),
				this.createMenuItem(Action.WORKSPACE_LOGS),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.DOWNLOAD_UNBOUND_BIBLES),
				this.createMenuItem(Action.DOWNLOAD_ZEFANIA_BIBLES),
				this.createMenuItem(Action.DOWNLOAD_OPENSONG_BIBLES),
				new SeparatorMenuItem(),
				this.createMenuItem(Action.CHECK_FOR_UPDATE),
				this.createMenuItem(Action.ABOUT));
		
		this.getMenus().addAll(mnuFile, mnuEdit, mnuWindow, mnuHelp);
		
		// on windows when focus returns after the user alt-tabbing to another application
		// the alt status stays in place and if the user types anything it steals what they
		// type if there's a matching mnemonic
		// https://stackoverflow.com/questions/73383089/javafx-menu-bar-steals-focus
		// JAVABUG (L) 10/12/23 [workaround] https://bugs.openjdk.org/browse/JDK-8238731
		
		// NOTE: this was removed due to it causing ColorPicker to crash (it would force focus loss
		// on the color picker removing the popup before the custom color picker had disposed of it
		// causing it to attempt to dispose it again.  I tested this again on Java/JavaFX 21 and
		// did see the issue of the alt state being retained
		
//		if (RuntimeProperties.IS_WINDOWS_OS) {
//			context.windowFocusedProperty().addListener((obs, ov, nv) -> {
//				if (!nv) {
//					this.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false));
//				}
//			});
//		}
	}

	private MenuItem createMenuItem(Action action) {
		MenuItem item = new MenuItem();
		item.setOnAction(e -> this.executeAction(action));
		this.setMenuItemProperties(item, action);
		return item;
	}
	
	private MenuItem createToggleMenuItem(Action action, BooleanProperty target) {
		CheckMenuItem item = new CheckMenuItem();
		item.selectedProperty().bindBidirectional(target);
		this.setMenuItemProperties(item, action);
		return item;
	}
	
	private void setMenuItemProperties(MenuItem item, Action action) {
		item.setUserData(action);
		item.disableProperty().bind(this.context.getActionEnabledProperty(action).not());
		// for the Menus, always show everything
//		item.visibleProperty().bind(this.context.getActionVisibleProperty(action));
		
		Supplier<Node> graphicSupplier = action.getGraphicSupplier();
		if (graphicSupplier != null) {
			Node graphic = graphicSupplier.get();
			item.setGraphic(graphic);
		}
		
		KeyCombination accelerator = action.getAccelerator();
		if (accelerator != null) {
			item.setAccelerator(accelerator);
			this.context.getScene().getAccelerators().put(accelerator, () -> {
				this.executeAction(action);
			});
		}
		
		String messageKey = action.getMessageKey();
		if (messageKey != null) {
			String text = Translations.get(messageKey);
			item.setText(text);
		}
	}
	
	private void executeAction(Action action) {
		switch (action) {
			case IMPORT:
				this.promptImport();
				return;
			case APPLICATION_LOGS:
				this.viewApplicationLogs();
				return;
			case WORKSPACE_LOGS:
				this.viewWorkspaceLogs();
				return;
			case RESTART:
				this.restart();
				return;
			case EXIT:
				this.exit();
				return;
			case CHECK_FOR_UPDATE:
				this.checkForUpdate();
				return;
			case DOWNLOAD_ZEFANIA_BIBLES:
				this.openUrl("https://sourceforge.net/projects/zefania-sharp/files/Bibles/");
				return;
			case DOWNLOAD_UNBOUND_BIBLES: 
				this.openUrl("https://github.com/wnbittle/unbound-bible-archive");
				return;
			case DOWNLOAD_OPENSONG_BIBLES:
				this.openUrl("http://www.opensong.org/home/download");
				return;
			case ABOUT:
				this.showAbout();
				return;
			default:
				break;
		}
		
		this.context.executeAction(action).exceptionallyCompose(AsyncHelper.onJavaFXThreadAndWait((t) -> {
			Platform.runLater(() -> {
				Alert alert = Dialogs.exception(this.context.stage, t);
				alert.show();
			});
		}));
	}
	
	private void promptImport() {
		FileChooser fc = new FileChooser();
		fc.setTitle(Translations.get("action.import"));
		List<File> files = fc.showOpenMultipleDialog(this.context.stage);
		this.context.importFiles(files).exceptionallyCompose(AsyncHelper.onJavaFXThreadAndWait((t) -> {
			Platform.runLater(() -> {
				Alert alert = Dialogs.exception(this.context.stage, t);
				alert.show();
			});
		}));
	}
	
	private void switchWorkspace(Path path) {
		LifecycleHandler lifecycleHandler = new LifecycleHandler();
		lifecycleHandler.restart(this.context, path);
	}
	
	private void restart() {
		LifecycleHandler lifecycleHandler = new LifecycleHandler();
		lifecycleHandler.restart(this.context, this.context.workspaceManager.getWorkspacePathResolver().getBasePath());
	}
	
	private void exit() {
		LifecycleHandler lifecycleHandler = new LifecycleHandler();
		lifecycleHandler.stop(this.context);
	}
	
	private void viewApplicationLogs() {
		// Desktop must be used from the AWT EventQueue
		// https://stackoverflow.com/a/65863422
    	EventQueue.invokeLater(() -> {
    		if (Desktop.isDesktopSupported()) {
	    		try {
	    			Desktop.getDesktop().open(Paths.get(Constants.LOGS_ABSOLUTE_PATH).toFile());
				} catch (IOException ex) {
					LOGGER.error("Unable to open logs directory due to: " + ex.getMessage(), ex);
				}
    		} else {
    			LOGGER.warn("Desktop is not supported. Failed to open log path.");
    		}
    	});
	}
	
	private void viewWorkspaceLogs() {
		// Desktop must be used from the AWT EventQueue
		// https://stackoverflow.com/a/65863422
		EventQueue.invokeLater(() -> {
			if (Desktop.isDesktopSupported()) {
			    try {
					Desktop.getDesktop().open(this.context.getWorkspaceManager().getWorkspacePathResolver().getLogsPath().toFile());
				} catch (IOException ex) {
					LOGGER.error("Unable to open logs directory due to: " + ex.getMessage(), ex);
				}
			} else {
				LOGGER.warn("Desktop is not supported. Failed to open log path.");
			}
		});
	}
	
	private void checkForUpdate() {
		UpgradeChecker uc = new UpgradeChecker();
		uc.getLatestReleaseVersion().thenAccept(version -> {
			// set the latest version in the context
			Platform.runLater(() -> {
				this.context.setLatestVersion(version);
			});
			
			String message = null;
			if (version == null) {
				// we ran into an issue checking for the latest version
				// go to some URL to check the version manually
				message = Translations.get("menu.help.update.check.error");
			} else if (version.isGreaterThan(Version.VERSION)) {
				// there's an update
				message = Translations.get("menu.help.update.check.updateAvailable", version.toString(), Version.STRING);
			} else {
				// no update available
				message = Translations.get("menu.help.update.check.noUpdateAvailable", Version.STRING);
			}
			final String msg = message;
			Platform.runLater(() -> {
				DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
				Alert alert = Dialogs.info(
						this.context.stage,
						Modality.WINDOW_MODAL, 
						Translations.get("menu.help.update.check.title"), 
						Translations.get("menu.help.update.check.header", formatter.format(LocalDateTime.now())), 
						msg);
				alert.show();
			});
		}).exceptionally(t -> {
			LOGGER.error("Failed to check for new version: " + t.getMessage(), t);
			Platform.runLater(() -> {
				Alert alert = Dialogs.exception(this.context.stage, t);
				alert.show();
			});
			return null;
		});
	}
	
	private void openUrl(String url) {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
			    Desktop.getDesktop().browse(new URI(url));
			} catch (Exception e) {
				LOGGER.error("Failed to open default browser for URL: " + url, e);
			}
		}
	}
	
	private void showAbout() {
		if (this.aboutDialog == null) {
			AboutPane pneAbout = new AboutPane();
			
			Window owner = this.getScene().getWindow();
			this.aboutDialog = new Stage();
			this.aboutDialog.initOwner(owner);
			this.aboutDialog.setTitle(Translations.get("about.title"));
			this.aboutDialog.initModality(Modality.WINDOW_MODAL);
			this.aboutDialog.initStyle(StageStyle.DECORATED);
			this.aboutDialog.setWidth(600);
			this.aboutDialog.setHeight(375);
			this.aboutDialog.setResizable(true);
			this.aboutDialog.setScene(WindowHelper.createSceneWithOwnerCss(pneAbout, owner));
			this.context.attachZoomHandler(this.aboutDialog.getScene());
			this.context.attachAccentHandler(this.aboutDialog.getScene());
			WindowHelper.setIcons(this.aboutDialog);
		}
		
		this.aboutDialog.show();
		WindowHelper.centerOnParent(this.getScene().getWindow(), this.aboutDialog);
	}
}
