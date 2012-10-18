package org.praisenter.display;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import org.apache.log4j.Logger;

/**
 * Class containing helper methods for rendering text.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// FIXME need to add options for vertical alignment of text
public class TextRenderer {
	/** The static logger */
	private static final Logger LOGGER = Logger.getLogger(TextRenderer.class);
	
	/** The line separator character */
	public static final char LINE_SEPARATOR = '\n';
	
	// paragraph methods
	
	/**
	 * Renders a paragraph of text bounded by the given width to the given graphics
	 * object at the given coordinates.
	 * <p>
	 * This method will by default render to (0, 0) coordinates and use center alignment.
	 * <p>
	 * This method will also break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param width the maximum width
	 */
	public static final void renderParagraph(Graphics2D g2d, String text, float width) {
		TextRenderer.renderParagraph(g2d, text, TextAlignment.CENTER, 0, 0, width);
	}
	
	/**
	 * Renders a paragraph of text bounded by the given width to the given graphics
	 * object at the given coordinates.
	 * <p>
	 * This method will default to center alignment of the text.
	 * <p>
	 * This method will also break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the maximum width
	 */
	public static final void renderParagraph(Graphics2D g2d, String text, float x, float y, float width) {
		TextRenderer.renderParagraph(g2d, text, TextAlignment.CENTER, x, y, width);
	}
	
	/**
	 * Renders a paragraph of text bounded by the given width to the given graphics
	 * object at the given coordinates.
	 * <p>
	 * This method will by default render to (0, 0) coordinates.
	 * <p>
	 * This method will also break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param alignment the text alignment
	 * @param width the maximum width
	 */
	public static final void renderParagraph(Graphics2D g2d, String text, TextAlignment alignment, float width) {
		TextRenderer.renderParagraph(g2d, text, alignment, 0, 0, width);
	}
	
	/**
	 * Renders a paragraph of text bounded by the given width to the given graphics
	 * object at the given coordinates.
	 * <p>
	 * This method will also break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param alignment the text alignment
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the maximum width
	 */
	public static final void renderParagraph(Graphics2D g2d, String text, TextAlignment alignment, float x, float y, float width) {
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, g2d.getFont());
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		// create a line break measurer to measure out lines
		LineBreakMeasurer measurer = new LineBreakMeasurer(it, g2d.getFontRenderContext());
	    
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
	    	if (alignment == TextAlignment.LEFT) {
	    		if (leftToRight) {
	    			dx = 0;
	    		} else {
	    			dx = layout.getVisibleAdvance() - layout.getAdvance();
	    		}
	    	} else if (alignment == TextAlignment.RIGHT) {
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
	        
	        layout.draw(g2d, x + dx, y);
	        y += layout.getDescent() + layout.getLeading();
	    }
	}
	
	/**
	 * Returns the height of the given text laid out as a paragraph with a maximum width.
	 * <p>
	 * This method will break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param text the string
	 * @param font the font
	 * @param fontRenderContext the font rendering context
	 * @param width the maximum width
	 * @return float
	 */
	public static final float getParagraphHeight(String text, Font font, FontRenderContext fontRenderContext, float width) {
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, font);
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		// create a line break measurer to measure out lines
		LineBreakMeasurer measurer = new LineBreakMeasurer(it, fontRenderContext);
	    
		// compute the height by laying out the lines
	    float h = 0;
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
	    }
	    
	    // return the height
	    return h;
	}
	
	/**
	 * Returns the font size that fills the given width and height with the 
	 * font size bounded by the given font's current size.
	 * <p>
	 * This method assumes the current font size specified in font is the maximum font size.
	 * <p>
	 * This method will break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param font the initial font
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width of the bounds
	 * @param height the height of the bounds
	 * @return float
	 */
	public static final float getFittingParagraphFontSize(Font font, FontRenderContext fontRenderContext, String text, float width, float height) {
		return TextRenderer.getFittingParagraphFontSize(font, font.getSize2D(), fontRenderContext, text, width, height);
	}
	
	/**
	 * Returns the font size that fills the given width and height with the 
	 * font size bounded by the given maximum.  Will always return a font size
	 * of 1.0 or greater.
	 * <p>
	 * This method will break on new line characters specified by {@link TextRenderer#LINE_SEPARATOR}.
	 * @param font the initial font
	 * @param max the maximum font size; use Float.MAX_VALUE to specify no maximum size
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width of the bounds
	 * @param height the height of the bounds
	 * @return float
	 */
	public static final float getFittingParagraphFontSize(Font font, float max, FontRenderContext fontRenderContext, String text, float width, float height) {
		// get the current font size
		float cur = font.getSize2D();
		// clamp the beginning size to 1
		if (cur < 1.0f) cur = 1.0f;
		// get the initial paragraph height
		float h = getParagraphHeight(text, font, fontRenderContext, width);
		// loop until the text fills the area
		// the if condition allows REDUCE_FONT_ONLY to exit early
		float min = (h <= height && max != Float.MAX_VALUE) ? max : 1.0f;
		int i = 0;
		while (h > height || max - min > 1.0f) {
			// check the paragraph height against the maximum height
			if (h < height) {
				// we need to binary search up
				min = cur;
				// compute an estimated next size if the maximum begins with Float.MAX_VALUE
				// this is to help convergence to a safe maximum
				float rmax = (max == Float.MAX_VALUE ? height * (cur / h) : max);
				cur = (float)Math.ceil((cur + rmax) * 0.5f);
				font = font.deriveFont(cur);
			} else {
				// we need to binary search down
				max = cur;
				cur = (float)Math.floor((min + cur) * 0.5f);
				// do a check for minimum font size
				if (cur <= 1.0f) break;
				font = font.deriveFont(cur);
			}
			// get the new paragraph height for the new font size
			h = getParagraphHeight(text, font, fontRenderContext, width);
			i++;
		}
		LOGGER.debug("Font fitting iterations: " + i);
		return cur;
	}
	
	// line methods
	
	/**
	 * Renders a line of text bounded by the given width to the given graphics object.
	 * <p>
	 * This method will by default render to (0, 0) coordinates and use center alignment.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param width the maximum width
	 */
	public static final void renderLine(Graphics2D g2d, String text, int width) {
		TextRenderer.renderLine(g2d, text, TextAlignment.CENTER, 0, 0, width);
	}
	
	/**
	 * Renders a line of text bounded by the given width to the given graphics object at the given coordinates.
	 * <p>
	 * This method will default to center alignment of the text.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the maximum width
	 */
	public static final void renderLine(Graphics2D g2d, String text, float x, float y, int width) {
		TextRenderer.renderLine(g2d, text, TextAlignment.CENTER, x, y, width);
	}
	
	/**
	 * Renders a paragraph of text bounded by the given width to the given graphics object at the given coordinates.
	 * <p>
	 * This method will by default render to (0, 0) coordinates.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param alignment the text alignment
	 * @param width the maximum width
	 */
	public static final void renderLine(Graphics2D g2d, String text, TextAlignment alignment, int width) {
		TextRenderer.renderLine(g2d, text, alignment, 0, 0, width);
	}
	
	/**
	 * Renders a line of text bounded by the given width to the given graphics
	 * object at the given coordinates.
	 * @param g2d the graphics to render to
	 * @param text the text to render
	 * @param alignment the text alignment
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the maximum width
	 */
	public static final void renderLine(Graphics2D g2d, String text, TextAlignment alignment, float x, float y, int width) {
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, g2d.getFont());
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		TextLayout layout = new TextLayout(it, g2d.getFontRenderContext());
		
		float dx = 0; 
    	boolean leftToRight = layout.isLeftToRight();
    	if (alignment == TextAlignment.LEFT) {
    		if (leftToRight) {
    			dx = 0;
    		} else {
    			dx = layout.getVisibleAdvance() - layout.getAdvance();
    		}
    	} else if (alignment == TextAlignment.RIGHT) {
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
        
        layout.draw(g2d, x + dx, y + layout.getAscent());
	}
	
	/**
	 * Returns the font size required to fit the given text on one line.
	 * <p>
	 * This method assumes the current font size specified in font is the maximum font size.
	 * @param font the text font
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width to fit the text in
	 * @param height the height to fit the text in
	 * @return float
	 */
	public static final float getFittingLineFontSize(Font font, FontRenderContext fontRenderContext, String text, float width, float height) {
		return TextRenderer.getFittingLineFontSize(font, font.getSize2D(), fontRenderContext, text, width, height);
	}
	
	/**
	 * Returns the font size required to fit the given text on one line.
	 * @param font the text font
	 * @param max the maximum; use Float.MAX_VALUE to specify no maximum size
	 * @param fontRenderContext the font rendering context
	 * @param text the text
	 * @param width the width to fit the text in
	 * @param height the height to fit the text in
	 * @return float
	 */
	public static final float getFittingLineFontSize(Font font, float max, FontRenderContext fontRenderContext, String text, float width, float height) {
		// create an attributed string and assign the font
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, font);
		// get the character iterator
		AttributedCharacterIterator it = as.getIterator();
		TextLayout layout = new TextLayout(it, fontRenderContext);
		// get the single line text width
		float tw = layout.getVisibleAdvance();
		float th = layout.getAscent() + layout.getDescent() + layout.getLeading();
		// return the font size scaled by the difference in widths (or height)
		float fw = width / tw;
		float fh = height / th;
		// choose the smallest dimension
		float factor = fw < fh ? fw : fh;
		float cur = font.getSize2D();
		// if the scaling factor is less than one (the size must be reduced)
		// or the maximum size is unbounded, then scale the current font size
		if (factor < 1.0 || max == Float.MAX_VALUE) {
			return cur * factor;
		}
		return cur;
	}
}
