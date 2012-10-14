package org.praisenter.data.song.ui;

import java.util.EventListener;

import org.praisenter.data.song.Song;

/**
 * Represents an object that is interested when songs are added, deleted, or modified.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SongListener extends EventListener {
	/**
	 * Called when a new song has been added.
	 * @param song the song added
	 */
	public abstract void songAdded(Song song);
	
	/**
	 * Called when a song has been deleted.
	 * @param song the song deleted
	 */
	public abstract void songDeleted(Song song);
	
	/**
	 * Called when a song has been changed.
	 * @param song the song that was changed
	 */
	public abstract void songChanged(Song song);
}
