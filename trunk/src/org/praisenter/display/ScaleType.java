package org.praisenter.display;

/**
 * Enumeration of supported image scale types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ScaleType {
	/** Uniform scaling will scale the image to fit the bounds using the largest dimension */
	UNIFORM,
	
	/** Non-uniform scaling will scale the image to fit the bounds in both dimensions */
	NONUNIFORM,
	
	/** No scaling performed */
	NONE
}
