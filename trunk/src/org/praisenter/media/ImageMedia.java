package org.praisenter.media;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.praisenter.images.Images;
import org.praisenter.utilities.ImageUtilities;
import org.praisenter.utilities.WindowUtilities;

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
		// uniformly scale the image and compute the size and position
		double sw = (double)size.width / (double)this.image.getWidth();
		double sh = (double)size.height / (double)this.image.getHeight();
		double s = sw < sh ? sw : sh;
		int w = (int)Math.ceil((double)this.image.getWidth() * s);
		int h = (int)Math.ceil((double)this.image.getHeight() * s);
		int x = (size.width - w) / 2;
		int y = (size.height - h) / 2;
		
		// use the default device to create an image to render to
		GraphicsDevice device = WindowUtilities.getDefaultDevice();
		GraphicsConfiguration conf = device.getDefaultConfiguration();
		BufferedImage image = conf.createCompatibleImage(size.width, size.height, Transparency.BITMASK);
		
		// render a tiled transparent background first
		if (this.image.getTransparency() != Transparency.OPAQUE) {
			// only render this if the image is translucent or bitmask
			ImageUtilities.renderTiledImage(Images.TRANSPARENT_BACKGROUND, image, x, y, w, h);
		}
		
		// render the scaled image
		Graphics2D g = image.createGraphics();
		// render the scaled image to the graphics
		g.drawImage(this.image, x, y, w, h, null);
		g.dispose();
		
		// return the thumbnail
		return new MediaThumbnail(this.file, image, this.type);
	}
}
