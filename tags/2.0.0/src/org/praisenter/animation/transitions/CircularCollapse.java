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
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Represents a circular collapse {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class CircularCollapse extends AbstractTransition implements Transition, Serializable {
	/** The version id */
	private static final long serialVersionUID = 9212743413162569245L;
	
	/** The {@link CircularCollapse} transition id */
	public static final int ID = 51;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public CircularCollapse(TransitionType type) {
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
		
		// create the clip circle
		int hw = image0.getWidth() / 2;
		int hh = image0.getHeight() / 2;
		double r = Math.sqrt(hw * hw + hh * hh) * (1.0 - pc);
		double s = 2.0 * r;
		double x = hw - r;
		double y = hh - r;
		Ellipse2D.Double circle = new Ellipse2D.Double(x, y, s, s);
		
		if (image0 != null) {
			// unfortunately we need to do the EXACT opposite clipping operation
			// for pixel perfect results (we can't just clip by the circle)
			Area area = new Area(new Rectangle(0, 0, image0.getWidth(), image0.getHeight()));
			area.intersect(new Area(circle));
			g2d.setClip(area);
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == TransitionType.IN && image1 != null) {
			Area area = new Area(new Rectangle(0, 0, image1.getWidth(), image1.getHeight()));
			area.exclusiveOr(new Area(circle));
			g2d.setClip(area);
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
