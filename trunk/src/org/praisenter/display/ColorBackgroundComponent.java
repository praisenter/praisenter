package org.praisenter.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;

/**
 * Represents a background of a {@link Display} using a solid color.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class ColorBackgroundComponent extends BackgroundComponent {
	/** The color to use */
	protected Color color;
	
	/** The cached image of the color background */
	protected BufferedImage image;
	
	/**
	 * Minimal constructor.
	 * @param name the name of this component
	 * @param size the size of the background
	 */
	public ColorBackgroundComponent(String name, Dimension size) {
		this(name, size, Color.BLACK);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of this component
	 * @param size the size of the background
	 * @param color the color of the background
	 */
	public ColorBackgroundComponent(String name, Dimension size, Color color) {
		super(name, size);
		this.color = color;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.DisplayComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D graphics) {
		// make sure its visible
		if (this.visible) {
			// see if we need to re-generate the background
			if (this.image == null || this.isDirty()) {
				// the image set here may or may not be in this device's best format
				// to avoid the cost of converting the image on each render, we go
				// ahead and convert the given image to an image of the same size
				// that is compatible with this display
				GraphicsConfiguration gc = graphics.getDeviceConfiguration();
				this.image = gc.createCompatibleImage(this.size.width, this.size.height, this.color.getTransparency());
				// blit the original to the new one
				Graphics2D ig = this.image.createGraphics();
				ig.setColor(this.color);
				ig.fillRect(0, 0, this.size.width, this.size.height);
				ig.dispose();
				this.setDirty(false);
			}
			
			graphics.drawImage(this.image, 0, 0, null);
		}
	}
	
	/**
	 * Returns the color used by this background.
	 * @return Color
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Sets the color used by this background.
	 * @param color the color
	 */
	public void setColor(Color color) {
		this.color = color;
		this.setDirty(true);
	}
}
