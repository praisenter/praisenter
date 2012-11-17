package org.praisenter.media;

import java.awt.Dimension;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;

/**
 * Concrete class for image media.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageMedia extends AbstractMedia {
	/** The image */
	protected BufferedImage image;
	
	/**
	 * Full constructor.
	 * @param file the file information
	 * @param image the image
	 */
	public ImageMedia(ImageMediaFile file, BufferedImage image) {
		super(file, MediaType.IMAGE);
		this.image = image;
	}
	
	/**
	 * Returns the image.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public MediaThumbnail getThumbnail(Dimension size) {
		// resize the image to a thumbnail size
		BufferedImage image = ImageUtilities.getUniformScaledImage(this.image, size.width, size.height, AffineTransformOp.TYPE_BILINEAR);
		// return the thumbnail
		return new MediaThumbnail(this.file, image, this.type);
	}
}
