package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;

public interface ReadOnlySlideColor extends SlidePaint, Copyable {
	public double getRed();
	public double getGreen();
	public double getBlue();
	public double getAlpha();
	
	public ReadOnlyDoubleProperty redProperty();
	public ReadOnlyDoubleProperty greenProperty();
	public ReadOnlyDoubleProperty blueProperty();
	public ReadOnlyDoubleProperty alphaProperty();
}
