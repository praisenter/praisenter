package org.praisenter.slide.media;

import org.praisenter.media.Media;
import org.praisenter.slide.PositionedSlideComponent;
import org.praisenter.slide.SlideComponent;

/**
 * Represents a component that displays media from the media library.
 * @param <E> the {@link Media} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MediaComponent<E extends Media> extends SlideComponent, PositionedSlideComponent {
	/** 
	 * Returns the media.
	 * @return E
	 */
	public abstract E getMedia();

	/**
	 * Sets the media.
	 * @param media the media
	 */
	public abstract void setMedia(E media);
}
