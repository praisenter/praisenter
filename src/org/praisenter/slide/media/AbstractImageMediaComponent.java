package org.praisenter.slide.media;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.praisenter.display.ScaleType;
import org.praisenter.media.AbstractImageMedia;
import org.praisenter.media.Media;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.utilities.ImageUtilities;

/**
 * Represents an abstract image media component that manages any type of image based media.
 * @param <E> the {@link Media} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AbstractImageMediaComponent<E extends AbstractImageMedia> extends AbstractMediaComponent<E> implements SlideComponent, PositionedSlideComponent, MediaComponent<E> {
	/** The image scale type */
	protected ScaleType scaleType;
	
	/** The image scale quality */
	protected ScaleQuality scaleQuality;
	
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractImageMediaComponent(int width, int height) {
		this(0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractImageMediaComponent(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.scaleType = ScaleType.NONUNIFORM;
		this.scaleQuality = ScaleQuality.BILINEAR;
	}

	/**
	 * Returns the image for rendering.
	 * @return BufferedImage
	 */
	public abstract BufferedImage getImage();
	
	/**
	 * Returns the preview image for rendering.
	 * @return BufferedImage
	 */
	public abstract BufferedImage getPreviewImage();
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// perform the background/border rendering
		super.render(g);
		// render the preview image with the correct scaleing
		this.renderImage(g, this.getImage());
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// perform the background/border rendering
		super.renderPreview(g);
		// render the preview image with the correct scaleing
		this.renderImage(g, this.getPreviewImage());
	}
	
	/**
	 * Renders the given image to the given graphics object using
	 * this components scaling type and quality.
	 * @param g the graphics object to render to
	 * @param image the image to render
	 */
	protected void renderImage(Graphics2D g, BufferedImage image) {
		if (image != null) {
			int w = this.width;
			int h = this.height;
			// now scale (these methods will return images with the same
			// color model as the one passed in)
			BufferedImage scaled = null;
			if (this.scaleType == ScaleType.UNIFORM) {
				scaled = ImageUtilities.getUniformScaledImage(image, w, h, this.scaleQuality.getQuality());
			} else if (this.scaleType == ScaleType.NONUNIFORM) {
				scaled = ImageUtilities.getNonUniformScaledImage(image, w, h, this.scaleQuality.getQuality());
			} else {
				// scaling type was none, so don't scale
				scaled = image;
			}
			
			// center the image
			int iw = scaled.getWidth();
			int ih = scaled.getHeight();
			int x = (this.width - iw) / 2;
			int y = (this.height - ih) / 2;
			g.drawImage(scaled, x,  y, null);
		}
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
	}

	/**
	 * Returns the image scale quality.
	 * @return {@link ScaleQuality}
	 */
	public ScaleQuality getScaleQuality() {
		return this.scaleQuality;
	}

	/**
	 * Sets the image scale quality.
	 * @param scaleQuality the scale quality
	 */
	public void setScaleQuality(ScaleQuality scaleQuality) {
		this.scaleQuality = scaleQuality;
	}
}
