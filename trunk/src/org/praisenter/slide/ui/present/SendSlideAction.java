package org.praisenter.slide.ui.present;

import org.praisenter.slide.Slide;
import org.praisenter.transitions.TransitionAnimator;

public class SendSlideAction implements PresentAction {
	protected Slide slide;
	protected TransitionAnimator animator;
	
	public SendSlideAction(Slide slide, TransitionAnimator animator) {
		this.slide = slide;
		this.animator = animator;
	}
	
	public Slide getSlide() {
		return slide;
	}
	public TransitionAnimator getAnimator() {
		return animator;
	}
}
