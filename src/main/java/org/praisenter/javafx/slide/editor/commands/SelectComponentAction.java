package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.CommandOperation;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;

public final class SelectComponentAction<T extends CommandOperation> implements CommandAction<T> {
	private final ObjectProperty<ObservableSlideRegion<?>> property;
	private final ObservableSlideRegion<?> before;
	private final ObservableSlideRegion<?> after;
	
	public SelectComponentAction(ObjectProperty<ObservableSlideRegion<?>> property, ObservableSlideRegion<?> select) {
		this.property = property;
		this.before = property.get();
		this.after = select;
	}
	
	@Override
	public void undo(T operation) {
		this.property.set(this.before);
	}
	
	@Override
	public void redo(T operation) {
		this.property.set(this.after);
	}
}
