package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.editor.controls.TimeStringConverter;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextInputControl;

public final class SlideTimeEditCommand  extends SlideRegionValueChangedEditCommand<String, ObservableSlide<?>> implements EditCommand {
	private final TimeStringConverter converter;
	private final TextInputControl control;
	
	public SlideTimeEditCommand(String oldValue, String newValue, ObservableSlide<?> slide, ObjectProperty<ObservableSlideRegion<?>> selection, TextInputControl control) {
		super(oldValue, newValue, slide, selection);
		this.control = control;
		this.converter = new TimeStringConverter();
	}
	
	@Override
	public void execute() {
		this.region.setTime(this.converter.fromString(this.newValue));
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof SlideTimeEditCommand) {
			ObservableSlide<?> region = ((SlideTimeEditCommand)command).region;
			return this.region == region;
		}
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.control != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof SlideTimeEditCommand) {
			SlideTimeEditCommand other = (SlideTimeEditCommand)command;
			return new SlideTimeEditCommand(
				other.oldValue,
				this.newValue,
				this.region,
				this.selection,
				this.control);
		}
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setTime(this.converter.fromString(this.oldValue));
		
		this.selectRegion();
		this.text(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setTime(this.converter.fromString(this.newValue));
		
		this.selectRegion();
		this.text(this.control, this.newValue);
	}
}
