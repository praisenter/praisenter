package org.praisenter.slide;

import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * Common interface for positioned slide components.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PositionedSlideComponent extends SlideComponent, RenderableSlideComponent {
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
	 * Returns the paint used to paint the border.
	 * @return Paint
	 */
	public abstract Paint getBorderPaint();
	
	/**
	 * Sets the paint used to paint the border.
	 * @param paint the paint
	 */
	public abstract void setBorderPaint(Paint paint);
	
	/**
	 * Returns the border stroke.
	 * @return Stroke
	 */
	public abstract Stroke getBorderStroke();
	
	/**
	 * Sets the border stroke.
	 * @param stroke the stroke to use for the border
	 */
	public abstract void setBorderStroke(Stroke stroke);
	
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
