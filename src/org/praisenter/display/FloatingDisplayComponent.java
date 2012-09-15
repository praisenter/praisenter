package org.praisenter.display;

import java.awt.Rectangle;

/**
 * Represents a {@link DisplayComponent} that can be moved and resized.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class FloatingDisplayComponent extends DisplayComponent {
	/** The x coordinate of this component */
	protected int x;
	
	/** The y coordinate of this component */
	protected int y;
	
	/** The width of this component */
	protected int width;
	
	/** The height of this component */
	protected int height;
	
	/**
	 * Minimal constructor.
	 * @param name the name of this component
	 * @param width the width
	 * @param height the height
	 */
	public FloatingDisplayComponent(String name, int width, int height) {
		this(name, 0, 0, width, height);
	}

	/**
	 * Full constructor.
	 * @param name the name of this component
	 * @param bounds the bounds
	 */
	public FloatingDisplayComponent(String name, Bounds bounds) {
		this(name, bounds.x, bounds.y, bounds.w, bounds.h);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of this component
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width
	 * @param height the height
	 */
	public FloatingDisplayComponent(String name, int x, int y, int width, int height) {
		super(name);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Translates this component by the given delta x and y.
	 * @param dx the delta x
	 * @param dy the delta y
	 */
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		// moving the component does not invalidate
		// the cached info
	}
	
	/**
	 * Resizes this component.
	 * @param dw the delta width
	 * @param dh the delta height
	 */
	public void resize(int dw, int dh) {
		if (dw == 0 && dh == 0) return;
		// limit the minimum size by 50,50
		if (this.width + dw > 50)
			this.width += dw;
		if (this.height + dh > 50)
			this.height += dh;
		this.setDirty(true);
	}
	
	/**
	 * Resizes this components width.
	 * @param dw the delta width
	 */
	public void resizeWidth(int dw) {
		this.resize(dw, 0);
	}
	
	/**
	 * Resizes this components height.
	 * @param dh the delta height
	 */
	public void resizeHeight(int dh) {
		this.resize(0, dh);
	}
	
	/**
	 * Sets the x coordinate of this component.
	 * @param x the x coordinate
	 */
	public void setX(int x) {
		this.x = x;
		// moving the component does not invalidate
		// the cached info
	}

	/**
	 * Sets the y coordinate of this component.
	 * @param y the y coordinate
	 */
	public void setY(int y) {
		this.y = y;
		// moving the component does not invalidate
		// the cached info
	}

	/**
	 * Sets the width of this component.
	 * @param width the width
	 */
	public void setWidth(int width) {
		this.width = width;
		this.setDirty(true);
	}
	
	/**
	 * Sets the height of this component.
	 * @param height the height
	 */
	public void setHeight(int height) {
		this.height = height;
		this.setDirty(true);
	}
	
	/**
	 * Returns a new rectangle object that encloses the bounds for this component.
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		return new Rectangle(this.x, this.y, this.width, this.height);
	}
	
	/**
	 * Returns the x coordinate.
	 * @return int
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Returns the y coordinate.
	 * @return int
	 */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Returns the width.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the height.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
}
