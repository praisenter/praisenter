package org.praisenter.ui.slide.transition;

import org.praisenter.data.slide.effects.Direction;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class SwipeTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<Direction> direction;
	private final DoubleProperty width;
	private final DoubleProperty height;
	
	public SwipeTransition() {
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
		
		Shape clip = null;
		switch(direction) {
			case UP:
				clip = getUpClip(width, height, frac);
				break;
			case RIGHT:
				clip = getRightClip(width, height, frac);
				break;
			case DOWN:
				clip = getDownClip(width, height, frac);
				break;
			case LEFT:
				clip = getLeftClip(width, height, frac);
				break;
			case CLOCKWISE:
				clip = getClockwiseClip(width, height, frac);
				break;
			case COUNTER_CLOCKWISE:
				clip = getCounterClockwiseClip(width, height, frac);
				break;
			case WEDGE_DOWN:
				clip = getWedgeDownClip(width, height, frac);
				break;
			case WEDGE_UP:
				clip = getWedgeUpClip(width, height, frac);
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
	
	private Shape getUpClip(double w, double h, double frac) {
		double p = Math.ceil(h * (1.0 - frac));
		if (this.isInTransition()) {
			return new Rectangle(0, p, w, h);
		} else {
			return new Rectangle(0, 0, w, p);
		}
	}

	private Shape getRightClip(double w, double h, double frac) {
		double p = Math.ceil(w * frac);
		if (this.isInTransition()) {
			return new Rectangle(0, 0, p, h);
		} else {
			return new Rectangle(p, 0, w, h);
		}
	}

	private Shape getDownClip(double w, double h, double frac) {
		double p = Math.ceil(h * frac);
		if (this.isInTransition()) {
			return new Rectangle(0, 0, w, p);
		} else {
			return new Rectangle(0, p, w, h * Math.ceil(1.0 - frac));
		}
	}

	private Shape getLeftClip(double w, double h, double frac) {
		double p = Math.ceil(w * (1.0 - frac));
		if (this.isInTransition()) {
			return new Rectangle(p, 0, w, h);
		} else {
			return new Rectangle(0, 0, p, h);
		}
	}

	private Shape getClockwiseClip(double w, double h, double frac) {
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90.0, -360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.isInTransition()) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	private Shape getCounterClockwiseClip(double w, double h, double frac) {
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90.0, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.isInTransition()) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	private Shape getWedgeDownClip(double w, double h, double frac) {
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);

		if (this.isInTransition()) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	private Shape getWedgeUpClip(double w, double h, double frac) {
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, -90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.isInTransition()) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
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
