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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import org.apache.log4j.Logger;

/**
 * Class containing helper methods for rendering text.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class TextRenderer {
	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(TextRenderer.class);
	
	/** The line separator character */
	public static final char LINE_SEPARATOR = '\n';
	
	/** Hidding default constructor */
	private TextRenderer() {}
	
	// paragraph methods
	
	/**
	 * Renders a paragraph of text bounded by the given width to the given graphics
	 * object at the given coordinates.
	 * <p>
	 * This method will also break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param properties the text properties
	 */
	public static final void renderParagraph(Graphics2D g2d, String text, TextRenderProperties properties) {
		float x = properties.x;
		float y = properties.y;
		float width = properties.width;
		HorizontalTextAlignment alignment = properties.horizontalAlignment;
		Paint textPaint = properties.textPaint;
		Paint outlinePaint = properties.outlinePaint;
		Stroke outlineStroke = properties.outlineStroke;
		
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, g2d.getFont());
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		// create a line break measurer to measure out lines
		LineBreakMeasurer measurer = new LineBreakMeasurer(it, g2d.getFontRenderContext());
	    
		boolean paintOutline = properties.isOutlinePainted();
		g2d.setPaint(textPaint);
		
		// compute the height by laying out the lines
		boolean isLastLayoutNewLine = false;
	    while (measurer.getPosition() < text.length()) {
	    	// get the expected ending character for this line
	    	int bindex = measurer.getPosition();
	    	int offset = measurer.nextOffset(width);
	    	boolean isTerminatedByNewLine = false;
	    	int index = text.indexOf(TextRenderer.LINE_SEPARATOR, bindex);
	    	if (index >= 0) {
	    		offset = index;
	    		isTerminatedByNewLine = true;
	    	}
	    	TextLayout layout;
	    	// check the offset against the beginning index
	    	if (offset == bindex && isTerminatedByNewLine) {
	    		// the line was terminated by a new line
	    		// move the position by one (to skip over the new line)
	    		measurer.setPosition(bindex + 1);
	    		// check if the last layout was terminated by a new line
	    		if (isLastLayoutNewLine) {
		    		// this will happen if a new line is found and the last
	    			// line was terminated by a new line
		    		layout = new TextLayout(" ", g2d.getFont(), g2d.getFontRenderContext());
		    		isLastLayoutNewLine = true;
	    		} else {
		    		isLastLayoutNewLine = true;
		    		// dont put anything into the layout just yet
	    			continue;
	    		}
	    	} else {
	    		// get the whole line as measured
	    		layout = measurer.nextLayout(width, offset, false);
	    		isLastLayoutNewLine = false;
	    	}
	    	
	    	y += (layout.getAscent());
	    	float dx = 0; 
	    	boolean leftToRight = layout.isLeftToRight();
	    	if (alignment == HorizontalTextAlignment.LEFT) {
	    		if (leftToRight) {
	    			dx = 0;
	    		} else {
	    			dx = layout.getVisibleAdvance() - layout.getAdvance();
	    		}
	    	} else if (alignment == HorizontalTextAlignment.RIGHT) {
	    		if (leftToRight) {
	    			dx = width - layout.getVisibleAdvance();
	    		} else {
	    			dx = 0;
	    		}
	    	} else {
	    		// default to center
	    		if (leftToRight) {
	    			dx = (width - layout.getVisibleAdvance()) * 0.5f;
	    		} else {
	    			dx = (width + layout.getAdvance()) * 0.5f - layout.getAdvance();
	    		}
	    	}
	    	
	    	// paint the text
	    	layout.draw(g2d, x + dx, y);
	    	
	    	// paint the outline if necessary
	    	// painting the outline after painting the text is what must
	    	// be done for good looking outlines
	    	if (paintOutline) {
	    		Stroke oStroke = g2d.getStroke();
	    		g2d.setPaint(outlinePaint);
	    		g2d.setStroke(outlineStroke);
	    		
	    		Shape shape = layout.getOutline(AffineTransform.getTranslateInstance(x + dx, y));
	    		g2d.draw(shape);
	    		
	    		g2d.setStroke(oStroke);
	    		g2d.setPaint(textPaint);
	    	}
	        
	        y += layout.getDescent() + layout.getLeading();
	    }
	}
	
	/**
	 * Returns the bounds of the given text laid out as a paragraph with a maximum width.
	 * <p>
	 * This method will break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param text the string
	 * @param font the font
	 * @param fontRenderContext the font rendering context
	 * @param width the maximum width
	 * @return {@link TextBounds}
	 */
	public static final TextBounds getParagraphBounds(String text, Font font, FontRenderContext fontRenderContext, float width) {
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, font);
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		// create a line break measurer to measure out lines
		LineBreakMeasurer measurer = new LineBreakMeasurer(it, fontRenderContext);
	    
		// compute the height by laying out the lines
	    float h = 0;
	    float w = 0;
	    boolean isLastLayoutNewLine = false;
	    while (measurer.getPosition() < text.length()) {
	    	// get the expected ending character for this line
	    	int bindex = measurer.getPosition();
	    	int offset = measurer.nextOffset(width);
	    	boolean isTerminatedByNewLine = false;
	    	// see if there are any line break characters in this line
	    	int index = text.indexOf(TextRenderer.LINE_SEPARATOR, bindex);
	    	if (index >= 0) {
	    		offset = index;
	    		isTerminatedByNewLine = true;
	    	}
	    	TextLayout layout;
	    	// check the offset against the beginning index
	    	if (offset == bindex && isTerminatedByNewLine) {
	    		measurer.setPosition(bindex + 1);
	    		if (isLastLayoutNewLine) {
		    		// this will happen if a new line is found and the last
	    			// line was terminated by a new line
		    		layout = new TextLayout(" ", font, fontRenderContext);
		    		isLastLayoutNewLine = true;
	    		} else {
	    			isLastLayoutNewLine = true;
	    			// dont put anything into the layout just yet
	    			continue;
	    		}
	    	} else {
	    		// get the whole line as measured
	    		layout = measurer.nextLayout(width, offset, false);
	    		isLastLayoutNewLine = false;
	    	}
	    	// accumulate this lines height
	    	h += layout.getAscent() + layout.getDescent() + layout.getLeading();
	    	// keep the maximum width
	    	float tw = layout.getAdvance();
	    	w = w < tw ? tw : w;
	    }
	    
	    // return the bounds
	    return new TextBounds(w, h);
	}
	
	/**
	 * Returns the {@link TextMetrics} that fills the given width and height with the 
	 * font size bounded by the given maximum.  Will always return a font size
	 * of 1.0 or greater.
	 * <p>
	 * This method will break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * <p>
	 * This method will reduce the font only.
	 * @param font the initial font
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width of the bounds
	 * @param height the height of the bounds
	 * @return float
	 */
	public static final TextMetrics getFittingParagraphMetrics(Font font, FontRenderContext fontRenderContext, String text, float width, float height) {
		return TextRenderer.getFittingParagraphMetrics(font, font.getSize2D(), fontRenderContext, text, width, height);
	}
	
	/**
	 * Returns the {@link TextMetrics} that fills the given width and height with the 
	 * font size bounded by the given maximum.  Will always return a font size
	 * of 1.0 or greater.
	 * <p>
	 * This method will break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param font the initial font
	 * @param max the maximum font size; use Float.MAX_VALUE to specify no maximum size; use the font's current size for reduction only
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width of the bounds
	 * @param height the height of the bounds
	 * @return float
	 */
	public static final TextMetrics getFittingParagraphMetrics(Font font, float max, FontRenderContext fontRenderContext, String text, float width, float height) {
		// get the current font size
		float cur = font.getSize2D();
		// clamp the beginning size to 1
		if (cur < 1.0f) cur = 1.0f;
		// get the initial paragraph height
		TextBounds bounds = getParagraphBounds(text, font, fontRenderContext, width);
		// loop until the text fills the area
		// the if condition allows REDUCE_FONT_ONLY to exit early
		float min = (bounds.height <= height && max != Float.MAX_VALUE) ? max : 1.0f;
		int i = 0;
		while (bounds.height > height || (int)Math.floor(max - min) > 1) {
			// check the paragraph height against the maximum height
			if (bounds.height < height) {
				// we need to binary search up
				min = cur;
				// compute an estimated next size if the maximum begins with Float.MAX_VALUE
				// this is to help convergence to a safe maximum
				float rmax = (max == Float.MAX_VALUE ? height * (cur / bounds.height) : max);
				cur = (float)Math.ceil((cur + rmax) * 0.5f);
				font = font.deriveFont(cur);
			} else {
				// we need to binary search down
				max = cur;
				// get the next test font size
				float temp = (float)Math.floor((min + cur) * 0.5f);
				// do a check for minimum font size
				if (temp <= 1.0f) break;
				// its not the minimum so continue
				cur = temp;
				font = font.deriveFont(cur);
			}
			// get the new paragraph height for the new font size
			bounds = getParagraphBounds(text, font, fontRenderContext, width);
			i++;
		}
		if (i > 0) {
			LOGGER.trace("Font fitting iterations: " + i);
		}
		return new TextMetrics(cur, bounds.width, bounds.height);
	}
	
	// line methods
	
	/**
	 * Renders a line of text bounded by the given width to the given graphics
	 * object at the given coordinates.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param properties the text rendering properties
	 */
	public static final void renderLine(Graphics2D g2d, String text, TextRenderProperties properties) {
		float x = properties.x;
		float y = properties.y;
		float width = properties.width;
		HorizontalTextAlignment alignment = properties.horizontalAlignment;
		Paint textPaint = properties.textPaint;
		Paint outlinePaint = properties.outlinePaint;
		Stroke outlineStroke = properties.outlineStroke;
		
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, g2d.getFont());
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		TextLayout layout = new TextLayout(it, g2d.getFontRenderContext());
		
		boolean paintOutline = properties.isOutlinePainted();
		
		float dx = 0; 
    	boolean leftToRight = layout.isLeftToRight();
    	if (alignment == HorizontalTextAlignment.LEFT) {
    		if (leftToRight) {
    			dx = 0;
    		} else {
    			dx = layout.getVisibleAdvance() - layout.getAdvance();
    		}
    	} else if (alignment == HorizontalTextAlignment.RIGHT) {
    		if (leftToRight) {
    			dx = width - layout.getVisibleAdvance();
    		} else {
    			dx = 0;
    		}
    	} else {
    		// default to center
    		if (leftToRight) {
    			dx = (width - layout.getVisibleAdvance()) * 0.5f;
    		} else {
    			dx = (width + layout.getAdvance()) * 0.5f - layout.getAdvance();
    		}
    	}
        
    	// paint the text
    	g2d.setPaint(textPaint);
    	layout.draw(g2d, x + dx, y + layout.getAscent());
    	
    	// paint the outline if necessary
    	// painting the outline after painting the text is what must
    	// be done for good looking outlines
    	if (paintOutline) {
    		Stroke oStroke = g2d.getStroke();
    		g2d.setPaint(outlinePaint);
    		g2d.setStroke(outlineStroke);
    		
    		Shape shape = layout.getOutline(AffineTransform.getTranslateInstance(x + dx, y + layout.getAscent()));
    		g2d.draw(shape);
    		
    		g2d.setStroke(oStroke);
    	}
	}
	
	/**
	 * Returns the bounds of a single line of text.
	 * @param font the text font
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @return {@link TextBounds}
	 */
	public static final TextBounds getLineBounds(Font font, FontRenderContext fontRenderContext, String text) {
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, font);
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		TextLayout layout = new TextLayout(it, fontRenderContext);
		// get the single line text width
		float tw = layout.getVisibleAdvance();
		float th = layout.getAscent() + layout.getDescent() + layout.getLeading();
		// return the metrics
		return new TextBounds(tw, th);
	}
	
	/**
	 * Returns the metrics of a single line of text reducing the text
	 * to fit the given bounds if necessary.
	 * @param font the text font
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width to fit the text in
	 * @param height the height to fit the text in
	 * @return {@link TextMetrics}
	 */
	public static final TextMetrics getFittingLineMetrics(Font font, FontRenderContext fontRenderContext, String text, float width, float height) {
		return TextRenderer.getFittingLineMetrics(font, font.getSize2D(), fontRenderContext, text, width, height);
	}
	
	/**
	 * Returns the metrics of a single line of text reducing or increasing the text
	 * to fit the given bounds if necessary.
	 * @param font the text font
	 * @param max the maximum; use Float.MAX_VALUE to specify no maximum size; use the current font size to allow only reduction
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width to fit the text in
	 * @param height the height to fit the text in
	 * @return {@link TextMetrics}
	 */
	public static final TextMetrics getFittingLineMetrics(Font font, float max, FontRenderContext fontRenderContext, String text, float width, float height) {
		// get the line bounds
		TextBounds bounds = getLineBounds(font, fontRenderContext, text);
		// return the font size scaled by the difference in widths (or height)
		float fw = width / bounds.width;
		float fh = height / bounds.height;
		// choose the smallest dimension
		float factor = fw < fh ? fw : fh;
		float cur = font.getSize2D();
		// if the scaling factor is less than one (the size must be reduced)
		// or the maximum size is unbounded, then scale the current font size
		if (factor < 1.0 || max == Float.MAX_VALUE) {
			// estimate the font size
			cur *= factor;
			// its possible that the text does not fit within the bounds with the 
			// scaled font size so we still need to perform a search for the font
			// size if it doesnt work
			bounds = getLineBounds(font.deriveFont(cur), fontRenderContext, text);
			if (bounds.width <= width && bounds.height <= height && max != Float.MAX_VALUE) {
				// the estimate was precise enough so use that
				LOGGER.trace("Font fitting iterations: 1");
				return new TextMetrics(cur, bounds.width, bounds.height);
			} else {
				// if the text is still too big or the maximum we passed in was Float.MAX_VALUE then
				// we must binary search for the correct size
				float min = 1.0f;
				int i = 0;
				while (bounds.height > height || bounds.width > width || (int)Math.floor(max - min) > 1) {
					// check the line height against the maximum height and width
					if (bounds.height < height && bounds.width < width) {
						// we need to binary search up
						min = cur;
						// compute an estimated next size if the maximum is Float.MAX_VALUE
						// this is to help convergence to a safe maximum
						float sw = width / bounds.width;
						float sh = height / bounds.height;
						// use the smallest scale, that way we increase the font size conservatively
						float rmax = 0.0f;
						if (max != Float.MAX_VALUE) {
							rmax = max;
						} else if (sw < sh) {
							rmax = cur * sw;
						} else {
							rmax = cur * sh;
						}
						cur = (float)Math.ceil((cur + rmax) * 0.5f);
						font = font.deriveFont(cur);
					} else {
						// we need to binary search down
						max = cur;
						// get the next test font size
						float temp = (float)Math.floor((min + cur) * 0.5f);
						// do a check for minimum font size
						if (temp <= 1.0f) break;
						// its not the minimum so continue
						cur = temp;
						font = font.deriveFont(cur);
					}
					// get the new paragraph height for the new font size
					bounds = getLineBounds(font, fontRenderContext, text);
					i++;
				}
				if (i > 0) {
					LOGGER.trace("Font fitting iterations: " + i);
				}
			}
		} else {
			LOGGER.trace("Font fitting iterations: 0");
		}
		return new TextMetrics(cur, bounds.width, bounds.height);
	}
}