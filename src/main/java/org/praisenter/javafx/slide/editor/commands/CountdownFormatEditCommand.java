package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.converters.TimeFormatConverter;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

public final class CountdownFormatEditCommand extends SlideRegionValueChangedEditCommand<String, ObservableCountdownComponent> implements EditCommand {
	private final ComboBox<String> control;
	
	public CountdownFormatEditCommand(String oldValue, String newValue, ObservableCountdownComponent component, ObjectProperty<ObservableSlideRegion<?>> selection, ComboBox<String> control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setCountdownFormat(TimeFormatConverter.getFormat(this.newValue));
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
		this.region.setCountdownFormat(TimeFormatConverter.getFormat(this.oldValue));
		
		this.selectRegion();
		this.combo(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setCountdownFormat(TimeFormatConverter.getFormat(this.newValue));
		
		this.selectRegion();
		this.combo(this.control, this.newValue);
	}
}
