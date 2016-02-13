package org.praisenter.javafx.transition;

import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public abstract class CustomTransition extends Transition {
	/** The node being transitioned */
	final Region node;
	
	/** The transition type */
	final TransitionType type;
	
	/** The transition duration */
	final Duration duration;
	
	public CustomTransition(Region node, TransitionType type, Duration duration) {
		if (node == null) throw new NullPointerException();
		this.type = type;
		this.node = node;
		this.duration = duration;
		this.setCycleDuration(duration);
	}
	
	/**
	 * Returns a unique transition id for a transition sub class.
	 * @return int
	 */
	public abstract int getId();
	
	public TransitionType getType() {
		return type;
	}

	public Duration getDuration() {
		return duration;
	}

	public Region getNode() {
		return node;
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
