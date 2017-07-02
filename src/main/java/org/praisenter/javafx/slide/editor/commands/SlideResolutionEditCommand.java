package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.CommandFactory;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.slide.ObservableSlide;

public class SlideResolutionEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Resolution>> {
	
	private final ObservableSlide<?> slide;
	
	@SafeVarargs
	public SlideResolutionEditCommand(ObservableSlide<?> slide, ValueChangedCommandOperation<Resolution> operation, CommandAction<ValueChangedCommandOperation<Resolution>>... actions) {
		this(slide, operation, Arrays.asList(actions));
	}
	
	public SlideResolutionEditCommand(ObservableSlide<?> slide, ValueChangedCommandOperation<Resolution> operation, List<CommandAction<ValueChangedCommandOperation<Resolution>>> actions) {
		super(operation, actions);
		this.slide = slide;
	}
	
	@Override
	public void execute() {
		this.slide.fit(this.operation.getNewValue().getWidth(), this.operation.getNewValue().getHeight());
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof SlideResolutionEditCommand) {
			ObservableSlide<?> slide = ((SlideResolutionEditCommand)command).slide;
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
			return new SlideResolutionEditCommand(
				this.slide,
				CommandFactory.changed(((SlideResolutionEditCommand)command).operation.getOldValue(), this.operation.getNewValue()), 
				this.actions);
		}
		return null;
	}
	
	@Override
	public void undo() {
		this.slide.fit(this.operation.getOldValue().getWidth(), this.operation.getOldValue().getHeight());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.slide.fit(this.operation.getNewValue().getWidth(), this.operation.getNewValue().getHeight());
		super.redo();
	}
}
