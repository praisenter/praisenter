package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextComponent;
import org.praisenter.slide.text.FontScaleType;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

public final class FontScalingEditCommand extends SlideRegionValueChangedEditCommand<Option<FontScaleType>, ObservableTextComponent<?>> implements EditCommand {
	private final ComboBox<Option<FontScaleType>> control;
	
	public FontScalingEditCommand(Option<FontScaleType> oldValue, Option<FontScaleType> newValue, ObservableTextComponent<?> component, ObjectProperty<ObservableSlideRegion<?>> selection, ComboBox<Option<FontScaleType>> control) {
		super(oldValue, newValue, component, selection);
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.setValue(this.newValue);
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
		this.setValue(this.oldValue);
		
		this.selectRegion();
		this.combo(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.setValue(this.newValue);
		
		this.selectRegion();
		this.combo(this.control, this.newValue);
	}
	
	private void setValue(Option<FontScaleType> option) {
		FontScaleType type = option != null ? option.getValue() : null;
		this.region.setFontScaleType(type != null ? type : FontScaleType.NONE);
	}
}
