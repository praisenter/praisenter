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
package org.praisenter.animation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.Timer;

import org.praisenter.animation.easings.CubicEasing;
import org.praisenter.animation.easings.Easing;
import org.praisenter.animation.easings.Easings;
import org.praisenter.animation.transitions.Swap;
import org.praisenter.animation.transitions.Transition;
import org.praisenter.animation.transitions.TransitionType;

/**
 * Represents an animator for a transtion.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class TransitionAnimator implements ActionListener, Serializable {
	/** The version id */
	private static final long serialVersionUID = -3895949621800401735L;

	/** The easing function */
	protected static final Easing DEFAULT_EASING = Easings.getEasingForId(CubicEasing.ID);
	
	/** The transition to animate */
	protected Transition transition;
	
	/** The total duration of the transition in nanoseconds */
	protected long duration;
	
	/** The delay in milliseconds for the transition animation */
	protected int delay;
	
	/** The easing for the transition */
	protected Easing easing;
	
	/** The timer for the transition */
	protected transient Timer timer;

	/** The transitioning component */
	protected transient Component component;

	/** The start time in nanoseconds */
	protected long time;
	
	/** The percent complete */
	protected double percentComplete;
	
	/** True if the transition is complete */
	protected boolean complete;

	/** True if the transition should do one more iteration */
	protected boolean lastIteration;
	
	/**
	 * Minimal constructor.
	 * @param transition the transition
	 */
	public TransitionAnimator(Transition transition) {
		this(transition, 0);
	}
	
	/**
	 * Optional constructor.
	 * @param transition the transition
	 * @param duration the duration in milliseconds
	 */
	public TransitionAnimator(Transition transition, int duration) {
		this(transition, duration, 20);
	}
	
	/**
	 * Optional constructor.
	 * @param transition the transition
	 * @param duration the duration in milliseconds
	 * @param delay the delay in milliseconds
	 */
	public TransitionAnimator(Transition transition, int duration, int delay) {
		this(transition, duration, delay, DEFAULT_EASING);
	}
	
	/**
	 * Full constructor.
	 * @param transition the transition
	 * @param duration the duration in milliseconds
	 * @param delay the delay in milliseconds
	 * @param easing the easing function
	 */
	public TransitionAnimator(Transition transition, int duration, int delay, Easing easing) {
		if (duration < 0) {
			duration = 0;
		}
		
		if (transition instanceof Swap) {
			duration = 0;
		}
		
		if (easing == null) {
			easing = DEFAULT_EASING;
		}
		
		this.transition = transition;
		this.duration = milliToNano(duration);
		this.delay = delay;
		this.easing = easing;
		
		this.component = null;
		this.time = 0;
		this.percentComplete = 0.0;
		this.complete = false;
		this.lastIteration = false;
	}
	
	/**
	 * Starts this transition.
	 * @param component the component to repaint
	 */
	public void start(Component component) {
		this.component = component;
		this.time = System.nanoTime();
		this.percentComplete = 0.0;
		this.complete = false;
		this.lastIteration = false;
		
		// setup the timer
		this.timer = new Timer(0, this);
		this.timer.setDelay(this.delay);
		
		this.timer.start();
	}
	
	/**
	 * Stops this transition.
	 */
	public void stop() {
		this.timer.stop();
		this.percentComplete = 1.0;
		this.complete = true;
		this.lastIteration = true;
		// execute one last repaint to update
		// the component
		if (this.component != null) {
			this.component.repaint();
		}
	}

	/**
	 * Returns true if this transition is complete.
	 * @return boolean
	 */
	public boolean isComplete() {
		return this.complete;
	}
	
	/**
	 * Returns this transition total duration in milliseconds.
	 * @return long
	 */
	public long getDuration() {
		return nanoToMilli(this.duration);
	}
	
	/**
	 * Returns the transition.
	 * @return {@link Transition}
	 */
	public Transition getTransition() {
		return this.transition;
	}
	
	/**
	 * Returns the percent complete.
	 * @return double
	 */
	public double getPercentComplete() {
		return this.percentComplete;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		long t1 = System.nanoTime();
		// compute the delta time
		long dt = t1 - this.time;
		
		if (this.lastIteration) {
			this.stop();
		}
		
		// compute the percent complete
		if (this.duration > 0) {
			// we need to stop animating when the elapsed time is greater than the duration
			if (dt > this.duration) {
				this.lastIteration = true;
				this.percentComplete = 1.0;
			} else {
				// do the ease in/out depending on the transition type
				if (this.transition.getType() == TransitionType.IN) {
					this.percentComplete = this.easing.easeIn(dt, this.duration);
				} else {
					this.percentComplete = this.easing.easeOut(dt, this.duration);
				}
			}
		} else {
			// a duration of zero basically means swap
			this.percentComplete = 1.0;
			this.lastIteration = true;
		}
		
		if (this.component != null) {
			this.component.repaint();
		}
		
		if (!this.component.isDisplayable()) {
			this.stop();
		}
	}

	/**
	 * Converts the given value from the milli to nano.
	 * @param m the milli value
	 * @return long
	 */
	protected static final long milliToNano(int m) {
		return (long)m * 1000000l;
	}
	
	/**
	 * Converts the given value from the nano to milli.
	 * @param n the nano value
	 * @return int
	 */
	protected static final int nanoToMilli(long n) {
		return (int)Math.ceil((double)n / 1000000.0);
	}
}
