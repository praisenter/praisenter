package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ToggleButton;

public final class TextWrappingEditCommand extends SlideRegionValueChangedEditCommand<Boolean, ObservableTextComponent<?>> implements EditCommand {
	private final ToggleButton control;
	
	public TextWrappingEditCommand(Boolean oldValue, Boolean newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, ToggleButton control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setTextWrapping(this.newValue);
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
		this.region.setTextWrapping(this.oldValue);
		
		this.selectRegion();
		this.toggle(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setTextWrapping(this.newValue);
		
		this.selectRegion();
		this.toggle(this.control, this.newValue);
	}
}
