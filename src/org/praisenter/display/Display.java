package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Represents a generic display with sub components.
 * @author William Bittle
 * @version 1.0.0
 */
public abstract class Display {
	/** The default screen padding; used for setup of a default bible display */
	public static final int DEFAULT_SCREEN_PADDING = 30;
	
	// general
	
	/** The display size */
	protected Dimension displaySize;
	
	// backgrounds
	
	/** The color background component */
	protected ColorBackgroundComponent colorBackground;
	
	/** The image background component */
	protected ImageBackgroundComponent imageBackground;
	
	/**
	 * Minimal constructor.
	 * @param displaySize the target display size
	 */
	public Display(Dimension displaySize) {
		this.displaySize = displaySize;
		this.colorBackground = null;
		this.imageBackground = null;
	}
	
	/**
	 * Creates a new {@link ColorBackgroundComponent} for this display.
	 * @param name the name of the component
	 * @return {@link ColorBackgroundComponent}
	 */
	public ColorBackgroundComponent createColorBackgroundComponent(String name) {
		return new ColorBackgroundComponent(name, this.displaySize);
	}
	
	/**
	 * Creates a new {@link ImageBackgroundComponent} for this display.
	 * @param name the name of the component
	 * @return {@link ImageBackgroundComponent}
	 */
	public ImageBackgroundComponent createImageBackgroundComponent(String name) {
		return new ImageBackgroundComponent(name, this.displaySize);
	}
	
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
		// set the size
		this.displaySize = displaySize;
		// set the size of the base background
		if (this.colorBackground != null) {
			this.colorBackground.setSize(displaySize);
		}
		// set the size of the main background
		if (this.imageBackground != null) {
			this.imageBackground.setSize(displaySize);
		}
	}
	
	/**
	 * Returns the color background for this {@link Display}.
	 * @return {@link ColorBackgroundComponent}
	 */
	public ColorBackgroundComponent getColorBackgroundComponent() {
		return this.colorBackground;
	}
	
	/**
	 * Sets the color background for this {@link Display}.
	 * @param background the color background
	 */
	public void setColorBackgroundComponent(ColorBackgroundComponent background) {
		this.colorBackground = background;
	}
	
	/**
	 * Returns the image background for this {@link Display}.
	 * @return {@link ImageBackgroundComponent}
	 */
	public ImageBackgroundComponent getImageBackgroundComponent() {
		return this.imageBackground;
	}
	
	/**
	 * Sets the image background for this {@link Display}.
	 * @param background the image background
	 */
	public void setImageBackgroundComponent(ImageBackgroundComponent background) {
		this.imageBackground = background;
	}
	
	/**
	 * Renders the display to the given graphics object.
	 * @param graphics the graphics object
	 */
	public void render(Graphics2D graphics) {
		// TODO allow ordering of the backgrounds
		
		// render the base background
		if (this.colorBackground != null) {
			this.colorBackground.render(graphics);
		}
		// render the main background
		if (this.imageBackground != null) {
			this.imageBackground.render(graphics);
		}
	}
}
