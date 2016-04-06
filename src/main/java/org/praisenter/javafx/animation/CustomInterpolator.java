package org.praisenter.javafx.animation;

import javafx.animation.Interpolator;

import org.praisenter.slide.easing.Easing;
import org.praisenter.slide.easing.Linear;

public class CustomInterpolator extends Interpolator {
	final Easing easing;
	
	public CustomInterpolator(Easing easing) {
		this.easing = easing != null ? easing : new Linear();
	}
	
	@Override
	protected double curve(double t) {
		return this.easing.curve(t);
	}
}
