package org.praisenter.javafx.slide.editor.commands;

import org.praisenter.javafx.command.EditCommand;
import org.praisenter.javafx.slide.ObservableSlideRegion;

import javafx.beans.property.ObjectProperty;

public abstract class SlideRegionEditCommand<V extends ObservableSlideRegion<?>> extends SlideRegionValueChangedEditCommand<Void, V> implements EditCommand {
	public SlideRegionEditCommand(V region, ObjectProperty<ObservableSlideRegion<?>> selection) {
		super(null, null, region, selection);
	}
}
