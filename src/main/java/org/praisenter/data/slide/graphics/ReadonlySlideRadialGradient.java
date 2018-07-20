package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;

public interface ReadonlySlideRadialGradient extends ReadonlySlideGradient, SlidePaint, Copyable {
	public double getCenterX();
	public double getCenterY();
	public double getRadius();
	
	public ReadOnlyDoubleProperty centerXProperty();
	public ReadOnlyDoubleProperty centerYProperty();
	public ReadOnlyDoubleProperty radiusProperty();
}
