package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public interface ReadOnlySlideGradient extends SlidePaint, Copyable {
	public SlideGradientType getType();
	public double getStartX();
	public double getStartY();
	public double getEndX();
	public double getEndY();	
	public SlideGradientCycleType getCycleType();
	
	public ObservableList<SlideGradientStop> getStopsUnmodifiable();

	public ReadOnlyObjectProperty<SlideGradientType> typeProperty();
	public ReadOnlyDoubleProperty startXProperty();
	public ReadOnlyDoubleProperty startYProperty();
	public ReadOnlyDoubleProperty endXProperty();
	public ReadOnlyDoubleProperty endYProperty();
	public ReadOnlyObjectProperty<SlideGradientCycleType> cycleTypeProperty();
}
