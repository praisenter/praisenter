package org.praisenter.transitions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.praisenter.transitions.easing.CubicEasing;
import org.praisenter.transitions.easing.Easing;

/**
 * Represents an animator for a transtion.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TransitionAnimator implements ActionListener {
	/** The easing function */
	protected static final Easing EASING = new CubicEasing();
	
	/** The transition to animate */
	protected Transition transition;
	
	/** The total duration of the transition in nanoseconds */
	protected long duration;
	
	/** The timer for the transition */
	protected Timer timer;

	/** The transitioning component */
	protected Component component;

	/** The start time in nanoseconds */
	protected long time;
	
	/** The percent complete */
	protected double percentComplete;

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
		if (duration < 0) {
			duration = 0;
		}
		this.transition = transition;
		this.duration = milliToNano(duration);
		this.timer = new Timer(0, this);
		if (duration == 0 || transition instanceof Swap) {
			this.timer.setRepeats(false);
		}
		this.component = null;
		this.time = 0;
		this.percentComplete = 0;
	}

	/**
	 * Starts this transition.
	 * @param component the component to repaint
	 */
	public void start(Component component) {
		this.component = component;
		this.time = System.nanoTime();
		this.percentComplete = 0.0;
		this.timer.start();
	}
	
	/**
	 * Stops this transition.
	 */
	public void stop() {
		this.timer.stop();
		this.percentComplete = 1.0;
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
		return !this.timer.isRunning();
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
		return percentComplete;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		long t1 = System.nanoTime();
		// compute the delta time
		long dt = t1 - this.time;
		
		// compute the percent complete
		if (this.duration > 0) {
			// do the ease in/out depending on the transition type
			if (this.transition.type == Transition.Type.IN) {
				this.percentComplete = EASING.easeIn(dt, this.duration);
			} else {
				this.percentComplete = EASING.easeOut(dt, this.duration);
			}
		} else {
			// a duration of zero basically means swap
			this.percentComplete = 1.0;
		}
		
		if (this.component != null) {
			this.component.repaint();
		}
		
		// see if we have animated long enough
		if (dt >= this.duration) {
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
