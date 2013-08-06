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
package org.praisenter.animation.transitions;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Represents a horizontal split collapse {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class HorizontalSplitCollapse extends AbstractTransition implements Transition, Serializable {
	/** The version id */
	private static final long serialVersionUID = -4306724627059838539L;
	
	/** The {@link HorizontalSplitCollapse} transition id */
	public static final int ID = 43;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public HorizontalSplitCollapse(TransitionType type) {
		super(type);
	} 

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getId() {
		return ID;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
		Shape shape = g2d.getClip();
		// to get pixel perfect results we need to make sure we use the y and height from
		// image0 so that image1 will be clipped appropriately (this is only necessary when
		// we have both image0 and image1)
		int y0 = 0;
		int h0 = 0;
		if (image0 != null) {
			int hh = image0.getHeight() / 2;
			y0 = (int)Math.floor((double)hh * pc);
			h0 = (int)Math.floor(image0.getHeight() * (1.0 - pc));
			g2d.setClip(0, y0, image0.getWidth(), h0);
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == TransitionType.IN && image1 != null) {
			int w = image1.getWidth();
			int h = 0;
			int y = 0;
			// create two rectangles and merge them into one area for the clip
			int hh = image1.getHeight() / 2;
			if (image0 != null) {
				h = y0;
				y = y0 + h0;
			} else {
				// h = hh - hh * pc
				h = (int)Math.ceil((double)hh * pc);
				// y = hh + hh * pc
				y = (int)Math.ceil((double)image1.getHeight() - (double)hh * pc);
			}
			Rectangle left = new Rectangle(0, 0, w, h);
			Rectangle right = new Rectangle(0, y, w, Math.max(image1.getHeight() - y, 0));
			Area area = new Area();
			area.add(new Area(left));
			area.add(new Area(right));
			g2d.setClip(area);
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
