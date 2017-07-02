package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.slide.graphics.SlideShadow;

public class GlowEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<SlideShadow>> {
	
	private final ObservableSlideRegion<?> region;
	
	@SafeVarargs
	public GlowEditCommand(ObservableSlideRegion<?> region, ValueChangedCommandOperation<SlideShadow> operation, CommandAction<ValueChangedCommandOperation<SlideShadow>>... actions) {
		this(region, operation, Arrays.asList(actions));
	}
	
	public GlowEditCommand(ObservableSlideRegion<?> region, ValueChangedCommandOperation<SlideShadow> operation, List<CommandAction<ValueChangedCommandOperation<SlideShadow>>> actions) {
		super(operation, actions);
		this.region = region;
	}
	
	@Override
	public void execute() {
		this.region.setGlow(this.operation.getNewValue());
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return this.region != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setGlow(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.region.setGlow(this.operation.getNewValue());
		super.redo();
	}
}
