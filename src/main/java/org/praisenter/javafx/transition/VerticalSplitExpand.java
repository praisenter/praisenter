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
package org.praisenter.javafx.transition;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Represents a vertical split expand {@link CustomTransition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class VerticalSplitExpand extends AbstractTransition implements CustomTransition, Serializable {
	/** The version id */
	private static final long serialVersionUID = 9144968808098631341L;
	
	/** The {@link VerticalSplitExpand} transition id */
	public static final int ID = 41;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public VerticalSplitExpand(TransitionType type) {
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
		// to get pixel perfect results we need to make sure we use the x and width from
		// image0 so that image1 will be clipped appropriately (this is only necessary when
		// we have both image0 and image1)
		int x0 = 0;
		int w0 = 0;
		if (image0 != null) {
			// create two rectangles and merge them into one area for the clip
			double hw = (double)image0.getWidth() / 2.0;
			// w = hw - hw * pc
			w0 = (int)Math.floor(hw * (1.0 - pc));
			int h = image0.getHeight();
			// x = hw + hw * pc
			x0 = (int)Math.ceil(hw * (1.0 + pc));
			if (pc >= 1.0) {
				w0 = 0;
				x0 = image0.getWidth();
			}
			Rectangle left = new Rectangle(0, 0, w0, h);
			Rectangle right = new Rectangle(x0, 0, Math.max(image0.getWidth() - x0, 0), h);
			Area area = new Area();
			area.add(new Area(left));
			area.add(new Area(right));
			g2d.setClip(area);
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == TransitionType.IN && image1 != null) {
			int x = 0;
			int w = 0;
			if (image0 != null) {
				w = x0 - w0;
				x = w0;
			} else {
				int hw = image1.getWidth() / 2;
				x = (int)Math.ceil((double)hw * (1.0 - pc));
				w = (int)Math.ceil(image1.getWidth() * pc);
			}
			g2d.setClip(x, 0, w, image1.getHeight());
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
