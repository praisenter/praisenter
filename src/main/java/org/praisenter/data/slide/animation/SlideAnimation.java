package org.praisenter.data.slide.animation;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SlideAnimation implements ReadOnlySlideAnimation, Copyable {
	// animation and transitions
	
	private final ObjectProperty<AnimationFunction> animationFunction;
	private final LongProperty duration;
	private final ObjectProperty<AnimationEasingFunction> easingFunction;
	private final ObjectProperty<AnimationEasingType> easingType;
	private final ObjectProperty<AnimationOrientation> orientation;
	private final ObjectProperty<AnimationOperation> operation;
	private final ObjectProperty<AnimationDirection> direction;
	private final ObjectProperty<AnimationShapeType> shapeType;
	private final IntegerProperty blindCount;
	
	// animation only
	
	private final ObjectProperty<AnimationType> animationType;
	private final LongProperty delay;
	private final IntegerProperty repeatCount;
	private final BooleanProperty autoReverseEnabled;
	
	public SlideAnimation() {
		this.animationFunction = new SimpleObjectProperty<AnimationFunction>(AnimationFunction.SWAP);
		this.duration = new SimpleLongProperty(300);
		this.easingFunction = new SimpleObjectProperty<>(AnimationEasingFunction.LINEAR);
		this.easingType = new SimpleObjectProperty<AnimationEasingType>(AnimationEasingType.IN);
		this.orientation = new SimpleObjectProperty<>(AnimationOrientation.VERTICAL);
		this.operation = new SimpleObjectProperty<>(AnimationOperation.EXPAND);
		this.direction = new SimpleObjectProperty<>(AnimationDirection.UP);
		this.shapeType = new SimpleObjectProperty<>(AnimationShapeType.CIRCLE);
		this.blindCount = new SimpleIntegerProperty(12);
		
		this.animationType = new SimpleObjectProperty<AnimationType>(AnimationType.IN);
		this.delay = new SimpleLongProperty(0);
		this.repeatCount = new SimpleIntegerProperty(1);
		this.autoReverseEnabled = new SimpleBooleanProperty(false);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName())
		  .append("[")
		  .append(this.animationFunction.get()).append(", ")
		  .append(this.duration.get()).append(", ")
		  .append(this.easingFunction.get()).append(", ")
		  .append(this.easingType.get())
		  .append("]");
		return sb.toString();
	}
	
	public SlideAnimation copy() {
		SlideAnimation tx = new SlideAnimation();
		this.copyTo(tx);
		return tx;
	}
	
	protected void copyTo(SlideAnimation other) {
		other.animationFunction.set(this.animationFunction.get());
		other.duration.set(this.duration.get());
		other.easingFunction.set(this.easingFunction.get());
		other.easingType.set(this.easingType.get());
		other.orientation.set(this.orientation.get());
		other.direction.set(this.direction.get());
		other.operation.set(this.operation.get());
		other.shapeType.set(this.shapeType.get());
		other.blindCount.set(this.blindCount.get());
		
		other.animationType.set(this.animationType.get());
		other.delay.set(this.delay.get());
		other.repeatCount.set(this.repeatCount.get());
		other.autoReverseEnabled.set(this.autoReverseEnabled.get());
	}

	@Override
	public long getTotalTime() {
		return Math.max(0, this.duration.get());
	}

	@Override
	@JsonProperty
	public AnimationFunction getAnimationFunction() {
		return this.animationFunction.get();
	}
	
	@JsonProperty
	public void setAnimationFunction(AnimationFunction animationFunction) {
		this.animationFunction.set(animationFunction);
	}
	
	@Override
	public ObjectProperty<AnimationFunction> animationFunctionProperty() {
		return this.animationFunction;
	}
	
	@Override
	@JsonProperty
	public long getDuration() {
		return this.duration.get();
	}
	
	@JsonProperty
	public void setDuration(long duration) {
		this.duration.set(duration);
	}
	
	@Override
	public LongProperty durationProperty() {
		return this.duration;
	}
	
	@Override
	@JsonProperty
	public AnimationEasingFunction getEasingFunction() {
		return this.easingFunction.get();
	}
	
	@JsonProperty
	public void setEasingFunction(AnimationEasingFunction easingFunction) {
		this.easingFunction.set(easingFunction);
	}
	
	@Override
	public ObjectProperty<AnimationEasingFunction> easingFunctionProperty() {
		return this.easingFunction;
	}
	
	@Override
	@JsonProperty
	public AnimationEasingType getEasingType() {
		return this.easingType.get();
	}
	
	@JsonProperty
	public void setEasingType(AnimationEasingType easingType) {
		this.easingType.set(easingType);
	}
	
	@Override
	public ObjectProperty<AnimationEasingType> easingTypeProperty() {
		return this.easingType;
	}

	@Override
	@JsonProperty
	public AnimationDirection getDirection() {
		return this.direction.get();
	}
	
	@JsonProperty
	public void setDirection(AnimationDirection direction) {
		this.direction.set(direction);
	}
	
	@Override
	public ObjectProperty<AnimationDirection> directionProperty() {
		return this.direction;
	}

	@Override
	@JsonProperty
	public AnimationOrientation getOrientation() {
		return this.orientation.get();
	}
	
	@JsonProperty
	public void setOrientation(AnimationOrientation orientation) {
		this.orientation.set(orientation);
	}
	
	@Override
	public ObjectProperty<AnimationOrientation> orientationProperty() {
		return this.orientation;
	}

	@Override
	@JsonProperty
	public AnimationOperation getOperation() {
		return this.operation.get();
	}
	
	@JsonProperty
	public void setOperation(AnimationOperation operation) {
		this.operation.set(operation);
	}
	
	@Override
	public ObjectProperty<AnimationOperation> operationProperty() {
		return this.operation;
	}
	
	@Override
	@JsonProperty
	public AnimationShapeType getShapeType() {
		return this.shapeType.get();
	}
	
	@JsonProperty
	public void setShapeType(AnimationShapeType shapeType) {
		this.shapeType.set(shapeType);
	}
	
	@Override
	public ObjectProperty<AnimationShapeType> shapeTypeProperty() {
		return this.shapeType;
	}

	@Override
	@JsonProperty
	public int getBlindCount() {
		return this.blindCount.get();
	}
	
	@JsonProperty
	public void setBlindCount(int count) {
		this.blindCount.set(count);
	}
	
	@Override
	public IntegerProperty blindCountProperty() {
		return this.blindCount;
	}

	// animation only
	
	@Override
	@JsonProperty
	public AnimationType getAnimationType() {
		return this.animationType.get();
	}

	@JsonProperty
	public void setAnimationType(AnimationType type) {
		this.animationType.set(type);
	}
	
	@Override
	public ObjectProperty<AnimationType> animationTypeProperty() {
		return this.animationType;
	}
	
	@Override
	@JsonProperty
	public long getDelay() {
		return this.delay.get();
	}

	@JsonProperty
	public void setDelay(long delay) {
		this.delay.set(delay);
	}
	
	@Override
	public LongProperty delayProperty() {
		return this.delay;
	}
	
	@Override
	@JsonProperty
	public int getRepeatCount() {
		return this.repeatCount.get();
	}

	@JsonProperty
	public void setRepeatCount(int repeatCount) {
		this.repeatCount.set(repeatCount);
	}
	
	@Override
	public IntegerProperty repeatCountProperty() {
		return this.repeatCount;
	}
	
	@Override
	@JsonProperty
	public boolean isAutoReverseEnabled() {
		return this.autoReverseEnabled.get();
	}

	@JsonProperty
	public void setAutoReverseEnabled(boolean flag) {
		this.autoReverseEnabled.set(flag);
	}
	
	@Override
	public BooleanProperty autoReverseEnabledProperty() {
		return this.autoReverseEnabled;
	}
}
