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

import org.praisenter.slide.animation.Animation;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Swap;

/**
 * A simple transition where the object is immediately shown.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class SwapTransition extends CustomTransition<Swap> {
	/**
	 * Default constructor.
	 */
	public SwapTransition() {
		super(new Swap(
				AnimationType.IN,
				Animation.DEFAULT_DURATION,
				Animation.DEFAULT_DELAY,
				Animation.DEFAULT_REPEAT_COUNT,
				Animation.DEFAULT_AUTO_REVERSE,
				Animation.DEFAULT_EASING));
	}
	
	/**
	 * Full constructor.
	 * @param animation the animation configuration
	 */
	public SwapTransition(Swap animation) {
		super(animation);
	}

	/* (non-Javadoc)
	 * @see javafx.animation.Animation#stop()
	 */
	@Override
	public void stop() {
		super.stop();
		if (this.node != null) {
			this.node.setVisible(true);
		}
	}
	
	/* (non-Javadoc)
	 * @see javafx.animation.Transition#interpolate(double)
	 */
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		if (this.animation.getType() != AnimationType.IN) {
			this.node.setVisible(false);
		} else {
			this.node.setVisible(true);
		}
	}
}
