package org.praisenter.slide.ui.present.jogl;

import java.awt.Graphics2D;

import org.praisenter.slide.SlideComponent;

/**
 * Interface for caching renderings of {@link SlideComponent}s for improved performance. 
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SlideComponentCache {
	/**
	 * Renders the cache to the given graphics object.
	 * @param g the graphics object to render to
	 */
	public abstract void render(Graphics2D g);
}
