package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.text.SlideFont;

public class FontEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<SlideFont>> {
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public FontEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<SlideFont> operation, CommandAction<ValueChangedCommandOperation<SlideFont>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public FontEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<SlideFont> operation, List<CommandAction<ValueChangedCommandOperation<SlideFont>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setFont(this.operation.getNewValue());
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
		this.component.setFont(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setFont(this.operation.getNewValue());
		super.redo();
	}
}
