package org.praisenter.ui.slide;

import org.praisenter.data.TextStore;
import org.praisenter.data.slide.Slide;

public class TransitionRequest {
	private final TransitionRequestType type;
	private final Slide slide;
	private final TextStore placeholderData;
	
	public TransitionRequest(Slide slide) {
		this.type = TransitionRequestType.SLIDE;
		this.slide = slide;
		this.placeholderData = null;
	}
	
	public TransitionRequest(TextStore placeholderData) {
		this.type = TransitionRequestType.PLACEHOLDERS;
		this.slide = null;
		this.placeholderData = placeholderData;
	}

	public TransitionRequestType getType() {
		return type;
	}

	public Slide getSlide() {
		return slide;
	}

	public TextStore getPlaceholderData() {
		return placeholderData;
	}
}
