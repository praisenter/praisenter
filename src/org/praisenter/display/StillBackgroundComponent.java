package org.praisenter.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.praisenter.utilities.ImageUtilities;

/**
 * Represents an still background for a {@link Display}.
 * <p>
 * A still background can be an image or color or both composited together.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class StillBackgroundComponent extends BackgroundComponent {
	// color settings
	
	/** The color to use */
	protected Color color;

	/** The color composite type */
	protected CompositeType colorCompositeType;
	
	/** True if the color is visible */
	protected boolean colorVisible;
	
	// image settings
	
	/** The image */
	protected BufferedImage image;

	/** The image scale type */
	protected ScaleType imageScaleType;
	
	/** The scaling quality */
	protected ScaleQuality imageScaleQuality;

	/** True if the image is visible */
	protected boolean imageVisible;
		
	// cached info
	
	/** A scaled version of the image */
	protected BufferedImage scaledImage;
	
	/** The cached version of the entire composite */
	protected BufferedImage cachedImage;

	/** True if the original image has been converted */
	protected boolean imageConverted;
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param size the size of the background
	 */
	public StillBackgroundComponent(String name, Dimension size) {
		super(name, size);
		// default color is fully transparent
		this.color = Color.WHITE;
		this.colorCompositeType = CompositeType.UNDERLAY;
		this.colorVisible = false;
		
		this.image = null;
		this.imageScaleType = ScaleType.NONUNIFORM;
		this.imageScaleQuality = ScaleQuality.BILINEAR;
		this.imageVisible = false;
		
		this.scaledImage = null;
		this.cachedImage = null;
		this.imageConverted = false;
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
				// see if the scaled version needs to be created
				if (this.scaledImage == null) {
					// now scale (these method will return images with the same
					// color model as the one passed in)
					if (this.imageScaleType == ScaleType.UNIFORM) {
						this.scaledImage = ImageUtilities.getUniformScaledImage(this.image, dw, dh, this.imageScaleQuality.getQuality());
					} else if (this.imageScaleType == ScaleType.NONUNIFORM) {
						this.scaledImage = ImageUtilities.getNonUniformScaledImage(this.image, dw, dh, this.imageScaleQuality.getQuality());
					} else {
						// scaling type was none, so dont scale
						this.scaledImage = this.image;
					}
				}
				// center the image
				int iw = this.scaledImage.getWidth();
				int ih = this.scaledImage.getHeight();
				int x = (dw - iw) / 2;
				int y = (dh - ih) / 2;
				ig.drawImage(this.scaledImage, x,  y, null);
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
		
		// check visibility
		if (this.visible) {
			graphics.drawImage(this.cachedImage, 0, 0, null);
		}
	}

	// color
	
	/**
	 * Returns the color.
	 * @return Color
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * Sets the color.
	 * @param color the color
	 */
	public void setColor(Color color) {
		this.color = color;
		this.setDirty(true);
	}

	/**
	 * Returns the color composite type.
	 * @return {@link CompositeType}
	 */
	public CompositeType getColorCompositeType() {
		return this.colorCompositeType;
	}
	
	/**
	 * Sets the color composite type.
	 * @param colorCompositeType the composite type
	 */
	public void setColorCompositeType(CompositeType colorCompositeType) {
		this.colorCompositeType = colorCompositeType;
		this.setDirty(true);
	}
	
	/**
	 * Returns true if the color is visible.
	 * @return boolean
	 */
	public boolean isColorVisible() {
		return this.colorVisible;
	}

	/**
	 * Sets the color's visibility.
	 * @param flag true if the color should be visible
	 */
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
		this.scaledImage = null;
		this.cachedImage = null;
		this.imageConverted = false;
		this.setDirty(true);
	}

	/**
	 * Returns true if the image is visible.
	 * @return boolean
	 */
	public boolean isImageVisible() {
		return this.imageVisible;
	}

	/**
	 * Sets the image's visibility.
	 * @param flag true if the image should be visible
	 */
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
		this.scaledImage = null;
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
		this.scaledImage = null;
		this.setDirty(true);
	}
}
