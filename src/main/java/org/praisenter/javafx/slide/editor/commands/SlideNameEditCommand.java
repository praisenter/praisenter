package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlide;

public class SlideNameEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<String>> {
	private final ObservableSlide<?> slide;
	
	@SafeVarargs
	public SlideNameEditCommand(ObservableSlide<?> slide, ValueChangedCommandOperation<String> operation, CommandAction<ValueChangedCommandOperation<String>>... actions) {
		this(slide, operation, Arrays.asList(actions));
	}
	
	public SlideNameEditCommand(ObservableSlide<?> slide, ValueChangedCommandOperation<String> operation, List<CommandAction<ValueChangedCommandOperation<String>>> actions) {
		super(operation, actions);
		this.slide = slide;
	}
	
	@Override
	public void execute() {
		slide.setName(this.operation.getNewValue());
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof SlideNameEditCommand) {
			ObservableSlide<?> slide = ((SlideNameEditCommand)command).slide;
			return this.slide == slide;
		}
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.slide != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (this.isMergeSupported(command)) {
			return new SlideNameEditCommand(
				this.slide,
				CommandFactory.changed(((SlideNameEditCommand)command).operation.getOldValue(), this.operation.getNewValue()), 
				this.actions);
		}
		return null;
	}
	
	@Override
	public void undo() {
		slide.setName(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		slide.setName(this.operation.getNewValue());
		super.redo();
	}
}
