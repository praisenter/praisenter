package org.praisenter.data.slide;

import java.util.List;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.animation.SlideAnimation;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.Rectangle;

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
	
	public List<SlideAnimation> getAnimationsUnmodifiable();
	
	public boolean isOverlapping(ReadOnlySlideComponent component);
	
//	/**
//	 * Returns the bounds of the component taking any shadow, glow,
//	 * or border spill over from the defined bounds.
//	 * @return {@link Rectangle}
//	 */
//	public Rectangle getLocalOffsetBounds();
}
