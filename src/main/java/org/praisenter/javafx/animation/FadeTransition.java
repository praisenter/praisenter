package org.praisenter.javafx.animation;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Fade;

public class FadeTransition extends CustomTransition<Fade> {
	
	public FadeTransition(Fade animation) {
		super(animation);
	}

	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		double alpha = clamp(frac, 0.0, 1.0);
		if (this.animation.getType() == AnimationType.IN) {
			this.node.setOpacity(alpha);
		} else {
			this.node.setOpacity(1.0 - alpha);
		}
	}
}
