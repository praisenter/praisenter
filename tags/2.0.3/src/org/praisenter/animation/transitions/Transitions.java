/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.animation.transitions;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;

/**
 * Helper class for transitions.
 * @author William Bittle
 * @version 2.0.3
 * @since 1.0.0
 */
public final class Transitions {
	/** Hidden default constructor */
	private Transitions() {}
	
	/** The list of "in" transitions */
	public static final Transition[] IN = Transitions.getTransitions(TransitionType.IN);
	
	/** The list of "out" transitions */
	public static final Transition[] OUT = Transitions.getTransitions(TransitionType.OUT);
	
	/** The default "in" transition */
	public static final Transition DEFAULT_IN_TRANSITION = new Swap(TransitionType.IN);
	
	/** The default "out" transition */
	public static final Transition DEFAULT_OUT_TRANSITION = new Swap(TransitionType.IN);
	
	/**
	 * Returns a new array of transitions for the given type.
	 * @param type the transition type
	 * @return {@link Transition}[]
	 * @since 2.0.3
	 */
	private static final Transition[] getTransitions(TransitionType type) {
		// return a new array of transitions with the given type
		// in order of transition name
		Transition[] transitions = new Transition[] {
			new HorizontalBlinds(type),
			new VerticalBlinds(type),
			new CircularCollapse(type),
			new CircularExpand(type),
			new Fade(type),
			new PushDown(type),
			new PushLeft(type),
			new PushRight(type),
			new PushUp(type),
			new HorizontalSplitCollapse(type),
			new HorizontalSplitExpand(type),
			new VerticalSplitCollapse(type),
			new VerticalSplitExpand(type),
			new Swap(type),
			new SwipeClockwise(type),
			new SwipeCounterClockwise(type),
			new SwipeDown(type),
			new SwipeLeft(type),
			new SwipeRight(type),
			new SwipeUp(type),
			new SwipeWedgeDown(type),
			new SwipeWedgeUp(type),
			new ZoomIn(type),
			new ZoomOut(type)
		};
		
		return transitions;
	}
	
	/**
	 * Returns a transition for the given id.
	 * <p>
	 * Returns the default (Swap) if not found.
	 * @param id the transition id
	 * @param type the transition type
	 * @return {@link Transition}
	 */
	public static final Transition getTransitionForId(int id, TransitionType type) {
		Transition[] transitions = null;
		if (type == TransitionType.IN) {
			transitions = IN;
		} else {
			transitions = OUT;
		}
		for (Transition transition : transitions) {
			if (transition.getId() == id) {
				return transition;
			}
		}
		if (type == TransitionType.IN) {
			return DEFAULT_IN_TRANSITION;
		} else {
			return DEFAULT_OUT_TRANSITION;
		}
	}
	
	/**
	 * Returns true if transitions are supported by the given device.
	 * @param device the device
	 * @return boolean
	 */
	public static final boolean isTransitionSupportAvailable(GraphicsDevice device) {
		return device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT);
	}
}
