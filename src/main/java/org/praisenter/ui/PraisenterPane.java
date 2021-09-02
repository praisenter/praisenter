package org.praisenter.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.BackgroundTask;
import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.data.Persistable;
import org.praisenter.ui.controls.Alerts;
import org.praisenter.ui.display.DisplaysController;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.translations.Translations;
import org.praisenter.ui.upgrade.UpgradeChecker;
import org.praisenter.utility.RuntimeProperties;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;

final class PraisenterPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	
	private final ObservableList<Persistable> items;
	
	private final ObjectProperty<Version> latestVersion;
	
	private final ObservableList<ReadOnlyBackgroundTask> sortedTasks;
	
	public PraisenterPane(GlobalContext context) {
		this.context = context;

		this.latestVersion = new SimpleObjectProperty<>();
		
		// menu
		
		MenuItem mnuReindex = new MenuItem(Translations.get("menu.file.reindex"));
		mnuReindex.setOnAction((e) -> {
			BackgroundTask task = new BackgroundTask();
			task.setName(Translations.get("task.reindex"));
			task.setMessage(Translations.get("task.reindex"));
			this.context.addBackgroundTask(task);
			
			this.context.workspaceManager.reindex().thenApply(AsyncHelper.onJavaFXThreadAndWait(() -> {
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
		
		Menu mnuSwitchWorkspace = new Menu(Translations.get("menu.file.switchWorkspace"));
		
		for (Path path : context.getWorkspaceManager().getOtherWorkspaces()) {
			MenuItem mnuSelectWorkspace = new MenuItem(path.toAbsolutePath().toString());
			mnuSelectWorkspace.setOnAction(e -> {
				StartupHandler sh = new StartupHandler();
				try {
					sh.restart(context, path);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			mnuSwitchWorkspace.getItems().add(mnuSelectWorkspace);
		}
		
		MenuItem mnuNewWorkspace = new MenuItem(Translations.get("menu.file.newWorkspace"));
		mnuNewWorkspace.setOnAction(e -> {
			StartupHandler sh = new StartupHandler();
			try {
				sh.restart(context);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		mnuSwitchWorkspace.getItems().add(mnuNewWorkspace);
		
		MenuItem mnuApplicationLogs = new MenuItem(Translations.get("menu.help.startupLogs"));
		mnuApplicationLogs.setOnAction(e -> {
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
		MenuItem mnuWorkspaceLogs = new MenuItem(Translations.get("menu.help.workspaceLogs"));
		mnuWorkspaceLogs.setOnAction(e -> {
			// open the log directory
			if (Desktop.isDesktopSupported()) {
			    try {
					Desktop.getDesktop().open(context.getWorkspaceManager().getWorkspacePathResolver().getLogsPath().toFile());
				} catch (IOException ex) {
					LOGGER.error("Unable to open logs directory due to: " + ex.getMessage(), ex);
				}
			} else {
				LOGGER.warn("Desktop is not supported. Failed to open log path.");
			}
		});
		MenuItem mnuUpdate = new MenuItem(Translations.get("menu.help.update.check"));
		mnuUpdate.setOnAction(e -> {
			UpgradeChecker uc = new UpgradeChecker();
			uc.getLatestReleaseVersion().thenAccept(version -> {
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
					Alert alert = Alerts.info(
							context.stage,
							Modality.WINDOW_MODAL, 
							Translations.get("menu.help.update.check.title"), 
							Translations.get("menu.help.update.check.header", formatter.format(LocalDateTime.now())), 
							msg);
					alert.show();
				});
			}).exceptionally(t -> {
				LOGGER.error("Failed to check for new version: " + t.getMessage(), t);
				Platform.runLater(() -> {
					Alert alert = Alerts.exception(context.stage, t);
					alert.show();
				});
				return null;
			});
		});
		
		MenuItem mnuAbout = new MenuItem(Translations.get("menu.help.about"), Glyphs.MENU_ABOUT.duplicate());
		
		Menu mnuFile = new Menu(Translations.get("menu.file"), null, mnuReindex, mnuSettings, mnuSwitchWorkspace);
		Menu mnuHelp = new Menu(Translations.get("menu.help"), null, mnuApplicationLogs, mnuWorkspaceLogs, mnuUpdate, mnuAbout);
		MenuBar mainMenu = new MenuBar(mnuFile, mnuHelp);
		mainMenu.setUseSystemMenuBar(true);
		
		// main content area
		
		ActionBar actionBar = new ActionBar(context);
		DocumentsPane documentsPane = new DocumentsPane(context);
		
		this.items = new FilteredList<>(context.getWorkspaceManager().getItemsUnmodifiable(), (i) -> {
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
		
		DisplaysController displayControllers = new DisplaysController(context);
		
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
		
		BorderPane libraryManager = new BorderPane();
		libraryManager.setLeft(actionBar);
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
		tabs.setSide(Side.LEFT);
		Tab tab1 = new Tab(Translations.get("area.present"), displayControllers);
		tab1.setClosable(false);
		Tab tab2 = new Tab(Translations.get("area.manage"), libraryManager);
		tab2.setClosable(false);
		Tab tab3 = new Tab(Translations.get("area.tasks"), tasks);
		tab3.setClosable(false);
		
		tabs.getTabs().addAll(tab1, tab2, tab3);
		
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
			Version latest = this.latestVersion.get();
			if (latest != null && latest.isGreaterThan(Version.VERSION)) {
				return "A new version (" + latest + ") is available";
			}
			return null;
		}, this.latestVersion));
		lblUpdateAvailable.graphicProperty().bind(Bindings.createObjectBinding(() -> {
			Version latest = this.latestVersion.get();
			if (latest != null && latest.isGreaterThan(Version.VERSION)) {
				return Glyphs.INFO.duplicate();
			}
			return null;
		}, this.latestVersion));
		
		Label lblVersion = new Label("Praisenter: " + Version.STRING);
		lblVersion.setPadding(new Insets(0, 5, 0, 0));
		
		Label lblJfxVersion = new Label("JFX: " + System.getProperties().get("javafx.runtime.version"));
		lblJfxVersion.setPadding(new Insets(0, 5, 0, 0));

		Label lblLuceneVersion = new Label("Lucene: " + org.apache.lucene.util.Version.LATEST);
		lblLuceneVersion.setPadding(new Insets(0, 5, 0, 0));
		
		Label lblJavaVersion = new Label("Java: " + RuntimeProperties.JAVA_VERSION);
		lblJavaVersion.setPadding(new Insets(0, 5, 0, 0));
		
		BorderPane bottom = new BorderPane();
		bottom.setLeft(new HBox(5, 
				lblCompletedTasks, new Separator(Orientation.VERTICAL),
				progress, lblCurrentTask));
		bottom.setRight(new HBox(5, 
				lblUpdateAvailable, new Separator(Orientation.VERTICAL), 
				lblJavaVersion, new Separator(Orientation.VERTICAL), 
				lblJfxVersion, new Separator(Orientation.VERTICAL), 
				lblLuceneVersion, new Separator(Orientation.VERTICAL), 
				lblVersion));
		
		UpgradeChecker checker = new UpgradeChecker();
		checker.getLatestReleaseVersion().thenAccept(nv -> {
			if (nv.isGreaterThan(Version.VERSION)) {
				Platform.runLater(() -> {
					this.latestVersion.set(nv);
				});
			}
		});
		
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
			this.context.importFiles(db.getFiles());
			e.setDropCompleted(true);
		}
	}
	
	private void dragDone(DragEvent e) {
		// nothing to do
	}
}
