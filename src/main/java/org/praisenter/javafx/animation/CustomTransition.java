package org.praisenter.javafx.animation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.slide.animation.SlideAnimation;

import javafx.animation.Transition;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

// FIXME convert other transitions to use the getBounds method on this class

public abstract class CustomTransition<T extends SlideAnimation> extends Transition {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final Rectangle2D EMPTY_BOUNDS = new Rectangle2D(0, 0, 0, 0);
	
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
	
	protected Rectangle2D getBounds() {
		Rectangle2D bounds = EMPTY_BOUNDS;
		if (this.node == null) {
			LOGGER.warn("Node to animate is null for animation: " + this.animation.getId());
			return bounds;
		}
		
		if (this.node instanceof Region) {
			bounds = new Rectangle2D(0, 0, ((Region)this.node).getPrefWidth(), ((Region)this.node).getPrefHeight());
		} else if (this.node instanceof MediaView) {
			bounds = new Rectangle2D(0, 0, ((MediaView)this.node).getFitWidth(), ((MediaView)this.node).getFitHeight());
		} else if (this.node instanceof ImageView) {
			bounds = new Rectangle2D(0, 0, ((ImageView)this.node).getFitWidth(), ((ImageView)this.node).getFitHeight());
		} else if (this.node instanceof Canvas) {
			bounds = new Rectangle2D(0, 0, ((Canvas)this.node).getWidth(), ((Canvas)this.node).getHeight());
		} else {
			Bounds bds = this.node.getBoundsInParent();
			bounds = new Rectangle2D(0, 0, bds.getWidth(), bds.getHeight());
			LOGGER.warn("Unable to determine the node's bounds based on its type.  Using the getBoundsInParent method instead which can give incorrect results.");
		}
		
		return bounds;
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
