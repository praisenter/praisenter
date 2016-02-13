package org.praisenter.javafx.transition;

import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

final class CircularCollapse extends ClipTransition {
	/** The {@link CircularCollapse} transition id */
	static final int ID = 51;
	
	public CircularCollapse(Region node, TransitionType type, Duration duration) {
		super(node, type, duration);
	} 

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.transition.CustomTransition#getId()
	 */
	@Override
	public int getId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see javafx.animation.Transition#interpolate(double)
	 */
	@Override
	protected void interpolate(double frac) {
		if (frac >= 1.0) {
			this.node.setClip(null);
		}
		
		// compute shape params
		double w = this.node.getPrefWidth();
		double h = this.node.getPrefHeight();

		double hw = w * 0.5;
		double hh = h * 0.5;
		double r = Math.sqrt(hw * hw + hh * hh) * (1.0 - frac);
		Rectangle all = new Rectangle(0, 0, w, h);
		Circle circle = new Circle(hw, hh, r);
		
		// create the clip shape
		Shape clip = null;
		if (this.type == TransitionType.IN) {
			clip = Shape.subtract(all, circle);
		} else {
			clip = circle;
		}
		
		this.node.setClip(clip);
	}
}
