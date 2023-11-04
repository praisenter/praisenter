package org.praisenter.ui;

import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

final class FooterPane extends BorderPane {
	private static final String FOOTER_CLASS = "p-footer";
	
	public FooterPane(GlobalContext context) {
		Label lblCompletedTasks = new Label();
		lblCompletedTasks.textProperty().bind(Bindings.createStringBinding(() -> {
			return Translations.get("task.complete.count", String.valueOf(context.getBackgroundTasksUnmodifiable().stream().filter(t -> t.isComplete()).count()));
		}, context.getBackgroundTasksUnmodifiable()));
		
		ProgressBar progress = new ProgressBar();
		progress.visibleProperty().bind(context.backgroundTaskExecutingProperty());
		Label lblCurrentTask = new Label();
		lblCurrentTask.setPrefWidth(200);
		lblCurrentTask.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(lblCurrentTask, Priority.ALWAYS);
		lblCurrentTask.textProperty().bind(context.backgroundTaskNameProperty());
		
		Label lblWorkspacePath = new Label(context.getWorkspaceManager().getWorkspacePathResolver().getBasePath().toAbsolutePath().toString());
		
		HBox left = new HBox( 
				lblCompletedTasks, 
				new Separator(Orientation.VERTICAL),
				progress, 
				new Separator(Orientation.VERTICAL), 
				lblCurrentTask);
		left.setAlignment(Pos.CENTER_LEFT);
		
		HBox right = new HBox(
				lblWorkspacePath);
		right.setAlignment(Pos.CENTER_RIGHT);
		
		this.getStyleClass().add(FOOTER_CLASS);
		this.setLeft(left);
		this.setRight(right);
	}
}
