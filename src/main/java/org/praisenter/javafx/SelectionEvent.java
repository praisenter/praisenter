package org.praisenter.javafx;

import java.io.Serializable;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class SelectionEvent extends Event implements Serializable {
	private static final long serialVersionUID = -7638832493983231255L;
	
	public static final EventType<SelectionEvent> ALL = new EventType<SelectionEvent>("ALL");
	public static final EventType<SelectionEvent> SELECT = new EventType<SelectionEvent>(ALL, "SELECT");
	public static final EventType<SelectionEvent> SELECT_MULTIPLE = new EventType<SelectionEvent>(ALL, "SELECT_MULTIPLE");
	public static final EventType<SelectionEvent> DESELECT = new EventType<SelectionEvent>(ALL, "DESELECT");
	public static final EventType<SelectionEvent> DESELECT_MULTIPLE = new EventType<SelectionEvent>(ALL, "DESELECT_MULTIPLE");
	
	public SelectionEvent(Object source, EventTarget target, EventType<SelectionEvent> type) {
		super(source, target, type);
	}
}
