package org.praisenter.transitions;

import org.praisenter.transitions.Transition.Type;

/**
 * Helper class for transitions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Transitions {
	// TODO i don't like the way that the transitions are modifiable from these arrays
	/** The list of "in" transitions */
	public static final Transition[] IN = new Transition[] {
		new Swap(Type.IN),
		new FadeIn(400)
	};
	
	/** The list of "out" transitions */
	public static final Transition[] OUT = new Transition[] {
		new Swap(Type.OUT),
		new FadeOut(400)
	};
	
	/**
	 * Returns a transition, from the {@link #IN} or {@link #OUT} arrays, that matches the
	 * given simple class name.
	 * @param simpleName the simple class name
	 * @return {@link Transition}
	 */
	public static final Transition getTransitionForSimpleClassName(String simpleName) {
		for (Transition transition : IN) {
			if (transition.getClass().getSimpleName().equalsIgnoreCase(simpleName)) {
				return transition;
			}
		}
		for (Transition transition : OUT) {
			if (transition.getClass().getSimpleName().equalsIgnoreCase(simpleName)) {
				return transition;
			}
		}
		return null;
	}
}
