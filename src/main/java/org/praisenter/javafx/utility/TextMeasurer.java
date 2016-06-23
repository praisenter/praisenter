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
package org.praisenter.javafx.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 * Class used to measure text in JavaFX for doing automatic text resizing.
 * @author William Bittle
 * @version 3.0.0
 */
public final class TextMeasurer {
	/** Hidden constructor */
	private TextMeasurer() {}
	
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** A reusable node for measuring */
    private static final Text JAVAFX_TEXT_NODE = new Text();
    
    /** We should never hit this, but just in case. I've never seen above 25 or so */
    private static final int MAXIMUM_ITERATIONS = 100;
    
    // defaults 
    
    /** The default wrapping width */
    private static final double DEFAULT_WRAPPING_WIDTH = JAVAFX_TEXT_NODE.getWrappingWidth();
    
    /** The default line spacing */
    private static final double DEFAULT_LINE_SPACING = JAVAFX_TEXT_NODE.getLineSpacing();
    
    /** The default text */
    private static final String DEFAULT_TEXT = JAVAFX_TEXT_NODE.getText();
    
    /** The default bounds type */
    private static final TextBoundsType DEFAULT_BOUNDS_TYPE = JAVAFX_TEXT_NODE.getBoundsType();

    /**
     * Resets the shared text node after measuring.
     */
    private static final void reset() {
    	JAVAFX_TEXT_NODE.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
        JAVAFX_TEXT_NODE.setLineSpacing(DEFAULT_LINE_SPACING);
        JAVAFX_TEXT_NODE.setText(DEFAULT_TEXT);
        JAVAFX_TEXT_NODE.setBoundsType(DEFAULT_BOUNDS_TYPE);
    }
    
    /**
     * Returns the bounds of a paragraph for the given text, font, target width, line spacing
     * and bounds type. 
     * @param text the text to measure
     * @param font the font
     * @param targetWidth the target wrapping width
     * @param lineSpacing the line spacing
     * @param boundsType the bounds type
     * @return Bounds
     */
    public static final Bounds getParagraphBounds(String text, Font font, double targetWidth, double lineSpacing, TextBoundsType boundsType) {
        // setup the node
    	JAVAFX_TEXT_NODE.setText(text);
        JAVAFX_TEXT_NODE.setFont(font);
        JAVAFX_TEXT_NODE.setWrappingWidth(targetWidth);
        JAVAFX_TEXT_NODE.setLineSpacing(lineSpacing);
        JAVAFX_TEXT_NODE.setBoundsType(boundsType);
        // perform the measurement
        final Bounds bounds = JAVAFX_TEXT_NODE.getLayoutBounds();
        // reset the node
        reset();
        return bounds;
    }
    
    /**
     * Returns a new font that allows the text to fit within the given bounds, increasing up
     * to the given maxFontSize and decreasing to fit if needed.
     * @param text the text to measure
     * @param font the font
     * @param maxFontSize the maximum font size
     * @param targetWidth the target wrapping width
     * @param targetHeight the target height
     * @param lineSpacing the line spacing
     * @param boundsType the bounds type
     * @return Bounds
     */
    public static final Font getFittingFontForParagraph(String text, Font font, double maxFontSize, double targetWidth, double targetHeight, double lineSpacing, TextBoundsType boundsType) {
    	Bounds bounds = TextMeasurer.getParagraphBounds(text, font, targetWidth, lineSpacing, boundsType);
		double max = maxFontSize;
		double min = (bounds.getHeight() <= targetHeight && max != Double.MAX_VALUE) ? max : 1.0;
		double cur = font.getSize();
		
		if (cur < 1.0) {
			cur = 1.0;
		}
		
		Font nf = font;
		int i = 0;
		while (bounds.getHeight() > targetHeight || Math.abs(max - min) > 0.1) {
			// check the paragraph height against the maximum height
			if (bounds.getHeight() < targetHeight) {
				// we need to binary search up
				min = cur;
				// compute an estimated next size if the maximum begins with Float.MAX_VALUE
				// this is to help convergence to a safe maximum
				double rmax = (max == Double.MAX_VALUE ? targetHeight * (cur / bounds.getHeight()) : max);
				cur = (cur + rmax) * 0.5;
				nf = new Font(font.getName(), cur);
			} else {
				// we need to binary search down
				max = cur;
				// get the next test font size
				double temp = (min + cur) * 0.5;
				// do a check for minimum font size
				if (temp <= 1.0f) break;
				// its not the minimum so continue
				cur = temp;
				nf = new Font(font.getName(), cur);
			}
			// get the new paragraph height for the new font size
			bounds = TextMeasurer.getParagraphBounds(text, nf, targetWidth, lineSpacing, boundsType);
			// don't run forever
			if (i >= MAXIMUM_ITERATIONS) {
				LOGGER.warn("Hit maximum number of iterations before determining optimal font size. Current: {} Minimum: {} Maximum: {} Target Width: {} Target Height: {} Maximum Size: {} Text: {}",
						cur, min, max, targetWidth, targetHeight, maxFontSize, text);
				break;
			}
			i++;
		}
		if (i > 0) {
			LOGGER.debug("Font fitting iterations: " + i);
		}
		// the Math.min(min, cur) ensures we choose the lower bound
		// the - 1.0 is further insurance that its small enough
		// the Math.min(1.0, x) ensures the minimum font we return is 1
		double size = Math.max(1.0, Math.min(min, cur) - 1.0);
		return new Font(font.getName(), size);
    }
    
    /**
     * Returns the bounds of a line for the given text and font.
     * @param text the text to measure
     * @param font the font
     * @param boundsType the bounds type
     * @return Bounds
     */
    public static final Bounds getLineBounds(String text, Font font, TextBoundsType boundsType) {
    	// setup the node
    	JAVAFX_TEXT_NODE.setText(text);
        JAVAFX_TEXT_NODE.setFont(font);
        JAVAFX_TEXT_NODE.setWrappingWidth(0);
        JAVAFX_TEXT_NODE.setLineSpacing(0);
        JAVAFX_TEXT_NODE.setBoundsType(boundsType);
        // perform the measurement
        final Bounds bounds = JAVAFX_TEXT_NODE.getLayoutBounds();
        // reset the node
        reset();
        return bounds;
    }
    
    /**
     * Returns a new font that allows the text to fit within the given width, increasing up
     * to the given maxFontSize and decreasing to fit if needed.
     * @param text the text to measure
     * @param font the font
     * @param maxFontSize the maximum font size
     * @param targetWidth the target width
     * @param boundsType the bounds type
     * @return Bounds
     */
    public static final Font getFittingFontForLine(String text, Font font, double maxFontSize, double targetWidth, TextBoundsType boundsType) {
    	Bounds bounds = TextMeasurer.getLineBounds(text, font, boundsType);
		double max = maxFontSize;
		double min = (bounds.getWidth() < targetWidth && max != Double.MAX_VALUE) ? max : 1.0;
		double cur = font.getSize();
		
		if (cur < 1.0) {
			cur = 1.0;
		}
		
		Font nf = font;
		int i = 0;
		while (bounds.getWidth() > targetWidth || Math.abs(max - min) > 0.1) {
			// check the paragraph height against the maximum height
			if (bounds.getWidth() < targetWidth) {
				// we need to binary search up
				min = cur;
				// compute an estimated next size if the maximum begins with Float.MAX_VALUE
				// this is to help convergence to a safe maximum
				double rmax = (max == Double.MAX_VALUE ? targetWidth * (cur / bounds.getWidth()) : max);
				cur = (cur + rmax) * 0.5;
				nf = new Font(font.getName(), cur);
			} else {
				// we need to binary search down
				max = cur;
				// get the next test font size
				double temp = (min + cur) * 0.5;
				// do a check for minimum font size
				if (temp <= 1.0f) break;
				// its not the minimum so continue
				cur = temp;
				nf = new Font(font.getName(), cur);
			}
			// get the new paragraph height for the new font size
			bounds = TextMeasurer.getLineBounds(text, nf, boundsType);
			// don't run forever
			if (i >= MAXIMUM_ITERATIONS) {
				LOGGER.warn("Hit maximum number of iterations before determining optimal font size. Current: {} Minimum: {} Maximum: {} Target Width: {} Maximum Size: {} Text: {}",
						cur, min, max, targetWidth, maxFontSize, text);
				break;
			}
			i++;
		}
		if (i > 0) {
			LOGGER.debug("Font fitting iterations: " + i);
		}
		// the Math.min(min, cur) ensures we choose the lower bound
		// the - 1.0 is further insurance that its small enough
		// the Math.min(1.0, x) ensures the minimum font we return is 1
		double size = Math.max(1.0, Math.min(min, cur) - 1.0);
		return new Font(font.getName(), size);
    }
}

