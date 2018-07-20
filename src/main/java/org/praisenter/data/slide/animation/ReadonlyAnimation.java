package org.praisenter.data.slide.animation;

import org.praisenter.data.Copyable;
import org.praisenter.slide.easing.Easing;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadonlyAnimation extends Copyable {
	public AnimationType getAnimationType();
	public long getDuration();
	public long getDelay();
	public int getRepeatCount();
	public boolean isAutoReverseEnabled();
	public Easing getEasing();
	
	public ReadOnlyObjectProperty<AnimationType> animationTypeProperty();
	public ReadOnlyLongProperty durationProperty();
	public ReadOnlyLongProperty delayProperty();
	public ReadOnlyIntegerProperty repeatCountProperty();
	public ReadOnlyBooleanProperty autoReverseEnabledProperty();
	public ReadOnlyObjectProperty<Easing> easingProperty();
}
