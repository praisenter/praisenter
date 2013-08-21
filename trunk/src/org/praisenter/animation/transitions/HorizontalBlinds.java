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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Represents a horizontal blinds {@link Transition}.
 * @author William Bittle
 * @version 2.0.3
 * @since 2.0.3
 */
public class HorizontalBlinds extends AbstractBlindsTransition implements Transition, Serializable {
	/** The version id */
	private static final long serialVersionUID = 6033288490467318927L;
	
	/** The {@link HorizontalBlinds} transition id */
	public static final int ID = 91;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public HorizontalBlinds(TransitionType type) {
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
		int w = 0;
		int h = 0;
		
		Area area = null;
		if (this.type == TransitionType.IN) {
			// for the IN transition we will subtract areas from the full rectangle
			w = image1.getWidth();
			h = image1.getHeight();
			area = new Area(new Rectangle(0, 0, w, h));
		} else {
			// for the OUT transition we will add areas
			w = image0.getWidth();
			h = image0.getHeight();
			area = new Area();
		}
		
		// compute the number of blinds
		final int blinds = (int)Math.ceil((double)h * BLIND_COUNT_FACTOR);
		double y = 0;
		// compute the blind width
		double bh = (double)h / (double)blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		for (int i = 0; i < blinds; i++) {
			Rectangle2D.Double blind = new Rectangle2D.Double(0, y + bh * pc, w, bh * (1.0 - pc));
			if (this.type == TransitionType.IN) {
				area.subtract(new Area(blind));
			} else {
				area.add(new Area(blind));
			}
			y += bh;
		}
		
		// draw the animation
		Shape shape = g2d.getClip();
		if (this.type == TransitionType.IN) {
			g2d.drawImage(image0, 0, 0, null);
			g2d.setClip(area);
			g2d.drawImage(image1, 0, 0, null);
		} else {
			g2d.setClip(area);
			g2d.drawImage(image0, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
