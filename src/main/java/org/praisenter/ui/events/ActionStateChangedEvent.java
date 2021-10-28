package org.praisenter.ui.events;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class ActionStateChangedEvent extends Event {
	private static final long serialVersionUID = -1818732710497511105L;
	
	public static final EventType<ActionStateChangedEvent> ALL = new EventType<>(Event.ANY, "ACTION_STATE_CHANGED_ALL");
	public static final EventType<ActionStateChangedEvent> UNDO_REDO = new EventType<>(ActionStateChangedEvent.ALL, "ACTION_STATE_CHANGED_UNDO_REDO");
	public static final EventType<ActionStateChangedEvent> CLIPBOARD = new EventType<>(ActionStateChangedEvent.ALL, "ACTION_STATE_CHANGED_CLIPBOARD");
	public static final EventType<ActionStateChangedEvent> SELECTION = new EventType<>(ActionStateChangedEvent.ALL, "ACTION_STATE_CHANGED_SELECTION");
	
	public ActionStateChangedEvent(Object source, EventTarget target) {
		super(source, target, ALL);
	}
	
	public ActionStateChangedEvent(Object source, EventTarget target, EventType<ActionStateChangedEvent> type) {
		super(source, target, type);
	}
}
