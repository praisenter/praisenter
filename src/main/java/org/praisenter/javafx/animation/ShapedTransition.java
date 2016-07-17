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

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Operation;
import org.praisenter.slide.animation.ShapeType;
import org.praisenter.slide.animation.Shaped;

/**
 * Represents a transition where a clip shape is used.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class ShapedTransition extends CustomTransition<Shaped> {
	/**
	 * Full constructor.
	 * @param animation the animation configuration
	 */
	public ShapedTransition(Shaped animation) {
		super(animation);
	}

	/* (non-Javadoc)
	 * @see javafx.animation.Animation#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		if (this.node != null) {
			this.node.setClip(null);
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.animation.Transition#interpolate(double)
	 */
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Shape clip = null;
		
		Rectangle2D bounds = this.getBounds();
		
		// circle collapse/expand
		if (this.animation.getShapeType() == ShapeType.CIRCLE) {
			clip = this.getCircleClip(bounds, frac);
		}
		
		node.setClip(clip);
	}
	
	/**
	 * Returns a circle clip shape.
	 * @param bounds the bounds of the node
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getCircleClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();

		if (this.animation.getOperation() == Operation.COLLAPSE) {
			double hw = w * 0.5;
			double hh = h * 0.5;
			double r = Math.sqrt(hw * hw + hh * hh) * (1.0 - frac);
			Rectangle all = new Rectangle(0, 0, w, h);
			Circle circle = new Circle(hw, hh, r);
			
			// create the clip shape
			if (this.animation.getType() == AnimationType.IN) {
				return Shape.subtract(all, circle);
			} else {
				return circle;
			}
		} else if (this.animation.getOperation() == Operation.EXPAND) {
			double hw = w * 0.5;
			double hh = h * 0.5;
			double r = Math.sqrt(hw * hw + hh * hh) * frac;
			Rectangle all = new Rectangle(0, 0, w, h);
			Circle circle = new Circle(hw, hh, r);
			
			// create the clip shape
			if (this.animation.getType() == AnimationType.IN) {
				return circle;
			} else {
				return Shape.subtract(all, circle);
			}
		}
		
		return null;
	}
}
