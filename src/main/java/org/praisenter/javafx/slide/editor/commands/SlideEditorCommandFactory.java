package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.operation.CommandOperation;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;

public final class SlideEditorCommandFactory {
	private SlideEditorCommandFactory() {}
	
	public static final <T extends CommandOperation> SelectComponentAction<T> select(ObjectProperty<ObservableSlideRegion<?>> property, ObservableSlideRegion<?> select) {
		return new SelectComponentAction<T>(property, select);
	}
}
