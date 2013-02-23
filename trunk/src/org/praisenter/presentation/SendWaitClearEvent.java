/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.presentation;

import java.io.Serializable;

import org.praisenter.animation.TransitionAnimator;
import org.praisenter.slide.Slide;

/**
 * Represents a custom {@link SendEvent} for notifications.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SendWaitClearEvent extends SendEvent implements PresentationEvent, Serializable {
	/** The version id */
	private static final long serialVersionUID = 5930251544324893092L;

	/** The transition out animator */
	protected TransitionAnimator outAnimator;
	
	/** The wait period between the in and out transitions */
	protected int waitPeriod;
	
	/**
	 * Full constructor.
	 * @param configuration the event configuration
	 * @param inAnimator the in animator; see {@link #getAnimator()}
	 * @param slide the slide
	 * @param outAnimator the out animator; see {@link #getOutAnimator()}
	 * @param waitPeriod the wait period
	 */
	public SendWaitClearEvent(PresentationEventConfiguration configuration, TransitionAnimator inAnimator, Slide slide, TransitionAnimator outAnimator, int waitPeriod) {
		super(configuration, inAnimator, slide);
		this.outAnimator = outAnimator;
		this.waitPeriod = waitPeriod;
	}
	
	/**
	 * Returns the out transition animator.
	 * @return {@link TransitionAnimator}
	 */
	public TransitionAnimator getOutAnimator() {
		return this.outAnimator;
	}

	/**
	 * Returns the wait period between the in and out transitions.
	 * @return int
	 */
	public int getWaitPeriod() {
		return this.waitPeriod;
	}
}
