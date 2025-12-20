package org.praisenter.ui.pages;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.ui.Icons;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

final class BackgroundTaskListCell extends ListCell<ReadOnlyBackgroundTask> {
	private static final String TASK_LIST_CELL_CLASS = "p-task-list-cell";
	private static final String TASK_LIST_CELL_ICON_CLASS = "p-task-list-cell-icon";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);

	private final Node pendingIcon = Icons.getIcon(Icons.PENDING);
	private final Node errorIcon = Icons.getIcon(Icons.ERROR);
	private final Node successIcon = Icons.getIcon(Icons.SUCCESS);
	
	private final ObjectProperty<Node> iconProperty;
	private final Label name;
	private final ProgressBar progress;
	
	public BackgroundTaskListCell() {
		this.getStyleClass().add(TASK_LIST_CELL_CLASS);
		
		this.iconProperty = new SimpleObjectProperty<>();
		this.name = new Label();
		this.progress = new ProgressBar();
		this.progress.setMaxWidth(Double.MAX_VALUE);
		this.progress.setMaxHeight(2);
		
		// build the basic layout
		BorderPane layout = new BorderPane();
		BorderPane left = new BorderPane();
		left.getStyleClass().add(TASK_LIST_CELL_ICON_CLASS);
		left.setPadding(new Insets(5));
		left.centerProperty().bind(iconProperty);
		layout.setLeft(left);
		
		VBox middle = new VBox(5);
		middle.getChildren().add(name);
		middle.getChildren().add(progress);
		layout.setCenter(middle);
		
		this.setGraphic(layout);
	}
	
    @Override
    public void updateItem(ReadOnlyBackgroundTask item, boolean empty) {
        super.updateItem(item, empty);
        
    	setTooltip(null);
    	
    	this.name.textProperty().unbind();
    	this.name.setText(null);
    	
    	this.iconProperty.unbind();
    	this.iconProperty.set(null);
    	
    	this.progress.progressProperty().unbind();
    	this.progress.disableProperty().unbind();
    	this.progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    	this.progress.setVisible(false);
    	
        if (item != null && !empty) {
        	this.progress.setVisible(true);
        	
        	this.name.textProperty().bind(Bindings.createObjectBinding(() -> {
        		String message = FORMATTER.format(item.getStartTime()) + " " + item.getMessage();
        		return message;
        	}, item.nameProperty(), item.messageProperty(), item.completeProperty()));
        	
        	this.iconProperty.bind(Bindings.createObjectBinding(() -> {
        		Node graphic = null;
            	if (!item.isComplete()) {
            		graphic = pendingIcon;
            	} else if (item.isSuccess()) {
            		graphic = successIcon;
            	} else {
            		graphic = errorIcon;
            	}
            	return graphic;
        	}, item.completeProperty()));
        	
        	this.progress.progressProperty().bind(Bindings.createDoubleBinding(() -> {
        		boolean complete = item.isComplete();
        		if (!complete) {
        			return ProgressBar.INDETERMINATE_PROGRESS;
        		}
        		return 1.0;
        	}, item.completeProperty()));
        	this.progress.disableProperty().bind(item.completeProperty());
        }
    }
}
