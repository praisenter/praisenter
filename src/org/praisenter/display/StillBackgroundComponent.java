package org.praisenter.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;

/**
 * Represents an image background for a {@link Display}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// TODO rename this to StillBackgroundComponent
public class StillBackgroundComponent extends BackgroundComponent {
	// color settings
	
	/** The color to use */
	protected Color color;

	protected boolean colorVisible;
	
	/** The color composite type */
	protected CompositeType colorCompositeType;
	
	// image settings
	
	/** The image */
	protected BufferedImage image;

	protected boolean imageVisible;
	
	/** The image scale type */
	protected ScaleType imageScaleType;
	
	/** The scaling quality */
	protected ScaleQuality imageScaleQuality;
	
	// cached info
	
	protected boolean imageConverted;
	
	/** The scaled image */
	protected BufferedImage cachedImage;
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param size the size of the background
	 */
	public StillBackgroundComponent(String name, Dimension size) {
		super(name, size);
		// default color is fully transparent
		this.color = Color.WHITE;
		this.colorVisible = false;
		this.colorCompositeType = CompositeType.UNDERLAY;
		
		this.image = null;
		this.imageVisible = false;
		this.imageScaleType = ScaleType.NONUNIFORM;
		this.imageScaleQuality = ScaleQuality.BILINEAR;
		
		this.imageConverted = false;
		this.cachedImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.DisplayComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D graphics) {
		// this will always center the image
		int dw = this.size.width;
		int dh = this.size.height;
		
		GraphicsConfiguration gc = graphics.getDeviceConfiguration();
		
		// make sure the source image is compatible
		if (this.imageVisible && this.image != null && !this.imageConverted) {
			// the image set here may or may not be in this device's best format
			// to avoid the cost of converting the image on each render, we go
			// ahead and convert the given image to an image of the same size
			// that is compatible with this display
			BufferedImage original = this.image;
			this.image = gc.createCompatibleImage(original.getWidth(), original.getHeight(), original.getTransparency());
			// blit the original to the new one (this performs the color/data model conversion)
			Graphics2D ig = this.image.createGraphics();
			ig.drawImage(original, 0, 0, null);
			ig.dispose();
			// set the converted flag
			this.imageConverted = true;
		}
		
		// make sure the cached image is created
		if (this.isDirty() || this.cachedImage == null) {
			// set the transparency to the color's transparency
			int transparency = Transparency.BITMASK;
			if (this.imageVisible && this.image != null) {
				// if the image is present and the user wants to use it
				// then use the transparency model of the image
				transparency = this.image.getTransparency();
			} else if (this.colorVisible) {
				transparency = this.color.getTransparency();
			}
			// create the scaled image surface
			this.cachedImage = gc.createCompatibleImage(dw, dh, transparency);
			Graphics2D ig = this.cachedImage.createGraphics();
			// check the color composite type
			if (this.colorVisible && this.colorCompositeType == CompositeType.UNDERLAY) {
				// render the color to the image first
				ig.setColor(this.color);
				ig.fillRect(0, 0, dw, dh);
			}
			// render a scaled version of the image
			if (this.imageVisible && this.image != null) {
				// now scale (these method will return images with the same
				// color model as the one passed in)
				if (this.imageScaleType == ScaleType.UNIFORM) {
					ImageUtilities.drawUniformScaledImage(this.image, dw, dh, this.imageScaleQuality.getQuality(), this.cachedImage);
//					this.scaledImage = ImageUtilities.getUniformScaledImage(this.image, dw, dh, this.scaleQuality.getQuality());
				} else if (this.imageScaleType == ScaleType.NONUNIFORM) {
					ImageUtilities.drawNonUniformScaledImage(this.image, dw, dh, this.imageScaleQuality.getQuality(), this.cachedImage);
//					this.scaledImage = ImageUtilities.getNonUniformScaledImage(this.image, dw, dh, this.scaleQuality.getQuality());
				} else {
//					this.scaledImage = this.image;
					// scaling type was none, so dont scale
					// center the image
					int iw = this.image.getWidth();
					int ih = this.image.getHeight();
					int x = (dw - iw) / 2;
					int y = (dh - ih) / 2;
					ig.drawImage(this.image, x,  y, null);
				}
			}
			// check the color composite type
			if (this.colorVisible && this.colorCompositeType == CompositeType.OVERLAY) {
				// render the color to the image first
				ig.setColor(this.color);
				ig.fillRect(0, 0, dw, dh);
			}
			ig.dispose();
			// set the dirty flag
			this.setDirty(false);
		}
		
//		// see if the scaled image has been created or
//		// if the bounds have changed
//		if (this.isDirty() || this.scaledImage == null) {
//			GraphicsConfiguration gc = graphics.getDeviceConfiguration();
//			// see if we need to conver the image (or verify its the same
//			// color/data model
//			if (!this.imageConverted) {
//				// the image set here may or may not be in this device's best format
//				// to avoid the cost of converting the image on each render, we go
//				// ahead and convert the given image to an image of the same size
//				// that is compatible with this display
//				BufferedImage original = this.image;
//				this.image = gc.createCompatibleImage(original.getWidth(), original.getHeight(), original.getTransparency());
//				// blit the original to the new one (this performs the color/data model conversion)
//				Graphics2D ig = this.image.createGraphics();
//				ig.drawImage(original, 0, 0, null);
//				ig.dispose();
//				// set the converted flag
//				this.imageConverted = true;
//			}
//			// create the scaled image surface
//			this.scaledImage = gc.createCompatibleImage(dw, dh, this.image.getTransparency());
//			Graphics2D ig = this.scaledImage.createGraphics();
//			// check the color composite type
//			if (this.useColor && this.colorCompositeType == CompositeType.UNDERLAY) {
//				// render the color to the image first
//				ig.setColor(this.color);
//				ig.fillRect(0, 0, dw, dh);
//			}
//			// render the scaled image
//			
//			// check the color composite type
//			if (this.useColor && this.colorCompositeType == CompositeType.OVERLAY) {
//				// render the color to the image first
//				ig.setColor(this.color);
//				ig.fillRect(0, 0, dw, dh);
//			}
//			// now scale (these method will return images with the same
//			// color model as the one passed in)
//			if (this.scaleType == ScaleType.UNIFORM) {
//				this.scaledImage = ImageUtilities.getUniformScaledImage(this.image, dw, dh, this.scaleQuality.getQuality());
//			} else if (this.scaleType == ScaleType.NONUNIFORM) {
//				this.scaledImage = ImageUtilities.getNonUniformScaledImage(this.image, dw, dh, this.scaleQuality.getQuality());
//			} else {
//				// scaling type was none, so dont scale
//				this.scaledImage = this.image;
//			}
//			this.setDirty(false);
//		}
		
		// check visibility
		if (this.visible) {
			// this will always center the image
//			int sw = this.scaledImage.getWidth();
//			int sh = this.scaledImage.getHeight();
//			
//			int x = (dw - sw) / 2;
//			int y = (dh - sh) / 2;
			
			graphics.drawImage(this.cachedImage, 0, 0, null);
		}
	}

	// color
	

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		this.setDirty(true);
	}

	public CompositeType getColorCompositeType() {
		return colorCompositeType;
	}

	public void setColorCompositeType(CompositeType colorCompositeType) {
		this.colorCompositeType = colorCompositeType;
		this.setDirty(true);
	}

	public boolean isColorVisible() {
		return colorVisible;
	}

	public void setColorVisible(boolean flag) {
		this.colorVisible = flag;
		this.setDirty(true);
	}
	
	// image
	
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
		this.imageConverted = false;
		this.setDirty(true);
	}

	public boolean isImageVisible() {
		return imageVisible;
	}

	public void setImageVisible(boolean flag) {
		this.imageVisible = flag;
		this.setDirty(true);
	}
	
	/**
	 * Returns the image scale type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getImageScaleType() {
		return this.imageScaleType;
	}
	
	/**
	 * Sets the image scale type.
	 * @param scaleType the scale type
	 */
	public void setImageScaleType(ScaleType scaleType) {
		this.imageScaleType = scaleType;
		this.setDirty(true);
	}
	
	/**
	 * Returns the image scaling quality.
	 * @return {@link ScaleQuality}
	 */
	public ScaleQuality getImageScaleQuality() {
		return this.imageScaleQuality;
	}
	
	/**
	 * Returns the image scaling quality.
	 * @param scaleQuality the scale quality
	 */
	public void setImageScaleQuality(ScaleQuality scaleQuality) {
		this.imageScaleQuality = scaleQuality;
		this.setDirty(true);
	}
}
