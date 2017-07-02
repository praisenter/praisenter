package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.CommandOperation;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;

public final class StackOrderEditCommand extends ActionsEditCommand<CommandOperation> implements EditCommand {
	private final ObservableSlide<?> slide;
	private final ObservableSlideComponent<?> component;
	private final int direction;
	
	@SafeVarargs
	public StackOrderEditCommand(ObservableSlide<?> slide, ObservableSlideComponent<?> component, int direction, CommandAction<CommandOperation>... actions) {
		this(slide, component, direction, Arrays.asList(actions));
	}

	public StackOrderEditCommand(ObservableSlide<?> slide, ObservableSlideComponent<?> component, int direction, List<CommandAction<CommandOperation>> actions) {
		super(null, actions);
		this.slide = slide;
		this.component = component;
		this.direction = direction;
	}

	@Override
	public void execute() {
		this.moveComponent(this.direction);
	}
	
	private void moveComponent(int direction) {
		if (direction == -1) {
			this.slide.moveComponentDown(this.component);
		} else if (direction == 1) {
			this.slide.moveComponentUp(this.component);
		} else if (direction < -1) {
			this.slide.moveComponentBack(this.component);
		} else if (direction > 1) {
			this.slide.moveComponentFront(this.component);
		}
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.slide != null && this.component != null && this.direction != 0;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.moveComponent(this.direction * -1);
		super.undo();
	}
	
	@Override
	public void redo() {
		this.moveComponent(this.direction);
		super.redo();
	}
}
