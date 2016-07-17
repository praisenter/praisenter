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
package org.praisenter.javafx.animation;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Zoom;

/**
 * Represents a zoom transition.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class ZoomTransition extends CustomTransition<Zoom> {
	/**
	 * Full constructor.
	 * @param animation the animation configuration
	 */
	public ZoomTransition(Zoom animation) {
		super(animation);
	}

	/* (non-Javadoc)
	 * @see javafx.animation.Animation#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		if (this.node != null) {
			this.node.setScaleX(1);
			this.node.setScaleY(1);
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.animation.Transition#interpolate(double)
	 */
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		// FIXME need to do testing with zoom in/out transitions
//		Bounds bounds = node.getBoundsInParent();
//		double w = bounds.getWidth();
//		double h = bounds.getHeight();
		
		if (this.animation.getType() == AnimationType.IN) {
			node.setScaleX(Math.max(frac, 0));
			node.setScaleY(Math.max(frac, 0));
		} 
//		else {
//			// for the out transition we'll just clip the center
////			double w = this.node.getPrefWidth();
////			double h = this.node.getPrefHeight();
//			double hw = w * 0.5;
//			double hh = h * 0.5;
//			Shape clip = new Rectangle(0, 0, w, h);
//			Shape center = new Rectangle(hw * (1.0 - frac), hh * (1.0 - frac), h * frac, h * frac);
//			node.setClip(Shape.subtract(clip, center));
//		}
		
//		if (this.type == AnimationType.IN) {
//			// for the out transition we'll just clip the center
////			double w = this.node.getPrefWidth();
////			double h = this.node.getPrefHeight();
//			double hw = w * 0.5;
//			double hh = h * 0.5;
//			Shape clip = new Rectangle(0, 0, w, h);
//			Shape center = new Rectangle(hw * frac, hh * frac, h * (1.0 - frac), h * (1.0 - frac));
//			node.setClip(Shape.subtract(clip, center));
//		} 
		else {
			node.setScaleX(Math.max(0.0, 1.0 - frac));
			node.setScaleY(Math.max(0.0, 1.0 - frac));
		}
	}
}
