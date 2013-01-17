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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.GenericComponent;
import org.praisenter.slide.PositionedComponent;
import org.praisenter.slide.RenderableComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.graphics.ColorFill;
import org.praisenter.slide.graphics.Fill;
import org.praisenter.slide.graphics.FillTypeAdapter;
import org.praisenter.slide.graphics.LinearGradientFill;
import org.praisenter.slide.graphics.RadialGradientFill;
import org.praisenter.utilities.FontManager;
import org.praisenter.xml.FontTypeAdapter;

/**
 * Represents a component that displays text.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
// TODO add text shadow (color, direction, width, visible)
// TODO add text outline (stroke, paint, visible)
// TODO make a static text component that shows the current date/time
@XmlRootElement(name = "TextComponent")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({
	ColorFill.class,
	LinearGradientFill.class,
	RadialGradientFill.class
})
public class TextComponent extends GenericComponent implements SlideComponent, RenderableComponent, PositionedComponent {
	// TODO change this to a AttributedString so we can support all kinds of string formatting (highlighting, super/sub scripts, etc)
	/** The text */
	@XmlElement(name = "Text", required = false, nillable = true)
	protected String text;
	
	/** The text color */
	@XmlElement(name = "TextFill")
	@XmlJavaTypeAdapter(value = FillTypeAdapter.class)
	protected Fill textFill;
	
	/** The text font */
	@XmlElement(name = "TextFont", required = false, nillable = true)
	@XmlJavaTypeAdapter(value = FontTypeAdapter.class)
	protected Font textFont;
	
	/** The horizontal text alignment */
	@XmlElement(name = "HorizontalTextAlignment", required = false, nillable = true)
	protected HorizontalTextAlignment horizontalTextAlignment;
	
	/** The vertical text alignment */
	@XmlElement(name = "VerticalTextAlignment", required = false, nillable = true)
	protected VerticalTextAlignment verticalTextAlignment; 
	
	/** The font scale type */
	@XmlElement(name = "FontScaleType", required = false, nillable = true)
	protected FontScaleType textFontScaleType;
	
	/** True if this text should wrap */
	@XmlElement(name = "TextWrapped", required = false, nillable = true)
	protected boolean textWrapped;
	
	/** The inner component padding */
	@XmlElement(name = "TextPadding", required = false, nillable = true)
	protected int textPadding;
	
	/** True if the text should be visible */
	@XmlElement(name = "TextVisible", required = false, nillable = true)
	protected boolean textVisible;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected TextComponent() {
		super(null, 0, 0, 0, 0);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public TextComponent(String name, int width, int height) {
		this(name, 0, 0, width, height, null);
	}
	
	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 */
	public TextComponent(String name, int x, int y, int width, int height) {
		this(name, x, y, width, height, null);
	}

	/**
	 * Optional constructor.
	 * @param name the name of the component
	 * @param x the x coordinate in pixels
	 * @param y the y coordinate in pixels
	 * @param width the width in pixels
	 * @param height the height in pixels
	 * @param text the text
	 */
	public TextComponent(String name, int x, int y, int width, int height, String text) {
		super(name, x, y, width, height);
		this.text = text;
		this.textFill = new ColorFill(Color.WHITE);
		this.textFont = null;
		this.horizontalTextAlignment = HorizontalTextAlignment.CENTER;
		this.verticalTextAlignment = VerticalTextAlignment.TOP;
		this.textFontScaleType = FontScaleType.REDUCE_SIZE_ONLY;
		this.textWrapped = true;
		this.textPadding = 5;
		this.textVisible = true;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public TextComponent(TextComponent component) {
		super(component);
		this.text = component.text;
		this.textFill = component.textFill;
		this.textFont = component.textFont;
		this.horizontalTextAlignment = component.horizontalTextAlignment;
		this.verticalTextAlignment = component.verticalTextAlignment;
		this.textFontScaleType = component.textFontScaleType;
		this.textWrapped = component.textWrapped;
		this.textPadding = component.textPadding;
		this.textVisible = component.textVisible;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#copy()
	 */
	@Override
	public TextComponent copy() {
		return new TextComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#render(java.awt.Graphics2D)
	 */
	@Override
	public void render(Graphics2D g) {
		// render the background/border
		super.render(g);
		// only render the text if its visible
		if (this.textVisible) {
			// render the text
			this.renderText(g);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.GenericSlideComponent#renderPreview(java.awt.Graphics2D)
	 */
	@Override
	public void renderPreview(Graphics2D g) {
		// render the background/border
		super.renderPreview(g);
		// only render the text if its visible
		if (this.textVisible) {
			// render the text
			this.renderText(g);
		}
	}
	
	/**
	 * Renders the text to the given graphics object.
	 * @param g the graphics object to render to
	 */
	protected void renderText(Graphics2D g) {
		if (this.text != null && this.text.trim().length() > 0) {
			// compute the real width
			int rw = this.getTextWidth();
			int rh = this.getTextHeight();

			// set the font
			Font font = this.textFont;
			if (font == null) {
				// if the given font is null then use the default font
				font = FontManager.getDefaultFont();
			}
			
			// make sure the available width and height is greater
			// than zero before trying to fit the text
			if (rw > 0 && rh > 0) {
				// save the old font and color
				Font oFont = g.getFont();
				Paint oPaint = g.getPaint();
				Shape oClip = g.getClip();
				
				String text = this.text;
				// make sure the line break characters are correct
				text = text.replaceAll("(\\r\\n)|(\\r)", String.valueOf(TextRenderer.LINE_SEPARATOR));
				// get the text metrics
				TextMetrics metrics = null;
				// check the font scaling method
				if (this.textFontScaleType == FontScaleType.REDUCE_SIZE_ONLY) {
					// check the wrap flag
					if (this.textWrapped) {
						// get a scaled font size to fit the width and height but is maxed at the current font size
						metrics = TextRenderer.getFittingParagraphMetrics(font, g.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						metrics = TextRenderer.getFittingLineMetrics(font, g.getFontRenderContext(), text, rw, rh);
					}
				} else if (this.textFontScaleType == FontScaleType.BEST_FIT) {
					// check the wrap flag
					if (this.textWrapped) {
						// get a scaled font size to fit the width and height but is maxed at the current font size
						metrics = TextRenderer.getFittingParagraphMetrics(font, Float.MAX_VALUE, g.getFontRenderContext(), text, rw, rh);
					} else {
						// get a scaled font size to fit the entire line on one line
						metrics = TextRenderer.getFittingLineMetrics(font, Float.MAX_VALUE, g.getFontRenderContext(), text, rw, rh);
					}
				} else {
					// get the bounds without modifying the font size
					TextBounds bounds = null;
					if (this.textWrapped) {
						bounds = TextRenderer.getParagraphBounds(text, font, g.getFontRenderContext(), rw);
					} else {
						bounds = TextRenderer.getLineBounds(font, g.getFontRenderContext(), text);
					}
					metrics = new TextMetrics(font.getSize2D(), bounds.width, bounds.height);
				}
				
				// see if we need to derive the font
				if (font.getSize2D() != metrics.fontSize) {
					g.setFont(font.deriveFont(metrics.fontSize));
				} else {
					g.setFont(font);
				}
				
				// vertical align top is easy, just render at y=0
				float x = this.x + this.textPadding;
				float y = this.y + this.textPadding;
				if (this.verticalTextAlignment == VerticalTextAlignment.CENTER) {
					y += ((float)rh - metrics.height) / 2.0f;
				} else if (this.verticalTextAlignment == VerticalTextAlignment.BOTTOM) {
					y += (float)rh - metrics.height;
				}
				
				// set the text color
				if (this.textFill != null) {
					// make sure the fill is using the text metrics rather than the
					// text components bounds
					float fx = x;
					// we also need to take the horizontal alignment into consideration
					if (this.horizontalTextAlignment == HorizontalTextAlignment.RIGHT) {
						fx = this.x + (this.width - this.textPadding - metrics.width);
					} else if (this.horizontalTextAlignment == HorizontalTextAlignment.CENTER) {
						fx = this.x + (this.width / 2.0f) - metrics.width / 2.0f;
					}
					Paint paint = this.textFill.getPaint(
							(int)Math.floor(fx), 
							(int)Math.floor(y), 
							(int)Math.ceil(metrics.width), 
							(int)Math.ceil(metrics.height));
					g.setPaint(paint);
				} else {
					g.setPaint(Color.WHITE);
				}
				// setup the clip region
				g.clipRect(this.x, this.y, this.width, this.height);
				if (this.textWrapped) {
					// render the text as a paragraph
					TextRenderer.renderParagraph(g, text, this.horizontalTextAlignment, x, y, rw);
				} else {
					// render the text as a line
					TextRenderer.renderLine(g, text, this.horizontalTextAlignment, x, y, rw);
				}
				
				g.setClip(oClip);
				g.setPaint(oPaint);
				g.setFont(oFont);
			}
		}
	}
	
	/**
	 * This returns the available width to render the text.
	 * @return int
	 */
	protected int getTextWidth() {
		return this.width - this.textPadding * 2 - 2;
	}
	
	/**
	 * This returns the available height to render the text.
	 * @return int
	 */
	protected int getTextHeight() {
		return this.height - this.textPadding * 2 - 2;
	}
	
	/**
	 * Returns the text of this text component.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Sets the text of this component.
	 * @param text the text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Returns the text fill.
	 * @return {@link Fill}
	 */
	public Fill getTextFill() {
		return this.textFill;
	}
	
	/**
	 * Sets the text fill.
	 * @param fill the text fill
	 */
	public void setTextFill(Fill fill) {
		this.textFill = fill;
	}
	
	/**
	 * Returns the text font.
	 * @return Font
	 */
	public Font getTextFont() {
		return this.textFont;
	}
	
	/**
	 * Sets the text font.
	 * @param font the font
	 */
	public void setTextFont(Font font) {
		this.textFont = font;
	}
	
	/**
	 * Returns the horizontal text alignment.
	 * @return {@link HorizontalTextAlignment}
	 */
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment;
	}
	
	/**
	 * Sets the horizontal text alignment.
	 * @param alignment the alignment
	 */
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		this.horizontalTextAlignment = alignment;
	}

	/**
	 * Returns the vertical text alignment.
	 * @return {@link VerticalTextAlignment}
	 */
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment;
	}
	
	/**
	 * Sets the vertical text alignment.
	 * @param alignment the alignment
	 */
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		this.verticalTextAlignment = alignment;
	}
	
	/**
	 * Returns the text padding.
	 * @return int
	 */
	public int getTextPadding() {
		return this.textPadding;
	}
	
	/**
	 * Sets the text padding.
	 * @param padding the padding
	 */
	public void setTextPadding(int padding) {
		this.textPadding = padding;
	}
	
	/**
	 * Returns the font scale type.
	 * @return {@link FontScaleType}
	 */
	public FontScaleType getTextFontScaleType() {
		return this.textFontScaleType;
	}
	
	/**
	 * Sets the font scale type.
	 * @param fontScaleType the font scale type
	 */
	public void setTextFontScaleType(FontScaleType fontScaleType) {
		this.textFontScaleType = fontScaleType;
	}
	
	/**
	 * Returns true if this text wraps.
	 * @return boolean
	 */
	public boolean isTextWrapped() {
		return this.textWrapped;
	}
	
	/**
	 * Sets the text to wrap inside this component.
	 * @param flag true to wrap the text
	 */
	public void setTextWrapped(boolean flag) {
		this.textWrapped = flag;
	}

	/**
	 * Returns true if the text is visible.
	 * @return boolean
	 */
	public boolean isTextVisible() {
		return this.textVisible;
	}

	/**
	 * Toggles the visibility of the text.
	 * @param visible true if the text should be visible
	 */
	public void setTextVisible(boolean visible) {
		this.textVisible = visible;
	}
}
