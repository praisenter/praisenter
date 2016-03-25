package org.praisenter.javafx.animation;

import org.praisenter.slide.animation.SlideAnimation;

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

public abstract class CustomTransition<T extends SlideAnimation> extends Transition {
	final T animation;
	Node node;
	
	public CustomTransition(T animation) {
		if (animation == null) {
			throw new NullPointerException("The animation parameter is null.");
		}
		
		this.animation = animation;
		this.setInterpolator(new CustomInterpolator(animation.getEasing()));
		this.setDelay(Duration.millis(Math.max(0, animation.getDelay())));
		this.setCycleDuration(Duration.millis(Math.max(0, animation.getDuration())));
	}
	
	/**
	 * Clamps the given value between the min and max inclusive.
	 * @param value the value to clamp
	 * @param min the minimum value
	 * @param max the maximum value
	 * @return float
	 */
	protected static final double clamp(double value, double min, double max) {
		return Math.max(Math.min(value, max), min);
	}
	
	public Node getNode() {
		return this.node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
