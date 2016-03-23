package org.praisenter.javafx.animation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.slide.animation.Blinds;
import org.praisenter.slide.animation.Fade;
import org.praisenter.slide.animation.Push;
import org.praisenter.slide.animation.Shaped;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Split;
import org.praisenter.slide.animation.Swap;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.animation.Zoom;

import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

public abstract class CustomTransition<T extends SlideAnimation> extends Transition {
	private static final Logger LOGGER = LogManager.getLogger();
	
	final T animation;
	Node node;
	
	public CustomTransition(T animation) {
		this.animation = animation;
		this.setInterpolator(new CustomInterpolator(animation.getEasing()));
		this.setDelay(Duration.millis(animation.getDelay()));
		this.setCycleDuration(Duration.millis(animation.getDuration()));
	}
	
	public static final CustomTransition<?> createCustomTransition(SlideAnimation animation) {
		if (animation.getClass() == Blinds.class) {
			return new BlindsTransition((Blinds)animation);
		} else if (animation.getClass() == Fade.class) {
			return new FadeTransition((Fade)animation);
		} else if (animation.getClass() == Push.class) {
			return new PushTransition((Push)animation);
		} else if (animation.getClass() == Shaped.class) {
			return new ShapedTransition((Shaped)animation);
		} else if (animation.getClass() == Split.class) {
			return new SplitTransition((Split)animation);
		} else if (animation.getClass() == Swap.class) {
			return new SwapTransition((Swap)animation);
		} else if (animation.getClass() == Swipe.class) {
			return new SwipeTransition((Swipe)animation);
		} else if (animation.getClass() == Zoom.class) {
			return new ZoomTransition((Zoom)animation);
		}
		
		LOGGER.warn("No Java FX transition is mapped for animation type {}", animation.getClass().getName());
		
		// swap is the default transition
		Swap def = new Swap();
		def.setId(animation.getId());
		return new SwapTransition(def);
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
	
	public T getAnimation() {
		return this.animation;
	}
}
