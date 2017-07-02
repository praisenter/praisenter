package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;

public class TextWrappingEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Boolean>> {
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public TextWrappingEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<Boolean> operation, CommandAction<ValueChangedCommandOperation<Boolean>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public TextWrappingEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<Boolean> operation, List<CommandAction<ValueChangedCommandOperation<Boolean>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setTextWrapping(this.operation.getNewValue());
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
		this.component.setTextWrapping(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setTextWrapping(this.operation.getNewValue());
		super.redo();
	}
}
