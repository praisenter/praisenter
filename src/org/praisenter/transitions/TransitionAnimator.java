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
package org.praisenter.transitions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.praisenter.easings.CubicEasing;
import org.praisenter.easings.Easing;
import org.praisenter.easings.Easings;
import org.praisenter.preferences.Preferences;
import org.praisenter.preferences.RenderQuality;

/**
 * Represents an animator for a transtion.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TransitionAnimator implements ActionListener {
	/** The easing function */
	protected static final Easing DEFAULT_EASING = Easings.getEasingForId(CubicEasing.ID);
	
	/** The transition to animate */
	protected Transition transition;
	
	/** The total duration of the transition in nanoseconds */
	protected long duration;
	
	/** The easing for the transition */
	protected Easing easing;
	
	/** The timer for the transition */
	protected Timer timer;

	/** The transitioning component */
	protected Component component;

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
	 * @param duration the duration
	 */
	public TransitionAnimator(Transition transition, int duration) {
		this(transition, duration, DEFAULT_EASING);
	}
	
	/**
	 * Full constructor.
	 * @param transition the transition
	 * @param duration the duration
	 * @param easing the easing function
	 */
	public TransitionAnimator(Transition transition, int duration, Easing easing) {
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
		this.easing = easing;
		this.timer = new Timer(0, this);
		
		// set the frequency of updates based on the render quality
		RenderQuality quality = Preferences.getInstance().getRenderQuality();
		
		// the choice of delay is tricky since the user has control of the 
		// duration of the animation.  They could choose 10 milliseconds. 
		// I think that 100 milliseconds is probably the lowest visible 
		// animation speed, so we can safely assume that we will get a few
		// cycles depending on the quality (it almost just looks like a 
		// swap any lower)
		
		// 1000 / 20 = 50 times/second
		this.timer.setDelay(20);
		if (quality == RenderQuality.HIGH) {
			// 1000 / 10 = 100 times/second
			this.timer.setDelay(10);
		} else if (quality == RenderQuality.LOW) {
			// 1000 / 50 = 20 times/ second
			this.timer.setDelay(50);
		}
		
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
			// do the ease in/out depending on the transition type
			double pc = 0.0;
			if (this.transition.type == Transition.Type.IN) {
				pc = this.easing.easeIn(dt, this.duration);
			} else {
				pc = this.easing.easeOut(dt, this.duration);
			}
			// clamp the percent complete
			this.percentComplete = Math.min(pc, 1.0);
			if (pc > 1.0) {
				this.lastIteration = true;
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
