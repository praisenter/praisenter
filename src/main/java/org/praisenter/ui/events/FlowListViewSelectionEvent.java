package org.praisenter.ui.events;

import java.io.Serializable;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class FlowListViewSelectionEvent extends Event implements Serializable {
	private static final long serialVersionUID = -7638832493983231255L;

	public static final EventType<FlowListViewSelectionEvent> ALL = new EventType<FlowListViewSelectionEvent>("SELECTION_ALL");
	public static final EventType<FlowListViewSelectionEvent> DOUBLE_CLICK = new EventType<FlowListViewSelectionEvent>("SELECTION_DOUBLE_CLICK");
	
	public FlowListViewSelectionEvent(Object source, EventTarget target, EventType<FlowListViewSelectionEvent> type) {
		super(source, target, type);
	}
}
