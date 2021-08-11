package org.praisenter.ui;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.praisenter.async.ReadOnlyBackgroundTask;
import org.praisenter.ui.translations.Translations;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

final class BackgroundTaskListCell extends ListCell<ReadOnlyBackgroundTask> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
	
	private final Node pendingIcon = Glyphs.PENDING.duplicate();
	private final Node errorIcon = Glyphs.ERROR.duplicate();
	private final Node successIcon = Glyphs.SUCCESS.duplicate();
	
	private final Tooltip tooltip = new Tooltip();
	
    @Override
    public void updateItem(ReadOnlyBackgroundTask item, boolean empty) {
        super.updateItem(item, empty);
        
        graphicProperty().unbind();
    	textProperty().unbind();
    	tooltip.textProperty().unbind();
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
        		String message = FORMATTER.format(item.getStartTime()) + " ";
        		if (!item.isComplete()) {
        			message += item.getName() + ": " + item.getMessage();
        		} else {
        			message += item.getName();
        		}
        		return message;
        	}, item.nameProperty(), item.messageProperty(), item.completeProperty()));
        	
        	tooltip.textProperty().bind(Bindings.createObjectBinding(() -> {
        		String message = "";
        		if (!item.isComplete()) {
        			message = item.getMessage();
        		} else if (!item.isSuccess()) {
        			Throwable t = item.getException();
        			message = t.toString();
        		} else {
        			message = Translations.get("task.success");
        		}
        		return message;
        	}, item.nameProperty(), item.messageProperty(), item.completeProperty()));
        	
        	setTooltip(tooltip);
        }
    }
}
