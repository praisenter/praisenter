package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlideShadow;

public class TextGlowEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<SlideShadow>> {
	
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public TextGlowEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<SlideShadow> operation, CommandAction<ValueChangedCommandOperation<SlideShadow>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public TextGlowEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<SlideShadow> operation, List<CommandAction<ValueChangedCommandOperation<SlideShadow>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setTextGlow(this.operation.getNewValue());
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
		this.component.setTextGlow(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setTextGlow(this.operation.getNewValue());
		super.redo();
	}
}
