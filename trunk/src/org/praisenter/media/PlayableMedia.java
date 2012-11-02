package org.praisenter.media;

/**
 * Represents a media type that is playable.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PlayableMedia extends Media {
	/**
	 * Begins play of the media.
	 */
	public abstract void play();
	
	/**
	 * Stops the media from playing and seeks to the
	 * beginning of the media.
	 */
	public abstract void stop();
	
	/**
	 * Pauses play of the media.
	 */
	public abstract void pause();
	
	/**
	 * Seeks to the specified position in the media.
	 * @param position the position
	 */
	public abstract void seek(long position);
}
