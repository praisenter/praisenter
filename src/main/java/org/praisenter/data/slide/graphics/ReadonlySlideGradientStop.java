package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

public interface ReadonlySlideGradientStop extends Copyable {
	 public double getOffset();
	 public SlideColor getColor();
	 
	 public ReadOnlyDoubleProperty offsetProperty();
	 public ObjectProperty<SlideColor> colorProperty();
}
