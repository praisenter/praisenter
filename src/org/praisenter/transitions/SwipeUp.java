package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a swipe up {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SwipeUp extends Transition {
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public SwipeUp(Type type) {
		super(Messages.getString("transition.swipeUp"), type);
	} 

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getTransitionId() {
		return 32;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
		Shape shape = g2d.getClip();
		g2d.setClip(0, 0, image0.getWidth(), image0.getHeight() - (int)Math.ceil(image0.getHeight() * pc));
		g2d.drawImage(image0, 0, 0, null);
		if (this.type == Transition.Type.IN) {
			g2d.setClip(0, image1.getHeight() - (int)Math.ceil(image1.getHeight() * pc), image1.getWidth(), image1.getHeight());
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
