package org.praisenter.slide.text;

/**
 * Enumeration of the font scaling types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum FontScaleType {
	/** No scaling */
	NONE,
	
	/** The specified font is the maximum size */
	REDUCE_SIZE_ONLY,
	
	/** The font will grow/shrink to fix the available space */
	BEST_FIT
}
