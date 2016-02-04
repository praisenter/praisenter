package org.praisenter.javafx.transition;

import javafx.scene.Node;

public final class Fade extends CustomTransition {
	/** The {@link Fade} transition id */
	public static final int ID = 20;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public Fade(TransitionType type) {
		super(type);
	}

	@Override
	public int getId() {
		return ID;
	}
	
	@Override
	protected void interpolate(double frac) {
		Node node = this.node;
		if (node != null) {
			double alpha = clamp(frac, 0.0, 1.0);
			if (this.type == TransitionType.IN) {
				node.setOpacity(alpha);
			} else {
				node.setOpacity(1.0 - alpha);
			}
		}
	}
}
