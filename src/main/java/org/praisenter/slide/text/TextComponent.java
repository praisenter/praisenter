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
import org.praisenter.slide.effects.SlideShadow;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a component of a slide that displays text.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@Type(value = BasicTextComponent.class, name = "text"),
	@Type(value = CountdownComponent.class, name = "countdown"),
	@Type(value = DateTimeComponent.class, name = "datetime"),
	@Type(value = TextPlaceholderComponent.class, name = "placeholder")
})
@XmlSeeAlso({
	BasicTextComponent.class,
	DateTimeComponent.class,
	TextPlaceholderComponent.class,
	CountdownComponent.class
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
	 * Returns the text shadow.
	 * @return {@link SlideShadow}
	 */
	public abstract SlideShadow getTextShadow();
	
	/**
	 * Sets the text shadow.
	 * @param shadow the shadow
	 */
	public abstract void setTextShadow(SlideShadow shadow);

	/**
	 * Returns the text glow.
	 * @return {@link SlideShadow}
	 */
	public abstract SlideShadow getTextGlow();
	
	/**
	 * Sets the text glow.
	 * @param glow the glow
	 */
	public abstract void setTextGlow(SlideShadow glow);
	
	/**
	 * Sets the font.
	 * @param font the font
	 */
	public abstract void setFont(SlideFont font);
	
	/**
	 * Returns the font.
	 * @return {@link SlideFont}
	 */
	public abstract SlideFont getFont();
	
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
	public abstract void setPadding(SlidePadding padding);
	
	/**
	 * Returns the padding between the text and the shape's edge.
	 * @return {@link SlidePadding}
	 */
	public abstract SlidePadding getPadding();
	
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
	 * Returns true if text wrapping is enabled.
	 * @return boolean
	 */
	public abstract boolean isTextWrapping();
	
	/**
	 * Sets whether text wrapping is enabled.
	 * @param flag true to enable text wrapping
	 */
	public abstract void setTextWrapping(boolean flag);
	
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
