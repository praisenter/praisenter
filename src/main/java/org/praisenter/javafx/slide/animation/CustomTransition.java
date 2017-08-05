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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.slide.animation.Animation;

import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

/**
 * Represents a custom Java FX transition defined by a {@link Animation}.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @param <T> the {@link Animation} type
 */
public abstract class CustomTransition<T extends Animation> extends Transition {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** An empty/degenerate rectangle */
	private static final Rectangle2D EMPTY_BOUNDS = new Rectangle2D(0, 0, 0, 0);
	
	// data
	
	/** The animation */
	final T animation;
	
	/** The node */
	Node node;
	
	/**
	 * Full constructor.
	 * @param animation the animation configuration
	 */
	public CustomTransition(T animation) {
		if (animation == null) {
			throw new NullPointerException("The animation parameter is null.");
		}
		
		int cycleCount = animation.getRepeatCount();
		if (cycleCount == Animation.INFINITE) {
			cycleCount = Transition.INDEFINITE;
		} else if (animation.isAutoReverse()) {
			cycleCount = cycleCount * 2 - 1;
		}
		
		this.animation = animation;
		this.setInterpolator(new CustomInterpolator(animation.getEasing()));
//		this.setDelay(Duration.millis(Math.max(0, animation.getDelay())));
		this.setCycleDuration(Duration.millis(Math.max(0, animation.getDuration())));
		this.setAutoReverse(animation.isAutoReverse());
		this.setCycleCount(cycleCount);
	}
	
	/**
	 * Returns a new bounds based on the type of node this transition is attached to.
	 * @return Rectangle2D
	 */
	protected Rectangle2D getBounds() {
		Rectangle2D bounds = EMPTY_BOUNDS;
		if (this.node == null) {
			return bounds;
		}
		
		bounds = getBounds(this.node);
		
		return bounds;
	}
	
	/**
	 * Returns a new bounds based on the type of the parent of the node this transition is attached to.
	 * @return Rectangle2D
	 */
	protected Rectangle2D getParentBounds() {
		Rectangle2D bounds = EMPTY_BOUNDS;
		if (this.node == null) {
			return bounds;
		}
		
		if (this.node.getParent() == null) {
			return bounds;
		}
		
		bounds = getBounds(this.node.getParent());
		
		return bounds;
	}
	
	/**
	 * Returns a new bounds based on the type of node given.
	 * @param node the node
	 * @return Rectangle2D
	 */
	private final static Rectangle2D getBounds(Node node) {
		Rectangle2D bounds = EMPTY_BOUNDS;
		
		if (node instanceof Region) {
			Region r = (Region)node;
			bounds = new Rectangle2D(r.getLayoutX(), r.getLayoutY(), r.getWidth(), r.getHeight());
		} else if (node instanceof MediaView) {
			MediaView m = (MediaView)node;
			bounds = new Rectangle2D(m.getX(), m.getY(), m.getFitWidth(), m.getFitHeight());
		} else if (node instanceof ImageView) {
			ImageView i = (ImageView)node;
			bounds = new Rectangle2D(i.getX(), i.getY(), i.getFitWidth(), i.getFitHeight());
		} else if (node instanceof Canvas) {
			Canvas c = (Canvas)node;
			bounds = new Rectangle2D(c.getLayoutX(), c.getLayoutY(), c.getWidth(), c.getHeight());
		} else {
			Bounds bds = node.getBoundsInParent();
			bounds = new Rectangle2D(bds.getMinX(), bds.getMinY(), bds.getWidth(), bds.getHeight());
			LOGGER.warn("Unable to determine the node's bounds based on its type.  Using the getBoundsInParent method instead which can give incorrect results.");
		}
		
		return bounds;
	}
	
	/**
	 * Clamps the given value between the min and max inclusive.
	 * @param value the value to clamp
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return float
	 */
	protected static final double clamp(double value, double min, double max) {
		return Math.max(Math.min(value, max), min);
	}
	
	/**
	 * Returns the node being animated.
	 * @return Node
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * Sets the node to be animated.
	 * @param node the node
	 */
	public void setNode(Node node) {
		this.node = node;
	}
}
