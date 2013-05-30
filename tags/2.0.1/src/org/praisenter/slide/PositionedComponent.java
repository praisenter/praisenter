/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.slide;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;

import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.LineStyle;

/**
 * Common interface for positioned slide components.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public interface PositionedComponent extends RenderableComponent, SlideComponent, Serializable {
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
