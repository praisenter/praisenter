package org.praisenter.slide.media;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.display.ScaleType;
import org.praisenter.media.AbstractImageMedia;
import org.praisenter.media.Media;
import org.praisenter.media.MediaException;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.PlayableMedia;
import org.praisenter.slide.GenericSlideComponent;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideComponentCopyException;
import org.praisenter.utilities.ImageUtilities;
import org.praisenter.xml.MediaTypeAdapter;

/**
 * Represents an abstract image media component that manages any type of image based media.
 * @param <E> the {@link Media} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractImageMediaComponent<E extends AbstractImageMedia> extends GenericSlideComponent implements SlideComponent, RenderableSlideComponent, PositionedSlideComponent, MediaComponent<E> {
	/** The media */
	@XmlElement(name = "Media", required = true, nillable = false)
	@XmlJavaTypeAdapter(MediaTypeAdapter.class)
	protected E media;
	
	/** The image scale type */
	@XmlAttribute(name = "ScaleType", required = false)
	protected ScaleType scaleType;
	
	/** The image scale quality */
	@XmlAttribute(name = "ScaleQuality", required = false)
	protected ScaleQuality scaleQuality;
	
	/**
	 * Minimal constructor.
	 * @param media the media
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractImageMediaComponent(E media, int width, int height) {
		this(media, 0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param media the media
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractImageMediaComponent(E media, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.media = media;
		this.scaleType = ScaleType.NONUNIFORM;
		this.scaleQuality = ScaleQuality.BILINEAR;
	}

	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 * @throws SlideComponentCopyException thrown if the media cannot be copied
	 */
	@SuppressWarnings("unchecked")
	public AbstractImageMediaComponent(AbstractImageMediaComponent<E> component) throws SlideComponentCopyException {
		super(component);
		// we only need to do this with playable media
		if (component.media != null && component.media instanceof PlayableMedia) {
			try {
				this.media = (E)MediaLibrary.getMedia(component.media.getFileProperties().getFilePath(), true);
			} catch (MediaException e) {
				throw new SlideComponentCopyException(e);
			}
		}
		this.media = component.media;
		this.scaleType = component.scaleType;
		this.scaleQuality = component.scaleQuality;
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
	
	/**
	 * Scales the given image using the component width and height. 
	 * @param image the image to scale
	 * @return BufferedImage
	 */
	protected BufferedImage getScaledImage(BufferedImage image) {
		int w = this.width;
		int h = this.height;
		// now scale (these methods will return images with the same
		// color model as the one passed in)
		int iw = image.getWidth();
		int ih = image.getHeight();
		
		if (iw != w || ih != h) {
			if (this.scaleType == ScaleType.UNIFORM) {
				image = ImageUtilities.getUniformScaledImage(image, w, h, this.scaleQuality.getQuality());
			} else if (this.scaleType == ScaleType.NONUNIFORM) {
				image = ImageUtilities.getNonUniformScaledImage(image, w, h, this.scaleQuality.getQuality());
			}
		}
		return image;
	}
	
	/**
	 * Renders the given image to the given graphics object using
	 * this components scaling type and quality.
	 * @param g the graphics object to render to
	 * @param image the image to render
	 */
	protected void renderImage(Graphics2D g, BufferedImage image) {
		if (image != null) {
			// setup the clip for this component
			Shape oClip = g.getClip();
			g.setClip(this.x, this.y, this.width, this.height);

			// center the image
			int iw = image.getWidth();
			int ih = image.getHeight();
			int x = (this.width - iw) / 2 + this.x;
			int y = (this.height - ih) / 2 + this.y;
			g.drawImage(image, x,  y, null);
			
			g.setClip(oClip);
		}
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#getMedia()
	 */
	@Override
	public E getMedia() {
		return this.media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#setMedia(org.praisenter.media.Media)
	 */
	@Override
	public void setMedia(E media) {
		this.media = media;
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
