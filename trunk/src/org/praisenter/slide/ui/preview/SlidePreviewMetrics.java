package org.praisenter.slide.ui.preview;

/**
 * Represents metrics about a slide.
 * <p>
 * The metrics reflect the exact bounds of the <i>rendered</i> slide.  This
 * is an important distinction.  The preview panels will use the available
 * area for a display to compute the best fitting preview.  These metrics
 * reflect the best fitting preview size.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlidePreviewMetrics {
	/** The scaling factor used to render the display */
	public double scale;
	
	/** The integer width of the scaled display */
	public int width;
	
	/** The integer height of the scaled display */
	public int height;
	
	/** The integer text height for the display name */
	public int textHeight;
	
	/** The integer total width for the display (includes all preview add-ons like shadow, borders, etc.) */
	public int totalWidth;
	
	/** The integer total height for the display (includes all preview add-ons like shadow, borders, etc.) */
	public int totalHeight;
}
