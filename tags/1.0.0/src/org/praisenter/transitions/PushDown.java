package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a push down {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PushDown extends Transition {
	/** The {@link PushDown} transition id */
	public static final int ID = 63;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public PushDown(Type type) {
		super(Messages.getString("transition.pushDown"), type);
	} 

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getTransitionId() {
		return ID;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
		Shape shape = g2d.getClip();
		if (image0 != null) {
			int y = (int)Math.ceil(image0.getHeight() * pc);
			g2d.setClip(0, 0, image0.getWidth(), image0.getHeight());
			g2d.drawImage(image0, 0, y, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			int y = (int)Math.ceil(-image1.getHeight() * (1.0 - pc));
			g2d.setClip(0, 0, image1.getWidth(), image1.getHeight());
			g2d.drawImage(image1, 0, y, null);
		}
		g2d.setClip(shape);
	}
}
