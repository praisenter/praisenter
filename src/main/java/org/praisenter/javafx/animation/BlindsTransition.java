package org.praisenter.javafx.animation;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Blinds;

public class BlindsTransition extends CustomTransition<Blinds> {
	public BlindsTransition(Blinds animation) {
		super(animation);
	}

	@Override
	public void stop() {
		super.stop();
		if (this.node != null) {
			this.node.setClip(null);
		}
	}
	
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Rectangle2D bounds = this.getBounds();
		
		Shape clip = null;
		switch(this.animation.getOrientation()) {
			case HORIZONTAL:
				clip = getHorizontalBlinds(bounds, frac);
				break;
			case VERTICAL:
				clip = getVerticalBlinds(bounds, frac);
				break;
			default:
				break;
		}
		
		this.node.setClip(clip);
	}

	private Shape getHorizontalBlinds(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		Rectangle rect = new Rectangle();
		if (this.animation.getType() == AnimationType.IN) {
			// for the IN transition we will subtract areas from the full rectangle
			rect.setWidth(w);
			rect.setHeight(h);
		}
		// for the OUT transition we will add areas
		
		// compute the number of blinds
		final int blinds = this.animation.getBlindCount();
		double y = 0;
		// compute the blind width
		double bh = h / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		Shape clip = rect;
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(0, y + bh * frac, w, bh * (1.0 - frac));
			if (this.animation.getType() == AnimationType.IN) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			y += bh;
		}
		
		return clip;
	}
	
	private Shape getVerticalBlinds(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		
		Shape clip = null;
		if (this.animation.getType() == AnimationType.IN) {
			clip = new Rectangle(0, 0, w, h);
		} else {
			clip = new Rectangle();
		}
		
		// compute the number of blinds
		final int blinds = this.animation.getBlindCount();
		double x = 0;
		// compute the blind width
		double bw = w / blinds;
		// compute the area that needs to be painted by either removing
		// vertical bars or adding vertical bars
		for (int i = 0; i < blinds; i++) {
			Rectangle blind = new Rectangle(x + bw * frac, 0, bw * (1.0 - frac), h);
			if (this.animation.getType() == AnimationType.IN) {
				clip = Shape.subtract(clip, blind);
			} else {
				clip = Shape.union(clip, blind);
			}
			x += bw;
		}
		
		return clip;
	}
}
