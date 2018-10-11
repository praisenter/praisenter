package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideStrokeStyle extends Copyable {
	public SlideStrokeType getType();
	public SlideStrokeJoin getJoin();
	public SlideStrokeCap getCap();
	public Double[] getDashes();
	
	public ReadOnlyObjectProperty<SlideStrokeType> typeProperty();
	public ReadOnlyObjectProperty<SlideStrokeJoin> joinProperty();
	public ReadOnlyObjectProperty<SlideStrokeCap> capProperty();
	public ReadOnlyObjectProperty<Double[]> dashesProperty();
}
