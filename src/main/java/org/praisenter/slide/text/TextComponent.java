/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

/**
 * Represents a component of a slide that displays text.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlSeeAlso({
	BasicTextComponent.class,
	DateTimeComponent.class,
	TextPlaceholderComponent.class
})
public interface TextComponent extends SlideRegion, SlideComponent {
	/**
	 * Returns the text paint.
	 * @return {@link SlidePaint}
	 */
	public abstract SlidePaint getTextPaint();
	
	/**
	 * Sets the text paint.
	 * @param paint the paint; color, gradient, or image
	 */
	public abstract void setTextPaint(SlidePaint paint);
	
	/**
	 * Returns the text border.
	 * @return {@link SlideStroke}
	 */
	public abstract SlideStroke getTextBorder();
	
	/**
	 * Sets the text border.
	 * @param border the border
	 */
	public abstract void setTextBorder(SlideStroke border);
	
	/**
	 * Sets the font name.
	 * <p>
	 * A default font will be used in the case the font is not present
	 * at the time of presentation.
	 * @param name the font name
	 */
	public abstract void setFontName(String name);
	// FEATURE have the default font configurable
	
	/**
	 * Returns the font name.
	 * @return String
	 */
	public abstract String getFontName();
	
	/**
	 * Sets the font size.
	 * <p>
	 * Depending on the value of {@link #getFontScaleType()} this font size
	 * may be either the exact or min and/or max size.
	 * @param size the size
	 */
	public abstract void setFontSize(int size);
	
	/**
	 * Returns the desired font size.
	 * @return int
	 */
	public abstract int getFontSize();
	
	/**
	 * Sets the vertical alignment of the text relative to the containing shape.
	 * @param alignment the vertical alignment
	 */
	public abstract void setVerticalTextAlignment(VerticalTextAlignment alignment);
	
	/**
	 * Returns the vertical alignment of the text relative to the containing shape.
	 * @return {@link VerticalTextAlignment}
	 */
	public abstract VerticalTextAlignment getVerticalTextAlignment();
	
	/**
	 * Sets the horizontal alignment of the text.
	 * @param alignment the horizontal alignment
	 */
	public abstract void setHorizontalTextAlignment(HorizontalTextAlignment alignment);
	
	/**
	 * Returns the horizontal alignment of the text.
	 * @return {@link HorizontalTextAlignment}
	 */
	public abstract HorizontalTextAlignment getHorizontalTextAlignment();
	
	/**
	 * Sets the font scaling type.
	 * <p>
	 * This is useful primarily for {@link TextPlaceholderComponent}s since the text
	 * length may vary.  This provides a way to force the text to fit within some bounds
	 * or to scale it to the bounds.
	 * @param type the font scaling type
	 */
	public abstract void setFontScaleType(FontScaleType type);
	
	/**
	 * Returns the font scale type.
	 * @return {@link FontScaleType}
	 */
	public abstract FontScaleType getFontScaleType();
	
	/**
	 * Sets the padding between the text and the shape's edge.
	 * @param padding the padding
	 */
	public abstract void setPadding(double padding);
	
	/**
	 * Returns the padding between the text and the shape's edge.
	 * @return double
	 */
	public abstract double getPadding();
	
	/**
	 * Returns the spacing between lines of text.
	 * @return double
	 */
	public abstract double getLineSpacing();
	
	/**
	 * Sets the line spacing between lines of text.
	 * @param spacing the spacing
	 */
	public abstract void setLineSpacing(double spacing);
	
	/**
	 * Returns the text.
	 * @return String
	 */
	public abstract String getText();
	
	/**
	 * Sets the text.
	 * @param text the text
	 */
	public abstract void setText(String text);
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	public abstract TextComponent copy();
}