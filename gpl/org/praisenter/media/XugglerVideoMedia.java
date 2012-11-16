package org.praisenter.media;

import java.awt.Dimension;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;
import org.praisenter.xml.FileProperties;

/**
 * Represents a video media type using the Xuggler library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XugglerVideoMedia extends AbstractVideoMedia implements Media, PlayableMedia, XugglerPlayableMedia {
	/** The first frame of the video */
	protected BufferedImage firstFrame;
	
	/**
	 * Full constructor.
	 * @param fileProperties the file properties
	 * @param firstFrame the first frame of the video
	 */
	public XugglerVideoMedia(FileProperties fileProperties, BufferedImage firstFrame) {
		super(fileProperties);
		this.firstFrame = firstFrame;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.AbstractVideoMedia#getFirstFrame()
	 */
	@Override
	public BufferedImage getFirstFrame() {
		return this.firstFrame;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public MediaThumbnail getThumbnail(Dimension size) {
		// resize the image to a thumbnail size
		BufferedImage image = ImageUtilities.getUniformScaledImage(this.firstFrame, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// return the thumbnail
		return new MediaThumbnail(this.fileProperties, image, this.type);
	}
}
