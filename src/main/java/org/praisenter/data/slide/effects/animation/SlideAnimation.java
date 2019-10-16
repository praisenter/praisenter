package org.praisenter.data.slide.effects.animation;

import org.praisenter.data.slide.effects.ease.EasingFunction;
import org.praisenter.data.slide.effects.ease.EasingType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface SlideAnimation {
	public static final long INFINITE = -1;
	// TODO slide animations
	
	public SlideAnimationType getAnimationType();
	public long getDuration();
	public long getDelay();
	public int getRepeatCount();
	public boolean isAutoReverseEnabled();
	public EasingFunction getEasingFunction();
	public EasingType getEasingType();
	
	public ReadOnlyObjectProperty<SlideAnimationType> animationTypeProperty();
	public LongProperty durationProperty();
	public LongProperty delayProperty();
	public IntegerProperty repeatCountProperty();
	public BooleanProperty autoReverseEnabledProperty();
	public ObjectProperty<EasingFunction> easingFunctionProperty();
	public ObjectProperty<EasingType> easingTypeProperty();
	
	public SlideAnimation copy();
	public long getTotalTime();
}
