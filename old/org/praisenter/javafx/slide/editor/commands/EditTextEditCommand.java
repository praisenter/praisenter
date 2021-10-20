package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextInputControl;

public final class EditTextEditCommand extends SlideRegionValueChangedEditCommand<String, ObservableTextComponent<?>> implements EditCommand {
	private final TextInputControl control;
	
	public EditTextEditCommand(String oldValue, String newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, TextInputControl control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setText(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof EditTextEditCommand) {
			ObservableTextComponent<?> region = ((EditTextEditCommand)command).region;
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
		if (command != null && command instanceof EditTextEditCommand) {
			EditTextEditCommand other = (EditTextEditCommand)command;
			return new EditTextEditCommand(
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
		this.region.setText(this.oldValue);
		
		this.selectRegion();
		this.text(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setText(this.newValue);
		
		this.selectRegion();
		this.text(this.control, this.newValue);
	}
}
