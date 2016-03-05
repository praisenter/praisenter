package org.praisenter.javafx.media;

import java.io.Serializable;

import javafx.event.EventTarget;

import org.praisenter.media.Media;

public final class MediaRenamedEvent extends MediaMetadataEvent implements Serializable {
	private static final long serialVersionUID = 7525223765039656381L;
	
	final Media oldValue;
	final Media newValue;
	
	public MediaRenamedEvent(Object source, EventTarget target, Media oldValue, Media newValue) {
		super(source, target, RENAMED);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Media getOldValue() {
		return oldValue;
	}

	public Media getNewValue() {
		return newValue;
	}
	
	
}
