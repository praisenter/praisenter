package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Represents a transition from one image to another.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Transition {
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
	
	/** The transition type */
	protected Type type;
	
	/** The transition name */
	protected String name;
	
	/**
	 * Minimal constructor.
	 * @param name the transition name
	 * @param type the transition type
	 */
	public Transition(String name, Type type) {
		if (type == null || name == null) throw new NullPointerException();
		this.name = name;
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Transition) {
			Transition other = (Transition)obj;
			if (this.getTransitionId() == other.getTransitionId()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Renders this transition to the given graphics given the beginning image and the
	 * ending image.
	 * @param g2d the graphics to render to
	 * @param image0 the beginning image
	 * @param image1 the ending image
	 * @param pc the percentage completed
	 */
	public abstract void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc);
	
	/**
	 * Returns a unique transition id for a transition sub class.
	 * @return int
	 */
	public abstract int getTransitionId();
	
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
}
