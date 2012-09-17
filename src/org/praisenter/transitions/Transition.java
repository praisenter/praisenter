package org.praisenter.transitions;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

/**
 * Represents a transition from one image to another.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Transition implements ActionListener, Cloneable {
	/**
	 * The transition type.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static enum Type {
		/** Transition in */
		IN,
		
		/** Transition out */
		OUT
	}
	
	/** The transition name */
	protected String name;
	
	/** The transition type */
	protected Type type;
	
	/** The total duration of the transition in nanoseconds */
	protected long duration;

	/** The timer for the transition */
	protected Timer timer;

	/** The transitioning component */
	protected Component component;
	
	/** The start time in nanoseconds */
	protected long time;

	/**
	 * Minimal constructor.
	 * @param name the transition name
	 * @param type the transition type
	 */
	public Transition(String name, Type type) {
		if (type == null || name == null) throw new NullPointerException();
		this.name = name;
		this.type = type;
		this.timer = new Timer(0, this);
		this.duration = 0;
		this.time = 0;
		this.component = null;
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
	
	/**
	 * Returns the frequency of updates given the transition duration.
	 * @param duration the duration
	 * @return int
	 */
	protected static final int getFrequency(int duration) {
		// the frequency of the transition should be 10 iterations per half a second
		return duration / 500 * 50;
	}
	
	/**
	 * Renders this transition to the given graphics given the beginning image and the
	 * ending image.
	 * @param g2d the graphics to render to
	 * @param image0 the beginning image
	 * @param image1 the ending image
	 */
	public abstract void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1);
	
	/**
	 * Returns the unique id for a transition type.
	 * <p>
	 * Each instance of the same subclass of {@link Transition} must
	 * have the same id (in most cases). 
	 * @return int
	 */
	public abstract int getTransitionId();
	
	/**
	 * Starts this transition.
	 * @param component the component to repaint
	 */
	public void start(Component component) {
		this.component = component;
		this.time = System.nanoTime();
		this.timer.start();
	}
	
	/**
	 * Stops this transition.
	 */
	public void stop() {
		this.timer.stop();
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
	 * Returns this transition name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns this transition type.
	 * @return {@link Type}
	 */
	public Type getType() {
		return this.type;
	}
	
	/**
	 * Returns this transition total duration in milliseconds.
	 * @return long
	 */
	public long getDuration() {
		return nanoToMilli(this.duration);
	}
	
	/**
	 * Sets the total duration of this transition.
	 * @param duration the total duration in milliseconds
	 */
	public void setDuration(int duration) {
		this.duration = milliToNano(duration);
		// the frequency of the transition should be 10 iterations per half a second
		this.timer.setDelay(getFrequency(duration));
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.component != null) {
			this.component.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Transition clone() {
		return null;
	}
}
