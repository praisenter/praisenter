package org.praisenter.slide;

/**
 * Represents a slide template.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Template {
	/**
	 * Creates a new {@link Slide} from this template.
	 * @return {@link Slide}
	 */
	public Slide createSlide();
}
