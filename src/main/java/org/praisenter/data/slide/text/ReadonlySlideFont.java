package org.praisenter.data.slide.text;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReadonlySlideFont extends Copyable {
	public String getFamily();
	public SlideFontPosture getPosture();
	public SlideFontWeight getWeight();
	public double getSize();
	
	public ReadOnlyStringProperty familyProperty();
	public ReadOnlyObjectProperty<SlideFontPosture> postureProperty();
	public ReadOnlyObjectProperty<SlideFontWeight> weightProperty();
	public ReadOnlyDoubleProperty sizeProperty();
}
