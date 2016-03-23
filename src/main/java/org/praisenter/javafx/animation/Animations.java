/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.animation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Fade;
import org.praisenter.slide.animation.Swap;

/**
 * Helper class used to generate a transition for two nodes.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Animations {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
//	/** All the transitions by id */
//	private static final Map<Integer, Class<?>> BY_ID = new HashMap<Integer, Class<?>>();
//	
//	static {
//		BY_ID.put(Swap.ID, Swap.class);
//		BY_ID.put(Fade.ID, Fade.class);
//		
//		// swipes
//		BY_ID.put(SwipeLeft.ID, SwipeLeft.class);
//		BY_ID.put(SwipeRight.ID, SwipeRight.class);
//		BY_ID.put(SwipeUp.ID, SwipeUp.class);
//		BY_ID.put(SwipeDown.ID, SwipeDown.class);
//		BY_ID.put(SwipeClockwise.ID, SwipeClockwise.class);
//		BY_ID.put(SwipeCounterClockwise.ID, SwipeCounterClockwise.class);
//		BY_ID.put(SwipeWedgeDown.ID, SwipeWedgeDown.class);
//		BY_ID.put(SwipeWedgeUp.ID, SwipeWedgeUp.class);
//		
//		// circle
//		BY_ID.put(CircularCollapse.ID, CircularCollapse.class);
//		BY_ID.put(CircularExpand.ID, CircularExpand.class);
//		
//		// push
//		BY_ID.put(PushLeft.ID, PushLeft.class);
//		BY_ID.put(PushRight.ID, PushRight.class);
//		BY_ID.put(PushUp.ID, PushUp.class);
//		BY_ID.put(PushDown.ID, PushDown.class);
//		
//		// split
//		BY_ID.put(HorizontalSplitCollapse.ID, HorizontalSplitCollapse.class);
//		BY_ID.put(HorizontalSplitExpand.ID, HorizontalSplitExpand.class);
//		BY_ID.put(VerticalSplitCollapse.ID, VerticalSplitCollapse.class);
//		BY_ID.put(VerticalSplitExpand.ID, VerticalSplitExpand.class);
//		
//		// blinds
//		BY_ID.put(HorizontalBlinds.ID, HorizontalBlinds.class);
//		BY_ID.put(VerticalBlinds.ID, VerticalBlinds.class);
//		
//		// zoom
//		BY_ID.put(ZoomIn.ID, ZoomIn.class);
//		BY_ID.put(ZoomOut.ID, ZoomOut.class);
//	}
	
	/** Hidden default constructor */
	private Animations() {}
	
//	public static final Set<Integer> getAnimationIds() {
//		return Collections.unmodifiableSet(BY_ID.keySet());
//	}
//	
//	public static final SlideAnimation getTransition(int id) {
//		Class<?> clazz = BY_ID.get(id);
//		try {
//			return (SlideAnimation)clazz.newInstance();
//		} catch (Exception e) {
//			return null;
//		}
//	}
	
	
	// TODO transfer the parallel/sequential code elsewhere
	// TODO zoom in/out need to be examined too
//	
//	/**
//	 * Returns a new instance of the given transition class.
//	 * <p>
//	 * Returns null if clazz is null.
//	 * @param clazz the transition class
//	 * @param node the node to transition
//	 * @param type the transition type
//	 * @param duration the transition duration
//	 * @param easing the transition easing
//	 * @return Transition
//	 */
//	private static final Transition getTransition(Class<?> clazz, Region node, TransitionType type, Duration duration, Interpolator easing) {
//		if (clazz != null) {
//			if (Swap.class.isAssignableFrom(clazz)) {
//				return new Swap(node, type);
//			} else {
//				try {
//					Transition tx = (Transition)clazz.getConstructor(Region.class, TransitionType.class, Duration.class).newInstance(node, type, duration);
//					tx.setInterpolator(easing);
//					return tx;
//				} catch (Exception e) {
//					LOGGER.warn("Failed to instantiate class " + clazz.getName() + ".", e);
//				}
//			}
//		}
//		return null;
//	}
//	
//	/**
//	 * Returns a transition to handle the incoming and outgoing nodes.
//	 * @param clazz the transition class
//	 * @param in the incoming node
//	 * @param out the outgoing node
//	 * @param duration the transition duration
//	 * @param easing the transition easing
//	 * @param parallel true if the transitions for the in and out nodes are performed in parllel or sequence
//	 * @return Transition
//	 */
//	private static final Transition getTransition(Class<?> clazz, Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
//		Transition cTx = getTransition(clazz, out, TransitionType.OUT, duration, easing);
//		Transition tTx = getTransition(clazz, in, TransitionType.IN, duration, easing);
//		if (cTx != null && tTx != null) {
//			if (parallel) {
//				return new ParallelTransition(cTx, tTx);
//			} else {
//				return new SequentialTransition(cTx, tTx);
//			}
//		} else if (cTx != null) {
//			return cTx;
//		} else {
//			return tTx;
//		}
//	}
//
//	/**
//	 * Returns a transition to handle the incoming and outgoing nodes by the transition id.
//	 * @param id the transition id
//	 * @param in the incoming node
//	 * @param out the outgoing node
//	 * @param duration the transition duration
//	 * @param easing the transition easing
//	 * @param parallel true if the transitions for the in and out nodes are performed in parllel or sequence
//	 * @return Transition
//	 */
//	public static final Transition getTransition(int id, Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
//		Class<?> clazz = BY_ID.get(id);
//		Transition cTx = getTransition(clazz, out, TransitionType.OUT, duration, easing);
//		Transition tTx = getTransition(clazz, in, TransitionType.IN, duration, easing);
//		if (cTx != null && tTx != null) {
//			if (parallel) {
//				return new ParallelTransition(cTx, tTx);
//			} else {
//				return new SequentialTransition(cTx, tTx);
//			}
//		} else if (cTx != null) {
//			return cTx;
//		} else {
//			return tTx;
//		}
//	}
//	
//	/**
//	 * Returns a {@link Swap} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @return Transition
//	 */
//	public static final Transition getSwap(Region in, Region out) {
//		return getTransition(Swap.class, in, out, Duration.ZERO, null, true);
//	}
//	
//	/**
//	 * Returns a {@link CircularCollapse} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getCircularCollapse(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
//		return getTransition(CircularCollapse.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link CircularExpand} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getCircularExpand(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(CircularExpand.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link HorizontalBlinds} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getHorizontalBlinds(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
//		return getTransition(HorizontalBlinds.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link VerticalBlinds} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getVerticalBlinds(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(VerticalBlinds.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link HorizontalSplitCollapse} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getHorizontalSplitCollapse(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(HorizontalSplitCollapse.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link HorizontalSplitExpand} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getHorizontalSplitExpand(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(HorizontalSplitExpand.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link VerticalSplitCollapse} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getVerticalSplitCollapse(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(VerticalSplitCollapse.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link VerticalSplitExpand} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getVerticalSplitExpand(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(VerticalSplitExpand.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link PushDown} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getPushDown(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(PushDown.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link PushUp} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getPushUp(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(PushUp.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link PushLeft} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getPushLeft(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(PushLeft.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link PushRight} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getPushRight(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(PushRight.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link Fade} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getFade(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(Fade.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link SwipeClockwise} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeClockwise(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeClockwise.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link SwipeCounterClockwise} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeCounterClockwise(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeCounterClockwise.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link SwipeDown} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeDown(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeDown.class, in, out, duration, easing, parallel);
//	}
//
//	/**
//	 * Returns a {@link SwipeUp} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeUp(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeUp.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link SwipeLeft} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeLeft(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeLeft.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link SwipeRight} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeRight(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeRight.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link SwipeWedgeDown} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeWedgeDown(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeWedgeDown.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link SwipeWedgeUp} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getSwipeWedgeUp(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) { 
//		return getTransition(SwipeWedgeUp.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link ZoomIn} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getZoomIn(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
//		// if the incoming node is null
//		// then the user really wants to zoom out the out going node (otherwise it would just be a clip transition)
//		if (in == null) {
//			return getTransition(ZoomOut.class, in, out, duration, easing, parallel);
//		}
//		return getTransition(ZoomIn.class, in, out, duration, easing, parallel);
//	}
//	
//	/**
//	 * Returns a {@link ZoomOut} transition for the given nodes.
//	 * @param in the incoming node
//	 * @param out the out going node
//	 * @param duration the transition duration
//	 * @param easing the transition interpolator
//	 * @param parallel true if the in and out transitions should be in parallel
//	 * @return Transition
//	 */
//	public static final Transition getZoomOut(Region in, Region out, Duration duration, Interpolator easing, boolean parallel) {
//		// if the out going node is null
//		// then the user really wants to zoom in the incoming node (otherwise it would just be a clip transition)
//		if (out == null) {
//			return getTransition(ZoomIn.class, in, out, duration, easing, parallel);
//		}
//		return getTransition(ZoomOut.class, in, out, duration, easing, parallel);
//	}
}
