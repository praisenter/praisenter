package org.praisenter.javafx.animation;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Swipe;

import javafx.geometry.Bounds;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class SwipeTransition extends CustomTransition<Swipe> {
	public SwipeTransition(Swipe animation) {
		super(animation);
	}

	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Bounds bounds = node.getBoundsInParent();
		
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

	private Shape getUpClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(h * (1.0 - frac));
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, p, w, h);
		} else {
			return new Rectangle(0, 0, w, p);
		}
	}
	
	private Shape getRightClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(w * frac);
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(p, 0, w, h);
		} else {
			return new Rectangle(0, 0, p, h);
		}
	}
	
	private Shape getDownClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(h * frac);
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(0, 0, w, p);
		} else {
			return new Rectangle(0, p, w, h * Math.ceil(1.0 - frac));
		}
	}
	
	private Shape getLeftClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double p = Math.ceil(w * (1.0 - frac));
		if (this.animation.getType() == AnimationType.IN) {
			return new Rectangle(p, 0, w, h);
		} else {
			return new Rectangle(0, 0, p, h);
		}
	}
	
	private Shape getClockwiseClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
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
	
	private Shape getCounterClockwiseClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
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
	
	private Shape getWedgeDownClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
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
	
	private Shape getWedgeUpClip(Bounds bounds, double frac) {
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
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
