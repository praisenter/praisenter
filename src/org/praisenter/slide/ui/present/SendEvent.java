package org.praisenter.slide.ui.present;

import org.praisenter.slide.Slide;
import org.praisenter.transitions.TransitionAnimator;

public class SendEvent implements PresentEvent {
	protected Slide slide;
	protected TransitionAnimator animator;
	
	public SendEvent(Slide slide, TransitionAnimator animator) {
		// always use a copy of the slide since it could be reused by 
		// the rest of the application, this shouldn't be a problem anyway
		// since the copy is really fast (mostly immutable objects)
		this.slide = slide.copy();
		this.animator = animator;
	}
	
	public Slide getSlide() {
		return this.slide;
	}
	
	public TransitionAnimator getAnimator() {
		return this.animator;
	}
}
