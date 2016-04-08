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
	public void stop() {
		super.stop();
		if (this.node != null) {
			this.node.setTranslateX(0);
			this.node.setTranslateY(0);
		}
	}
	
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Rectangle2D nb = this.getBounds();
		Rectangle2D pb = this.getParentBounds();
		
		Point2D dp = new Point2D(0, 0);
		switch(this.animation.getDirection()) {
			case UP:
				dp = getUpPosition(nb, pb, frac);
				break;
			case RIGHT:
				dp = getRightPosition(nb, pb, frac);
				break;
			case DOWN:
				dp = getDownPosition(nb, pb, frac);
				break;
			case LEFT:
				dp = getLeftPosition(nb, pb, frac);
				break;
			default:
				break;
		}
		
		node.setTranslateX(dp.getX());
		node.setTranslateY(dp.getY());
	}
	
	private Point2D getUpPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double y = (pb.getHeight() - nb.getMinY());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(0, y * (1.0 - frac));
		} else {
			return new Point2D(0, -y * (frac));
		}
	}
	
	private Point2D getRightPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double x = (pb.getWidth() - nb.getMinX());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(-x * (1.0 - frac), 0);
		} else {
			return new Point2D(x * frac, 0);
		}
	}
	
	private Point2D getDownPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double y = (pb.getHeight() - nb.getMinY());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(0, -y * (1.0 - frac));
		} else {
			return new Point2D(0, y * (frac));
		}
	}
	
	private Point2D getLeftPosition(Rectangle2D nb, Rectangle2D pb, double frac) {
		double x = (pb.getWidth() - nb.getMinX());
		if (this.animation.getType() == AnimationType.IN) {
			return new Point2D(x * (1.0 - frac), 0);
		} else {
			return new Point2D(-x * frac, 0);
		}
	}
}
