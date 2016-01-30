package org.praisenter.javafx.text;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public final class TextMeasurer {
	// the node used for measuring
	
    private static final Text JAVAFX_TEXT_NODE = new Text();
    
    // defaults 
    
    private static final double DEFAULT_WRAPPING_WIDTH = JAVAFX_TEXT_NODE.getWrappingWidth();
    private static final double DEFAULT_LINE_SPACING = JAVAFX_TEXT_NODE.getLineSpacing();
    private static final String DEFAULT_TEXT = JAVAFX_TEXT_NODE.getText();
    private static final TextBoundsType DEFAULT_BOUNDS_TYPE = JAVAFX_TEXT_NODE.getBoundsType();

    private static final void reset() {
    	JAVAFX_TEXT_NODE.setWrappingWidth(DEFAULT_WRAPPING_WIDTH);
        JAVAFX_TEXT_NODE.setLineSpacing(DEFAULT_LINE_SPACING);
        JAVAFX_TEXT_NODE.setText(DEFAULT_TEXT);
        JAVAFX_TEXT_NODE.setBoundsType(DEFAULT_BOUNDS_TYPE);
    }
    
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
		while (bounds.getHeight() > targetHeight || (int)Math.floor(max - min) > 1) {
			// check the paragraph height against the maximum height
			if (bounds.getHeight() < targetHeight) {
				// we need to binary search up
				min = cur;
				// compute an estimated next size if the maximum begins with Float.MAX_VALUE
				// this is to help convergence to a safe maximum
				double rmax = (max == Double.MAX_VALUE ? targetHeight * (cur / bounds.getHeight()) : max);
				cur = Math.ceil((cur + rmax) * 0.5f);
				nf = new Font(font.getName(), cur);
			} else {
				// we need to binary search down
				max = cur;
				// get the next test font size
				double temp = Math.floor((min + cur) * 0.5f);
				// do a check for minimum font size
				if (temp <= 1.0f) break;
				// its not the minimum so continue
				cur = temp;
				nf = new Font(font.getName(), cur);
			}
			// get the new paragraph height for the new font size
			bounds = TextMeasurer.getParagraphBounds(text, nf, targetWidth, lineSpacing, boundsType);
			i++;
		}
		if (i > 0) {
			System.out.println("Font fitting iterations: " + i);
		}
		return nf;
    }
    
    public static final Bounds getLineBounds(String text, Font font) {
    	// setup the node
    	JAVAFX_TEXT_NODE.setText(text);
        JAVAFX_TEXT_NODE.setFont(font);
        JAVAFX_TEXT_NODE.setWrappingWidth(0);
        JAVAFX_TEXT_NODE.setLineSpacing(0);
        JAVAFX_TEXT_NODE.setBoundsType(TextBoundsType.VISUAL);
        // perform the measurement
        final Bounds bounds = JAVAFX_TEXT_NODE.getLayoutBounds();
        // reset the node
        reset();
        return bounds;
    }
    
    public static final Font getFittingFontForLine(String text, Font font, double maxFontSize, double targetWidth) {
    	Bounds bounds = TextMeasurer.getLineBounds(text, font);
		double max = maxFontSize;
		double min = (bounds.getWidth() <= targetWidth && max != Double.MAX_VALUE) ? max : 1.0;
		double cur = font.getSize();
		
		if (cur < 1.0) {
			cur = 1.0;
		}
		
		Font nf = font;
		int i = 0;
		while (bounds.getWidth() > targetWidth || (int)Math.floor(max - min) > 1) {
			// check the paragraph height against the maximum height
			if (bounds.getWidth() < targetWidth) {
				// we need to binary search up
				min = cur;
				// compute an estimated next size if the maximum begins with Float.MAX_VALUE
				// this is to help convergence to a safe maximum
				double rmax = (max == Double.MAX_VALUE ? targetWidth * (cur / bounds.getWidth()) : max);
				cur = Math.ceil((cur + rmax) * 0.5f);
				nf = new Font(font.getName(), cur);
			} else {
				// we need to binary search down
				max = cur;
				// get the next test font size
				double temp = Math.floor((min + cur) * 0.5f);
				// do a check for minimum font size
				if (temp <= 1.0f) break;
				// its not the minimum so continue
				cur = temp;
				nf = new Font(font.getName(), cur);
			}
			// get the new paragraph height for the new font size
			bounds = TextMeasurer.getLineBounds(text, nf);
			i++;
		}
		if (i > 0) {
			System.out.println("Font fitting iterations: " + i);
		}
		return nf;
    }
}

