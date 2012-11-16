package org.praisenter.media;

import java.awt.image.BufferedImage;

import org.praisenter.xml.FileProperties;

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
	 * Returns the first frame of the video.
	 * @return BufferedImage
	 */
	public abstract BufferedImage getFirstFrame();
}
