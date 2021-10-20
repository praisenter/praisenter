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
import org.praisenter.slide.animation.Swipe;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Represents a swipe transition.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class SwipeTransition extends CustomTransition<Swipe> {
	/**
	 * Full constructor.
	 * @param animation the animation configuration
	 */
	public SwipeTransition(Swipe animation) {
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
		switch(this.animation.getDirection()) {
			case UP:
				clip = getUpClip(bounds, frac);
				break;
			case RIGHT:
				clip = getRightClip(bounds, frac);
				break;
			case DOWN:
				clip = getDownClip(bounds, frac);
				break;
			case LEFT:
				clip = getLeftClip(bounds, frac);
				break;
			case CLOCKWISE:
				clip = getClockwiseClip(bounds, frac);
				break;
			case COUNTER_CLOCKWISE:
				clip = getCounterClockwiseClip(bounds, frac);
				break;
			case WEDGE_DOWN:
				clip = getWedgeDownClip(bounds, frac);
				break;
			case WEDGE_UP:
				clip = getWedgeUpClip(bounds, frac);
				break;
			default:
				break;
		}
		
		node.setClip(clip);
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getUpClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(h * (1.0 - frac));
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, p, w, h);
		} else {
			return new Rectangle(0, 0, w, p);
		}
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getRightClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(w * frac);
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, 0, p, h);
		} else {
			return new Rectangle(p, 0, w, h);
		}
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getDownClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(h * frac);
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, 0, w, p);
		} else {
			return new Rectangle(0, p, w, h * Math.ceil(1.0 - frac));
		}
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getLeftClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(w * (1.0 - frac));
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(p, 0, w, h);
		} else {
			return new Rectangle(0, 0, p, h);
		}
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getClockwiseClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90.0, -360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getCounterClockwiseClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90.0, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getWedgeDownClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);

		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	/**
	 * Returns a clip for the transition.
	 * @param bounds the object bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getWedgeUpClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, -90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}
}
