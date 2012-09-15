package org.praisenter.control;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

/**
 * Class used to draw water marks on text fields.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class WaterMark {
	/**
	 * Paints the given text as a watermark on the given graphics object using the
	 * given field to position the text.
	 * @param g the graphics to render to
	 * @param field the field to render relative to
	 * @param text the text to render
	 */
	public static void paintTextWaterMark(Graphics g, JTextComponent field, String text) {
		String fieldText = field.getText();
        if ( fieldText == null || fieldText.length() < 1 ) {
        	Color prevColor = g.getColor();
        	// get the inactive text color
        	Color inactiveColor = UIManager.getColor("textInactiveText");
            if (inactiveColor != null) {
            	g.setColor(inactiveColor);
            } else {
            	// if its not found, then just use gray
            	g.setColor(Color.GRAY);
            }
                        
            // compute the location of the text
            Insets insets = field.getInsets();
            FontMetrics metrics = g.getFontMetrics();
            int x = insets.left;
            
            // setup the clipping bounds
            Shape clip = g.getClip();
            Rectangle r = field.getVisibleRect();
            r.x += insets.left;
            r.y += insets.top;
            r.width -= insets.left + insets.right;
            r.height -= insets.top + insets.bottom;
            g.setClip(r);
            
            // draw the text
            g.drawString(text, x, field.getHeight() / 2 + metrics.getHeight() / 2 - metrics.getDescent());
            
            // reset the color & clip
            g.setColor(prevColor);
            g.setClip(clip);
        }
	}
}
