package org.praisenter.data.slide;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.effects.SlideShadow;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadonlySlideComponent extends ReadonlySlideRegion, Copyable, Identifiable {
	public SlideShadow getShadow();
	public SlideShadow getGlow();
	
	public ReadOnlyObjectProperty<SlideShadow> shadowProperty();
	public ReadOnlyObjectProperty<SlideShadow> glowProperty();
}
