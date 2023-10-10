package org.praisenter.ui.pages;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.ui.Icons;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

final class BackgroundTaskListCell extends ListCell<ReadOnlyBackgroundTask> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT);

	private final Node pendingIcon = Icons.getIcon(Icons.PENDING);
	private final Node errorIcon = Icons.getIcon(Icons.ERROR);
	private final Node successIcon = Icons.getIcon(Icons.SUCCESS);
	
	public BackgroundTaskListCell() {}
	
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
