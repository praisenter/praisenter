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
package org.praisenter.javafx.slide.animation;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Split;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Represents a transition where the object is split vertical or horizontally.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class SplitTransition extends CustomTransition<Split> {
	/**
	 * Full constructor.
	 * @param animation the animation configuration
	 */
	public SplitTransition(Split animation) {
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
		
		Rectangle2D bounds = this.getBounds();
		
		Shape clip = null;
		switch(this.animation.getOrientation()) {
			case HORIZONTAL:
				switch(this.animation.getOperation()) {
					case COLLAPSE:
						clip = getHorizontalCollapse(bounds, frac);
						break;
					case EXPAND:
						clip = getHorizontalExpand(bounds, frac);
						break;
					default:
						break;
				}
				break;
			case VERTICAL:
				switch(this.animation.getOperation()) {
					case COLLAPSE:
						clip = getVerticalCollapse(bounds, frac);
						break;
					case EXPAND:
						clip = getVerticalExpand(bounds, frac);
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
		
		node.setClip(clip);
	}

	/**
	 * Returns a clip for a horizontal collapse transition.
	 * @param bounds the node bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getHorizontalCollapse(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(0, hh * frac, w, h * (1.0 - frac));
		
		if (this.animation.getType() == AnimationType.IN) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	/**
	 * Returns a clip for a horizontal expand transition.
	 * @param bounds the node bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getHorizontalExpand(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(0, hh * (1.0 - frac), w, h * frac);
		
		if (this.animation.getType() == AnimationType.IN) {
			return cut;
		} else {
			return Shape.subtract(all, cut);
		}
	}
	
	/**
	 * Returns a clip for a vertical collapse transition.
	 * @param bounds the node bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getVerticalCollapse(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(hw * frac, 0, w * (1.0 - frac), h);
		
		if (this.animation.getType() == AnimationType.IN) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	/**
	 * Returns a clip for a vertical expand transition.
	 * @param bounds the node bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getVerticalExpand(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(hw * (1.0 - frac), 0, w * frac, h);
		
		if (this.animation.getType() == AnimationType.IN) {
			return cut;
		} else {
			return Shape.subtract(all, cut);
		}
	}
}
