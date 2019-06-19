package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public interface ReadOnlySlideStrokeStyle extends Copyable {
	public SlideStrokeType getType();
	public SlideStrokeJoin getJoin();
	public SlideStrokeCap getCap();
	public ObservableList<Double> getDashes();
	
	public ReadOnlyObjectProperty<SlideStrokeType> typeProperty();
	public ReadOnlyObjectProperty<SlideStrokeJoin> joinProperty();
	public ReadOnlyObjectProperty<SlideStrokeCap> capProperty();
}
