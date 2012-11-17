package org.praisenter.media;

import java.awt.Dimension;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;

/**
 * Base class for video media.
 * <p>
 * Extend this class to allow video media to be displayed.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractVideoMedia extends AbstractMedia implements Media, PlayableMedia {
	/** The first frame of the video */
	protected BufferedImage firstFrame;
	
	/**
	 * Full constructor.
	 * @param file the file information
	 * @param firstFrame the first frame of the video
	 */
	public AbstractVideoMedia(VideoMediaFile file, BufferedImage firstFrame) {
		super(file, MediaType.VIDEO);
		this.firstFrame = firstFrame;
	}
	
	/**
	 * Returns the first frame of the video.
	 * @return BufferedImage
	 */
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
		return new MediaThumbnail(this.file, image, this.type);
	}
}
