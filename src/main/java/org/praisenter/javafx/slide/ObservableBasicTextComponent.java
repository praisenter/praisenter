package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.slide.text.BasicTextComponent;

public final class ObservableBasicTextComponent<T extends BasicTextComponent> extends ObservableTextComponent<T> {
	public ObservableBasicTextComponent(T component, ObservableSlideContext context, SlideMode mode) {
		super(component, context, mode);
		this.build();
	}
}
