package org.praisenter.javafx.animation;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Split;

public class SplitTransition extends CustomTransition<Split> {
	public SplitTransition(Split animation) {
		super(animation);
	}

	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Rectangle2D bounds = this.getBounds();
		
		Shape clip = null;
		switch(this.animation.getOrientation()) {
			case HORIZONTAL:
				switch(this.animation.getOperation()) {
					case COLLAPSE:
						clip = getHorizontalCollapse(bounds, frac);
						break;
					case EXPAND:
						clip = getHorizontalExpand(bounds, frac);
						break;
					default:
						break;
				}
				break;
			case VERTICAL:
				switch(this.animation.getOperation()) {
					case COLLAPSE:
						clip = getVerticalCollapse(bounds, frac);
						break;
					case EXPAND:
						clip = getVerticalExpand(bounds, frac);
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

	private Shape getHorizontalCollapse(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(0, hh * frac, w, h * (1.0 - frac));
		
		if (this.animation.getType() == AnimationType.IN) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	private Shape getHorizontalExpand(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hh = h * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(0, hh * (1.0 - frac), w, h * frac);
		
		if (this.animation.getType() == AnimationType.IN) {
			return cut;
		} else {
			return Shape.subtract(all, cut);
		}
	}
	
	private Shape getVerticalCollapse(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(hw * frac, 0, w * (1.0 - frac), h);
		
		if (this.animation.getType() == AnimationType.IN) {
			return Shape.subtract(all, cut);
		} else {
			return cut;
		}
	}
	
	private Shape getVerticalExpand(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Rectangle cut = new Rectangle(hw * (1.0 - frac), 0, w * frac, h);
		
		if (this.animation.getType() == AnimationType.IN) {
			return cut;
		} else {
			return Shape.subtract(all, cut);
		}
	}
}
