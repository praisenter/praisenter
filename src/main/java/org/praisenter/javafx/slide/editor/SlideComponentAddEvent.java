package org.praisenter.javafx.slide.editor;

import java.io.Serializable;

import org.praisenter.javafx.slide.ObservableSlideComponent;

import javafx.event.EventTarget;

class SlideComponentAddEvent extends SlideEditorEvent implements Serializable {
	final ObservableSlideComponent<?> component;
	final boolean centered;
	final boolean selected;
	
	public SlideComponentAddEvent(Object source, EventTarget target, ObservableSlideComponent<?> component) {
		this(source, target, component, true, true);
	}
	
	public SlideComponentAddEvent(Object source, EventTarget target, ObservableSlideComponent<?> component, boolean centered, boolean selected) {
		super(source, target, SlideEditorEvent.ADD_COMPONENT);
		this.component = component;
		this.centered = centered;
		this.selected = selected;
	}

	public ObservableSlideComponent<?> getComponent() {
		return component;
	}
	
	public boolean isCentered() {
		return this.centered;
	}
	
	public boolean isSelected() {
		return this.selected;
	}
}
