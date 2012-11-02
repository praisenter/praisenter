package org.praisenter.slide;

/**
 * Simple listener interface to listen for events from a slide.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SlideListener {
	/**
	 * Called when the slide's state has changed and must be rendered again.
	 */
	public abstract void render();
}
