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

import org.praisenter.slide.AbstractSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.effects.SlideShadow;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Abstract implementation of the {@link TextComponent} interface.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class AbstractTextComponent extends AbstractSlideComponent implements SlideRegion, SlideComponent, TextComponent {
	/** The text paint */
	@JsonProperty
	SlidePaint textPaint;
	
	/** The text border */
	@JsonProperty
	SlideStroke textBorder;
	
	/** The font */
	@JsonProperty
	SlideFont font;
	
	/** The font scaling type */
	@JsonProperty
	FontScaleType fontScaleType;
	
	/** The text vertical alignment */
	@JsonProperty
	VerticalTextAlignment verticalTextAlignment;
	
	/** The text horizontal alignment */
	@JsonProperty
	HorizontalTextAlignment horizontalTextAlignment;
	
	/** The bounds padding */
	@JsonProperty
	SlidePadding padding;
	
	/** The line spacing */
	@JsonProperty
	double lineSpacing;
	
	/** True if text should be wrapped */
	@JsonProperty
	boolean textWrapping;

	/** The text shadow */
	@JsonProperty
	SlideShadow textShadow;

	/** The text glow */
	@JsonProperty
	SlideShadow textGlow;
	
	/**
	 * Default constructor.
	 */
	public AbstractTextComponent() {
		super();
		this.textPaint = new SlideColor();
		this.textBorder = null;
		this.fontScaleType = FontScaleType.NONE;
		this.verticalTextAlignment = VerticalTextAlignment.TOP;
		this.horizontalTextAlignment = HorizontalTextAlignment.LEFT;
		this.padding = new SlidePadding();
		this.lineSpacing = 0;
		this.textWrapping = true;
		this.textShadow = null;
		this.textGlow = null;
	}
	
	/**
	 * Copy constructor.
	 * @param other the component to copy
	 * @param exact whether to copy the component exactly
	 */
	public AbstractTextComponent(AbstractTextComponent other, boolean exact) {
		super(other, exact);
		// NOTE: all are immutable
		this.font = other.font;
		this.fontScaleType = other.fontScaleType;
		this.horizontalTextAlignment = other.horizontalTextAlignment;
		this.lineSpacing = other.lineSpacing;
		this.padding = other.padding;
		this.textBorder = other.textBorder;
		this.textGlow = other.textGlow;
		this.textPaint = other.textPaint;
		this.textShadow = other.textShadow;
		this.textWrapping = other.textWrapping;
		this.verticalTextAlignment = other.verticalTextAlignment;
		this.textPaint = other.textPaint;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getTextPaint()
	 */
	@Override
	public SlidePaint getTextPaint() {
		return this.textPaint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setTextPaint(org.praisenter.slide.graphics.SlidePaint)
	 */
	@Override
	public void setTextPaint(SlidePaint paint) {
		if (paint == null) {
			paint = new SlideColor();
		}
		this.textPaint = paint;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getTextBorder()
	 */
	@Override
	public SlideStroke getTextBorder() {
		return this.textBorder;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setTextBorder(org.praisenter.slide.graphics.SlideStroke)
	 */
	@Override
	public void setTextBorder(SlideStroke border) {
		this.textBorder = border;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setFont(org.praisenter.slide.text.SlideFont)
	 */
	@Override
	public void setFont(SlideFont font) {
		if (font == null) {
			font = new SlideFont();
		}
		this.font = font;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getFont()
	 */
	@Override
	public SlideFont getFont() {
		return this.font;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setFontScaleType(org.praisenter.slide.text.FontScaleType)
	 */
	@Override
	public void setFontScaleType(FontScaleType type) {
		if (type == null) {
			type = FontScaleType.NONE;
		}
		this.fontScaleType = type;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getFontScaleType()
	 */
	@Override
	public FontScaleType getFontScaleType() {
		return this.fontScaleType;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setVerticalTextAlignment(org.praisenter.slide.text.VerticalTextAlignment)
	 */
	@Override
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		if (alignment == null) {
			alignment = VerticalTextAlignment.TOP;
		}
		this.verticalTextAlignment = alignment;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getVerticalTextAlignment()
	 */
	@Override
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setHorizontalTextAlignment(org.praisenter.slide.text.HorizontalTextAlignment)
	 */
	@Override
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		if (alignment == null) {
			alignment = HorizontalTextAlignment.LEFT;
		}
		this.horizontalTextAlignment = alignment;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getHorizontalTextAlignment()
	 */
	@Override
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setPadding(org.praisenter.slide.graphics.SlidePadding)
	 */
	@Override
	public void setPadding(SlidePadding padding) {
		if (padding == null) {
			padding = new SlidePadding();
		}
		this.padding = padding;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getPadding()
	 */
	@Override
	public SlidePadding getPadding() {
		return this.padding;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getLineSpacing()
	 */
	@Override
	public double getLineSpacing() {
		return lineSpacing;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setLineSpacing(double)
	 */
	@Override
	public void setLineSpacing(double lineSpacing) {
		this.lineSpacing = lineSpacing;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#isTextWrapping()
	 */
	@Override
	public boolean isTextWrapping() {
		return this.textWrapping;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setTextWrapping(boolean)
	 */
	@Override
	public void setTextWrapping(boolean flag) {
		this.textWrapping = flag;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getTextShadow()
	 */
	@Override
	public SlideShadow getTextShadow() {
		return this.textShadow;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setTextShadow(org.praisenter.slide.graphics.SlideShadow)
	 */
	@Override
	public void setTextShadow(SlideShadow shadow) {
		this.textShadow = shadow;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#getTextGlow()
	 */
	@Override
	public SlideShadow getTextGlow() {
		return this.textGlow;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.text.TextComponent#setTextGlow(org.praisenter.slide.graphics.SlideShadow)
	 */
	@Override
	public void setTextGlow(SlideShadow glow) {
		this.textGlow = glow;
	}
}
