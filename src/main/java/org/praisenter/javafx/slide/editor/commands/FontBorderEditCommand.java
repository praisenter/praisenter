package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlideStroke;

public class FontBorderEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<SlideStroke>> {
	private final ObservableTextComponent<?> component;
	
	@SafeVarargs
	public FontBorderEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<SlideStroke> operation, CommandAction<ValueChangedCommandOperation<SlideStroke>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public FontBorderEditCommand(ObservableTextComponent<?> component, ValueChangedCommandOperation<SlideStroke> operation, List<CommandAction<ValueChangedCommandOperation<SlideStroke>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setTextBorder(this.operation.getNewValue());
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
		this.component.setTextBorder(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setTextBorder(this.operation.getNewValue());
		super.redo();
	}
}
