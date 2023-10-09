package org.praisenter.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Version;
import org.praisenter.async.AsyncHelper;
import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.data.Persistable;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.display.DisplaysController;
import org.praisenter.ui.document.DocumentsPane;
import org.praisenter.ui.library.LibraryList;
import org.praisenter.ui.library.LibraryListType;
import org.praisenter.ui.pages.SettingsPage;
import org.praisenter.ui.pages.TaskListPage;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.theme.Tweaks;
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
import javafx.util.Duration;

final class PraisenterPane extends BorderPane {
	private static final Logger LOGGER = LogManager.getLogger();
	
	// TODO rename to left navigation
	private static final String TAB_NAVIGATION_CLASS = "p-tab-navigation";
	private static final String TAB_NAVIGATION_ICON_CLASS = "p-tab-navigation-icon";
	// TODO fix sizing on the icons to match aspect ratio of the source SVG
	private static final String TAB_NAVIGATION_ICON_PRESENT_CLASS = "p-tab-navigation-present-icon";
	private static final String TAB_NAVIGATION_ICON_LIBRARY_CLASS = "p-tab-navigation-library-icon";
	private static final String TAB_NAVIGATION_ICON_EDITOR_CLASS = "p-tab-navigation-editor-icon";
	private static final String TAB_NAVIGATION_ICON_SETTINGS_CLASS = "p-tab-navigation-settings-icon";
	private static final String TAB_NAVIGATION_ICON_TASKS_CLASS = "p-tab-navigation-tasks-icon";

	private final GlobalContext context;
	private final ObservableList<Persistable> items;
	
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
		
		BorderPane editor = new BorderPane();
		editor.setTop(actionBar);
		editor.setCenter(documentsPane);
		// TODO show a message to create new or edit an item in the library
		
		TabPane tabs = new TabPane();
		tabs.getStyleClass().add(TAB_NAVIGATION_CLASS);
		tabs.setSide(Side.LEFT);
		tabs.setTabMaxHeight(Double.MAX_VALUE);
		tabs.setTabMaxWidth(Double.MAX_VALUE);
		
		Tab presentTab = new Tab(null, displayControllers);
		presentTab.setGraphic(this.createTabGraphic(TAB_NAVIGATION_ICON_PRESENT_CLASS));
		presentTab.setTooltip(this.createTabTooltip(Translations.get("area.present")));
		presentTab.setClosable(false);
		
		Tab libraryTab = new Tab(null, itemListing);
		libraryTab.setGraphic(this.createTabGraphic(TAB_NAVIGATION_ICON_LIBRARY_CLASS));
		libraryTab.setTooltip(this.createTabTooltip(Translations.get("area.manage")));
		libraryTab.setClosable(false);
		
		Tab editorTab = new Tab(null, editor);
		editorTab.setGraphic(this.createTabGraphic(TAB_NAVIGATION_ICON_EDITOR_CLASS));
		editorTab.setTooltip(this.createTabTooltip(Translations.get("area.editor")));
		editorTab.setClosable(false);

		Tab settingsTab = new Tab(null, new SettingsPage(context));
		settingsTab.setGraphic(this.createTabGraphic(TAB_NAVIGATION_ICON_SETTINGS_CLASS));
		settingsTab.setTooltip(this.createTabTooltip(Translations.get("area.settings")));
		settingsTab.setClosable(false);
		
		Tab taskHistoryTab = new Tab(null, new TaskListPage(context));
		taskHistoryTab.setGraphic(this.createTabGraphic(TAB_NAVIGATION_ICON_TASKS_CLASS));
		taskHistoryTab.setTooltip(this.createTabTooltip(Translations.get("area.tasks")));
		taskHistoryTab.setClosable(false);
		
		tabs.getTabs().addAll(presentTab, libraryTab, editorTab, settingsTab, taskHistoryTab);
		
		context.currentDocumentProperty().addListener((obs, ov, nv) -> {
			if (nv != null) {
				// then make sure we are on the library tab
				tabs.getSelectionModel().select(editorTab);
			}
		});
		
		this.setTop(mainMenu);
		this.setCenter(tabs);
		
		VBox.setVgrow(documentsPane, Priority.ALWAYS);

		// bottom
		
		this.setBottom(new FooterPane(context));
		
		// drag-drop handlers
		
		this.setOnDragOver(this::dragOver);
		this.setOnDragDropped(this::dragDropped);
		this.setOnDragDone(this::dragDone);
	}

	private void dragOver(DragEvent e) {
		// only accept drag n drop from outside of the application
		if (e.getGestureSource() == null && e.getDragboard().hasFiles()) {
			e.acceptTransferModes(TransferMode.COPY);
		}
	}
	
	private void dragDropped(DragEvent e) {
		Dragboard db = e.getDragboard();
		if (e.getGestureSource() == null && db.hasFiles()) {
			this.context.importFiles(db.getFiles()).exceptionallyCompose(AsyncHelper.onJavaFXThreadAndWait((t) -> {
				Platform.runLater(() -> {
					Alert alert = Dialogs.exception(this.context.stage, t);
					alert.show();
				});
			}));
			e.setDropCompleted(true);
		}
	}
	
	private void dragDone(DragEvent e) {
		// nothing to do
	}
	
	private Tooltip createTabTooltip(String text) {
		Tooltip tooltip = new Tooltip(text);
		tooltip.setShowDelay(new Duration(300));
		tooltip.setForceIntegerRenderScale(true);
		return tooltip;
	}
	
	private Node createTabGraphic(String iconCssClass) {
		Region icon = new Region();
		icon.getStyleClass().addAll(TAB_NAVIGATION_ICON_CLASS, iconCssClass);
		return icon;
	}
}
