package org.praisenter.data.slide;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.effects.SlideShadow;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideComponent extends ReadOnlySlideRegion, Copyable, Identifiable {
	public double getX();
	public double getY();
	public SlideShadow getShadow();
	public SlideShadow getGlow();
	
	public ReadOnlyDoubleProperty xProperty();
	public ReadOnlyDoubleProperty yProperty();
	public ReadOnlyObjectProperty<SlideShadow> shadowProperty();
	public ReadOnlyObjectProperty<SlideShadow> glowProperty();
	
	public boolean isOverlapping(ReadOnlySlideComponent component);
}
