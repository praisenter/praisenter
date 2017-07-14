package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.TextVariant;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideRegion;
import org.praisenter.javafx.slide.ObservableTextPlaceholderComponent;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ComboBox;

public final class PlaceholderVariantEditCommand extends SlideRegionValueChangedEditCommand<Option<TextVariant>, ObservableTextPlaceholderComponent> implements EditCommand {
	private final ObservableSlide<?> slide;
	private final ComboBox<Option<TextVariant>> control;
	
	public PlaceholderVariantEditCommand(Option<TextVariant> oldValue, Option<TextVariant> newValue, ObservableSlide<?> slide, ObservableTextPlaceholderComponent component, ObjectProperty<ObservableSlideRegion<?>> selection, ComboBox<Option<TextVariant>> control) {
		super(oldValue, newValue, component, selection);
		this.slide = slide;
		this.control = control;
	}
	
	@Override
	public void execute() {
		this.setPlaceholderVariant(this.newValue);
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
		this.setPlaceholderVariant(this.oldValue);
		
		this.selectRegion();
		this.combo(this.control, this.oldValue);
	}
	
	@Override
	public void redo() {
		this.setPlaceholderVariant(this.newValue);
		
		this.selectRegion();
		this.combo(this.control, this.newValue);
	}
	
	private void setPlaceholderVariant(Option<TextVariant> option) {
		TextVariant variant = TextVariant.PRIMARY;
		if (option != null) {
			TextVariant temp = option.getValue();
			if (temp != null) {
				variant = temp;
			}
		}
		this.region.setPlaceholderVariant(variant);
		this.slide.updatePlaceholders();
	}
}