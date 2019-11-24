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
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.display.DisplayController;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
		
		ActionBar ab = new ActionBar(context);
		DocumentsPane dep = new DocumentsPane(context);
		
		this.items = new FilteredList<>(context.getDataManager().getItemsUnmodifiable(), (i) -> {
			if (i instanceof Configuration) {
				return false;
			}
			return true;
		});
		
		LibraryList itemListing = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.values());
		Bindings.bindContent(itemListing.getItems(), this.items);
		
		DisplayController sdp = new DisplayController(context);
		
		SplitPane split = new SplitPane(dep, itemListing);
		split.setDividerPositions(0.75);
		split.setOrientation(Orientation.VERTICAL);
		
		this.setTop(mainMenu);
		this.setCenter(split);
		this.setLeft(ab);
		this.setRight(sdp);
		
		VBox.setVgrow(dep, Priority.ALWAYS);

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
