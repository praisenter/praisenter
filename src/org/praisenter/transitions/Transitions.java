package org.praisenter.transitions;

import java.util.ArrayList;
import java.util.List;

import org.praisenter.transitions.Transition.Type;

/**
 * Helper class for transitions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Transitions {
	/** The list of all transitions */
	public static final Transition[] ALL = new Transition[] {
		// the defaults are the first two
		new SwapIn(),
		new SwapOut(),
		new FadeIn(),
		new FadeOut()
	};
	
	/** The list of "in" transitions */
	public static final Transition[] IN = getTransitions(Type.IN);
	
	/** The list of "out" transitions */
	public static final Transition[] OUT = getTransitions(Type.OUT);
	
	/** The default "in" transition */
	public static final Transition DEFAULT_IN_TRANSITION = ALL[0];
	
	/** The default "out" transition */
	public static final Transition DEFAULT_OUT_TRANSITION = ALL[1];
	
	/**
	 * Returns an array of {@link Transition}s of the given type.
	 * @param type the transition type
	 * @return {@link Transition}[]
	 */
	private static final Transition[] getTransitions(Type type) {
		List<Transition> transitions = new ArrayList<Transition>();
		for (Transition transition : ALL) {
			if (transition.type == type) {
				transitions.add(transition);
			}
		}
		return transitions.toArray(new Transition[0]);
	}
	
	/**
	 * Returns a transition for the given id.
	 * <p>
	 * Returns null if the transition id is not found.
	 * @param id the transition id
	 * @return {@link Transition}
	 */
	public static final Transition getTransitionForId(int id) {
		for (Transition transition : ALL) {
			if (transition.getTransitionId() == id) {
				return transition;
			}
		}
		return null;
	}
}
