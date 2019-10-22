package org.praisenter.ui.slide.animation;

import org.praisenter.data.slide.animation.AnimationOperation;
import org.praisenter.data.slide.animation.AnimationShapeType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class ShapedTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<AnimationShapeType> shapeType;
	private final ObjectProperty<AnimationOperation> operation;
	private final ObjectProperty<Bounds> bounds;
	
	public ShapedTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.shapeType = new SimpleObjectProperty<AnimationShapeType>();
		this.operation = new SimpleObjectProperty<AnimationOperation>();
		this.bounds = new SimpleObjectProperty<Bounds>();

		this.duration.addListener((obs, ov, nv) -> {
			this.setCycleDuration(nv);
		});
	}
	
	@Override
	protected void interpolate(double frac) {
		Node node = this.node.get();
		if (node == null) return;
		
		AnimationShapeType shapeType = this.shapeType.get();
		if (shapeType == null) return;
		
		AnimationOperation operation = this.operation.get();
		if (operation == null) return;
		
		Shape clip = null;		
		switch (shapeType) {
			case CIRCLE:
				clip = operation == AnimationOperation.COLLAPSE 
					? this.getCollapsingCircleClip(frac)
					: this.getExpandingCircleClip(frac);
				break;
			default:
				break;
		}
		
		node.setClip(clip);
	}

	@Override
	public void stop() {
		super.stop();
		Node node = this.node.get();
		if (node == null) return;
		node.setClip(null);
	}
	
	private Shape getCollapsingCircleClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh) * (1.0 - frac);
		Rectangle all = new Rectangle(x, y, w, h);
		Circle circle = new Circle(x + hw, y + hh, r);
		
		// create the clip shape
		if (this.isInTransition()) {
			return Shape.subtract(all, circle);
		} else {
			return circle;
		}
	}
	
	private Shape getExpandingCircleClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh) * frac;
		Rectangle all = new Rectangle(x, y, w, h);
		Circle circle = new Circle(x + hw, y + hh, r);
		
		// create the clip shape
		if (this.isInTransition()) {
			return circle;
		} else {
			return Shape.subtract(all, circle);
		}
	}

	public Duration getDuration() {
		return this.duration.get();
	}
	
	public void setDuration(Duration duration) {
		this.duration.set(duration);
	}
	
	public ObjectProperty<Duration> durationProperty() {
		return this.duration;
	}
	
	public AnimationShapeType getShapeType() {
		return this.shapeType.get();
	}
	
	public void setShapeType(AnimationShapeType shapeType) {
		this.shapeType.set(shapeType);
	}
	
	public ObjectProperty<AnimationShapeType> shapeTypeProperty() {
		return this.shapeType;
	}
	
	public AnimationOperation getOperation() {
		return this.operation.get();
	}
	
	public void setOperation(AnimationOperation operation) {
		this.operation.set(operation);
	}
	
	public ObjectProperty<AnimationOperation> operationProperty() {
		return this.operation;
	}

	public void setBounds(Bounds bounds) {
		this.bounds.set(bounds);
	}
	
	public Bounds getBounds() {
		return this.bounds.get();
	}
	
	public ObjectProperty<Bounds> boundsProperty() {
		return this.bounds;
	}
}
