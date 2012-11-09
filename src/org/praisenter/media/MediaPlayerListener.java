package org.praisenter.media;

import java.awt.image.BufferedImage;

/**
 * Interface to implement to receive events from playing media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MediaPlayerListener {
	/**
	 * Called when the media player has started playing the media.
	 */
	public void started();
	
	/**
	 * Called when the media player has paused playback of the media.
	 */
	public void paused();
	
	/**
	 * Called when the media player has resumed playback of the media.
	 */
	public void resumed();
	
	/**
	 * Called when the media player has stopped playback of the media.
	 */
	public void stopped();
	
	/**
	 * Called when the media has been seeked to a position in the media.
	 */
	public void seeked();
	
	// specific methods
	
	/**
	 * Special method for updating a screen when the playback of a video
	 * has been advanced.
	 * @param image the image to display
	 */
	public void onVideoPicture(BufferedImage image);
}
