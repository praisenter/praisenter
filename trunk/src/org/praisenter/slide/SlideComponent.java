package org.praisenter.slide;

/**
 * Common interface for all slide components.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SlideComponent {
	/**
	 * Performs a deep copy of this object and returns the result.
	 * @return {@link SlideComponent}
	 */
	public abstract SlideComponent copy();
}
