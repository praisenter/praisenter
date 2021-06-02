package org.praisenter.ui.slide.animation;

import javafx.scene.Node;
import javafx.util.Duration;

public final class SwapTransition extends CustomTransition {
	public SwapTransition() {
		// NOTE: you have to have SOME duration for it to even play the transition
		setCycleDuration(new Duration(10));
	}
	
	@Override
	public void stop() {
		super.stop();
		Node node = this.node.get();
		if (node == null) return;
		node.setVisible(false);
	}
	
	@Override
	protected void interpolate(double frac) {
		Node node = this.node.get();
		if (node == null) return;
		
		if (this.isInTransition()) {
			node.setVisible(true);
		} else {
			node.setVisible(false);
		}
	}
}
