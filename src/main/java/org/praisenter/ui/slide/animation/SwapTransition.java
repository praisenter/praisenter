package org.praisenter.ui.slide.animation;

import javafx.scene.Node;

public final class SwapTransition extends CustomTransition {
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
