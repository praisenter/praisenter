package org.praisenter.javafx.slide.editor;

import java.io.Serializable;

import org.praisenter.javafx.slide.ObservableSlideComponent;

import javafx.event.EventTarget;
import javafx.event.EventType;

final class SlideComponentOrderEvent extends SlideComponentEvent implements Serializable {
	static final String OPERATION_FORWARD = "forward";
	static final String OPERATION_BACKWARD = "backward";
	static final String OPERATION_FRONT = "front";
	static final String OPERATION_BACK = "back";

	final ObservableSlideComponent<?> component;
	final String operation;
	
	public SlideComponentOrderEvent(Object source, EventTarget target, ObservableSlideComponent<?> component, String operation) {
		super(source, target, SlideComponentEvent.ORDER);
		
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
