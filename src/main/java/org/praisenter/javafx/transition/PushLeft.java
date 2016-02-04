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
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Represents a push left {@link CustomTransition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PushLeft extends AbstractTransition implements CustomTransition, Serializable {
	/** The version id */
	private static final long serialVersionUID = -5559169367641238277L;
	
	/** The {@link PushLeft} transition id */
	public static final int ID = 61;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public PushLeft(TransitionType type) {
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
		if (image0 != null) {
			int x = (int)Math.ceil(-image0.getWidth() * pc);
			g2d.setClip(0, 0, image0.getWidth(), image0.getHeight());
			g2d.drawImage(image0, x, 0, null);
		}
		if (this.type == TransitionType.IN && image1 != null) {
			int x = (int)Math.ceil(image1.getWidth() * (1.0 - pc));
			g2d.setClip(0, 0, image1.getWidth(), image1.getHeight());
			g2d.drawImage(image1, x, 0, null);
		}
		g2d.setClip(shape);
	}
}
