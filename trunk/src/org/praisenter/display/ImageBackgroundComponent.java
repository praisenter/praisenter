package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;

/**
 * Represents an image background for a {@link Display}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageBackgroundComponent extends BackgroundComponent {
	/** The image */
	protected BufferedImage image;
	
	/** The image scale type */
	protected ScaleType scaleType;
	
	/** The scaling quality */
	protected ScaleQuality scaleQuality;
	
	// cached info
	
	/** The scaled image */
	protected BufferedImage scaledImage;
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param size the size of the background
	 */
	public ImageBackgroundComponent(String name, Dimension size) {
		this(name, size, null);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param size the size of the background
	 * @param image the image; can be null
	 */
	public ImageBackgroundComponent(String name, Dimension size, BufferedImage image) {
		super(name, size);
		this.image = image;
		this.scaleType = ScaleType.NONUNIFORM;
		this.scaleQuality = ScaleQuality.BILINEAR;
		this.scaledImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.DisplayComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D graphics) {
		// make sure the image isn't null
		if (this.image != null) {
			// this will always center the image
			int dw = this.size.width;
			int dh = this.size.height;
			
			// see if the scaled image has been created or
			// if the bounds have changed
			if (this.isDirty() || this.scaledImage == null) {
				// the image set here may or may not be in this device's best format
				// to avoid the cost of converting the image on each render, we go
				// ahead and convert the given image to an image of the same size
				// that is compatible with this display
				GraphicsConfiguration gc = graphics.getDeviceConfiguration();
				BufferedImage original = this.image;
				this.image = gc.createCompatibleImage(original.getWidth(), original.getHeight(), original.getTransparency());
				// blit the original to the new one
				Graphics2D ig = this.image.createGraphics();
				ig.drawImage(original, 0, 0, null);
				ig.dispose();
				// now scale (these method will return images with the same
				// color model as the one passed in)
				if (this.scaleType == ScaleType.UNIFORM) {
					this.scaledImage = ImageUtilities.getUniformScaledImage(this.image, dw, dh, this.scaleQuality.getQuality());
				} else if (this.scaleType == ScaleType.NONUNIFORM) {
					this.scaledImage = ImageUtilities.getNonUniformScaledImage(this.image, dw, dh, this.scaleQuality.getQuality());
				} else {
					// scaling type was none, so dont scale
					this.scaledImage = this.image;
				}
				this.setDirty(false);
			}
			
			// check visibility
			if (this.visible) {
				// this will always center the image
				int sw = this.scaledImage.getWidth();
				int sh = this.scaledImage.getHeight();
				
				int x = (dw - sw) / 2;
				int y = (dh - sh) / 2;
				
				graphics.drawImage(this.scaledImage, x, y, null);
			}
		}
	}

	/**
	 * Returns the source image.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/**
	 * Sets the source image.
	 * @param image the image
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		this.setDirty(true);
	}
	
	/**
	 * Returns the image scale type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getScaleType() {
		return this.scaleType;
	}
	
	/**
	 * Sets the image scale type.
	 * @param scaleType the scale type
	 */
	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
		this.setDirty(true);
	}
	
	/**
	 * Returns the image scaling quality.
	 * @return {@link ScaleQuality}
	 */
	public ScaleQuality getScaleQuality() {
		return this.scaleQuality;
	}
	
	/**
	 * Returns the image scaling quality.
	 * @param scaleQuality the scale quality
	 */
	public void setScaleQuality(ScaleQuality scaleQuality) {
		this.scaleQuality = scaleQuality;
		this.setDirty(true);
	}
}
