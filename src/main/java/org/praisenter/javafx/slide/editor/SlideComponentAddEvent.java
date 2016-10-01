package org.praisenter.javafx.slide.editor;

import java.io.Serializable;

import org.praisenter.javafx.slide.ObservableSlideComponent;

import javafx.event.EventTarget;

final class SlideComponentAddEvent extends SlideEditorEvent implements Serializable {
	final ObservableSlideComponent<?> component;
	
	public SlideComponentAddEvent(Object source, EventTarget target, ObservableSlideComponent<?> component) {
		super(source, target, SlideEditorEvent.ADD_COMPONENT);
		this.component = component;
	}

	public ObservableSlideComponent<?> getComponent() {
		return component;
	}
}
