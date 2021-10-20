package org.praisenter.ui.slide.animation;

import org.praisenter.data.slide.animation.AnimationOperation;
import org.praisenter.data.slide.animation.AnimationOrientation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class SplitTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<AnimationOrientation> orientation;
	private final ObjectProperty<AnimationOperation> operation;
	private final ObjectProperty<Bounds> bounds;
	
	public SplitTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.orientation = new SimpleObjectProperty<AnimationOrientation>();
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
		
		AnimationOrientation orientation = this.orientation.get();
		if (orientation == null) return;
		
		AnimationOperation operation = this.operation.get();
		if (operation == null) return;
		
		Shape clip = null;
		switch(orientation) {
			case HORIZONTAL:
				switch(operation) {
					case COLLAPSE:
						clip = getHorizontalCollapse(frac);
						break;
					case EXPAND:
						clip = getHorizontalExpand(frac);
						break;
					default:
						break;
				}
				break;
			case VERTICAL:
				switch(operation) {
					case COLLAPSE:
						clip = getVerticalCollapse(frac);
						break;
					case EXPAND:
						clip = getVerticalExpand(frac);
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
	
	private Shape getHorizontalCollapse(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(x, y, w, h);
		Rectangle cut = new Rectangle(x, y + hh * frac, w, h * (1.0 - frac));
		
		if (this.isInTransition()) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	private Shape getHorizontalExpand(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(x, y, w, h);
		Rectangle cut = new Rectangle(x, y + hh * (1.0 - frac), w, h * frac);
		
		if (this.isInTransition()) {
			return cut;
		} else {
			return Shape.subtract(all, cut);
		}
	}
	
	private Shape getVerticalCollapse(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(x, y, w, h);
		Rectangle cut = new Rectangle(x + hw * frac, y, w * (1.0 - frac), h);
		
		if (this.isInTransition()) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	private Shape getVerticalExpand(double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(x, y, w, h);
		Rectangle cut = new Rectangle(x + hw * (1.0 - frac), y, w * frac, h);
		
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

	public AnimationOrientation getOrientation() {
		return this.orientation.get();
	}
	
	public void setOrientation(AnimationOrientation orientation) {
		this.orientation.set(orientation);
	}
	
	public ObjectProperty<AnimationOrientation> orientationProperty() {
		return this.orientation;
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
