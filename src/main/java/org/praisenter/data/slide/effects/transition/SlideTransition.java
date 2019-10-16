package org.praisenter.data.slide.effects.transition;

import org.praisenter.data.Copyable;
import org.praisenter.data.slide.effects.Direction;
import org.praisenter.data.slide.effects.Operation;
import org.praisenter.data.slide.effects.Orientation;
import org.praisenter.data.slide.effects.ShapeType;
import org.praisenter.data.slide.effects.ease.EasingFunction;
import org.praisenter.data.slide.effects.ease.EasingType;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SlideTransition implements ReadOnlySlideTransition, Copyable {
	public static final TransitionType DEFAULT_TRANSITION_TYPE = TransitionType.SWAP;
	public static final long DEFAULT_DURATION = 300;
	public static final Orientation DEFAULT_ORIENTATION = Orientation.VERTICAL;
	public static final Operation DEFAULT_OPERATION = Operation.EXPAND;
	public static final Direction DEFAULT_DIRECTION = Direction.UP;
	public static final ShapeType DEFAULT_SHAPE_TYPE = ShapeType.CIRCLE;
	public static final int DEFAULT_BLIND_COUNT = 12;
	
	private final ObjectProperty<TransitionType> transitionType;
	private final LongProperty duration;
	private final ObjectProperty<EasingFunction> easingFunction;
	private final ObjectProperty<EasingType> easingType;
	private final ObjectProperty<Orientation> orientation;
	private final ObjectProperty<Operation> operation;
	private final ObjectProperty<Direction> direction;
	private final ObjectProperty<ShapeType> shapeType;
	private final IntegerProperty blindCount;
	
	public SlideTransition() {
		this.transitionType = new SimpleObjectProperty<TransitionType>(DEFAULT_TRANSITION_TYPE);
		this.duration = new SimpleLongProperty(DEFAULT_DURATION);
		this.easingFunction = new SimpleObjectProperty<>(EasingFunction.LINEAR);
		this.easingType = new SimpleObjectProperty<EasingType>(EasingType.IN);
		this.orientation = new SimpleObjectProperty<>(DEFAULT_ORIENTATION);
		this.operation = new SimpleObjectProperty<>(DEFAULT_OPERATION);
		this.direction = new SimpleObjectProperty<>(DEFAULT_DIRECTION);
		this.shapeType = new SimpleObjectProperty<>(DEFAULT_SHAPE_TYPE);
		this.blindCount = new SimpleIntegerProperty(DEFAULT_BLIND_COUNT);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName())
		  .append("[")
		  .append(this.transitionType.get()).append(", ")
		  .append(this.duration.get()).append(", ")
		  .append(this.easingFunction.get()).append(", ")
		  .append(this.easingType.get())
		  .append("]");
		return sb.toString();
	}
	
	public SlideTransition copy() {
		SlideTransition tx = new SlideTransition();
		this.copyTo(tx);
		return tx;
	}
	
	protected void copyTo(SlideTransition other) {
		other.transitionType.set(this.transitionType.get());
		other.duration.set(this.duration.get());
		other.easingFunction.set(this.easingFunction.get());
		other.easingType.set(this.easingType.get());
		other.orientation.set(this.orientation.get());
		other.direction.set(this.direction.get());
		other.operation.set(this.operation.get());
		other.shapeType.set(this.shapeType.get());
		other.blindCount.set(this.blindCount.get());
	}

	@Override
	public long getTotalTime() {
		return Math.max(0, this.duration.get());
	}

	@Override
	@JsonProperty
	public TransitionType getTransitionType() {
		return this.transitionType.get();
	}
	
	@JsonProperty
	public void setTransitionType(TransitionType type) {
		this.transitionType.set(type);
	}
	
	@Override
	public ObjectProperty<TransitionType> transitionTypeProperty() {
		return this.transitionType;
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
	public EasingFunction getEasingFunction() {
		return this.easingFunction.get();
	}
	
	@JsonProperty
	public void setEasingFunction(EasingFunction easingFunction) {
		this.easingFunction.set(easingFunction);
	}
	
	@Override
	public ObjectProperty<EasingFunction> easingFunctionProperty() {
		return this.easingFunction;
	}
	
	@Override
	@JsonProperty
	public EasingType getEasingType() {
		return this.easingType.get();
	}
	
	@JsonProperty
	public void setEasingType(EasingType easingType) {
		this.easingType.set(easingType);
	}
	
	@Override
	public ObjectProperty<EasingType> easingTypeProperty() {
		return this.easingType;
	}

	@Override
	@JsonProperty
	public Direction getDirection() {
		return this.direction.get();
	}
	
	@JsonProperty
	public void setDirection(Direction direction) {
		this.direction.set(direction);
	}
	
	@Override
	public ObjectProperty<Direction> directionProperty() {
		return this.direction;
	}

	@Override
	@JsonProperty
	public Orientation getOrientation() {
		return this.orientation.get();
	}
	
	@JsonProperty
	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}
	
	@Override
	public ObjectProperty<Orientation> orientationProperty() {
		return this.orientation;
	}

	@Override
	@JsonProperty
	public Operation getOperation() {
		return this.operation.get();
	}
	
	@JsonProperty
	public void setOperation(Operation operation) {
		this.operation.set(operation);
	}
	
	@Override
	public ObjectProperty<Operation> operationProperty() {
		return this.operation;
	}
	
	@Override
	@JsonProperty
	public ShapeType getShapeType() {
		return this.shapeType.get();
	}
	
	@JsonProperty
	public void setShapeType(ShapeType shapeType) {
		this.shapeType.set(shapeType);
	}
	
	@Override
	public ObjectProperty<ShapeType> shapeTypeProperty() {
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
}
