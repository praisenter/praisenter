package org.praisenter.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.BackgroundTask;
import org.praisenter.data.Persistable;
import org.praisenter.data.configuration.Configuration;
import org.praisenter.data.media.Media;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.song.Song;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.display.DisplayController;
import org.praisenter.ui.display.DisplaysController;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

final class PraisenterPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	
	private final ObservableList<Persistable> items;
	
	public PraisenterPane(GlobalContext context) {
		this.context = context;

		// menu
		
		MenuItem mnuReindex = new MenuItem(Translations.get("menu.file.reindex"));
		mnuReindex.setOnAction((e) -> {
			BackgroundTask task = new BackgroundTask();
			task.setName(Translations.get("task.reindex"));
			task.setMessage(Translations.get("task.reindex"));
			this.context.addBackgroundTask(task);
			
			this.context.dataManager.reindex().thenApply(AsyncHelper.onJavaFXThreadAndWait(() -> {
				task.setProgress(1);
			})).exceptionally((ex) -> {
				LOGGER.error("Failed to reindex the lucene search index: " + ex.getMessage(), ex);
				task.setException(ex);
				Platform.runLater(() -> {
					Alert alert = Alerts.exception(this.context.stage, ex);
					alert.show();
				});
				return null;
			});
		});
		
		MenuItem mnuSettings = new MenuItem(Translations.get("menu.file.settings"), Glyphs.MENU_PREFERENCES.duplicate());
		// TODO action for settings
		
		MenuItem mnuLogs = new MenuItem(Translations.get("menu.help.logs"));
		mnuLogs.setOnAction(e -> {
			// open the log directory
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
		
		MenuItem mnuAbout = new MenuItem(Translations.get("menu.help.about"), Glyphs.MENU_ABOUT.duplicate());
		// TODO action for about
		
		Menu mnuFile = new Menu(Translations.get("menu.file"), null, mnuReindex, mnuSettings);
		Menu mnuHelp = new Menu(Translations.get("menu.help"), null, mnuLogs, mnuAbout);
		MenuBar mainMenu = new MenuBar(mnuFile, mnuHelp);
		mainMenu.setUseSystemMenuBar(true);
		
		// main content area
		
		ActionBar actionBar = new ActionBar(context);
		DocumentsPane documentsPane = new DocumentsPane(context);
		
		this.items = new FilteredList<>(context.getDataManager().getItemsUnmodifiable(), (i) -> {
			if (i instanceof Configuration) {
				return false;
			}
			return true;
		});
		
		LibraryList itemListing = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.values());
		Bindings.bindContent(itemListing.getItems(), this.items);
		
		
//		FilteredList<Persistable> media = new FilteredList<>(context.getDataManager().getItemsUnmodifiable(), (i) -> {
//			if (i instanceof Media) {
//				return true;
//			}
//			return false;
//		});
//		
//		LibraryList mediaList = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.AUDIO, LibraryListType.VIDEO, LibraryListType.IMAGE);
//		Bindings.bindContent(mediaList.getItems(), media);
//		
//		FilteredList<Persistable> slides = new FilteredList<>(context.getDataManager().getItemsUnmodifiable(), (i) -> {
//			if (i instanceof Slide) {
//				return true;
//			}
//			return false;
//		});
//		
//		LibraryList slideList = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.SLIDE);
//		Bindings.bindContent(slideList.getItems(), slides);
//		
//		FilteredList<Persistable> songs = new FilteredList<>(context.getDataManager().getItemsUnmodifiable(), (i) -> {
//			if (i instanceof Song) {
//				return true;
//			}
//			return false;
//		});
//		
//		LibraryList songList = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.SONG);
//		Bindings.bindContent(songList.getItems(), songs);
//		
//		TabPane libraryTabs = new TabPane();
//		libraryTabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
//		libraryTabs.getTabs().add(new Tab("Media", mediaList));
//		libraryTabs.getTabs().add(new Tab("Slides", slideList));
//		libraryTabs.getTabs().add(new Tab("Songs", songList));
		
		DisplaysController dc = new DisplaysController(context);
		
//		SplitPane split = new SplitPane(dep, libraryTabs);
//		split.setDividerPositions(0.75);
//		split.setOrientation(Orientation.VERTICAL);
		
//		BorderPane bp = new BorderPane();
//		
////		this.setCenter(split);
//		bp.setCenter(documentsPane);
//		bp.setLeft(actionBar);
//		bp.setBottom(libraryTabs);
		
		SplitPane splLibrary = new SplitPane(documentsPane, itemListing);
		splLibrary.setDividerPosition(0, 0.8);
		splLibrary.setOrientation(Orientation.VERTICAL);
		
		BorderPane bp = new BorderPane();
		bp.setLeft(actionBar);
		bp.setCenter(splLibrary);
		
		TabPane tabs = new TabPane();
		tabs.setSide(Side.LEFT);
		Tab tab1 = new Tab("Present", dc);
		tab1.setClosable(false);
		Tab tab2 = new Tab("Manage", bp);
		tab2.setClosable(false);
		tabs.getTabs().addAll(tab1, tab2);
		
		this.setTop(mainMenu);
		this.setCenter(tabs);
		
		
		VBox.setVgrow(documentsPane, Priority.ALWAYS);

		// bottom
		
		ProgressBar progress = new ProgressBar();
		progress.visibleProperty().bind(context.backgroundTaskExecutingProperty());
		Label lblCurrentTask = new Label();
		lblCurrentTask.textProperty().bind(context.backgroundTaskNameProperty());
		
		this.setBottom(new HBox(5, progress, lblCurrentTask));
		
		// drag-drop handlers
		
		this.setOnDragOver(this::dragOver);
		this.setOnDragDropped(this::dragDropped);
		this.setOnDragDone(this::dragDone);
	}

	private void dragOver(DragEvent e) {
		if (e.getDragboard().hasFiles()) {
			e.acceptTransferModes(TransferMode.COPY);
		}
	}
	
	private void dragDropped(DragEvent e) {
		Dragboard db = e.getDragboard();
		if (db.hasFiles()) {
			this.context.importFiles(db.getFiles());
			e.setDropCompleted(true);
		}
	}
	
	private void dragDone(DragEvent e) {
		// nothing to do
	}
}
