package org.praisenter.javafx.transition;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public final class Transitions {
	/** Hidden default constructor */
	private Transitions() {}

	private static final boolean isParallel(Region in, Region out) {
		if (in.getLayoutX() != out.getLayoutX() ||
			in.getLayoutY() != out.getLayoutY() ||
			in.getPrefWidth() != out.getPrefWidth() ||
			in.getPrefHeight() != out.getPrefHeight()) {
			return false;
		}
		return true;
	}
	
	public static final CustomTransition getSwap() { return new Swap(); }
	
	public static final Transition getCircularCollapse(Rectangle2D bounds, Region in, Region out, Duration duration, Interpolator easing) {
		// get the transition for the existing node
		CircularCollapse cTx = null;
		if (out != null) {
			cTx = new CircularCollapse(TransitionType.OUT, out, duration, bounds);
			cTx.setInterpolator(easing);
		}
		
		// get the transition for the incoming node
		CircularCollapse tTx = null;
		if (in != null) {
			tTx = new CircularCollapse(TransitionType.IN, in, duration, bounds);
			tTx.setInterpolator(easing);
		}
		
		if (cTx != null && tTx != null) {
			if (isParallel(in, out)) {
				return new ParallelTransition(cTx, tTx);
			} else {
				return new SequentialTransition(cTx, tTx);
			}
		} else if (cTx != null) {
			return cTx;
		} else {
			return tTx;
		}
	}
	
	public static final Transition getCircularExpand(TransitionType type) { return new Fade(type); }
	public static final Transition getHorizontalBlinds(TransitionType type) { return new Fade(type); }
	public static final Transition getVerticalBlinds(TransitionType type) { return new Fade(type); }
	public static final Transition getHorizontalSplitCollapse(TransitionType type) { return new Fade(type); }
	public static final Transition getHorizontalSplitExpand(TransitionType type) { return new Fade(type); }
	public static final Transition getVerticalSplitCollapse(TransitionType type) { return new Fade(type); }
	public static final Transition getVerticalSplitExpand(TransitionType type) { return new Fade(type); }
	public static final Transition getPushDown(TransitionType type) { return new Fade(type); }
	public static final Transition getPushUp(TransitionType type) { return new Fade(type); }
	public static final Transition getPushLeft(TransitionType type) { return new Fade(type); }
	public static final Transition getPushRight(TransitionType type) { return new Fade(type); }
	public static final CustomTransition getFade(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeClockwise(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeCounterClockwise(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeDown(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeUp(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeLeft(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeRight(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeWedgeDown(TransitionType type) { return new Fade(type); }
	public static final Transition getSwipeWedgeUp(TransitionType type) { return new Fade(type); }
	public static final Transition getZoomIn(TransitionType type) { return new Fade(type); }
	public static final Transition getZoomOut(TransitionType type) { return new Fade(type); }
	
	public static final Transition get(int id, TransitionType type) {
		return null;
	}
}
