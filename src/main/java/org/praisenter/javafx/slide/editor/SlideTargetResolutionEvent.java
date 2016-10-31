package org.praisenter.javafx.slide.editor;

import java.io.Serializable;

import org.praisenter.javafx.screen.Resolution;
import org.praisenter.javafx.slide.ObservableSlide;

import javafx.event.EventTarget;

final class SlideTargetResolutionEvent extends SlideEditorEvent implements Serializable {
	final ObservableSlide<?> component;
	final Resolution resolution;
	
	public SlideTargetResolutionEvent(Object source, EventTarget target, ObservableSlide<?> component, Resolution resolution) {
		super(source, target, SlideEditorEvent.TARGET_RESOLUTION);
		
		this.component = component;
		this.resolution = resolution;
	}

	public ObservableSlide<?> getComponent() {
		return component;
	}

	public Resolution getResolution() {
		return resolution;
	}
}
