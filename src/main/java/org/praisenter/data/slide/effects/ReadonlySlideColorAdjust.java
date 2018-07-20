package org.praisenter.data.slide.effects;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;

public interface ReadonlySlideColorAdjust extends Copyable {
	public double getHue();
	public double getSaturation();
	public double getBrightness();
	public double getContrast();
	
	public ReadOnlyDoubleProperty hueProperty();
	public ReadOnlyDoubleProperty saturationProperty();
	public ReadOnlyDoubleProperty brightnessProperty();
	public ReadOnlyDoubleProperty contrastProperty();
}
