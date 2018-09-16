package org.praisenter.ui.events;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class ActionPromptPaneCompleteEvent extends Event {
	private static final long serialVersionUID = -8126148843499722206L;
	
	public static final EventType<ActionPromptPaneCompleteEvent> ALL = new EventType<>(Event.ANY, "ACTION_PROMPT_PANE_COMPLETE");
	public static final EventType<ActionPromptPaneCompleteEvent> ACCEPT = new EventType<>(ActionPromptPaneCompleteEvent.ALL, "ACTION_PROMPT_PANE_ACCEPT");
	public static final EventType<ActionPromptPaneCompleteEvent> CANCEL = new EventType<>(ActionPromptPaneCompleteEvent.ALL, "ACTION_PROMPT_PANE_CANCEL");
	
	public ActionPromptPaneCompleteEvent(Object source, EventTarget target) {
		super(source, target, ALL);
	}
	
	public ActionPromptPaneCompleteEvent(Object source, EventTarget target, EventType<ActionPromptPaneCompleteEvent> type) {
		super(source, target, type);
	}
}
