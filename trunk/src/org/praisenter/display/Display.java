package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

import org.praisenter.resources.Messages;

/**
 * Represents a generic display with sub components.
 * @author William Bittle
 * @version 1.0.0
 */
public abstract class Display {
	/** The default screen padding; used for setup of a default display */
	public static final int DEFAULT_SCREEN_PADDING = 30;
	
	// general
	
	/** The display name; primarily used for previewing */
	protected String name;
	
	/** The display size */
	protected Dimension displaySize;
	
	// backgrounds
	
	/** The still background component */
	protected StillBackgroundComponent stillBackground;
	
	/**
	 * Minimal constructor.
	 * @param displaySize the target display size
	 */
	public Display(Dimension displaySize) {
		this(Messages.getString("display.name.default"), displaySize);
	}
	
	/**
	 * Optional constructor.
	 * @param name the display name
	 * @param displaySize the target display size
	 */
	public Display(String name, Dimension displaySize) {
		this.name = name;
		this.displaySize = displaySize;
		this.stillBackground = null;
	}
	
	/**
	 * Creates a new {@link StillBackgroundComponent} for this display.
	 * @param name the name of the component
	 * @return {@link StillBackgroundComponent}
	 */
	public StillBackgroundComponent createStillBackgroundComponent(String name) {
		return new StillBackgroundComponent(name, this.displaySize);
	}

	/**
	 * Returns the display name.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the display name.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
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
		// set the size of the main background
		if (this.stillBackground != null) {
			this.stillBackground.setSize(displaySize);
		}
	}
	
	/**
	 * Returns the still background for this {@link Display}.
	 * @return {@link StillBackgroundComponent}
	 */
	public StillBackgroundComponent getStillBackgroundComponent() {
		return this.stillBackground;
	}
	
	/**
	 * Sets the still background for this {@link Display}.
	 * @param background the image background
	 */
	public void setStillBackgroundComponent(StillBackgroundComponent background) {
		this.stillBackground = background;
	}
	
	/**
	 * Renders the display to the given graphics object.
	 * @param graphics the graphics object
	 */
	public void render(Graphics2D graphics) {
		// render the main background
		if (this.stillBackground != null) {
			this.stillBackground.render(graphics);
		}
	}
}
