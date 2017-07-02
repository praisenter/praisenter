package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.Rectangle;

public class SizeEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Rectangle>> {
	private final ObservableSlideRegion<?> component;
	
	@SafeVarargs
	public SizeEditCommand(ObservableSlideRegion<?> component, ValueChangedCommandOperation<Rectangle> operation, CommandAction<ValueChangedCommandOperation<Rectangle>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public SizeEditCommand(ObservableSlideRegion<?> component, ValueChangedCommandOperation<Rectangle> operation, List<CommandAction<ValueChangedCommandOperation<Rectangle>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setWidth(this.operation.getNewValue().getWidth());
		this.component.setHeight(this.operation.getNewValue().getHeight());
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.component != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.component.setWidth(this.operation.getOldValue().getWidth());
		this.component.setHeight(this.operation.getOldValue().getHeight());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setWidth(this.operation.getNewValue().getWidth());
		this.component.setHeight(this.operation.getNewValue().getHeight());
		super.redo();
	}
}
