package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a push left {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class PushLeft extends Transition {
	/** The {@link PushLeft} transition id */
	public static final int ID = 61;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public PushLeft(Type type) {
		super(Messages.getString("transition.pushLeft"), type);
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
			int x = (int)Math.ceil(-image0.getWidth() * pc);
			g2d.setClip(0, 0, image0.getWidth(), image0.getHeight());
			g2d.drawImage(image0, x, 0, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			int x = (int)Math.ceil(image1.getWidth() * (1.0 - pc));
			g2d.setClip(0, 0, image1.getWidth(), image1.getHeight());
			g2d.drawImage(image1, x, 0, null);
		}
		g2d.setClip(shape);
	}
}
