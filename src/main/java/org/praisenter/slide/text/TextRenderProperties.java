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

import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.LineStyle;
import org.praisenter.slide.graphics.Point;

/**
 * Class to store the rendering properties of a piece of text that will
 * be rendered by the {@link TextRenderer}.
 * @author William Bittle
 * @version 2.0.2
 * @since 2.0.0
 */
public class TextRenderProperties {
	/** The x coordinate to render relative to */
	protected float x;
	
	/** The y coordinate to render relative to */
	protected float y;
	
	/** The bounds and text metrics */
	protected TextMetrics textMetrics;
	
	/** The vertical alignment */
	protected VerticalTextAlignment verticalAlignment;
	
	/** The text horizontal alignment */
	protected HorizontalTextAlignment horizontalAlignment;
	
	/** The text paint */
	protected SlidePaint textFill;
	
	/** Returns true if the outline is enabled */
	protected boolean outlineEnabled;
	
	/** The text outline paint */
	protected SlidePaint outlineFill;
	
	/** The text outline stroke */
	protected LineStyle outlineStyle;
	
	/** Returns true if the shadow is enabled */
	protected boolean shadowEnabled;
	
	/** The shadow fill */
	protected SlidePaint shadowFill;
	
	/** The shadow offset */
	protected Point shadowOffset;
	
	/**
	 * Minimal constructor.
	 * <p>
	 * The x and y coorindates are defaulted to zero, the vertical alignment
	 * is defaulted to top, the horizontal alignment is defaulted to center, 
	 * the text fill is defaulted to white, and the outline is defaulted to 
	 * off.
	 * <p>
	 * Use the setXXX methods to assign the remaining properties.
	 * @param textMetrics the bounds and text metrics
	 */
	public TextRenderProperties(TextMetrics textMetrics) {
		this.x = 0;
		this.y = 0;
		this.textMetrics = textMetrics;
		this.verticalAlignment = VerticalTextAlignment.TOP;
		this.horizontalAlignment = HorizontalTextAlignment.CENTER;
		this.textFill = new SlideColor(Color.WHITE);
		// no outline by default
		this.outlineEnabled = false;
		this.outlineFill = new SlideColor();
		this.outlineStyle = new LineStyle();
		// no shadow by default
		this.shadowEnabled = false;
		this.shadowFill = new SlideColor();
		this.shadowOffset = new Point();
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
	 * Returns the bounds and text metrics.
	 * @return {@link TextMetrics}
	 */
	public TextMetrics getTextMetrics() {
		return this.textMetrics;
	}
	
	/**
	 * Sets the bounds and text metrics.
	 * @param textMetrics the bounds and text metrics
	 */
	public void setTextMetrics(TextMetrics textMetrics) {
		this.textMetrics = textMetrics;
	}
	
	/**
	 * Returns the vertical text alignment.
	 * @return {@link VerticalTextAlignment}
	 */
	public VerticalTextAlignment getVerticalAlignment() {
		return this.verticalAlignment;
	}
	
	/**
	 * Sets the vertical text alignment.
	 * @param alignment the alignment
	 */
	public void setVerticalAlignment(VerticalTextAlignment alignment) {
		this.verticalAlignment = alignment;
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
	 * @param alignment the alignment
	 */
	public void setHorizontalAlignment(HorizontalTextAlignment alignment) {
		this.horizontalAlignment = alignment;
	}
	
	/**
	 * Returns the fill used for the text.
	 * @return {@link SlidePaint}
	 */
	public SlidePaint getTextFill() {
		return this.textFill;
	}
	
	/**
	 * Sets the fill used for the text.
	 * @param fill the fill for the text
	 */
	public void setTextFill(SlidePaint fill) {
		this.textFill = fill;
	}
	
	/**
	 * Returns true if the outline is enabled.
	 * @return boolean
	 */
	public boolean isOutlineEnabled() {
		return this.outlineEnabled;
	}
	
	/**
	 * Toggles the rendering of the text outline.
	 * <p>
	 * The {@link #getOutlineFill()} and {@link #getOutlineStyle()} must
	 * return non-null values for the outline to be rendered. 
	 * @param outlineEnabled true if the outline should be rendered
	 */
	public void setOutlineEnabled(boolean outlineEnabled) {
		this.outlineEnabled = outlineEnabled;
	}
	
	/**
	 * Returns the paint used for the text outline.
	 * @return {@link SlidePaint}
	 */
	public SlidePaint getOutlineFill() {
		return this.outlineFill;
	}
	
	/**
	 * Sets the fill used for the text outline.
	 * @param fill the fill for the outline; can be null
	 */
	public void setOutlineFill(SlidePaint fill) {
		this.outlineFill = fill;
	}
	
	/**
	 * Returns the line style used for the text outline.
	 * @return {@link LineStyle}
	 */
	public LineStyle getOutlineStyle() {
		return this.outlineStyle;
	}
	
	/**
	 * Sets the line style used for the text outline.
	 * @param style the line style for the outline; can be null
	 */
	public void setOutlineStyle(LineStyle style) {
		this.outlineStyle = style;
	}
	
	/**
	 * Returns true if text shadow is enabled.
	 * @return boolean
	 * @since 2.0.2
	 */
	public boolean isShadowEnabled() {
		return this.shadowEnabled;
	}

	/**
	 * Toggles the rendering of the text shadow.
	 * <p>
	 * The {@link #getShadowFill()}  return non-null values for 
	 * the shadow to be rendered. 
	 * @param shadowEnabled true if the shadow should be rendered
	 * @since 2.0.2
	 */
	public void setShadowEnabled(boolean shadowEnabled) {
		this.shadowEnabled = shadowEnabled;
	}

	/**
	 * Returns the fill used to paint the text shadow.
	 * @return {@link SlidePaint}
	 * @since 2.0.2
	 */
	public SlidePaint getShadowFill() {
		return this.shadowFill;
	}

	/**
	 * Sets the fill used to paint the text shadow.
	 * @param fill the shadow fill
	 * @since 2.0.2
	 */
	public void setShadowFill(SlidePaint fill) {
		this.shadowFill = fill;
	}

	/**
	 * Returns the text shadow offset.
	 * @return {@link Point}
	 * @since 2.0.2
	 */
	public Point getShadowOffset() {
		return this.shadowOffset;
	}

	/**
	 * Sets the text shadow offset.
	 * @param offset the offset in pixels
	 * @since 2.0.2
	 */
	public void setShadowOffset(Point offset) {
		this.shadowOffset = offset;
	}
}
