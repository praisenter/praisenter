package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableCountdownComponent;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.CheckBox;

public final class TimeOnlyEditCommand extends SlideRegionValueChangedEditCommand<Boolean, ObservableCountdownComponent> implements EditCommand {
	private final CheckBox control;

	public TimeOnlyEditCommand(Boolean oldValue, Boolean newValue, ObservableCountdownComponent component, ObjectProperty<ObservableSlideRegion<?>> selection, CheckBox control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.region.setCountdownTimeOnly(this.newValue);
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
		this.region.setCountdownTimeOnly(this.oldValue);
		
		this.selectRegion();
		this.check(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setCountdownTimeOnly(this.newValue);
		
		this.selectRegion();
		this.check(this.control, this.newValue);
	}
}
