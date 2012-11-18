package org.praisenter.media;

/**
 * Represents a player that can play timed media (like video and audio).
 * @param <E> the {@link PlayableMedia} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MediaPlayer<E extends PlayableMedia> {
	/**
	 * The enumeration of states the media player can be in.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static enum State {
		/** The stopped state */
		STOPPED,
		
		/** The playing state */
		PLAYING,
		
		/** The paused state */
		PAUSED,
		
		/** This state is useful for clean up only */
		ENDED
	}
	
	// properties
	
	/**
	 * Returns true if this player is in the playing state.
	 * @return boolean
	 */
	public abstract boolean isPlaying();
	
	/**
	 * Returns true if this player is in the paused state.
	 * @return boolean
	 */
	public abstract boolean isPaused();
	
	/**
	 * Returns true if this player is in the stopped state.
	 * @return boolean
	 */
	public abstract boolean isStopped();
	
	/**
	 * Sets the media to be played.
	 * <p>
	 * If media is already being played, it will be stopped and the
	 * given media will played.
	 * <p>
	 * Returns true if the media was opened and the player is ready
	 * for playback.
	 * @param media the media to play
	 * @return boolean
	 */
	public abstract boolean setMedia(E media);
	
	/**
	 * Returns the media the player is playing.
	 * @return E
	 */
	public abstract E getMedia();
	
	/**
	 * Returns the media player configuration.
	 * @return {@link MediaPlayerConfiguration}
	 */
	public abstract MediaPlayerConfiguration getConfiguration();
	
	/**
	 * Sets the media player configuration.
	 * <p>
	 * This controls looping, muted audio, video scaling, etc.
	 * @param configuration the configuration
	 */
	public abstract void setConfiguration(MediaPlayerConfiguration configuration);
	
	// controls
	
	/**
	 * Begins playback of the media.
	 */
	public abstract void play();
	
	/**
	 * Stops playback of the media.
	 * <p>
	 * This will reset the position in the media to the beginning.
	 */
	public abstract void stop();
	
	/**
	 * Pauses playback of the media.
	 */
	public abstract void pause();
	
	/**
	 * Resumes playback of the media.
	 */
	public abstract void resume();
	
	/**
	 * Seeks the media to the given position.
	 * @param position the position
	 */
	public abstract void seek(long position);
	
	/**
	 * Returns the current position in the media.
	 * @return long
	 */
	public abstract long getPosition();
	
	/**
	 * Ends any playback and releases any resources for
	 * this player.
	 * <p>
	 * After this method is called, media can no longer
	 * be played from this player.
	 */
	public abstract void release();
	
	// events
	
	/**
	 * Adds the given listener to the list of listeners.
	 * @param listener the listener to add
	 */
	public abstract void addMediaPlayerListener(MediaPlayerListener listener);
	
	/**
	 * Removes the given listener from the list of listeners.
	 * @param listener the listener to remove
	 * @return boolean true if the listener was removed
	 */
	public abstract boolean removeMediaPlayerListener(MediaPlayerListener listener);
}
