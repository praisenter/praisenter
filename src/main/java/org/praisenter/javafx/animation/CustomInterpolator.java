package org.praisenter.javafx.animation;

import org.praisenter.slide.easing.Easing;

import javafx.animation.Interpolator;

public class CustomInterpolator extends Interpolator {
	final Easing easing;
	
	public CustomInterpolator(Easing easing) {
		this.easing = easing;
	}
	
	@Override
	protected double curve(double t) {
		return this.easing.curve(t);
	}
}
