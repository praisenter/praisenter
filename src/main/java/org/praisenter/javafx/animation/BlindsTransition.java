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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Blinds;

/**
 * Represents a blinds transition for Java FX.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class BlindsTransition extends CustomTransition<Blinds> {
	/**
	 * Full constructor.
	 * @param animation the blinds configuration
	 */
	public BlindsTransition(Blinds animation) {
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
				clip = getHorizontalBlinds(bounds, frac);
				break;
			case VERTICAL:
				clip = getVerticalBlinds(bounds, frac);
				break;
			default:
				break;
		}
		
		this.node.setClip(clip);
	}

	/**
	 * Generates a horizontal blinds shape for the given bounds and position in the animation.
	 * @param bounds the bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getHorizontalBlinds(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		Rectangle rect = new Rectangle();
		if (this.animation.getType() == AnimationType.IN) {
			// for the IN transition we will subtract areas from the full rectangle
			rect.setWidth(w);
			rect.setHeight(h);
		}
		// for the OUT transition we will add areas
		
		// compute the number of blinds
		final int blinds = this.animation.getBlindCount();
		double y = 0;
		// compute the blind width
		double bh = h / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		Shape clip = rect;
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(0, y + bh * frac, w, bh * (1.0 - frac));
			if (this.animation.getType() == AnimationType.IN) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			y += bh;
		}
		
		return clip;
	}
	
	/**
	 * Generates a vertical blinds shape for the given bounds and position in the animation.
	 * @param bounds the bounds
	 * @param frac the position in the animation
	 * @return Shape
	 */
	private Shape getVerticalBlinds(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		Shape clip = null;
		if (this.animation.getType() == AnimationType.IN) {
			clip = new Rectangle(0, 0, w, h);
		} else {
			clip = new Rectangle();
		}
		
		// compute the number of blinds
		final int blinds = this.animation.getBlindCount();
		double x = 0;
		// compute the blind width
		double bw = w / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(x + bw * frac, 0, bw * (1.0 - frac), h);
			if (this.animation.getType() == AnimationType.IN) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			x += bw;
		}
		
		return clip;
	}
}
