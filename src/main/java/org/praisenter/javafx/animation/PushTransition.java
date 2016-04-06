package org.praisenter.javafx.animation;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Push;

public class PushTransition extends CustomTransition<Push> {
	public PushTransition(Push animation) {
		super(animation);
	}

	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Rectangle2D bounds = this.getBounds();
		
		Point2D dp = new Point2D(0, 0);
		switch(this.animation.getDirection()) {
			case UP:
				dp = getUpPosition(bounds, frac);
				break;
			case RIGHT:
				dp = getRightPosition(bounds, frac);
				break;
			case DOWN:
				dp = getDownPosition(bounds, frac);
				break;
			case LEFT:
				dp = getLeftPosition(bounds, frac);
				break;
			default:
				break;
		}
		
		System.out.println(dp.getX() + " " + dp.getY());
		
		node.setLayoutX(dp.getX());
		node.setLayoutY(dp.getY());
	}
	
	private Point2D getUpPosition(Rectangle2D bounds, double frac) {
		double h = bounds.getHeight();
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(0, h * (1.0 - frac));
		} else {
			return new Point2D(0, -h * (frac));
		}
	}
	
	private Point2D getRightPosition(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(-w * (1.0 - frac), 0);
		} else {
			return new Point2D(w * frac, 0);
		}
	}
	
	private Point2D getDownPosition(Rectangle2D bounds, double frac) {
		double h = bounds.getHeight();
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(0, -h * (1.0 - frac));
		} else {
			return new Point2D(0, h * (frac));
		}
	}
	
	private Point2D getLeftPosition(Rectangle2D bounds, double frac) {
		double w = bounds.getWidth();
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(w * (1.0 - frac), 0);
		} else {
			return new Point2D(-w * frac, 0);
		}
	}
}
