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
	 * Returns true if the given media class is supported by this {@link MediaPlayer}.
	 * @param clazz the type
	 * @return boolean
	 */
	public abstract <T extends PlayableMedia> boolean isTypeSupported(Class<T> clazz);
	
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
	 * @param media the media to play
	 */
	public abstract void setMedia(E media);
	
	/**
	 * Returns the media the player is playing.
	 * @return E
	 */
	public abstract E getMedia();
	
	/**
	 * Toggles the muted state of the audio.
	 * @param muted true if the audio should be muted
	 */
	public abstract void setMuted(boolean muted);
	
	/**
	 * Returns true if the audio is muted.
	 * @return boolean
	 */
	public abstract boolean isMuted();
	
	/**
	 * Toggles the looping of the media.
	 * @param looped true if the media should loop
	 */
	public abstract void setLooped(boolean looped);
	
	/**
	 * Returns true if the media is set to loop.
	 * @return boolean
	 */
	public abstract boolean isLooped();
	
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
