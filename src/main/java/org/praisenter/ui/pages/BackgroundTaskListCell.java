package org.praisenter.ui.pages;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.praisenter.async.ReadOnlyBackgroundTask;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;

final class BackgroundTaskListCell extends ListCell<ReadOnlyBackgroundTask> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);
	
	private static final String STATUS_ICON_CLASS = "p-tasklist-status-icon";
	private static final String STATUS_ICON_ERROR_CLASS = "p-tasklist-status-icon-error";
	private static final String STATUS_ICON_PENDING_CLASS = "p-tasklist-status-icon-pending";
	private static final String STATUS_ICON_SUCCESS_CLASS = "p-tasklist-status-icon-success";
	
	private final Node pendingIcon = new Region();
	private final Node errorIcon = new Region();
	private final Node successIcon = new Region();
	
	public BackgroundTaskListCell() {
		pendingIcon.getStyleClass().addAll(STATUS_ICON_CLASS, STATUS_ICON_PENDING_CLASS);
		errorIcon.getStyleClass().addAll(STATUS_ICON_CLASS, STATUS_ICON_ERROR_CLASS);
		successIcon.getStyleClass().addAll(STATUS_ICON_CLASS, STATUS_ICON_SUCCESS_CLASS);
	}
	
    @Override
    public void updateItem(ReadOnlyBackgroundTask item, boolean empty) {
        super.updateItem(item, empty);
        
        graphicProperty().unbind();
    	textProperty().unbind();
    	setTooltip(null);
    	setGraphic(null);
    	setText(null);
        
        if (item != null) {
        	graphicProperty().bind(Bindings.createObjectBinding(() -> {
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
        	
        	textProperty().bind(Bindings.createObjectBinding(() -> {
        		String message = FORMATTER.format(item.getStartTime()) + " " + item.getMessage();
        		return message;
        	}, item.nameProperty(), item.messageProperty(), item.completeProperty()));
        }
    }
}
