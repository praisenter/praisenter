package org.praisenter.javafx.slide.editor.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.converters.TimeFormatConverter;

public class CountdownFormatEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<String>> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObservableCountdownComponent component;

	@SafeVarargs
	public CountdownFormatEditCommand(ObservableCountdownComponent component, ValueChangedCommandOperation<String> operation, CommandAction<ValueChangedCommandOperation<String>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public CountdownFormatEditCommand(ObservableCountdownComponent component, ValueChangedCommandOperation<String> operation, List<CommandAction<ValueChangedCommandOperation<String>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setCountdownFormat(TimeFormatConverter.getFormat(this.operation.getNewValue()));
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
		this.component.setCountdownFormat(TimeFormatConverter.getFormat(this.operation.getOldValue()));
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setCountdownFormat(TimeFormatConverter.getFormat(this.operation.getNewValue()));
		super.redo();
	}
}
