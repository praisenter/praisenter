package org.praisenter.ui;

import org.praisenter.async.AsyncHelper;
import org.praisenter.ui.controls.Dialogs;
import org.praisenter.ui.pages.EditorPage;
import org.praisenter.ui.pages.LibraryPage;
import org.praisenter.ui.pages.PresentPage;
import org.praisenter.ui.pages.SettingsPage;
import org.praisenter.ui.pages.TaskListPage;
import org.praisenter.ui.translations.Translations;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

final class PraisenterPane extends BorderPane {
	// TODO background striping or something to split the screen controllers apart visually
	// TODO flicker on update of tags on library list
	
	private static final String LEFT_NAVIGATION_CLASS = "p-left-navigation";
	private static final String LEFT_NAVIGATION_ICON_CLASS = "p-left-navigation-icon";
	private static final String LEFT_NAVIGATION_ICON_PRESENT_CLASS = "p-left-navigation-present-icon";
	private static final String LEFT_NAVIGATION_ICON_LIBRARY_CLASS = "p-left-navigation-library-icon";
	private static final String LEFT_NAVIGATION_ICON_EDITOR_CLASS = "p-left-navigation-editor-icon";
	private static final String LEFT_NAVIGATION_ICON_SETTINGS_CLASS = "p-left-navigation-settings-icon";
	private static final String LEFT_NAVIGATION_ICON_TASKS_CLASS = "p-left-navigation-tasks-icon";

	private final GlobalContext context;
	
	public PraisenterPane(GlobalContext context) {
		this.context = context;
		
		// menu
		MainMenu mainMenu = new MainMenu(context);
		FooterPane footer = new FooterPane(context);
		
		// content area
		TabPane body = new TabPane();
		body.getStyleClass().add(LEFT_NAVIGATION_CLASS);
		body.setSide(Side.LEFT);
		body.setTabMaxHeight(Double.MAX_VALUE);
		body.setTabMaxWidth(Double.MAX_VALUE);
		
		Tab presentTab = new Tab(null, new PresentPage(context));
		presentTab.setGraphic(this.createTabGraphic(LEFT_NAVIGATION_ICON_PRESENT_CLASS));
		presentTab.setTooltip(this.createTabTooltip(Translations.get("area.present")));
		presentTab.setClosable(false);
		
		Tab libraryTab = new Tab(null, new LibraryPage(context));
		libraryTab.setGraphic(this.createTabGraphic(LEFT_NAVIGATION_ICON_LIBRARY_CLASS));
		libraryTab.setTooltip(this.createTabTooltip(Translations.get("area.manage")));
		libraryTab.setClosable(false);
		
		Tab editorTab = new Tab(null, new EditorPage(context));
		editorTab.setGraphic(this.createTabGraphic(LEFT_NAVIGATION_ICON_EDITOR_CLASS));
		editorTab.setTooltip(this.createTabTooltip(Translations.get("area.editor")));
		editorTab.setClosable(false);

		Tab settingsTab = new Tab(null, new SettingsPage(context));
		settingsTab.setGraphic(this.createTabGraphic(LEFT_NAVIGATION_ICON_SETTINGS_CLASS));
		settingsTab.setTooltip(this.createTabTooltip(Translations.get("area.settings")));
		settingsTab.setClosable(false);
		
		Tab taskHistoryTab = new Tab(null, new TaskListPage(context));
		taskHistoryTab.setGraphic(this.createTabGraphic(LEFT_NAVIGATION_ICON_TASKS_CLASS));
		taskHistoryTab.setTooltip(this.createTabTooltip(Translations.get("area.tasks")));
		taskHistoryTab.setClosable(false);
		
		body.getTabs().addAll(presentTab, libraryTab, editorTab, settingsTab, taskHistoryTab);
		
		context.currentPageProperty().addListener((obs, ov, nv) -> {
			// get the current page index
			int targetIndex = 0;
			if (nv != null) {
				targetIndex = nv.getPageIndex();
			}
			
			// is it different than what we have selected already?
			SingleSelectionModel<Tab> sm = body.getSelectionModel();
			if (targetIndex == sm.getSelectedIndex()) {
				return;
			}
			
			// it's different, so set the value
			sm.select(targetIndex);
		});
		
		body.getSelectionModel().selectedIndexProperty().addListener((obs, ov, nv) -> {
			// get the index selected by the user
			int targetIndex = 0;
			if (nv != null) {
				targetIndex = nv.intValue();
			}
			
			// get the page for the index
			Page page = Page.getPageForIndex(targetIndex);
			if (page == null) {
				page = Page.getPageForIndex(0);
			}
			
			// is it different than what is currently selected?
			if (page == context.getCurrentPage()) {
				return;
			}
			
			// it's different, so set it
			context.setCurrentPage(page);
		});
		
		// layout
		
		this.setTop(mainMenu);
		this.setCenter(body);
		this.setBottom(footer);
		
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
			this.context.importFiles(db.getFiles()).thenAccept((items) -> {
				// nothing else to do with the items
			}).exceptionallyCompose(AsyncHelper.onJavaFXThreadAndWait((t) -> {
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
		return tooltip;
	}
	
	private Node createTabGraphic(String iconCssClass) {
		Region icon = new Region();
		icon.getStyleClass().addAll(LEFT_NAVIGATION_ICON_CLASS, iconCssClass);
		return icon;
	}
}
