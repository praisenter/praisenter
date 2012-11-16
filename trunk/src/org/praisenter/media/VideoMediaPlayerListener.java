package org.praisenter.media;

import java.awt.image.BufferedImage;

/**
 * Interface to implement to receive events from playing video media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface VideoMediaPlayerListener extends MediaPlayerListener {
	/**
	 * Called when a new video image is ready for display.
	 * @param image the image to display
	 */
	public void onVideoImage(BufferedImage image);
}
