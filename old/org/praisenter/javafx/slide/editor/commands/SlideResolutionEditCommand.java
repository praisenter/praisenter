package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.configuration.Resolution;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.editor.events.SlideEditorEvent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

public final class SlideResolutionEditCommand extends SlideRegionValueChangedEditCommand<Resolution, ObservableSlide<?>> implements EditCommand {
	private final ComboBox<Resolution> control;
	private final SlideEditorEvent event;
	
	public SlideResolutionEditCommand(Resolution oldValue, Resolution newValue, ObservableSlide<?> slide, ObjectProperty<ObservableSlideRegion<?>> selection, ComboBox<Resolution> control) {
		super(oldValue, newValue, slide, selection);
		this.control = control;
		this.event = new SlideEditorEvent(this.control, this.control, SlideEditorEvent.TARGET_RESOLUTION);
	}
	
	@Override
	public void execute() {
		this.region.fit(this.newValue.getWidth(), this.newValue.getHeight());
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
		this.region.fit(this.oldValue.getWidth(), this.oldValue.getHeight());
		
		this.selectRegion();
		this.combo(this.control, this.oldValue);
		this.event(this.control, this.event);
	}
	
	@Override
	public void redo() {
		this.region.fit(this.newValue.getWidth(), this.newValue.getHeight());
		
		this.selectRegion();
		this.combo(this.control, this.newValue);
		this.event(this.control, this.event);
	}
}
