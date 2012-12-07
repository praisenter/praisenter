package org.praisenter.slide;

/**
 * Common interface for all slide components.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SlideComponent {
	/**
	 * Returns the name of the slide component.
	 * @return String
	 */
	public abstract String getName();
	
	/**
	 * Sets the name of the slide component.
	 * @param name the desired name
	 */
	public abstract void setName(String name);
	
	/**
	 * Performs a deep copy of this object and returns the result.
	 * @return {@link SlideComponent}
	 */
	public abstract SlideComponent copy();
}
