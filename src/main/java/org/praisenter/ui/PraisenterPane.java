package org.praisenter.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Version;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.data.Persistable;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.display.DisplaysController;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

final class PraisenterPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final ObservableList<Persistable> items;
	private final ObservableList<ReadOnlyBackgroundTask> sortedTasks;
	
	public PraisenterPane(GlobalContext context) {
		this.context = context;
		
		// main content area
		
		MainMenu mainMenu = new MainMenu(context);
		ActionBar actionBar = new ActionBar(context);
		DocumentsPane documentsPane = new DocumentsPane(context);
		
		this.items = new FilteredList<>(context.getWorkspaceManager().getItemsUnmodifiable(), (i) -> {
			return true;
		});
		
		LibraryList itemListing = new LibraryList(context, Orientation.HORIZONTAL, LibraryListType.values());
		Bindings.bindContent(itemListing.getItems(), this.items);
		
		DisplaysController displayControllers = new DisplaysController(context);
		
		SplitPane splLibrary = new SplitPane(documentsPane, itemListing);
		splLibrary.setDividerPosition(0, 0.7);
		splLibrary.setOrientation(Orientation.VERTICAL);
		
		BorderPane libraryManager = new BorderPane();
		libraryManager.setTop(actionBar);
		libraryManager.setCenter(splLibrary);
		
		BorderPane tasks = new BorderPane();
		this.sortedTasks = context.getBackgroundTasksUnmodifiable().sorted();
		ListView<ReadOnlyBackgroundTask> taskList = new ListView<>(this.sortedTasks);
		taskList.setCellFactory(new Callback<ListView<ReadOnlyBackgroundTask>, 
	            ListCell<ReadOnlyBackgroundTask>>() {
            @Override 
            public ListCell<ReadOnlyBackgroundTask> call(ListView<ReadOnlyBackgroundTask> list) {
                return new BackgroundTaskListCell();
            }
        });
		taskList.setPlaceholder(new Label(Translations.get("task.empty")));
		tasks.setCenter(taskList);
		
		TabPane tabs = new TabPane();
		tabs.getStyleClass().add("p-tab-navigation");
		tabs.setSide(Side.LEFT);
		tabs.setTabMaxHeight(Double.MAX_VALUE);
		tabs.setTabMaxWidth(Double.MAX_VALUE);
		
		Tab presentTab = new Tab(null, displayControllers);
		presentTab.setGraphic(this.createTabGraphic("p-icon-present"));
		presentTab.setTooltip(new Tooltip(Translations.get("area.present")));
		presentTab.setClosable(false);
		
		Tab libraryTab = new Tab(null, libraryManager);
		libraryTab.setGraphic(this.createTabGraphic("p-icon-library"));
		libraryTab.setTooltip(new Tooltip(Translations.get("area.manage")));
		libraryTab.setClosable(false);
		
		Tab taskHistoryTab = new Tab(null, tasks);
		taskHistoryTab.setGraphic(this.createTabGraphic("p-icon-tasks"));
		taskHistoryTab.setTooltip(new Tooltip(Translations.get("area.tasks")));
		taskHistoryTab.setClosable(false);
		
		Tab settingsTab = new Tab(null, new SettingsPane(context));
		settingsTab.setGraphic(this.createTabGraphic("p-icon-settings"));
		settingsTab.setTooltip(new Tooltip(Translations.get("area.settings")));
		settingsTab.setClosable(false);
		
		tabs.getTabs().addAll(presentTab, libraryTab, settingsTab, taskHistoryTab);
		
		context.currentDocumentProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				// then make sure we are on the library tab
				tabs.getSelectionModel().select(libraryTab);
			}
		});
		
		this.setTop(mainMenu);
		this.setCenter(tabs);
		
		VBox.setVgrow(documentsPane, Priority.ALWAYS);

		// bottom
		
		Label lblCompletedTasks = new Label();
		lblCompletedTasks.setPadding(new Insets(0, 0, 0, 5));
		lblCompletedTasks.textProperty().bind(Bindings.createStringBinding(() -> {
			return Translations.get("task.complete.count", String.valueOf(context.getBackgroundTasksUnmodifiable().stream().filter(t -> t.isComplete()).count()));
		}, context.getBackgroundTasksUnmodifiable()));
		
		ProgressBar progress = new ProgressBar();
		progress.visibleProperty().bind(context.backgroundTaskExecutingProperty());
		Label lblCurrentTask = new Label();
		lblCurrentTask.textProperty().bind(context.backgroundTaskNameProperty());
		
		Label lblUpdateAvailable = new Label();
		lblUpdateAvailable.setGraphicTextGap(5);
		lblUpdateAvailable.setPadding(new Insets(0, 5, 0, 0));
		lblUpdateAvailable.textProperty().bind(Bindings.createObjectBinding(() -> {
			Version latest = this.context.getLatestVersion();
			if (latest != null && latest.isGreaterThan(Version.VERSION)) {
				return "A new version (" + latest + ") is available";
			}
			return null;
		}, this.context.latestVersionProperty()));
		lblUpdateAvailable.graphicProperty().bind(Bindings.createObjectBinding(() -> {
			Version latest = this.context.getLatestVersion();
			if (latest != null && latest.isGreaterThan(Version.VERSION)) {
				return Glyphs.INFO.duplicate();
			}
			return null;
		}, this.context.latestVersionProperty()));
		
		Label lblVersion = new Label("Praisenter: " + Version.STRING);
		lblVersion.setPadding(new Insets(0, 5, 0, 0));
//		
//		Label lblJfxVersion = new Label("JFX: " + System.getProperties().get("javafx.runtime.version"));
//		lblJfxVersion.setPadding(new Insets(0, 5, 0, 0));
//
//		Label lblLuceneVersion = new Label("Lucene: " + org.apache.lucene.util.Version.LATEST);
//		lblLuceneVersion.setPadding(new Insets(0, 5, 0, 0));
//		
//		Label lblJavaVersion = new Label("Java: " + RuntimeProperties.JAVA_VERSION);
//		lblJavaVersion.setPadding(new Insets(0, 5, 0, 0));
//		
		Label lblWorkspacePath = new Label(context.getWorkspaceManager().getWorkspacePathResolver().getBasePath().toAbsolutePath().toString());
		lblWorkspacePath.setPadding(new Insets(0, 5, 0, 0));
		
		BorderPane bottom = new BorderPane();
		bottom.setLeft(new HBox(5, 
				lblCompletedTasks, new Separator(Orientation.VERTICAL),
				progress, lblCurrentTask));
		bottom.setRight(new HBox(5, 
				lblUpdateAvailable, new Separator(Orientation.VERTICAL), 
				lblWorkspacePath, new Separator(Orientation.VERTICAL), 
				lblVersion));
		
		this.setBottom(bottom);
		
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
			this.context.importFiles(db.getFiles()).exceptionallyCompose(AsyncHelper.onJavaFXThreadAndWait((t) -> {
				Platform.runLater(() -> {
					Alert alert = Alerts.exception(this.context.stage, t);
					alert.show();
				});
			}));
			e.setDropCompleted(true);
		}
	}
	
	private void dragDone(DragEvent e) {
		// nothing to do
	}
	
	private Node createTabGraphic(String iconCssClass) {
		Region icon = new Region();
		icon.getStyleClass().addAll("p-area-icon", iconCssClass);
		return icon;
	}
}
