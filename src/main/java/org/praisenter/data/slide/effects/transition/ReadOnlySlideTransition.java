package org.praisenter.data.slide.effects.transition;

import org.praisenter.data.Copyable;
import org.praisenter.data.slide.effects.Direction;
import org.praisenter.data.slide.effects.Operation;
import org.praisenter.data.slide.effects.Orientation;
import org.praisenter.data.slide.effects.ShapeType;
import org.praisenter.data.slide.effects.ease.EasingFunction;
import org.praisenter.data.slide.effects.ease.EasingType;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public interface ReadOnlySlideTransition extends Copyable {
	public TransitionType getTransitionType();
	public long getDuration();
	public EasingFunction getEasingFunction();
	public EasingType getEasingType();
	public Direction getDirection();
	public Orientation getOrientation();
	public Operation getOperation();
	public ShapeType getShapeType();
	public int getBlindCount();
	
	public ReadOnlyObjectProperty<TransitionType> transitionTypeProperty();
	public ReadOnlyLongProperty durationProperty();
	public ReadOnlyObjectProperty<EasingFunction> easingFunctionProperty();
	public ReadOnlyObjectProperty<EasingType> easingTypeProperty();
	public ReadOnlyObjectProperty<Direction> directionProperty();
	public ReadOnlyObjectProperty<Orientation> orientationProperty();
	public ReadOnlyObjectProperty<Operation> operationProperty();
	public ReadOnlyObjectProperty<ShapeType> shapeTypeProperty();
	public ReadOnlyIntegerProperty blindCountProperty();
	
	public long getTotalTime();
}
