package org.praisenter.data.slide.animation;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideAnimation {
	public static final long INFINITE = -1;

	public AnimationFunction getAnimationFunction();
	public AnimationType getAnimationType();
	public long getDuration();
	public long getDelay();
	public int getRepeatCount();
	public boolean isAutoReverseEnabled();
	public AnimationEasingFunction getEasingFunction();
	public AnimationEasingType getEasingType();
	public AnimationDirection getDirection();
	public AnimationOrientation getOrientation();
	public AnimationOperation getOperation();
	public AnimationShapeType getShapeType();
	public int getBlindCount();
	
	public ReadOnlyObjectProperty<AnimationFunction> animationFunctionProperty();
	public ReadOnlyObjectProperty<AnimationType> animationTypeProperty();
	public ReadOnlyLongProperty durationProperty();
	public ReadOnlyLongProperty delayProperty();
	public ReadOnlyIntegerProperty repeatCountProperty();
	public ReadOnlyBooleanProperty autoReverseEnabledProperty();
	public ReadOnlyObjectProperty<AnimationEasingFunction> easingFunctionProperty();
	public ReadOnlyObjectProperty<AnimationEasingType> easingTypeProperty();
	public ReadOnlyObjectProperty<AnimationDirection> directionProperty();
	public ReadOnlyObjectProperty<AnimationOrientation> orientationProperty();
	public ReadOnlyObjectProperty<AnimationOperation> operationProperty();
	public ReadOnlyObjectProperty<AnimationShapeType> shapeTypeProperty();
	public ReadOnlyIntegerProperty blindCountProperty();
	
	public ReadOnlySlideAnimation copy();
	public long getTotalTime();
}
