package org.praisenter.ui.slide.transition;

import org.praisenter.data.slide.effects.Operation;
import org.praisenter.data.slide.effects.ShapeType;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class ShapedTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<ShapeType> shapeType;
	private final ObjectProperty<Operation> operation;
	private final DoubleProperty width;
	private final DoubleProperty height;
	
	public ShapedTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.shapeType = new SimpleObjectProperty<ShapeType>();
		this.operation = new SimpleObjectProperty<Operation>();
		this.width = new SimpleDoubleProperty();
		this.height = new SimpleDoubleProperty();

		this.duration.addListener((obs, ov, nv) -> {
			this.setCycleDuration(nv);
		});
	}
	
	@Override
	protected void interpolate(double frac) {
		Node node = this.node.get();
		if (node == null) return;
		
		ShapeType shapeType = this.shapeType.get();
		if (shapeType == null) return;
		
		Operation operation = this.operation.get();
		if (operation == null) return;
		
		double width = this.width.get();
		double height = this.height.get();

		Shape clip = null;		
		switch (shapeType) {
			case CIRCLE:
				clip = operation == Operation.COLLAPSE 
					? this.getCollapsingCircleClip(width, height, frac)
					: this.getExpandingCircleClip(width, height, frac);
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
	
	private Shape getCollapsingCircleClip(double w, double h, double frac) {
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh) * (1.0 - frac);
		Rectangle all = new Rectangle(0, 0, w, h);
		Circle circle = new Circle(hw, hh, r);
		
		// create the clip shape
		if (this.isInTransition()) {
			return Shape.subtract(all, circle);
		} else {
			return circle;
		}
	}
	
	private Shape getExpandingCircleClip(double w, double h, double frac) {
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh) * frac;
		Rectangle all = new Rectangle(0, 0, w, h);
		Circle circle = new Circle(hw, hh, r);
		
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
	
	public ShapeType getShapeType() {
		return this.shapeType.get();
	}
	
	public void setShapeType(ShapeType shapeType) {
		this.shapeType.set(shapeType);
	}
	
	public ObjectProperty<ShapeType> shapeTypeProperty() {
		return this.shapeType;
	}
	
	public Operation getOperation() {
		return this.operation.get();
	}
	
	public void setOperation(Operation operation) {
		this.operation.set(operation);
	}
	
	public ObjectProperty<Operation> operationProperty() {
		return this.operation;
	}
	
	public double getWidth() {
		return this.width.get();
	}
	
	public void setWidth(double width) {
		this.width.set(width);
	}
	
	public DoubleProperty widthProperty() {
		return this.width;
	}
	
	public double getHeight() {
		return this.height.get();
	}
	
	public void setHeight(double height) {
		this.height.set(height);
	}
	
	public DoubleProperty heightProperty() {
		return this.height;
	}
}
