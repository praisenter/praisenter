package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.data.TextType;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

public final class PlaceholderTypeEditCommand extends SlideRegionValueChangedEditCommand<Option<TextType>, ObservableTextPlaceholderComponent> implements EditCommand {
	private final ObservableSlide<?> slide;
	private final ComboBox<Option<TextType>> control;
	
	public PlaceholderTypeEditCommand(Option<TextType> oldValue, Option<TextType> newValue, ObservableSlide<?> slide, ObservableTextPlaceholderComponent component, ObjectProperty<ObservableSlideRegion<?>> selection, ComboBox<Option<TextType>> control) {
		super(oldValue, newValue, component, selection);
		this.slide = slide;
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.setPlaceholderType(this.newValue);
	}
	
	@Override
	public boolean isMergeSupported(EditCommand command) {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return super.isValid() &&  this.slide != null;
	}
	
	@Override
	public EditCommand merge(EditCommand command) {
		return null;
	}
	
	@Override
	public void undo() {
		this.setPlaceholderType(this.oldValue);
		
		this.selectRegion();
		this.combo(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.setPlaceholderType(this.newValue);
		
		this.selectRegion();
		this.combo(this.control, this.newValue);
	}
	
	private void setPlaceholderType(Option<TextType> option) {
		TextType type = TextType.TEXT;
		if (option != null) {
			TextType temp = option.getValue();
			if (temp != null) {
				type = temp;
			}
		}
		this.region.setPlaceholderType(type);
		this.slide.updatePlaceholders();
	}
}
