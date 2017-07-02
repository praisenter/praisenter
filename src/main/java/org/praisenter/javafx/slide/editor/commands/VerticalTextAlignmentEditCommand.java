package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

public class VerticalTextAlignmentEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<VerticalTextAlignment>> {
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public VerticalTextAlignmentEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<VerticalTextAlignment> operation, CommandAction<ValueChangedCommandOperation<VerticalTextAlignment>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public VerticalTextAlignmentEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<VerticalTextAlignment> operation, List<CommandAction<ValueChangedCommandOperation<VerticalTextAlignment>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setVerticalTextAlignment(this.operation.getNewValue());
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
		this.component.setVerticalTextAlignment(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setVerticalTextAlignment(this.operation.getNewValue());
		super.redo();
	}
}
