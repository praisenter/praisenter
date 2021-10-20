package org.praisenter.ui.slide.animation;

import org.praisenter.data.slide.animation.AnimationOrientation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class BlindsTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<AnimationOrientation> orientation;
	private final IntegerProperty blindCount;
	private final ObjectProperty<Bounds> bounds;

	public BlindsTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.orientation = new SimpleObjectProperty<AnimationOrientation>();
		this.blindCount = new SimpleIntegerProperty();
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
		
		int blindCount = this.blindCount.get();
		
		Shape clip = null;
		switch(orientation) {
			case HORIZONTAL:
				clip = getHorizontalBlinds(blindCount, frac);
				break;
			case VERTICAL:
				clip = getVerticalBlinds(blindCount, frac);
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

	private Shape getHorizontalBlinds(int blinds, double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		Shape clip = null;
		if (this.isInTransition()) {
			clip = new Rectangle(x, y, w, h);
		} else {
			clip = new Rectangle();
		}
		
		double dy = y;
		// compute the blind width
		double bh = h / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(x, dy + bh * frac, w, bh * (1.0 - frac));
			if (this.isInTransition()) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			dy += bh;
		}
		
		return clip;
	}
	
	private Shape getVerticalBlinds(int blinds, double frac) {
		Bounds bounds = this.bounds.get();
		double x = bounds.getMinX();
		double y = bounds.getMinY();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		Shape clip = null;
		if (this.isInTransition()) {
			clip = new Rectangle(x, y, w, h);
		} else {
			clip = new Rectangle();
		}
		
		double dx = x;
		// compute the blind width
		double bw = w / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(dx + bw * frac, y, bw * (1.0 - frac), h);
			if (this.isInTransition()) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			dx += bw;
		}
		
		return clip;
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
	
	public int getBlindsCount() {
		return this.blindCount.get();
	}
	
	public void setBlindsCount(int blindCount) {
		this.blindCount.set(blindCount);
	}
	
	public IntegerProperty blindCountProperty() {
		return this.blindCount;
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
