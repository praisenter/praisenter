package org.praisenter.javafx.animation;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Swipe;

public class SwipeTransition extends CustomTransition<Swipe> {
	public SwipeTransition(Swipe animation) {
		super(animation);
	}

	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Rectangle2D bounds = this.getBounds();
		
		Shape clip = null;
		switch(this.animation.getDirection()) {
			case UP:
				clip = getUpClip(bounds, frac);
				break;
			case RIGHT:
				clip = getRightClip(bounds, frac);
				break;
			case DOWN:
				clip = getDownClip(bounds, frac);
				break;
			case LEFT:
				clip = getLeftClip(bounds, frac);
				break;
			case CLOCKWISE:
				clip = getClockwiseClip(bounds, frac);
				break;
			case COUNTER_CLOCKWISE:
				clip = getCounterClockwiseClip(bounds, frac);
				break;
			case WEDGE_DOWN:
				clip = getWedgeDownClip(bounds, frac);
				break;
			case WEDGE_UP:
				clip = getWedgeUpClip(bounds, frac);
				break;
			default:
				break;
		}
		
		node.setClip(clip);
	}

	private Shape getUpClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(h * (1.0 - frac));
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, p, w, h);
		} else {
			return new Rectangle(0, 0, w, p);
		}
	}
	
	private Shape getRightClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(w * frac);
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, 0, p, h);
		} else {
			return new Rectangle(p, 0, w, h);
		}
	}
	
	private Shape getDownClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(h * frac);
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, 0, w, p);
		} else {
			return new Rectangle(0, p, w, h * Math.ceil(1.0 - frac));
		}
	}
	
	private Shape getLeftClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(w * (1.0 - frac));
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(p, 0, w, h);
		} else {
			return new Rectangle(0, 0, p, h);
		}
	}
	
	private Shape getClockwiseClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90.0, -360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}
	
	private Shape getCounterClockwiseClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90.0, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}
	
	private Shape getWedgeDownClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, 90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);

		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}
	
	private Shape getWedgeUpClip(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh);
		
		Rectangle all = new Rectangle(0, 0, w, h);
		Arc arc = new Arc(hw, hh, r, r, -90 - 180 * frac, 360 * frac);
		arc.setType(ArcType.ROUND);
		
		if (this.animation.getType() == AnimationType.IN) {
			return arc;
		} else {
			return Shape.subtract(all, arc);
		}
	}

}
