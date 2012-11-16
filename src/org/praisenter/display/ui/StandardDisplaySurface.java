package org.praisenter.display.ui;

import org.praisenter.display.Display;
import org.praisenter.slide.transitions.TransitionAnimator;

/**
 * Represents a standard display surface supporting send and clear
 * actions with transitions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class StandardDisplaySurface extends DisplaySurface {
	/** The version id */
	private static final long serialVersionUID = 3405980404921580228L;

	/**
	 * Shows the given display.
	 * @param display the display to send
	 */
	public abstract void send(Display display);
	
	/**
	 * Shows the given display using the given transition.
	 * @param display the display to send
	 * @param animator the transition; can be null
	 */
	public abstract void send(Display display, TransitionAnimator animator);

	/**
	 * Clears the display.
	 */
	public abstract void clear();
	
	/**
	 * Clears the display using the given transition.
	 * @param animator the transition; can be null
	 */
	public abstract void clear(TransitionAnimator animator);
}
