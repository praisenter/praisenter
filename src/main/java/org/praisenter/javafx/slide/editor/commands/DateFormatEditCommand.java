package org.praisenter.javafx.slide.editor.commands;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableDateTimeComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

public final class DateFormatEditCommand extends SlideRegionValueChangedEditCommand<String, ObservableDateTimeComponent> implements EditCommand {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ComboBox<String> control;
	
	public DateFormatEditCommand(String oldValue, String newValue, ObservableDateTimeComponent component, ObjectProperty<ObservableSlideRegion<?>> selection, ComboBox<String> control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setDateTimeFormat(getFormat(this.newValue));
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.control != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setDateTimeFormat(getFormat(this.oldValue));
		
		this.selectRegion();
		this.combo(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setDateTimeFormat(getFormat(this.newValue));
		
		this.selectRegion();
		this.combo(this.control, this.newValue);
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
