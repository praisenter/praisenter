package org.praisenter.display;

import java.awt.Dimension;

/**
 * Represents a background on a {@link Display}.
 * <p>
 * A background must know the size in which it is supposed to render.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class BackgroundComponent extends DisplayComponent {
	/** The size of the background to render */
	protected Dimension size;
	
	/**
	 * Minimal constructor.
	 * @param name the name of this component
	 * @param size the size of the background
	 */
	protected BackgroundComponent(String name, Dimension size) {
		super(name);
		this.size = size;
	}
	
	/**
	 * Returns the width of the background.
	 * @return int
	 */
	public int getWidth() {
		return this.size.width;
	}
	
	/**
	 * Returns the height of the background.
	 * @return int
	 */
	public int getHeight() {
		return this.size.height;
	}
	
	/**
	 * Sets the size of this background.
	 * @param size the size
	 */
	public void setSize(Dimension size) {
		this.size = size;
		this.setDirty(true);
	}
	
	/**
	 * Sets the width of the background.
	 * @param width the width
	 */
	public void setWidth(int width) {
		this.size.setSize(width, this.size.height);
		this.setDirty(true);
	}
	
	/**
	 * Sets the height of the background.
	 * @param height the height
	 */
	public void setHeight(int height) {
		this.size.setSize(this.size.width, height);
		this.setDirty(true);
	}
}
