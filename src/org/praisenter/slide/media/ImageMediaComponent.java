package org.praisenter.slide.media;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.media.ImageMedia;
import org.praisenter.media.ScaleType;
import org.praisenter.slide.GenericSlideComponent;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.xml.MediaTypeAdapter;

/**
 * Component for showing images from the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "ImageMediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class ImageMediaComponent extends GenericSlideComponent implements SlideComponent, RenderableSlideComponent, PositionedSlideComponent, MediaComponent<ImageMedia> {
	/** The media */
	@XmlElement(name = "Media", required = true, nillable = false)
	@XmlJavaTypeAdapter(MediaTypeAdapter.class)
	protected ImageMedia media;
	
	/** The image scale type */
	@XmlAttribute(name = "ScaleType", required = false)
	protected ScaleType scaleType;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected ImageMediaComponent() {
		this(null, null, 0, 0, 0, 0);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param media the image media
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public ImageMediaComponent(String name, ImageMedia media, int width, int height) {
		this(name, media, 0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param media the image media
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public ImageMediaComponent(String name, ImageMedia media, int x, int y, int width, int height) {
		super(name, x, y, width, height);
		this.media = media;
		this.scaleType = ScaleType.NONUNIFORM;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public ImageMediaComponent(ImageMediaComponent component) {
		super(component);
		this.media = component.media;
		this.scaleType = component.scaleType;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#setMedia(org.praisenter.media.AbstractImageMedia)
	 */
	@Override
	public void setMedia(ImageMedia media) {
		this.media = media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#getMedia()
	 */
	@Override
	public ImageMedia getMedia() {
		return this.media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableSlideComponent#resize(int, int)
	 */
	@Override
	public void resize(int dw, int dh) {
		super.resize(dw, dh);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableSlideComponent#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		super.setHeight(height);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractRenderableSlideComponent#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		super.setWidth(width);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#copy()
	 */
	@Override
	public ImageMediaComponent copy() {
		return new ImageMediaComponent(this);
	}
	
	/**
	 * Returns the image for rendering.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		if (this.media != null) {
			return this.media.getImage();
		}
		return null;
	}
	
	/**
	 * Returns the preview image for rendering.
	 * @return BufferedImage
	 */
	public BufferedImage getPreviewImage() {
		return this.getImage();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		this.renderBackground(g, this.x, this.y);
		this.renderScaledImage(g, this.getImage());
		this.renderBorder(g);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		this.renderBackground(g, this.x, this.y);
		this.renderScaledImage(g, this.getImage());
		this.renderBorder(g);
	}
	
	
	/**
	 * Renders the scaled image to the given graphics object.
	 * @param g the graphics object to render to
	 * @param image the image to render scaled
	 */
	protected void renderScaledImage(Graphics2D g, BufferedImage image) {
		if (image != null) {
			// setup the clip for this component
			Shape oClip = g.getClip();
			g.clipRect(this.x, this.y, this.width, this.height);

			// compute the image dimensions
			int iw = image.getWidth();
			int ih = image.getHeight();
			
			if (iw != this.width || ih != this.height) {
				double sw = (double)this.width / (double)iw;
				double sh = (double)this.height / (double)ih;
				if (this.scaleType == ScaleType.UNIFORM) {
					if (sw < sh) {
						iw = this.width;
						ih = (int)Math.round(sw * ih);
					} else {
						iw = (int)Math.round(sh * iw);
						ih = this.height;
					}
				} else if (this.scaleType == ScaleType.NONUNIFORM) {
					iw = this.width;
					ih = this.height;
				}
				// center the image
				int x = (this.width - iw) / 2;
				int y = (this.height - ih) / 2;
				g.drawImage(image, this.x + x, this.y + y, iw, ih, null);
			} else {
				g.drawImage(image, this.x, this.y, null);
			}
			
			g.setClip(oClip);
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
}
