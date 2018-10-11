package org.praisenter.data.slide.ease;

import org.praisenter.data.Copyable;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlyEasing extends Copyable {
	public EasingType getEasingType();
	
	public ReadOnlyObjectProperty<EasingType> easingTypeProperty();
}
