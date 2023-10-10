package org.praisenter.ui.pages;

import java.io.PrintWriter;
import java.io.StringWriter;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public final class TaskListPage extends BorderPane {
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
		taskList.getStyleClass().addAll(TASKLIST_PAGE_LIST_CLASS, Tweaks.EDGE_TO_EDGE);//, Styles.STRIPED);
		
		Label lblTasks = new Label(Translations.get("task.page"));
		lblTasks.getStyleClass().addAll(Styles.TITLE_3, TASKLIST_PAGE_TITLE_CLASS);
		
		Label lblTaskName = new Label(Translations.get("task.name"));
		TextField txtTaskName = new TextField();
		txtTaskName.setEditable(false);
		txtTaskName.setPromptText(Translations.get("task.name"));
		
		Label lblTaskStatus = new Label(Translations.get("task.status"));
		TextField txtTaskStatus = new TextField();
		txtTaskStatus.setEditable(false);
		txtTaskStatus.setPromptText(Translations.get("task.status"));
		
		Label lblTaskDetail = new Label(Translations.get("task.detail"));
		TextArea txtTaskDetail = new TextArea();
		txtTaskDetail.setPromptText(Translations.get("task.detail"));
		txtTaskDetail.setMinHeight(100);
		txtTaskDetail.setPrefHeight(200);
		txtTaskDetail.setEditable(false);
		txtTaskDetail.setWrapText(true);
		
		Label lblTaskStack = new Label(Translations.get("task.stack"));
		TextArea txtTaskStack = new TextArea();
		txtTaskStack.setPromptText(Translations.get("task.stack"));
		txtTaskStack.setEditable(false);
		
		taskList.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			txtTaskName.setText(null);
			txtTaskStatus.setText(null);
			txtTaskDetail.setText(null);
			txtTaskStack.setText(null);
			txtTaskStatus.pseudoClassStateChanged(Styles.STATE_DANGER, false);
			txtTaskStatus.pseudoClassStateChanged(Styles.STATE_SUCCESS, false);
			txtTaskStatus.pseudoClassStateChanged(Styles.STATE_WARNING, false);
			
			if (nv != null) {
				if (!nv.isComplete()) {
					txtTaskStatus.setText(Translations.get("task.status.pending"));
					txtTaskName.setText(nv.getName());
					txtTaskDetail.setText(nv.getMessage());
					txtTaskStatus.pseudoClassStateChanged(Styles.STATE_WARNING, true);
				} else if (nv.isSuccess()) { 
					txtTaskStatus.setText(Translations.get("task.status.success"));
					txtTaskName.setText(nv.getName());
					txtTaskDetail.setText(nv.getMessage());
					txtTaskStatus.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);
				} else {
					Throwable t = nv.getException();
					if (t != null) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						t.printStackTrace(pw);
						txtTaskStack.setText(sw.toString());
						txtTaskDetail.setText(t.getMessage());
					} else {
						txtTaskDetail.setText(nv.getMessage());
					}
					txtTaskStatus.setText(Translations.get("task.status.failed"));
					txtTaskName.setText(nv.getName());
					txtTaskStatus.pseudoClassStateChanged(Styles.STATE_DANGER, true);
				}
			}
		});
		
		// layout
		
		VBox right = new VBox(
				lblTaskName, txtTaskName,
				lblTaskStatus, txtTaskStatus,
				lblTaskDetail, txtTaskDetail,
				lblTaskStack, txtTaskStack);
		right.getStyleClass().add(TASKLIST_PAGE_FIELDS_CLASS);
		
//		VBox top = new VBox(
//				lblTasks, new Separator());
		
		SplitPane split = new SplitPane(taskList, right);
		split.getStyleClass().add(TASKLIST_PAGE_SPLIT_CLASS);
		split.setDividerPositions(0.60);
		SplitPane.setResizableWithParent(right, false);
		
		this.setTop(lblTasks);
		this.setCenter(split);
		this.getStyleClass().addAll(TASKLIST_PAGE_CLASS);
	}
}
