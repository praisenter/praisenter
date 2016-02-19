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

import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Translates the node's y coordinate up.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PushUp extends CustomTransition {
	/** The transition id */
	public static final int ID = 62;

	/**
	 * Full constructor.
	 * @param node the node to animate
	 * @param type the transition type
	 * @param duration the transition duration
	 */
	public PushUp(Region node, TransitionType type, Duration duration) {
		super(node, type, duration);
	}

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
		double y = 0;
		if (this.type == TransitionType.IN) {
			y = this.node.getPrefHeight() * (1.0 - frac);
		} else {
			y = -this.node.getPrefHeight() * (frac);
		}
		this.node.setLayoutY(y);
	}
}