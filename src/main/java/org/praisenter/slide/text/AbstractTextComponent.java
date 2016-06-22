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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.AbstractSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlidePaintXmlAdapter;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

/**
 * Abstract implementation of the {@link TextComponent} interface.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractTextComponent extends AbstractSlideComponent implements SlideRegion, SlideComponent, TextComponent {
	/** The text paint */
	@XmlElement(name = "textPaint", required = false)
	@XmlJavaTypeAdapter(value = SlidePaintXmlAdapter.class)
	SlidePaint textPaint;
	
	/** The text border */
	@XmlElement(name = "textBorder", required = false)
	SlideStroke textBorder;
	
	/** The font */
	@XmlElement(name = "font", required = false)
	SlideFont font;
	
	/** The font scaling type */
	@XmlElement(name = "fontScaleType", required = false)
	FontScaleType fontScaleType;
	
	/** The text vertical alignment */
	@XmlElement(name = "verticalAlignment", required = false)
	VerticalTextAlignment verticalTextAlignment;
	
	/** The text horizontal alignment */
	@XmlElement(name = "horizontalAlignment", required = false)
	HorizontalTextAlignment horizontalTextAlignment;
	
	/** The bounds padding */
	@XmlElement(name = "padding", required = false)
	SlidePadding padding;
	
	/** The line spacing */
	@XmlElement(name = "lineSpacing", required = false)
	double lineSpacing;
	
	/** True if text should be wrapped */
	@XmlElement(name = "textWrapping", required = false)
	boolean textWrapping;
	
	/**
	 * Default constructor.
	 */
	public AbstractTextComponent() {
		this.fontScaleType = FontScaleType.NONE;
		this.font = null;
		this.verticalTextAlignment = VerticalTextAlignment.TOP;
		this.horizontalTextAlignment = HorizontalTextAlignment.LEFT;
		this.padding = new SlidePadding();
		this.lineSpacing = 0;
		this.textWrapping = true;
	}
	
	/**
	 * Copies over the values of this component to the given component.
	 * @param to the component to copy to
	 */
	protected void copy(AbstractTextComponent to) {
		// copy the super class stuff
		to.copy((SlideComponent)to);
		// copy the text component stuff
		to.setTextPaint(this.textPaint);
		to.setTextBorder(this.textBorder);
		to.setFont(this.font);
		to.setFontScaleType(this.fontScaleType);
		to.setVerticalTextAlignment(this.verticalTextAlignment);
		to.setHorizontalTextAlignment(this.horizontalTextAlignment);
		to.setPadding(this.padding);
		to.setLineSpacing(this.lineSpacing);
		to.setTextWrapping(this.textWrapping);
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
}
