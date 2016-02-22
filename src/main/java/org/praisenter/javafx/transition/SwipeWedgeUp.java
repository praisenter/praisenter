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

import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Clips the node by swiping counter clockwise and clockwise starting from the bottom.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SwipeWedgeUp extends CustomTransition {
	/** The transition id */
	public static final int ID = 37;

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
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, -90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		Shape clip = null;
		if (this.type == TransitionType.IN) {
			clip = arc;
		} else {
			clip = Shape.subtract(all, arc);
		}
		this.node.setClip(clip);
	}
}
