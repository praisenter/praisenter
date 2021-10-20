package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.javafx.slide.editor.controls.SlideFontPicker;
import org.praisenter.slide.text.SlideFont;

import javafx.beans.property.ObjectProperty;

public final class FontEditCommand extends SlideRegionValueChangedEditCommand<SlideFont, ObservableTextComponent<?>> implements EditCommand {
	private final SlideFontPicker fontPicker;
	
	public FontEditCommand(SlideFont oldValue, SlideFont newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, SlideFontPicker fontPicker) {
		super(oldValue, newValue, component, selection);
		this.fontPicker = fontPicker;
	}
	
	@Override
	public void execute() {
		this.region.setFont(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() && this.fontPicker != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.region.setFont(this.oldValue);
		
		this.selectRegion();
		this.focus(this.fontPicker);
		this.fontPicker.setFont(this.oldValue);
	}
	
	@Override
	public void redo() {
		this.region.setFont(this.newValue);
		
		this.selectRegion();
		this.focus(this.fontPicker);
		this.fontPicker.setFont(this.newValue);
	}
}
