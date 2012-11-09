package org.praisenter.slide.media;

import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.media.ImageMedia;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.RenderableSlideComponent;
import org.praisenter.slide.SlideComponent;

/**
 * Component for showing images from the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "ImageMediaComponent")
public class ImageMediaComponent extends AbstractImageMediaComponent<ImageMedia> implements SlideComponent, RenderableSlideComponent, PositionedSlideComponent, MediaComponent<ImageMedia> {
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
	 */
	public ImageMediaComponent(ImageMediaComponent component) {
		super(component);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#copy()
	 */
	@Override
	public ImageMediaComponent copy() {
		return new ImageMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getPreviewImage()
	 */
	@Override
	public BufferedImage getPreviewImage() {
		return this.media.getImage();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.AbstractImageMediaComponent#getImage()
	 */
	@Override
	public BufferedImage getImage() {
		return this.media.getImage();
	}
}
