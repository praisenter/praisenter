package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableMediaComponent;
import org.praisenter.slide.object.MediaObject;

public class MediaEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<MediaObject>> {
	private final ObservableMediaComponent component;
	
	@SafeVarargs
	public MediaEditCommand(ObservableMediaComponent component, ValueChangedCommandOperation<MediaObject> operation, CommandAction<ValueChangedCommandOperation<MediaObject>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public MediaEditCommand(ObservableMediaComponent component, ValueChangedCommandOperation<MediaObject> operation, List<CommandAction<ValueChangedCommandOperation<MediaObject>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setMedia(this.operation.getNewValue());
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
		this.component.setMedia(this.operation.getOldValue());
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setMedia(this.operation.getNewValue());
		super.redo();
	}
}
