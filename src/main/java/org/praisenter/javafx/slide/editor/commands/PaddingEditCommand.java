package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlidePadding;

public class PaddingEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Double>> {
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public PaddingEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<Double> operation, CommandAction<ValueChangedCommandOperation<Double>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public PaddingEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<Double> operation, List<CommandAction<ValueChangedCommandOperation<Double>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setPadding(new SlidePadding(this.operation.getNewValue()));
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
		this.component.setPadding(new SlidePadding(this.operation.getOldValue()));
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setPadding(new SlidePadding(this.operation.getNewValue()));
		super.redo();
	}
}
