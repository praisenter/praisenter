package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;

public class EditTextEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<String>> {
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public EditTextEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<String> operation, CommandAction<ValueChangedCommandOperation<String>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public EditTextEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<String> operation, List<CommandAction<ValueChangedCommandOperation<String>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		component.setText(this.operation.getNewValue());
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof EditTextEditCommand) {
			ObservableTextComponent<?> component = ((EditTextEditCommand)command).component;
			return this.component == component;
		}
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.component != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (this.isMergeSupported(command)) {
			return new EditTextEditCommand(
				this.component,
				CommandFactory.changed(((EditTextEditCommand)command).operation.getOldValue(), this.operation.getNewValue()), 
				this.actions);
		}
		return null;
	}
	
	@Override
	public void undo() {
		component.setText(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		component.setText(this.operation.getNewValue());
		super.redo();
	}
}
