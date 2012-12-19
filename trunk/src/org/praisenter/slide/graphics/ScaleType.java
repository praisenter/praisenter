package org.praisenter.slide.graphics;

/**
 * Enumeration of supported image scale types.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public enum ScaleType {
	/** No scaling performed */
	NONE,
	
	/** Uniform scaling will scale the image to fit the bounds using the largest dimension */
	UNIFORM,
	
	/** Non-uniform scaling will scale the image to fit the bounds in both dimensions */
	NONUNIFORM
}
