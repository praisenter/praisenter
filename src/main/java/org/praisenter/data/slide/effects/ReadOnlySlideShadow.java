package org.praisenter.data.slide.effects;

import org.praisenter.data.Copyable;
import org.praisenter.slide.graphics.ShadowType;
import org.praisenter.slide.graphics.SlideColor;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideShadow extends Copyable {
	public ShadowType getType();
	public SlideColor getColor();
	public double getOffsetX();
	public double getOffsetY();
	public double getRadius();
	public double getSpread();
	
	public ReadOnlyObjectProperty<ShadowType> typeProperty();
	public ReadOnlyObjectProperty<SlideColor> colorProperty();
	public ReadOnlyDoubleProperty offsetXProperty();
	public ReadOnlyDoubleProperty offsetYProperty();
	public ReadOnlyDoubleProperty radiusProperty();
	public ReadOnlyDoubleProperty spreadProperty();
}
