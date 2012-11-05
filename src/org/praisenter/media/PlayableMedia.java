package org.praisenter.media;

/**
 * Represents a media type that is playable.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PlayableMedia extends Media {
	public abstract boolean removeMediaListener(MediaPlayerListener listener);
	public abstract void addMediaListener(MediaPlayerListener listener);
	
	/**
	 * Begins play of the media.
	 */
	public abstract void play();
	
	/**
	 * Returns true if the media is playing.
	 * <p>
	 * This method returns true if the media is paused but
	 * false if the media is stopped.
	 * @return boolean
	 */
	public abstract boolean isPlaying();
	
	/**
	 * Stops the media from playing and seeks to the
	 * beginning of the media.
	 */
	public abstract void stop();
	
	/**
	 * Pauses/resumes play of the media.
	 * @param paused true if the media should be paused
	 */
	public abstract void setPaused(boolean paused);
	
	/**
	 * Returns true if the media is paused.
	 * @return boolean
	 */
	public abstract boolean isPaused();
	
	/**
	 * Seeks to the specified position in the media.
	 * @param position the position
	 */
	public abstract void seek(long position);
}
