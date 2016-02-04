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
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Represents a swipe clockwise {@link CustomTransition}.
 * @author William Bittle
 * @version 2.0.3
 * @since 2.0.3
 */
public class SwipeClockwise extends AbstractTransition implements CustomTransition, Serializable {
	/** The version id */
	private static final long serialVersionUID = 5674224321005962262L;
	
	/** The {@link SwipeClockwise} transition id */
	public static final int ID = 34;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public SwipeClockwise(TransitionType type) {
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
		// clamp the percentage
		pc = clamp((float)pc, 0.0f, 1.0f);

		// save the current clip
		Shape clip = g2d.getClip();
		
		// build the clip shape
		double w = image1.getWidth();
		double h = image1.getHeight();
		double a = -2.0 * Math.PI * pc;
		double hyp = Math.hypot(w, h);
		Arc2D.Double arc = new Arc2D.Double(
				w * 0.5 - hyp * 0.5,
				h * 0.5 - hyp * 0.5,
				hyp,
				hyp,
				90.0,
				Math.toDegrees(a),
				Arc2D.PIE);
		
		if (this.type == TransitionType.IN) {
			// draw the old
			g2d.drawImage(image0, 0, 0, null);

			// draw the new
			g2d.setClip(arc);
			g2d.drawImage(image1, 0, 0, null);
		} else {
			// do an xor clipping op here
			Area area = new Area(new Rectangle(0, 0, image0.getWidth(), image0.getHeight()));
			area.exclusiveOr(new Area(arc));
			g2d.setClip(area);
				
			g2d.drawImage(image0, 0, 0, null);
		}

		// restore the old clip
		g2d.setClip(clip);
	}
}
