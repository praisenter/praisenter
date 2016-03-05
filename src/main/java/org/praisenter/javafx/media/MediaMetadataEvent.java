package org.praisenter.javafx.media;

import java.io.Serializable;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

import org.praisenter.media.Media;

public class MediaMetadataEvent extends Event implements Serializable {
	private static final long serialVersionUID = 7525223765039656381L;
	
	public static final EventType<MediaMetadataEvent> ANY = new EventType<MediaMetadataEvent>("MEDIA_METADATA");
	public static final EventType<MediaRenamedEvent> RENAMED = new EventType<MediaRenamedEvent>("MEDIA_METADATA_RENAMED");
	
	public MediaMetadataEvent(Object source, EventTarget target, EventType<? extends MediaMetadataEvent> type) {
		super(source, target, type);
	}
}
