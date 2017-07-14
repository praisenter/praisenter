package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.command.ValueChangedEditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;

public abstract class SlideRegionValueChangedEditCommand<T, V extends ObservableSlideRegion<?>> extends ValueChangedEditCommand<T> implements EditCommand {
	protected final ObjectProperty<ObservableSlideRegion<?>> selection;
	protected final V region;
	
	public SlideRegionValueChangedEditCommand(T oldValue, T newValue, V region, ObjectProperty<ObservableSlideRegion<?>> selection) {
		super(oldValue, newValue);
		this.region = region;
		this.selection = selection;
	}
	
	protected SlideRegionValueChangedEditCommand(T oldValue, T newValue, SlideRegionValueChangedEditCommand<T, V> command) {
		super(oldValue, newValue);
		this.region = command.region;
		this.selection = command.selection;
	}
	
	@Override
	public boolean isValid() {
		return this.region != null;
	}
	
	public final void select(ObservableSlideRegion<?> region) {
		if (this.selection != null) {
			this.selection.set(region);
		}
	}
	
	public final void selectRegion() {
		if (this.selection != null) {
			this.selection.set(this.region);
		}
	}
}
