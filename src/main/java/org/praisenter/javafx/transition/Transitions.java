package org.praisenter.javafx.transition;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public final class Transitions {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** All the transitions by id */
	private static final Map<Integer, Class<?>> BY_ID = new HashMap<Integer, Class<?>>();
	
	static {
		BY_ID.put(Swap.ID, null);
		BY_ID.put(Fade.ID, Fade.class);
		
		// swipes
		BY_ID.put(SwipeLeft.ID, SwipeLeft.class);
		BY_ID.put(SwipeRight.ID, SwipeRight.class);
		BY_ID.put(SwipeUp.ID, SwipeUp.class);
		BY_ID.put(SwipeDown.ID, SwipeDown.class);
		BY_ID.put(SwipeClockwise.ID, SwipeClockwise.class);
		BY_ID.put(SwipeCounterClockwise.ID, SwipeCounterClockwise.class);
		BY_ID.put(SwipeWedgeDown.ID, SwipeWedgeDown.class);
		BY_ID.put(SwipeWedgeUp.ID, SwipeWedgeUp.class);
		
		// circle
		BY_ID.put(CircularCollapse.ID, CircularCollapse.class);
		BY_ID.put(CircularExpand.ID, CircularExpand.class);
		
		// push
		BY_ID.put(PushLeft.ID, PushLeft.class);
		BY_ID.put(PushRight.ID, PushRight.class);
		BY_ID.put(PushUp.ID, PushUp.class);
		BY_ID.put(PushDown.ID, PushDown.class);
		
		// split
		BY_ID.put(HorizontalSplitCollapse.ID, HorizontalSplitCollapse.class);
		BY_ID.put(HorizontalSplitExpand.ID, HorizontalSplitExpand.class);
		BY_ID.put(VerticalSplitCollapse.ID, VerticalSplitCollapse.class);
		BY_ID.put(VerticalSplitExpand.ID, VerticalSplitExpand.class);
		
		// blinds
		BY_ID.put(HorizontalBlinds.ID, HorizontalBlinds.class);
		BY_ID.put(VerticalBlinds.ID, VerticalBlinds.class);
		
		// zoom
		BY_ID.put(ZoomIn.ID, ZoomIn.class);
		BY_ID.put(ZoomOut.ID, ZoomOut.class);
	}
	
	/** Hidden default constructor */
	private Transitions() {}
	
	/**
	 * Returns a new instance of the given transition class.
	 * @param clazz the transition class
	 * @param node the node to transition
	 * @param type the transition type
	 * @param duration the transition duration
	 * @param easing the transition easing
	 * @return Transition
	 */
	private static final Transition getTransition(Class<?> clazz, Region node, TransitionType type, Duration duration, Interpolator easing) {
		if (clazz != null) {
			try {
				Transition tx = (Transition)clazz.getConstructor(Region.class, TransitionType.class, Duration.class).newInstance(node, type, duration);
				tx.setInterpolator(easing);
				return tx;
			} catch (Exception e) {
				LOGGER.warn("Failed to instantiate class " + clazz.getName() + ".", e);
			}
		}
		return new SwipeLeft(node, type, duration);
	}
	
	/**
	 * Returns a transition to handle the incoming and outgoing nodes.
	 * @param clazz the transition class
	 * @param in the incoming node
	 * @param out the outgoing node
	 * @param duration the transition duration
	 * @param easing the transition easing
	 * @param parallel true if the transitions for the in and out nodes are performed in parllel or sequence
	 * @return Transition
	 */
	private static final Transition getTransition(Class<?> clazz, Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
		Transition cTx = getTransition(clazz, out, TransitionType.OUT, duration, easing);
		Transition tTx = getTransition(clazz, in, TransitionType.IN, duration, easing);
		if (cTx != null && tTx != null) {
			if (parallel) {
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

	/**
	 * Returns a transition to handle the incoming and outgoing nodes by the transition id.
	 * @param id the transition id
	 * @param in the incoming node
	 * @param out the outgoing node
	 * @param duration the transition duration
	 * @param easing the transition easing
	 * @param parallel true if the transitions for the in and out nodes are performed in parllel or sequence
	 * @return Transition
	 */
	public static final Transition getTransition(int id, Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
		Class<?> clazz = BY_ID.get(id);
		Transition cTx = getTransition(clazz, out, TransitionType.OUT, duration, easing);
		Transition tTx = getTransition(clazz, in, TransitionType.IN, duration, easing);
		if (cTx != null && tTx != null) {
			if (parallel) {
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
	
	public static final Transition getCircularCollapse(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
		return getTransition(CircularCollapse.class, in, out, duration, easing, parallel);
	}
	
	public static final Transition getCircularExpand(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
		return getTransition(CircularExpand.class, in, out, duration, easing, parallel);
	}
	
	public static final Transition getHorizontalBlinds(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
		return getTransition(HorizontalBlinds.class, in, out, duration, easing, parallel);
	}
//	public static final Transition getVerticalBlinds(TransitionType type) { return new Fade(type); }
	public static final Transition getHorizontalSplitCollapse(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
		return getTransition(HorizontalSplitCollapse.class, in, out, duration, easing, parallel);
	}
//	public static final Transition getHorizontalSplitExpand(TransitionType type) { return new Fade(type); }
//	public static final Transition getVerticalSplitCollapse(TransitionType type) { return new Fade(type); }
//	public static final Transition getVerticalSplitExpand(TransitionType type) { return new Fade(type); }
	public static final Transition getPushDown(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
		return getTransition(PushDown.class, in, out, duration, easing, parallel);
	}
//	public static final Transition getPushUp(TransitionType type) { return new Fade(type); }
//	public static final Transition getPushLeft(TransitionType type) { return new Fade(type); }
//	public static final Transition getPushRight(TransitionType type) { return new Fade(type); }
	public static final Transition getFade(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
		return getTransition(Fade.class, in, out, duration, easing, parallel);
	}
//	public static final Transition getSwipeClockwise(TransitionType type) { return new Fade(type); }
//	public static final Transition getSwipeCounterClockwise(TransitionType type) { return new Fade(type); }
//	public static final Transition getSwipeDown(TransitionType type) { return new Fade(type); }
//	public static final Transition getSwipeUp(TransitionType type) { return new Fade(type); }
//	public static final Transition getSwipeLeft(TransitionType type) { return new Fade(type); }
//	public static final Transition getSwipeRight(TransitionType type) { return new Fade(type); }
//	public static final Transition getSwipeWedgeDown(TransitionType type) { return new Fade(type); }
//	public static final Transition getSwipeWedgeUp(TransitionType type) { return new Fade(type); }
//	public static final Transition getZoomIn(TransitionType type) { return new Fade(type); }
//	public static final Transition getZoomOut(TransitionType type) { return new Fade(type); }
	
	public static final Transition get(int id, TransitionType type) {
		return null;
	}
}
