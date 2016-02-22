/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Horizontal blinds transition.
 * @author William Bittle
 * @version 3.0.0
 */
public final class HorizontalBlinds extends AbstractBlindsTransition {
	/** The transition id */
	public static final int ID = 91;
	
	/* (non-Javadoc)
	 * @see org.praisenter.javafx.transition.CustomTransition#getId()
	 */
	@Override
	public int getId() {
		return ID;
	}
	
	/* (non-Javadoc)
	 * @see javafx.animation.Transition#interpolate(double)
	 */
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		double w = this.node.getPrefWidth();
		double h = this.node.getPrefHeight();
		Rectangle rect = new Rectangle();
		if (this.type == TransitionType.IN) {
			// for the IN transition we will subtract areas from the full rectangle
			rect.setWidth(w);
			rect.setHeight(h);
		}
		// for the OUT transition we will add areas
		
		// compute the number of blinds
		final int blinds = (int)Math.ceil(h * BLIND_COUNT_FACTOR);
		double y = 0;
		// compute the blind width
		double bh = h / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		Shape clip = rect;
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(0, y + bh * frac, w, bh * (1.0 - frac));
			if (this.type == TransitionType.IN) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			y += bh;
		}
		
		this.node.setClip(clip);
	}
}
