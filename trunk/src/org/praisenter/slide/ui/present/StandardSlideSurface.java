package org.praisenter.slide.ui.present;

import org.praisenter.slide.Slide;
import org.praisenter.transitions.TransitionAnimator;

/**
 * Represents a standard display surface supporting send and clear
 * actions with transitions.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class StandardSlideSurface extends SlideSurface {
	/** The version id */
	private static final long serialVersionUID = 3260549632836799867L;

	/**
	 * Shows the given slide.
	 * @param slide the slide to display
	 */
	public abstract void send(Slide slide);
	
	/**
	 * Shows the given slide using the given transition.
	 * @param slide the slide to send
	 * @param animator the transition; can be null
	 */
	public abstract void send(Slide slide, TransitionAnimator animator);

	/**
	 * Clears the slide.
	 */
	public abstract void clear();
	
	/**
	 * Clears the slide using the given transition.
	 * @param animator the transition; can be null
	 */
	public abstract void clear(TransitionAnimator animator);
}
