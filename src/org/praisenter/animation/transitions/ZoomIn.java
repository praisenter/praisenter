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
 * Represents a zoom-in {@link Transition}.
 * @author William Bittle
 * @version 2.0.3
 * @since 2.0.3
 */
public class ZoomIn extends AbstractTransition implements Transition, Serializable {
	/** The version id */
	private static final long serialVersionUID = 859864787761263155L;
	
	/** The {@link ZoomIn} transition id */
	public static final int ID = 70;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public ZoomIn(TransitionType type) {
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
		if (this.type == TransitionType.IN) {
			// draw the old
			g2d.drawImage(image0, 0, 0, null);
			// draw the new but sized down
			if (pc > 0.0) {
				g2d.drawImage(
						image1, 
						(int)Math.floor(image1.getWidth() * 0.5 * (1.0 - pc)), 
						(int)Math.floor(image1.getHeight() * 0.5 * (1.0 - pc)),
						(int)Math.floor(image1.getWidth() * pc), 
						(int)Math.floor(image1.getHeight() * pc),
						null);
			}
		} else {
			// setup a rectangular clip
			Shape clip = g2d.getClip();
			// don't bother with a clip region if pc <= 0
			if (pc > 0.0) {
				Area area = new Area(new Rectangle(0, 0, image0.getWidth(), image0.getHeight()));
				area.exclusiveOr(new Area(new Rectangle2D.Double(
						(1.0 - pc) * image0.getWidth() * 0.5,
						(1.0 - pc) * image0.getHeight() * 0.5,
						pc * image0.getWidth(),
						pc * image0.getHeight())));
				g2d.setClip(area);
			}
			g2d.drawImage(image0, 0, 0, null);
			// restore the old clip
			g2d.setClip(clip);
		}
	}
}
