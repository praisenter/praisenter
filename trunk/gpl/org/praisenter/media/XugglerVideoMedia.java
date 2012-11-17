package org.praisenter.media;

import java.awt.image.BufferedImage;

import org.praisenter.xml.FileProperties;

/**
 * Represents a video media type using the Xuggler library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerVideoMedia extends AbstractVideoMedia implements Media, PlayableMedia, XugglerPlayableMedia {
	/**
	 * Full constructor.
	 * @param fileProperties the file properties
	 * @param firstFrame the first frame of the video
	 */
	public XugglerVideoMedia(FileProperties fileProperties, BufferedImage firstFrame) {
		super(fileProperties, firstFrame);
	}
}
