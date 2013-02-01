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
package org.praisenter.slide.text;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * Class to store the rendering properties of a piece of text that will
 * be rendered by the {@link TextRenderer}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class TextRenderProperties {
	/** The x coordinate to render relative to */
	protected float x;
	
	/** The y coordinate to render relative to */
	protected float y;
	
	/** The constrained width of the rendered text (for wrapping or font scaling) */
	protected float width;
	
	/** The text horizontal alignment */
	protected HorizontalTextAlignment horizontalAlignment;
	
	/** The text paint */
	protected Paint textPaint;
	
	/** The text outline paint */
	protected Paint outlinePaint;
	
	/** The text outline stroke */
	protected Stroke outlineStroke;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * The x and y coorindates are defaulted to zero, the horizontal alignment
	 * is defaulted to center, the text paint is defaulted to white, and the
	 * outline paint/stroke is defaulted to null.
	 * <p>
	 * Use the setXXX methods to assign the remaining properties.
	 * @param width the width the text is bound by
	 */
	public TextRenderProperties(float width) {
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.horizontalAlignment = HorizontalTextAlignment.CENTER;
		this.textPaint = Color.WHITE;
		// no outline by default
		this.outlinePaint = null;
		this.outlineStroke = null;
	}
	
	/**
	 * Returns true if the outline should be painted.
	 * <p>
	 * The outline will be painted if both the outline paint and outline
	 * stroke are non-null.
	 * @return boolean
	 */
	public boolean isOutlinePainted() {
		return this.outlinePaint != null && this.outlineStroke != null;
	}
	
	/**
	 * Returns the x coordinate of the rendering.
	 * @return float
	 */
	public float getX() {
		return this.x;
	}
	
	/**
	 * Sets the x coordinate of the rendering.
	 * @param x the x coordinate in pixels
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Returns the y coordinate of the rendering.
	 * @return float
	 */
	public float getY() {
		return this.y;
	}
	
	/**
	 * Sets the y coordinate of the rendering.
	 * @param y the y coordinate in pixels
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	/**
	 * Returns the width the text is bound by.
	 * @return float
	 */
	public float getWidth() {
		return this.width;
	}
	
	/**
	 * Sets the width the text is bound by.
	 * @param width the width in pixels
	 */
	public void setWidth(float width) {
		this.width = width;
	}
	
	/**
	 * Returns the horizontal text alignment.
	 * @return {@link HorizontalTextAlignment}
	 */
	public HorizontalTextAlignment getHorizontalAlignment() {
		return this.horizontalAlignment;
	}
	
	/**
	 * Sets the horizontal text alignment.
	 * @param horizontalAlignment the alignment
	 */
	public void setHorizontalAlignment(HorizontalTextAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
	}
	
	/**
	 * Returns the paint used for the text.
	 * @return Paint
	 */
	public Paint getTextPaint() {
		return this.textPaint;
	}
	
	/**
	 * Sets the paint used for the text.
	 * @param textPaint the paint for the text
	 */
	public void setTextPaint(Paint textPaint) {
		this.textPaint = textPaint;
	}
	
	/**
	 * Returns the paint used for the text outline.
	 * @return Paint
	 */
	public Paint getOutlinePaint() {
		return this.outlinePaint;
	}
	
	/**
	 * Sets the paint used for the text outline.
	 * @param outlinePaint the paint for the outline; can be null
	 */
	public void setOutlinePaint(Paint outlinePaint) {
		this.outlinePaint = outlinePaint;
	}
	
	/**
	 * Returns the stroke used for the text outline.
	 * @return Stroke
	 */
	public Stroke getOutlineStroke() {
		return this.outlineStroke;
	}
	
	/**
	 * Sets the stroke used for the text outline.
	 * @param outlineStroke the stroke for the outline; can be null
	 */
	public void setOutlineStroke(Stroke outlineStroke) {
		this.outlineStroke = outlineStroke;
	}
}
