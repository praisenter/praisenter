package org.praisenter.ui.slide;

import org.praisenter.data.TextStore;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.workspace.PlaceholderTransitionBehavior;

final class TransitionRequest {
	private final PlaceholderTransitionBehavior type;
	private final Slide slide;
	private final TextStore placeholderData;
	
	private TransitionRequest(PlaceholderTransitionBehavior type, Slide slide, TextStore placeholderData) {
		this.type = type;
		this.slide = slide;
		this.placeholderData = placeholderData;
	}
	
	public static final TransitionRequest transitionSlide(Slide slide) {
		return new TransitionRequest(PlaceholderTransitionBehavior.SLIDE, slide, null);
	}
	
	public static final TransitionRequest transitionPlaceholders(TextStore placeholderData) {
		return new TransitionRequest(PlaceholderTransitionBehavior.PLACEHOLDERS, null, placeholderData);
	}
	
	public static final TransitionRequest transitionContent(TextStore placeholderData) {
		return new TransitionRequest(PlaceholderTransitionBehavior.CONTENT, null, placeholderData);
	}
	
	public PlaceholderTransitionBehavior getType() {
		return type;
	}

	public Slide getSlide() {
		return slide;
	}

	public TextStore getPlaceholderData() {
		return placeholderData;
	}
}
