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
import org.praisenter.slide.animation.Push;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Represents a push transition where one object pushes another out.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class PushTransition extends CustomTransition<Push> {
	/**
	 * Full constructor.
	 * @param animation the animation configuration
	 */
	public PushTransition(Push animation) {
		super(animation);
	}
	
	/* (non-Javadoc)
	 * @see javafx.animation.Animation#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		if (this.node != null) {
			this.node.setTranslateX(0);
			this.node.setTranslateY(0);
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.animation.Transition#interpolate(double)
	 */
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Rectangle2D nb = this.getBounds();
		Rectangle2D pb = this.getParentBounds();
		
		Point2D dp = new Point2D(0, 0);
		switch(this.animation.getDirection()) {
			case UP:
				dp = getUpPosition(nb, pb, frac);
				break;
			case RIGHT:
				dp = getRightPosition(nb, pb, frac);
				break;
			case DOWN:
				dp = getDownPosition(nb, pb, frac);
				break;
			case LEFT:
				dp = getLeftPosition(nb, pb, frac);
				break;
			default:
				break;
		}
		
		node.setTranslateX(dp.getX());
		node.setTranslateY(dp.getY());
	}
	
	/**
	 * Returns the position of the slide based on the given bounds and parent bounds
	 * and the current position in the animation.
	 * @param nb the node bounds
	 * @param pb the parent node bounds
	 * @param frac the position in the animation
	 * @return Point2D
	 */
	private Point2D getUpPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double y = (pb.getHeight() - nb.getMinY());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(0, y * (1.0 - frac));
		} else {
			return new Point2D(0, -y * (frac));
		}
	}
	
	/**
	 * Returns the position of the slide based on the given bounds and parent bounds
	 * and the current position in the animation.
	 * @param nb the node bounds
	 * @param pb the parent node bounds
	 * @param frac the position in the animation
	 * @return Point2D
	 */
	private Point2D getRightPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double x = (pb.getWidth() - nb.getMinX());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(-x * (1.0 - frac), 0);
		} else {
			return new Point2D(x * frac, 0);
		}
	}
	
	/**
	 * Returns the position of the slide based on the given bounds and parent bounds
	 * and the current position in the animation.
	 * @param nb the node bounds
	 * @param pb the parent node bounds
	 * @param frac the position in the animation
	 * @return Point2D
	 */
	private Point2D getDownPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double y = (pb.getHeight() - nb.getMinY());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(0, -y * (1.0 - frac));
		} else {
			return new Point2D(0, y * (frac));
		}
	}
	
	/**
	 * Returns the position of the slide based on the given bounds and parent bounds
	 * and the current position in the animation.
	 * @param nb the node bounds
	 * @param pb the parent node bounds
	 * @param frac the position in the animation
	 * @return Point2D
	 */
	private Point2D getLeftPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double x = (pb.getWidth() - nb.getMinX());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(x * (1.0 - frac), 0);
		} else {
			return new Point2D(-x * frac, 0);
		}
	}
}
