package org.praisenter.slide;

import java.awt.Rectangle;
import java.awt.Shape;

import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LineStyle;

/**
 * Common interface for positioned slide components.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public interface PositionedComponent extends SlideComponent, RenderableComponent {
	/**
	 * Returns the x coordinate in slide space.
	 * @return int
	 */
	public abstract int getX();
	
	/**
	 * Returns the y coordinate in slide space.
	 * @return int
	 */
	public abstract int getY();
	
	/**
	 * Sets the x coordinate.
	 * @param x the x coordinate in slide space
	 */
	public abstract void setX(int x);
	
	/**
	 * Sets the y coordinate.
	 * @param y the y coordinate in slide space
	 */
	public abstract void setY(int y);
	
	/**
	 * Translates this component by the given deltas.
	 * @param dx the change in x
	 * @param dy the change in y
	 */
	public abstract void translate(int dx, int dy);
	
	/**
	 * Retuns the shape of the bounds of this component.
	 * @return Shape
	 */
	public abstract Shape getBounds();
	
	/**
	 * Returns a Rectangle of the bounds of this component.
	 * @return Rectangle
	 */
	public abstract Rectangle getRectangleBounds();
	
	// rendering
	
	/**
	 * Returns the fill used to paint the border.
	 * @return {@link Fill}
	 */
	public abstract Fill getBorderFill();
	
	/**
	 * Sets the fill used to paint the border.
	 * @param fill the fill
	 */
	public abstract void setBorderFill(Fill fill);
	
	/**
	 * Returns the border style.
	 * @return {@link LineStyle}
	 */
	public abstract LineStyle getBorderStyle();
	
	/**
	 * Sets the border style.
	 * @param lineStyle the line style to use for the border
	 */
	public abstract void setBorderStyle(LineStyle lineStyle);
	
	/**
	 * Returns true if the border is visible (or will be rendered).
	 * @return boolean
	 */
	public abstract boolean isBorderVisible();
	
	/**
	 * Sets the border to visible (or rendered).
	 * @param visible true if the border should be rendered
	 */
	public abstract void setBorderVisible(boolean visible);
}
