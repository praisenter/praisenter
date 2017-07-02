package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableSlideRegion;

public class OpacityEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<Double>> {
	
	private final ObservableSlideRegion<?> region;
	
	@SafeVarargs
	public OpacityEditCommand(ObservableSlideRegion<?> region, ValueChangedCommandOperation<Double> operation, CommandAction<ValueChangedCommandOperation<Double>>... actions) {
		this(region, operation, Arrays.asList(actions));
	}
	
	public OpacityEditCommand(ObservableSlideRegion<?> region, ValueChangedCommandOperation<Double> operation, List<CommandAction<ValueChangedCommandOperation<Double>>> actions) {
		super(operation, actions);
		this.region = region;
	}
	
	@Override
	public void execute() {
		this.region.setOpacity(this.operation.getNewValue());
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
		this.region.setOpacity(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.region.setOpacity(this.operation.getNewValue());
		super.redo();
	}
}
