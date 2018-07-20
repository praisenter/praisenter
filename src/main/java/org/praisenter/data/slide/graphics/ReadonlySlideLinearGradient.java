package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;

public interface ReadonlySlideLinearGradient extends ReadonlySlideGradient, SlidePaint, Copyable {
	public double getStartX();
	public double getStartY();
	public double getEndX();
	public double getEndY();
	
	public ReadOnlyDoubleProperty startXProperty();
	public ReadOnlyDoubleProperty startYProperty();
	public ReadOnlyDoubleProperty endXProperty();
	public ReadOnlyDoubleProperty endYProperty();
}
