package org.praisenter.javafx.animation;

import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Swap;

public class SwapTransition extends CustomTransition<Swap> {
	
	public SwapTransition(Swap animation) {
		super(animation);
	}

	@Override
	protected void interpolate(double frac) {
		if (this.node == null) return;
		
		if (this.animation.getType() != AnimationType.IN) {
			this.node.setVisible(false);
		}
	}
}
