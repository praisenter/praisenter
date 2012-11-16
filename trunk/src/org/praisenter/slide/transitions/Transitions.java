package org.praisenter.slide.transitions;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;

/**
 * Helper class for transitions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
// TODO additional transitions
public class Transitions {
	/** The list of "in" transitions */
	public static final Transition[] IN = new Transition[] {
		new Swap(Transition.Type.IN),
		new Fade(Transition.Type.IN),
		new SwipeRight(Transition.Type.IN),
		new SwipeLeft(Transition.Type.IN),
		new SwipeUp(Transition.Type.IN),
		new SwipeDown(Transition.Type.IN),
		new VerticalSplitExpand(Transition.Type.IN),
		new VerticalSplitCollapse(Transition.Type.IN),
		new HorizontalSplitExpand(Transition.Type.IN),
		new HorizontalSplitCollapse(Transition.Type.IN),
		new CircularExpand(Transition.Type.IN),
		new CircularCollapse(Transition.Type.IN),
		new PushRight(Transition.Type.IN),
		new PushLeft(Transition.Type.IN),
		new PushUp(Transition.Type.IN),
		new PushDown(Transition.Type.IN)
	};
	
	/** The list of "out" transitions */
	public static final Transition[] OUT = new Transition[] {
		new Swap(Transition.Type.OUT),
		new Fade(Transition.Type.OUT),
		new SwipeRight(Transition.Type.OUT),
		new SwipeLeft(Transition.Type.OUT),
		new SwipeUp(Transition.Type.OUT),
		new SwipeDown(Transition.Type.OUT),
		new VerticalSplitExpand(Transition.Type.OUT),
		new VerticalSplitCollapse(Transition.Type.OUT),
		new HorizontalSplitExpand(Transition.Type.OUT),
		new HorizontalSplitCollapse(Transition.Type.OUT),
		new CircularExpand(Transition.Type.OUT),
		new CircularCollapse(Transition.Type.OUT),
		new PushRight(Transition.Type.OUT),
		new PushLeft(Transition.Type.OUT),
		new PushUp(Transition.Type.OUT),
		new PushDown(Transition.Type.OUT)
	};
	
	/** The default "in" transition */
	public static final Transition DEFAULT_IN_TRANSITION = IN[0];
	
	/** The default "out" transition */
	public static final Transition DEFAULT_OUT_TRANSITION = OUT[0];
	
	/**
	 * Returns a transition for the given id.
	 * <p>
	 * Returns null if the transition id is not found.
	 * @param id the transition id
	 * @param type the transition type
	 * @return {@link Transition}
	 */
	public static final Transition getTransitionForId(int id, Transition.Type type) {
		Transition[] transitions = null;
		if (type == Transition.Type.IN) {
			transitions = IN;
		} else {
			transitions = OUT;
		}
		for (Transition transition : transitions) {
			if (transition.getTransitionId() == id) {
				return transition;
			}
		}
		return null;
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
