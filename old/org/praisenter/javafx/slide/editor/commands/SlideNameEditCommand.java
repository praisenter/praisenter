package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextInputControl;

public final class SlideNameEditCommand  extends SlideRegionValueChangedEditCommand<String, ObservableSlide<?>> implements EditCommand {
	private final TextInputControl control;
	
	public SlideNameEditCommand(String oldValue, String newValue, ObservableSlide<?> slide, ObjectProperty<ObservableSlideRegion<?>> selection, TextInputControl control) {
		super(oldValue, newValue, slide, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setName(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof SlideNameEditCommand) {
			ObservableSlide<?> region = ((SlideNameEditCommand)command).region;
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
		if (command != null && command instanceof SlideNameEditCommand) {
			SlideNameEditCommand other = (SlideNameEditCommand)command;
			return new SlideNameEditCommand(
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
		this.region.setName(this.oldValue);
		
		this.selectRegion();
		this.text(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setName(this.newValue);
		
		this.selectRegion();
		this.text(this.control, this.newValue);
	}
}
