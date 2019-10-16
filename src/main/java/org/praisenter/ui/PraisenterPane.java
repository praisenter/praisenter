package org.praisenter.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.BackgroundTask;
import org.praisenter.data.Persistable;
import org.praisenter.data.configuration.Configuration;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.slide.SlideDataPane;
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
		
		MenuItem reindex = new MenuItem("reindex");
		reindex.setOnAction((e) -> {
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
					Alert alert = Alerts.exception(this.context.stage, null, null, null, ex);
					alert.show();
				});
				return null;
			});
		});
		
		Menu mnuFile = new Menu("file", null, reindex, new MenuItem("preferences", Glyphs.MENU_PREFERENCES.duplicate()));
		Menu mnuHelp = new Menu("help", null, new MenuItem("logs"), new MenuItem("about", Glyphs.MENU_ABOUT.duplicate()));
		MenuBar mainMenu = new MenuBar(mnuFile, mnuHelp);
		
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
		
		SlideDataPane sdp = new SlideDataPane(context);
		
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
