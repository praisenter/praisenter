package org.praisenter.ui.slide.convert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.effects.ease.EasingFunction;
import org.praisenter.data.slide.effects.ease.EasingType;
import org.praisenter.data.slide.effects.transition.SlideTransition;
import org.praisenter.ui.slide.transition.BlindsTransition;
import org.praisenter.ui.slide.transition.CustomInterpolator;
import org.praisenter.ui.slide.transition.PushTransition;
import org.praisenter.ui.slide.transition.ShapedTransition;
import org.praisenter.ui.slide.transition.SplitTransition;
import org.praisenter.ui.slide.transition.SwapTransition;
import org.praisenter.ui.slide.transition.SwipeTransition;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.util.Duration;

// TODO should we apply the transitions from the slide or component perspective wrt. placeholder changing?

public class TransitionConverter {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final Transition toJavaFX(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		if (source == null) {
			SwapTransition tx  = new SwapTransition();
			tx.setInterpolator(new CustomInterpolator(EasingFunction.LINEAR, EasingType.IN));
			tx.setNode(node);
			tx.setRate(isIn ? 1 : -1);
			return tx;
		}
		
		switch (source.getTransitionType()) {
			case BLINDS:
				return TransitionConverter.toBlinds(source, slide, component, node, isIn);
			case FADE:
				return TransitionConverter.toFade(source, slide, component, node, isIn);
			case PUSH:
				return TransitionConverter.toPush(source, slide, component, node, isIn);
			case SHAPE:
				return TransitionConverter.toShaped(source, slide, component, node, isIn);
			case SPLIT:
				return TransitionConverter.toSplit(source, slide, component, node, isIn);
			case SWAP:
				return TransitionConverter.toSwap(source, slide, component, node, isIn);
			case SWIPE:
				return TransitionConverter.toSwipe(source, slide, component, node, isIn);
			case ZOOM:
				return TransitionConverter.toZoom(source, slide, component, node, isIn);
			default:
				LOGGER.warn("Unknown transition type: {}. Using Swap instead.", source.getClass());
				return TransitionConverter.toSwap(source, slide, component, node, isIn);
		}
	}

	private static final SwapTransition toSwap(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		SwapTransition tx  = new SwapTransition();
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		return tx;
	}
	
	private static final FadeTransition toFade(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		FadeTransition tx  = new FadeTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setFromValue(isIn ? 0.0 : 1.0);
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setToValue(isIn ? 1 : 0.0);
		return tx;
	}
	
	private static final ScaleTransition toZoom(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		ScaleTransition tx  = new ScaleTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setFromX(isIn ? 0.0 : 1.0);
		tx.setFromY(isIn ? 0.0 : 1.0);
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setToX(isIn ? 1.0 : 0.0);
		tx.setToY(isIn ? 1.0 : 0.0);
		return tx;
	}
	
	private static final SwipeTransition toSwipe(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		SwipeTransition tx = new SwipeTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setDirection(source.getDirection());
		tx.setHeight(component != null ? component.getHeight() : slide.getHeight());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setWidth(component != null ? component.getWidth() : slide.getWidth());
		return tx;
	}
	
	private static final SplitTransition toSplit(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		SplitTransition tx = new SplitTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setOperation(source.getOperation());
		tx.setOrientation(source.getOrientation());
		tx.setHeight(component != null ? component.getHeight() : slide.getHeight());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setWidth(component != null ? component.getWidth() : slide.getWidth());
		return tx;
	}
	
	private static final ShapedTransition toShaped(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		ShapedTransition tx = new ShapedTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setOperation(source.getOperation());
		tx.setShapeType(source.getShapeType());
		tx.setHeight(component != null ? component.getHeight() : slide.getHeight());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setWidth(component != null ? component.getWidth() : slide.getWidth());
		return tx;
	}
	
	private static final BlindsTransition toBlinds(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		BlindsTransition tx = new BlindsTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setOrientation(source.getOrientation());
		tx.setBlindsCount(source.getBlindCount());
		tx.setHeight(component != null ? component.getHeight() : slide.getHeight());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setWidth(component != null ? component.getWidth() : slide.getWidth());
		return tx;
	}

	private static final PushTransition toPush(SlideTransition source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		PushTransition tx = new PushTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setDirection(source.getDirection());
		tx.setHeight(slide.getHeight());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setWidth(slide.getWidth());
		return tx;
	}
}
