package org.praisenter.slide.ui.present;

import org.praisenter.transitions.TransitionAnimator;

public class ClearEvent implements PresentEvent {
	protected TransitionAnimator animator;
	
	public ClearEvent(TransitionAnimator animator) {
		this.animator = animator;
	}
	
	public TransitionAnimator getAnimator() {
		return this.animator;
	}
}
