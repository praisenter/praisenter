package org.praisenter.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * Represents a full screen display with a background.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class FullScreenDisplay extends Display {
	// background
	
	/** The still background component */
	protected GraphicsComponent background;
	
	/**
	 * Minimal constructor.
	 * @param displaySize the target display size
	 */
	public FullScreenDisplay(Dimension displaySize) {
		super(displaySize);
		this.background = null;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.Display#render(java.awt.Graphics2D)
	 */
	public void render(Graphics2D graphics) {
		// render the main background
		if (this.background != null) {
			this.background.render(graphics);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.Display#invalidate()
	 */
	@Override
	public void invalidate() {
		if (this.background != null) {
			this.background.invalidate();
		}
	}
	
	/**
	 * Creates a new background for this display.
	 * @param name the name of the component
	 * @return {@link GraphicsComponent}
	 */
	public GraphicsComponent createBackgroundComponent(String name) {
		return new GraphicsComponent(name, 0, 0, this.displaySize.width, this.displaySize.height);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.display.Display#setDisplaySize(java.awt.Dimension)
	 */
	public void setDisplaySize(Dimension displaySize) {
		super.setDisplaySize(displaySize);
		// set the size of the background
		if (this.background != null) {
			this.background.setWidth(displaySize.width);
			this.background.setHeight(displaySize.height);
		}
	}
	
	/**
	 * Returns the still background for this {@link FullScreenDisplay}.
	 * @return {@link GraphicsComponent}
	 */
	public GraphicsComponent getBackgroundComponent() {
		return this.background;
	}
	
	/**
	 * Sets the still background for this {@link FullScreenDisplay}.
	 * @param background the image background
	 */
	public void setBackgroundComponent(GraphicsComponent background) {
		this.background = background;
	}
}
