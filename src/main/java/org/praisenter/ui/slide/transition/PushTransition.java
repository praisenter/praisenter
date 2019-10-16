package org.praisenter.ui.slide.transition;

import org.praisenter.data.slide.effects.Direction;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

public final class PushTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<Direction> direction;
	private final DoubleProperty width;
	private final DoubleProperty height;
	
	public PushTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.direction = new SimpleObjectProperty<Direction>();
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
		
		Direction direction = this.direction.get();
		if (direction == null) return;
		
		double width = this.width.get();
		double height = this.height.get();
		
		switch (direction) {
			case UP:
				node.setTranslateY(getUpPosition(height, frac));
				break;
			case DOWN:
				node.setTranslateY(getDownPosition(height, frac));
				break;
			case RIGHT:
				node.setTranslateX(getRightPosition(width, frac));
				break;
			case LEFT:
				node.setTranslateX(getLeftPosition(width, frac));
				break;
			default:
				break;
		}
	}
	
	@Override
	public void stop() {
		super.stop();
		Node node = this.node.get();
		if (node == null) return;
		node.setTranslateX(0);
		node.setTranslateY(0);
	}
	
	private double getUpPosition(double height, double frac) {
		if (this.isInTransition()) {
			return height * (1.0 - frac);
		} else {
			return -height * frac;
		}
	}
	
	private double getDownPosition(double height, double frac) {
		if (this.isInTransition()) {
			return -height * (1.0 - frac);
		} else {
			return height * frac;
		}
	}

	private double getRightPosition(double width, double frac) {
		if (this.isInTransition()) {
			return -width * (1.0 - frac);
		} else {
			return width * frac;
		}
	}
	
	private double getLeftPosition(double width, double frac) {
		if (this.isInTransition()) {
			return width * (1.0 - frac);
		} else {
			return -width * frac;
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
	
	public Direction getDirection() {
		return this.direction.get();
	}
	
	public void setDirection(Direction direction) {
		this.direction.set(direction);
	}
	
	public ObjectProperty<Direction> directionProperty() {
		return this.direction;
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
