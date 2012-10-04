package org.praisenter.display;

/**
 * Enumeration of the types of bounds changes.
 * <p>
 * This is mainly used for caching images.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum BoundsChangeType {
	/** This indicates that all the dimensions were increased or unchanged */
	INCREASED,
	
	/** This indicates that all the dimensions were decreased or unchanged */
	DECREASED,
	
	/** This indicates that a dimension was increased but another was decreased */
	CHANGED
}
