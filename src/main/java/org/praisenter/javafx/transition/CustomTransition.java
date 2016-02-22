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

import javafx.animation.Transition;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Represents a custom transition.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class CustomTransition extends Transition {
	/** The node being transitioned */
	Region node;
	
	/** The transition type */
	TransitionType type;
	
	/** The transition duration */
	Duration duration;
	
	/**
	 * Default constructor.
	 */
	public CustomTransition() {
		this.node = null;
		this.type = TransitionType.IN;
		this.duration = Duration.ZERO;
	}
	
	/**
	 * Returns a unique transition id for a transition sub class.
	 * @return int
	 */
	public abstract int getId();
	
	/**
	 * Returns the transition type.
	 * @return {@link TransitionType}
	 */
	public TransitionType getType() {
		return this.type;
	}

	/**
	 * Sets the transition type.
	 * @param type the type
	 */
	public void setType(TransitionType type) {
		this.type = type;
	}
	
	/**
	 * Returns the transition duration. 
	 * @return Duration
	 */
	public Duration getDuration() {
		return this.duration;
	}
	
	/**
	 * Sets the duration.
	 * @param duration the duration
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
		super.setCycleDuration(duration);
	}

	/**
	 * Returns the node this transition will apply to.
	 * @return Region
	 */
	public Region getNode() {
		return this.node;
	}
	
	/**
	 * Sets the node to animate.
	 * @param node the node
	 */
	public void setNode(Region node) {
		this.node = node;
	}
	
	/**
	 * Clamps the given value between the min and max inclusive.
	 * @param value the value to clamp
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return float
	 */
	static final double clamp(double value, double min, double max) {
		return Math.max(Math.min(value, max), min);
	}
}
