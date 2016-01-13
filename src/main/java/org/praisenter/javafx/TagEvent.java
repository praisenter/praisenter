package org.praisenter.javafx;

import java.io.Serializable;

import org.praisenter.Tag;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public final class TagEvent extends Event implements Serializable {
	private static final long serialVersionUID = 837797201591141937L;
	
	public static final EventType<TagEvent> ALL = new EventType<TagEvent>("TAG_ALL");
	public static final EventType<TagEvent> ADDED = new EventType<TagEvent>(ALL, "ADDED");
	public static final EventType<TagEvent> REMOVED = new EventType<TagEvent>(ALL, "REMOVED");
	
	final Tag tag;
	
	public TagEvent(Object source, EventTarget target, EventType<TagEvent> type, Tag tag) {
		super(source, target, type);
		this.tag = tag;
	}
	
	public Tag getTag() {
		return this.tag;
	}
}
