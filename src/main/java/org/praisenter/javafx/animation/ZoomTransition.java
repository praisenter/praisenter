package org.praisenter.javafx.animation;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Zoom;

public class ZoomTransition extends CustomTransition<Zoom> {
	
	public ZoomTransition(Zoom animation) {
		super(animation);
	}

	@Override
	public void stop() {
		super.stop();
		if (this.node != null) {
			this.node.setScaleX(1);
			this.node.setScaleY(1);
		}
	}
	
	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
//		Bounds bounds = node.getBoundsInParent();
//		double w = bounds.getWidth();
//		double h = bounds.getHeight();
		
		if (this.animation.getType() == AnimationType.IN) {
			node.setScaleX(Math.max(frac, 0));
			node.setScaleY(Math.max(frac, 0));
		} 
//		else {
//			// for the out transition we'll just clip the center
////			double w = this.node.getPrefWidth();
////			double h = this.node.getPrefHeight();
//			double hw = w * 0.5;
//			double hh = h * 0.5;
//			Shape clip = new Rectangle(0, 0, w, h);
//			Shape center = new Rectangle(hw * (1.0 - frac), hh * (1.0 - frac), h * frac, h * frac);
//			node.setClip(Shape.subtract(clip, center));
//		}
		
//		if (this.type == AnimationType.IN) {
//			// for the out transition we'll just clip the center
////			double w = this.node.getPrefWidth();
////			double h = this.node.getPrefHeight();
//			double hw = w * 0.5;
//			double hh = h * 0.5;
//			Shape clip = new Rectangle(0, 0, w, h);
//			Shape center = new Rectangle(hw * frac, hh * frac, h * (1.0 - frac), h * (1.0 - frac));
//			node.setClip(Shape.subtract(clip, center));
//		} 
		else {
			node.setScaleX(Math.max(0.0, 1.0 - frac));
			node.setScaleY(Math.max(0.0, 1.0 - frac));
		}
	}
}
