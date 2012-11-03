package org.praisenter.media;

import java.awt.image.BufferedImage;

/**
 * Base class for video media.
 * <p>
 * Extend this class to allow video media to be displayed.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractVideoMedia extends AbstractImageMedia implements Media, PlayableMedia {
	/**
	 * Minimal constructor.
	 * @param fileProperties the file properties
	 */
	public AbstractVideoMedia(FileProperties fileProperties) {
		super(fileProperties, MediaType.VIDEO);
	}
	
	/**
	 * Returns the current frame of the video.
	 * @return BufferedImage
	 */
	public abstract BufferedImage getCurrentFrame();
	
	/**
	 * Returns the first frame of the video.
	 * @return BufferedImage
	 */
	public abstract BufferedImage getFirstFrame();
}
