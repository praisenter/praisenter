package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.SlideStroke;

public class BorderEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<SlideStroke>> {
	private final ObservableSlideRegion<?> component;
	
	@SafeVarargs
	public BorderEditCommand(ObservableSlideRegion<?> component, ValueChangedCommandOperation<SlideStroke> operation, CommandAction<ValueChangedCommandOperation<SlideStroke>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public BorderEditCommand(ObservableSlideRegion<?> component, ValueChangedCommandOperation<SlideStroke> operation, List<CommandAction<ValueChangedCommandOperation<SlideStroke>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setBorder(this.operation.getNewValue());
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
		this.component.setBorder(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setBorder(this.operation.getNewValue());
		super.redo();
	}
}
