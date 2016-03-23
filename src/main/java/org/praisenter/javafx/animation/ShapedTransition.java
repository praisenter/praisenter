package org.praisenter.javafx.animation;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Operation;
import org.praisenter.slide.animation.ShapeType;
import org.praisenter.slide.animation.Shaped;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ShapedTransition extends CustomTransition<Shaped> {
	/** The vertical blind count factor: 12 bars for 1280 pixels */
	private static final double BLIND_COUNT_FACTOR = 12.0 / 1280.0;
	
	public ShapedTransition(Shaped animation) {
		super(animation);
	}

	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		Shape clip = null;
		
		// circle collapse/expand
		if (this.animation.getShapeType() == ShapeType.CIRCLE) {
			clip = this.getCircleClip(node, frac);
		}
		
		node.setClip(clip);
	}
	
	private Shape getCircleClip(Node node, double frac) {
		// get the bounds
		Bounds bounds = node.getBoundsInParent();
		double w = bounds.getWidth();
		double h = bounds.getHeight();

		// compute shape params
//		double w = this.node.getPrefWidth();
//		double h = this.node.getPrefHeight();
		
		if (this.animation.getOperation() == Operation.COLLAPSE) {
			double hw = w * 0.5;
			double hh = h * 0.5;
			double r = Math.sqrt(hw * hw + hh * hh) * (1.0 - frac);
			Rectangle all = new Rectangle(0, 0, w, h);
			Circle circle = new Circle(hw, hh, r);
			
			// create the clip shape
			if (this.animation.getType() == AnimationType.IN) {
				return Shape.subtract(all, circle);
			} else {
				return circle;
			}
		} else if (this.animation.getOperation() == Operation.EXPAND) {
			double hw = w * 0.5;
			double hh = h * 0.5;
			double r = Math.sqrt(hw * hw + hh * hh) * frac;
			Rectangle all = new Rectangle(0, 0, w, h);
			Circle circle = new Circle(hw, hh, r);
			
			// create the clip shape
			if (this.animation.getType() == AnimationType.IN) {
				return circle;
			} else {
				return Shape.subtract(all, circle);
			}
		}
		
		return null;
	}
}
