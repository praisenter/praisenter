package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;

/**
 * Represents a generic display with sub components.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Display {
	/** The default screen padding; used for setup of a default display */
	public static final int DEFAULT_SCREEN_PADDING = 30;
	
	// general
	
	/** The display size */
	protected Dimension displaySize;
	
	/**
	 * Minimal constructor.
	 * @param displaySize the target display size
	 */
	public Display(Dimension displaySize) {
		this.displaySize = displaySize;
	}

	/**
	 * Renders the display to the given graphics object.
	 * @param graphics the graphics object
	 */
	public abstract void render(Graphics2D graphics);
	
	/**
	 * Prepares the display for rendering.
	 * <p>
	 * This is useful when a display is complex and requires time to render.  This allows
	 * the display to be rendered on a background thread.
	 * @param configuration the target device graphics configuration
	 */
	public abstract void prepare(GraphicsConfiguration configuration);
	
	/**
	 * Invalidates any cached resources for this display.
	 */
	public abstract void invalidate();
	
	/**
	 * Returns the target size of this display.
	 * @return Dimension
	 */
	public Dimension getDisplaySize() {
		return this.displaySize;
	}
	
	/**
	 * Sets the target size of this display.
	 * @param displaySize the display size
	 */
	public void setDisplaySize(Dimension displaySize) {
		this.displaySize = displaySize;
	}
}
