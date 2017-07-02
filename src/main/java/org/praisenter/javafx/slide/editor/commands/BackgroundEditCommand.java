package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.SlidePaint;

public class BackgroundEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<SlidePaint>> {
	private final ObservableSlideRegion<?> component;
	
	@SafeVarargs
	public BackgroundEditCommand(ObservableSlideRegion<?> component, ValueChangedCommandOperation<SlidePaint> operation, CommandAction<ValueChangedCommandOperation<SlidePaint>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public BackgroundEditCommand(ObservableSlideRegion<?> component, ValueChangedCommandOperation<SlidePaint> operation, List<CommandAction<ValueChangedCommandOperation<SlidePaint>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setBackground(this.operation.getNewValue());
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
		this.component.setBackground(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setBackground(this.operation.getNewValue());
		super.redo();
	}
}
