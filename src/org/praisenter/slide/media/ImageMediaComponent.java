package org.praisenter.slide.media;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.display.ScaleType;
import org.praisenter.media.ImageMedia;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideComponentCopyException;

/**
 * Component for showing images from the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "ImageMediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class ImageMediaComponent extends AbstractImageMediaComponent<ImageMedia> implements SlideComponent, RenderableSlideComponent, PositionedSlideComponent, MediaComponent<ImageMedia> {
	/** Cache the scaled image */
	protected BufferedImage scaledImage;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected ImageMediaComponent() {
		super(null, 0, 0, 0, 0);
	}
	
	/**
	 * Minimal constructor.
	 * @param media the image media
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public ImageMediaComponent(ImageMedia media, int width, int height) {
		this(media, 0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param media the image media
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public ImageMediaComponent(ImageMedia media, int x, int y, int width, int height) {
		super(media, x, y, width, height);
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 * @throws SlideComponentCopyException thrown if the media could not be copied
	 */
	public ImageMediaComponent(ImageMediaComponent component) throws SlideComponentCopyException {
		super(component);
		this.scaledImage = component.scaledImage;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#setMedia(org.praisenter.media.AbstractImageMedia)
	 */
	@Override
	public void setMedia(ImageMedia media) {
		super.setMedia(media);
		this.scaledImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableSlideComponent#resize(int, int)
	 */
	@Override
	public void resize(int dw, int dh) {
		super.resize(dw, dh);
		this.scaledImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableSlideComponent#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		this.scaledImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableSlideComponent#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		this.scaledImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#setScaleQuality(org.praisenter.slide.media.ScaleQuality)
	 */
	@Override
	public void setScaleQuality(ScaleQuality scaleQuality) {
		super.setScaleQuality(scaleQuality);
		this.scaledImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#setScaleType(org.praisenter.display.ScaleType)
	 */
	@Override
	public void setScaleType(ScaleType scaleType) {
		super.setScaleType(scaleType);
		this.scaledImage = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#copy()
	 */
	@Override
	public ImageMediaComponent copy() throws SlideComponentCopyException {
		return new ImageMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getImage()
	 */
	@Override
	public BufferedImage getImage() {
		if (this.media != null) {
			return this.media.getImage();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getPreviewImage()
	 */
	@Override
	public BufferedImage getPreviewImage() {
		return this.getImage();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		this.renderBackground(g, this.x, this.y);
		this.renderScaledImage(g);
		this.renderBorder(g);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		this.renderBackground(g, this.x, this.y);
		this.renderScaledImage(g);
		this.renderBorder(g);
	}
	
	/**
	 * Renders the scaled image to the given graphics object.
	 * @param g the graphics object to render to
	 */
	protected void renderScaledImage(Graphics2D g) {
		if (this.scaledImage == null) {
			BufferedImage image = this.getImage();
			if (image != null) {
				this.scaledImage = this.getScaledImage(image);
			}
		}
		if (this.scaledImage != null) {
			this.renderImage(g, this.scaledImage);
		}
	}
}
