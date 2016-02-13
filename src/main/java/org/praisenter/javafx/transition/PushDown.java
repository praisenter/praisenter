package org.praisenter.javafx.transition;

import javafx.scene.layout.Region;
import javafx.util.Duration;

final class PushDown extends LocationTransition {
	/** The {@link PushDown} transition id */
	public static final int ID = 63;
	
	public PushDown(Region node, TransitionType type, Duration duration) {
		super(node, type, duration);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getId() {
		return ID;
	}
	
	@Override
	protected void interpolate(double frac) {
		double y = 0;
		if (this.type == TransitionType.IN) {
			y = -this.node.getPrefHeight() * (1.0 - frac);
		} else {
			y = this.node.getPrefHeight() * (frac);
		}
		this.node.setLayoutY(y);
	}
//	
//	/* (non-Javadoc)
//	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
//	 */
//	@Override
//	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
//		Shape shape = g2d.getClip();
//		if (image0 != null) {
//			int y = (int)Math.ceil(image0.getHeight() * pc);
//			g2d.setClip(0, 0, image0.getWidth(), image0.getHeight());
//			g2d.drawImage(image0, 0, y, null);
//		}
//		if (this.type == TransitionType.IN && image1 != null) {
//			int y = (int)Math.ceil(-image1.getHeight() * (1.0 - pc));
//			g2d.setClip(0, 0, image1.getWidth(), image1.getHeight());
//			g2d.drawImage(image1, 0, y, null);
//		}
//		g2d.setClip(shape);
//	}
}
