package org.praisenter.javafx.slide.editor.events;

import java.io.Serializable;

import org.praisenter.MediaType;
import org.praisenter.javafx.slide.ObservableSlideComponent;

import javafx.event.EventTarget;

public final class MediaComponentAddEvent extends SlideComponentAddEvent implements Serializable {
	final MediaType mediaType;
	
	public MediaComponentAddEvent(Object source, EventTarget target, ObservableSlideComponent<?> component, MediaType mediaType) {
		this(source, target, component, mediaType, true, true);
	}
	
	public MediaComponentAddEvent(Object source, EventTarget target, ObservableSlideComponent<?> component, MediaType mediaType, boolean centered, boolean selected) {
		super(source, target, component, centered, selected);
		this.mediaType = mediaType;
	}
	
	public MediaType getMediaType() {
		return this.mediaType;
	}
}
