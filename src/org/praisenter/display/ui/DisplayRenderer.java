package org.praisenter.display.ui;

import org.praisenter.display.Display;
import org.praisenter.transitions.TransitionAnimator;

// TODO it would be nice to be able to combine the screens with the panels

/**
 * Standard interface for rendering displays using transitions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public interface DisplayRenderer {
	/**
	 * Shows the given display using the given transition.
	 * @param display the display to send
	 * @param animator the transition; can be null
	 */
	public void send(Display display, TransitionAnimator animator);
	
	/**
	 * Clears the panel using the given transition.
	 * @param animator the transition; can be null
	 */
	public void clear(TransitionAnimator animator);
}
