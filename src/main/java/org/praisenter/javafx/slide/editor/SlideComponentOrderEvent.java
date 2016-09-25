package org.praisenter.javafx.slide.editor;

import java.io.Serializable;

import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.slide.ObservableSlideComponent;

import javafx.event.EventTarget;

final class SlideComponentOrderEvent extends SlideEditorEvent implements Serializable {
	public static final String OPERATION_BACK = "back";
	public static final String OPERATION_FRONT = "front";
	public static final String OPERATION_BACKWARD = "backward";
	public static final String OPERATION_FORWARD = "forward";
	
	final ObservableSlideComponent<?> component;
	final String operation;
	
	public SlideComponentOrderEvent(Object source, EventTarget target, ObservableSlideComponent<?> component, String operation) {
		super(source, target, SlideEditorEvent.ORDER);
		
		this.component = component;
		this.operation = operation;
	}

	public ObservableSlideComponent<?> getComponent() {
		return component;
	}

	public String getOperation() {
		return operation;
	}
}
