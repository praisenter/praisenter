package org.praisenter.ui.slide.transition;

import org.praisenter.data.slide.effects.Operation;
import org.praisenter.data.slide.effects.Orientation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class SplitTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<Orientation> orientation;
	private final ObjectProperty<Operation> operation;
	private final DoubleProperty width;
	private final DoubleProperty height;
	
	public SplitTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.orientation = new SimpleObjectProperty<Orientation>();
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
		
		Orientation orientation = this.orientation.get();
		if (orientation == null) return;
		
		Operation operation = this.operation.get();
		if (operation == null) return;
		
		double width = this.width.get();
		double height = this.height.get();
		
		Shape clip = null;
		switch(orientation) {
			case HORIZONTAL:
				switch(operation) {
					case COLLAPSE:
						clip = getHorizontalCollapse(width, height, frac);
						break;
					case EXPAND:
						clip = getHorizontalExpand(width, height, frac);
						break;
					default:
						break;
				}
				break;
			case VERTICAL:
				switch(operation) {
					case COLLAPSE:
						clip = getVerticalCollapse(width, height, frac);
						break;
					case EXPAND:
						clip = getVerticalExpand(width, height, frac);
						break;
					default:
						break;
				}
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
	
	private Shape getHorizontalCollapse(double w, double h, double frac) {
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(0, hh * frac, w, h * (1.0 - frac));
		
		if (this.isInTransition()) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	private Shape getHorizontalExpand(double w, double h, double frac) {
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(0, hh * (1.0 - frac), w, h * frac);
		
		if (this.isInTransition()) {
			return cut;
		} else {
			return Shape.subtract(all, cut);
		}
	}
	
	private Shape getVerticalCollapse(double w, double h, double frac) {
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(hw * frac, 0, w * (1.0 - frac), h);
		
		if (this.isInTransition()) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	private Shape getVerticalExpand(double w, double h, double frac) {
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(hw * (1.0 - frac), 0, w * frac, h);
		
		if (this.isInTransition()) {
			return cut;
		} else {
			return Shape.subtract(all, cut);
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

	public Orientation getOrientation() {
		return this.orientation.get();
	}
	
	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}
	
	public ObjectProperty<Orientation> orientationProperty() {
		return this.orientation;
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
