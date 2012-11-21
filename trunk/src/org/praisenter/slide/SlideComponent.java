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

	/**
	 * Returns the z-ordering of the component in the slide.
	 * @return int
	 */
	public abstract int getOrder();
	
	/**
	 * Sets the z-ordering of the component in the slide.
	 * <p>
	 * If the order is changed using this method, its up to the caller
	 * to ensure that the slide that contains this component is resorted.
	 * @param order the order
	 */
	public abstract void setOrder(int order);
}
