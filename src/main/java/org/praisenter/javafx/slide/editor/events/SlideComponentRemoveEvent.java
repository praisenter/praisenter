package org.praisenter.javafx.slide.editor.events;

import java.io.Serializable;

import org.praisenter.javafx.slide.ObservableSlideComponent;

import javafx.event.EventTarget;

public class SlideComponentRemoveEvent extends SlideEditorEvent implements Serializable {
	final ObservableSlideComponent<?> component;
	
	public SlideComponentRemoveEvent(Object source, EventTarget target, ObservableSlideComponent<?> component) {
		super(source, target, SlideEditorEvent.REMOVE_COMPONENT);
		this.component = component;
	}

	public ObservableSlideComponent<?> getComponent() {
		return component;
	}
}
