package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public interface ReadonlySlideGradient extends SlidePaint, Copyable {
	public SlideGradientCycleType getCycleType();
	
	public ObservableList<SlideGradientStop> getStopsUnmodifiable();
	
	public ReadOnlyObjectProperty<SlideGradientCycleType> cycleTypeProperty();
}
