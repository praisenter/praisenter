package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.graphics.SlidePadding;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Spinner;

public final class PaddingEditCommand extends SlideRegionValueChangedEditCommand<Double, ObservableTextComponent<?>> implements EditCommand {
	private final Spinner<Double> control;
	
	public PaddingEditCommand(Double oldValue, Double newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, Spinner<Double> control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setPadding(new SlidePadding(this.newValue));
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
		this.region.setPadding(new SlidePadding(this.oldValue));
		
		this.selectRegion();
		this.spinner(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setPadding(new SlidePadding(this.newValue));
		
		this.selectRegion();
		this.spinner(this.control, this.newValue);
	}
}
