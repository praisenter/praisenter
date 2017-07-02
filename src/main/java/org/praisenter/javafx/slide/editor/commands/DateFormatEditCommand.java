package org.praisenter.javafx.slide.editor.commands;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.command.ActionsEditCommand;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.action.CommandAction;
import org.praisenter.javafx.command.operation.ValueChangedCommandOperation;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;

public class DateFormatEditCommand extends ActionsEditCommand<ValueChangedCommandOperation<String>> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final ObservableDateTimeComponent component;
	
	@SafeVarargs
	public DateFormatEditCommand(ObservableDateTimeComponent component, ValueChangedCommandOperation<String> operation, CommandAction<ValueChangedCommandOperation<String>>... actions) {
		this(component, operation, Arrays.asList(actions));
	}
	
	public DateFormatEditCommand(ObservableDateTimeComponent component, ValueChangedCommandOperation<String> operation, List<CommandAction<ValueChangedCommandOperation<String>>> actions) {
		super(operation, actions);
		this.component = component;
	}
	
	@Override
	public void execute() {
		this.component.setDateTimeFormat(getFormat(this.operation.getNewValue()));
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
		this.component.setDateTimeFormat(getFormat(this.operation.getOldValue()));
		super.undo();
	}
	
	@Override
	public void redo() {
		this.component.setDateTimeFormat(getFormat(this.operation.getNewValue()));
		super.redo();
	}

	private SimpleDateFormat getFormat(String format) {
		if (format != null && format.trim().length() > 0) {
			try {
				return new SimpleDateFormat(format);
			} catch (Exception e) {
				LOGGER.error("Failed to create SimpleDateFormat for format '" + format + "'.", e);
			}
		}
		return null;
	}
}
