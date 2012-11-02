package org.praisenter.slide.media;

import org.praisenter.media.Media;
import org.praisenter.slide.GenericSlideComponent;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.SlideComponent;

/**
 * Abstract implementation of the {@link MediaComponent} interface.
 * @param <E> the {@link Media} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class AbstractMediaComponent<E extends Media> extends GenericSlideComponent implements SlideComponent, PositionedSlideComponent, MediaComponent<E> {
	/** The media (image, video, audio, etc.) */
	protected E media;
	
	/**
	 * Minimal constructor.
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractMediaComponent(int width, int height) {
		this(0, 0, width, height);
	}
	
	/**
	 * Optional constructor.
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public AbstractMediaComponent(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#getMedia()
	 */
	public E getMedia() {
		return media;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#setMedia(org.praisenter.media.Media)
	 */
	public void setMedia(E media) {
		this.media = media;
	}
}
