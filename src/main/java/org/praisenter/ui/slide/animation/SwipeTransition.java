package org.praisenter.ui.slide.animation;

import org.praisenter.data.slide.animation.AnimationDirection;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class SwipeTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<AnimationDirection> direction;
	private final ObjectProperty<Bounds> bounds;
	
	public SwipeTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.direction = new SimpleObjectProperty<AnimationDirection>();
		this.bounds = new SimpleObjectProperty<Bounds>();

		this.duration.addListener((obs, ov, nv) -> {
			this.setCycleDuration(nv);
		});
	}

	@Override
	protected void interpolate(double frac) {
		Node node = this.node.get();
		if (node == null) return;
		
		AnimationDirection direction = this.direction.get();
		if (direction == null) return;
		
		Shape clip = null;
		switch(direction) {
			case UP:
				clip = getUpClip(frac);
				break;
			case RIGHT:
				clip = getRightClip(frac);
				break;
			case DOWN:
				clip = getDownClip(frac);
				break;
			case LEFT:
				clip = getLeftClip(frac);
				break;
			case CLOCKWISE:
				clip = getClockwiseClip(frac);
				break;
			case COUNTER_CLOCKWISE:
				clip = getCounterClockwiseClip(frac);
				break;
			case WEDGE_DOWN:
				clip = getWedgeDownClip(frac);
				break;
			case WEDGE_UP:
				clip = getWedgeUpClip(frac);
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
	
	private Shape getUpClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double p = Math.ceil(h * (1.0 - frac));
		if (this.isInTransition()) {
			return new Rectangle(x, y + p, w, h);
		} else {
			return new Rectangle(x, y, w, p);
		}
	}

	private Shape getRightClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double p = Math.ceil(w * frac);
		if (this.isInTransition()) {
			return new Rectangle(x, y, p, h);
		} else {
			return new Rectangle(x + p, y, w, h);
		}
	}

	private Shape getDownClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double p = Math.ceil(h * frac);
		if (this.isInTransition()) {
			return new Rectangle(x, y, w, p);
		} else {
			return new Rectangle(x, y + p, w, h * Math.ceil(1.0 - frac));
		}
	}

	private Shape getLeftClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double p = Math.ceil(w * (1.0 - frac));
		if (this.isInTransition()) {
			return new Rectangle(x + p, y, w, h);
		} else {
			return new Rectangle(x, y, p, h);
		}
	}

	private Shape getClockwiseClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(x, y, w, h);
		Arc arc = new Arc(x + hw, y + hh, r, r, 90.0, -360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.isInTransition()) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	private Shape getCounterClockwiseClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(x, y, w, h);
		Arc arc = new Arc(x + hw, y + hh, r, r, 90.0, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.isInTransition()) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	private Shape getWedgeDownClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(x, y, w, h);
		Arc arc = new Arc(x + hw, y + hh, r, r, 90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);

		if (this.isInTransition()) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

	private Shape getWedgeUpClip(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(x, y, w, h);
		Arc arc = new Arc(x + hw, y + hh, r, r, -90 - 180 * frac, 360 * frac);
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

	public AnimationDirection getDirection() {
		return this.direction.get();
	}
	
	public void setDirection(AnimationDirection direction) {
		this.direction.set(direction);
	}
	
	public ObjectProperty<AnimationDirection> directionProperty() {
		return this.direction;
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
