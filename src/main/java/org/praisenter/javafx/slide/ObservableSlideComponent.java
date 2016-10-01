package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.SlideComponent;

public abstract class ObservableSlideComponent<T extends SlideComponent> extends ObservableSlideRegion<T> {
	public ObservableSlideComponent(T component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
	}
}
