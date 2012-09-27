package org.praisenter.transitions;

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
		new SwipeRight(Transition.Type.IN)
	};
	
	/** The list of "out" transitions */
	public static final Transition[] OUT = new Transition[] {
		new Swap(Transition.Type.OUT),
		new Fade(Transition.Type.OUT),
		new SwipeRight(Transition.Type.OUT)
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
}
