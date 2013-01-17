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
package org.praisenter.ui;

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
public final class WaterMark {
	/** Hidden default constructor */
	private WaterMark() {}
	
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
