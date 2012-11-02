package org.praisenter.slide.media;

import java.awt.image.BufferedImage;

import org.praisenter.media.ImageMedia;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.SlideComponent;

/**
 * Component for showing images from the media library.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ImageMediaComponent extends AbstractImageMediaComponent<ImageMedia> implements SlideComponent, PositionedSlideComponent, MediaComponent<ImageMedia> {
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public ImageMediaComponent(int width, int height) {
		this(0, 0, width, height, null);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public ImageMediaComponent(int x, int y, int width, int height) {
		this(x, y, width, height, null);
	}

	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param media the image media
	 */
	public ImageMediaComponent(int x, int y, int width, int height, ImageMedia media) {
		super(x, y, width, height);
		this.media = media;
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
