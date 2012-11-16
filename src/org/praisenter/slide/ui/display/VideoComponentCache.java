package org.praisenter.slide.ui.display;

import java.awt.Graphics2D;

import org.praisenter.slide.Slide;
import org.praisenter.slide.media.VideoMediaComponent;

/**
 * Represents a cache for {@link VideoMediaComponent}s.
 * <p>
 * Right now this class does not cache any data, but may in the future. This is mainly
 * available so that the compositing of a {@link Slide} can include video media which
 * constantly updates.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class VideoComponentCache implements SlideComponentCache {
	/** The video media component */
	protected VideoMediaComponent component;
	
	/**
	 * Full constructor.
	 * @param component the video media component
	 */
	public VideoComponentCache(VideoMediaComponent component) {
		this.component = component;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.ui.display.SlideComponentCache#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		if (this.component != null) {
			this.component.render(g);
		}
	}
}
