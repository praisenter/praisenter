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
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Transitions {
	/** Hidden default constructor */
	private Transitions() {}
	
	/** The list of "in" transitions */
	public static final Transition[] IN = new Transition[] {
		new Swap(TransitionType.IN),
		new Fade(TransitionType.IN),
		new SwipeRight(TransitionType.IN),
		new SwipeLeft(TransitionType.IN),
		new SwipeUp(TransitionType.IN),
		new SwipeDown(TransitionType.IN),
		new SwipeClockwise(TransitionType.IN),
		new SwipeCounterClockwise(TransitionType.IN),
		new VerticalSplitExpand(TransitionType.IN),
		new VerticalSplitCollapse(TransitionType.IN),
		new HorizontalSplitExpand(TransitionType.IN),
		new HorizontalSplitCollapse(TransitionType.IN),
		new CircularExpand(TransitionType.IN),
		new CircularCollapse(TransitionType.IN),
		new PushRight(TransitionType.IN),
		new PushLeft(TransitionType.IN),
		new PushUp(TransitionType.IN),
		new PushDown(TransitionType.IN),
		new ZoomIn(TransitionType.IN),
		new ZoomOut(TransitionType.IN)
	};
	
	/** The list of "out" transitions */
	public static final Transition[] OUT = new Transition[] {
		new Swap(TransitionType.OUT),
		new Fade(TransitionType.OUT),
		new SwipeRight(TransitionType.OUT),
		new SwipeLeft(TransitionType.OUT),
		new SwipeUp(TransitionType.OUT),
		new SwipeDown(TransitionType.OUT),
		new SwipeClockwise(TransitionType.OUT),
		new SwipeCounterClockwise(TransitionType.OUT),
		new VerticalSplitExpand(TransitionType.OUT),
		new VerticalSplitCollapse(TransitionType.OUT),
		new HorizontalSplitExpand(TransitionType.OUT),
		new HorizontalSplitCollapse(TransitionType.OUT),
		new CircularExpand(TransitionType.OUT),
		new CircularCollapse(TransitionType.OUT),
		new PushRight(TransitionType.OUT),
		new PushLeft(TransitionType.OUT),
		new PushUp(TransitionType.OUT),
		new PushDown(TransitionType.OUT),
		new ZoomIn(TransitionType.OUT),
		new ZoomOut(TransitionType.OUT)
	};
	
	/** The default "in" transition */
	public static final Transition DEFAULT_IN_TRANSITION = IN[0];
	
	/** The default "out" transition */
	public static final Transition DEFAULT_OUT_TRANSITION = OUT[0];
	
	/**
	 * Returns a transition for the given id.
	 * <p>
	 * Returns the default if not found.
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
