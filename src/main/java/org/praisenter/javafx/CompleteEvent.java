package org.praisenter.javafx;

import java.io.Serializable;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;

public final class CompleteEvent<T> extends ActionEvent implements Serializable {
	/** The serialization id */
	private static final long serialVersionUID = 7129541934144062024L;
	
	final T data;

	public CompleteEvent(Object source, EventTarget target, T data) {
		super(source, target);
		this.data = data;
	}

	public T getData() {
		return this.data;
	}
}
