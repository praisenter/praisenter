package org.praisenter.ui.pages;

import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.translations.Translations;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public final class TaskListPage extends BorderPane implements Page {
	private static final String TASKLIST_PAGE_CLASS = "p-tasklist-page";
	private static final String TASKLIST_PAGE_TITLE_CLASS = "p-tasklist-title";
	private static final String TASKLIST_PAGE_SPLIT_CLASS = "p-tasklist-split";
	private static final String TASKLIST_PAGE_LIST_CLASS = "p-tasklist-page-list";
	private static final String TASKLIST_PAGE_FIELDS_CLASS = "p-tasklist-page-fields";
	
	private final ObservableList<ReadOnlyBackgroundTask> sortedTasks;
	
	public TaskListPage(GlobalContext context) {
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
		taskList.getStyleClass().addAll(TASKLIST_PAGE_LIST_CLASS, Tweaks.EDGE_TO_EDGE, Styles.STRIPED);
		
		Label lblTasks = new Label(Translations.get("task.page"));
		lblTasks.getStyleClass().addAll(Styles.TITLE_3, TASKLIST_PAGE_TITLE_CLASS);
		
		BackgroundTaskDetailPane taskDetail = new BackgroundTaskDetailPane();
		
		VBox right = new VBox(taskDetail);
		right.getStyleClass().add(TASKLIST_PAGE_FIELDS_CLASS);
		right.visibleProperty().bind(taskList.getSelectionModel().selectedItemProperty().isNotNull());
		right.managedProperty().bind(right.visibleProperty());
		
		taskList.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			taskDetail.setBackgroundTask(nv);
		});
		
		// layout
		
		Label lblSelectTask = new Label(Translations.get("task.select"));
		lblSelectTask.visibleProperty().bind(taskList.getSelectionModel().selectedItemProperty().isNull());
		lblSelectTask.managedProperty().bind(lblSelectTask.visibleProperty());
		lblSelectTask.setWrapText(true);
		
		StackPane stackRight = new StackPane(lblSelectTask, right);
		
		SplitPane split = new SplitPane(taskList, stackRight);
		split.getStyleClass().add(TASKLIST_PAGE_SPLIT_CLASS);
		split.setDividerPositions(0.70);
		SplitPane.setResizableWithParent(stackRight, false);
		
		this.setTop(lblTasks);
		this.setCenter(split);
		this.getStyleClass().addAll(TASKLIST_PAGE_CLASS);
	}
	
	@Override
	public void setDefaultFocus() {
		// no-op
	}
}
