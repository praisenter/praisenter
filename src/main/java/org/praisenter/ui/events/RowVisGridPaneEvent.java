package org.praisenter.ui.events;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class RowVisGridPaneEvent extends Event {
	private static final long serialVersionUID = -2557066579224578076L;
	
	public static final EventType<RowVisGridPaneEvent> ALL = new EventType<>(Event.ANY, "ROW_VIS_GRID_PANE_ALL");
	public static final EventType<RowVisGridPaneEvent> RELAYOUT = new EventType<>(RowVisGridPaneEvent.ALL, "ROW_VIS_GRID_PANE_RELAYOUT");
	
	public RowVisGridPaneEvent(Object source, EventTarget target) {
		super(source, target, ALL);
	}
	
	public RowVisGridPaneEvent(Object source, EventTarget target, EventType<RowVisGridPaneEvent> type) {
		super(source, target, type);
	}
}
