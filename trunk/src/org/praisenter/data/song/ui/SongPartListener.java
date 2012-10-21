package org.praisenter.data.song.ui;

import java.util.EventListener;

import org.praisenter.data.song.SongPart;

/**
 * Represents an object that is interested when song parts are added, deleted, or modified.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SongPartListener extends EventListener {
	/**
	 * Called when a new song part has been added.
	 * @param part the song part added
	 */
	public abstract void songPartAdded(SongPart part);
	
	/**
	 * Called when a song part has been deleted.
	 * @param part the song part deleted
	 */
	public abstract void songPartDeleted(SongPart part);
	
	/**
	 * Called when a song part has been changed.
	 * @param part the song part that was changed
	 */
	public abstract void songPartChanged(SongPart part);
}
