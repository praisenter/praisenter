package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Slider;

public final class OpacityEditCommand extends SlideRegionValueChangedEditCommand<Double, ObservableSlideRegion<?>> implements EditCommand {
	private final Slider control;
	
	public OpacityEditCommand(Double oldValue, Double newValue, ObservableSlideRegion<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Slider control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setOpacity(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		if (command != null && command instanceof OpacityEditCommand) {
			OpacityEditCommand other = (OpacityEditCommand)command;
			return other.region == this.region;
		}
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.control != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		if (command != null && command instanceof OpacityEditCommand) {
			OpacityEditCommand other = (OpacityEditCommand)command;
			return new OpacityEditCommand(
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
		this.region.setOpacity(this.oldValue);
		
		this.selectRegion();
		this.slider(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setOpacity(this.newValue);
		
		this.selectRegion();
		this.slider(this.control, this.newValue);
	}
}
