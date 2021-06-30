package org.praisenter.ui.slide.convert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.animation.AnimationEasingFunction;
import org.praisenter.data.slide.animation.AnimationEasingType;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.graphics.Rectangle;
import org.praisenter.ui.slide.animation.BlindsTransition;
import org.praisenter.ui.slide.animation.CustomInterpolator;
import org.praisenter.ui.slide.animation.PushTransition;
import org.praisenter.ui.slide.animation.ShapedTransition;
import org.praisenter.ui.slide.animation.SplitTransition;
import org.praisenter.ui.slide.animation.SwapTransition;
import org.praisenter.ui.slide.animation.SwipeTransition;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

// TODO should we apply the transitions from the slide or component perspective wrt. placeholder changing?

public class TransitionConverter {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final Transition toJavaFX(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		if (source == null) {
			SwapTransition tx  = new SwapTransition();
			tx.setInterpolator(new CustomInterpolator(AnimationEasingFunction.LINEAR, AnimationEasingType.IN));
			tx.setNode(node);
			tx.setRate(isIn ? 1 : -1);
			tx.setCycleCount(1);
			tx.setAutoReverse(false);
			return tx;
		}
		
		switch (source.getAnimationFunction()) {
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

	private static final SwapTransition toSwap(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		SwapTransition tx  = new SwapTransition();
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		return tx;
	}
	
	private static final FadeTransition toFade(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		FadeTransition tx  = new FadeTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setFromValue(isIn ? 0.0 : 1.0);
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setToValue(isIn ? 1.0 : 0.0);
		return tx;
	}
	
	private static final ScaleTransition toZoom(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
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
	
	private static final SwipeTransition toSwipe(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		SwipeTransition tx = new SwipeTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setDirection(source.getDirection());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setBounds(TransitionConverter.getTransitionBounds(slide, component));
		return tx;
	}
	
	private static final SplitTransition toSplit(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		SplitTransition tx = new SplitTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setOperation(source.getOperation());
		tx.setOrientation(source.getOrientation());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setBounds(TransitionConverter.getTransitionBounds(slide, component));
		return tx;
	}
	
	private static final ShapedTransition toShaped(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		ShapedTransition tx = new ShapedTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setOperation(source.getOperation());
		tx.setShapeType(source.getShapeType());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setBounds(TransitionConverter.getTransitionBounds(slide, component));
		return tx;
	}
	
	private static final BlindsTransition toBlinds(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
		BlindsTransition tx = new BlindsTransition();
		tx.setDuration(new Duration(source.getDuration()));
		tx.setOrientation(source.getOrientation());
		tx.setBlindsCount(source.getBlindCount());
		tx.setInterpolator(new CustomInterpolator(source.getEasingFunction(), source.getEasingType()));
		tx.setNode(node);
		tx.setRate(isIn ? 1 : -1);
		tx.setBounds(TransitionConverter.getTransitionBounds(slide, component));
		return tx;
	}

	private static final PushTransition toPush(SlideAnimation source, Slide slide, SlideComponent component, Node node, boolean isIn) {
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
	
	private static final Bounds getTransitionBounds(Slide slide, SlideComponent component) {
		if (component != null) {
			// FEATURE This won't work if we support component level animation.  
			// The problem is that we don't know exactly the size of the components 
			// until they are rendered, but we can't render them until we've animated them.
			// One option is to clip all components to the bounds of the component
			// I think the only thing that can spill over the bounds are effects, borders, and text.
			// maybe we can use the TextMeasurer to get real bounds???
//			Rectangle ob = component.getLocalOffsetBounds();
			return new BoundingBox(
				-component.getX(), 
				-component.getY(), 
				slide.getWidth(), 
				slide.getHeight());
		} else {
			return new BoundingBox(
				0, 
				0, 
				slide.getWidth(), 
				slide.getHeight());
		}
	}
}
