package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideStroke extends Copyable {
	public SlideStrokeStyle getStyle();
	public SlidePaint getPaint();
	public double getWidth();
	public double getRadius();
	
	public ReadOnlyObjectProperty<SlideStrokeStyle> styleProperty();
	public ReadOnlyObjectProperty<SlidePaint> paintProperty();
	public ReadOnlyDoubleProperty widthProperty();
	public ReadOnlyDoubleProperty radiusProperty();
}
