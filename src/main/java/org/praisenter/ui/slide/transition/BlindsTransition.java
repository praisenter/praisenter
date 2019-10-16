package org.praisenter.ui.slide.transition;

import org.praisenter.data.slide.effects.Orientation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class BlindsTransition extends CustomTransition {
	private final ObjectProperty<Duration> duration;
	private final ObjectProperty<Orientation> orientation;
	private final DoubleProperty width;
	private final DoubleProperty height;
	private final IntegerProperty blindCount;

	public BlindsTransition() {
		this.duration = new SimpleObjectProperty<Duration>();
		this.orientation = new SimpleObjectProperty<Orientation>();
		this.width = new SimpleDoubleProperty();
		this.height = new SimpleDoubleProperty();
		this.blindCount = new SimpleIntegerProperty();

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
		
		double width = this.width.get();
		double height = this.height.get();
		
		int blindCount = this.blindCount.get();
		
		Shape clip = null;
		switch(orientation) {
			case HORIZONTAL:
				clip = getHorizontalBlinds(width, height, blindCount, frac);
				break;
			case VERTICAL:
				clip = getVerticalBlinds(width, height, blindCount, frac);
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

	private Shape getHorizontalBlinds(double w, double h, int blinds, double frac) {
		Rectangle rect = new Rectangle();
		if (this.isInTransition()) {
			// for the IN transition we will subtract areas from the full rectangle
			rect.setWidth(w);
			rect.setHeight(h);
		}
		double y = 0;
		// compute the blind width
		double bh = h / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		Shape clip = rect;
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(0, y + bh * frac, w, bh * (1.0 - frac));
			if (this.isInTransition()) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			y += bh;
		}
		
		return clip;
	}
	
	private Shape getVerticalBlinds(double w, double h, int blinds, double frac) {
		Shape clip = null;
		if (this.isInTransition()) {
			clip = new Rectangle(0, 0, w, h);
		} else {
			clip = new Rectangle();
		}
		
		double x = 0;
		// compute the blind width
		double bw = w / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(x + bw * frac, 0, bw * (1.0 - frac), h);
			if (this.isInTransition()) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			x += bw;
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
	
	public Orientation getOrientation() {
		return this.orientation.get();
	}
	
	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}
	
	public ObjectProperty<Orientation> orientationProperty() {
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
