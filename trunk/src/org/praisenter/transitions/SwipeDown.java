package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a swipe down {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SwipeDown extends Transition {
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public SwipeDown(Type type) {
		super(Messages.getString("transition.swipeDown"), type);
	} 

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getTransitionId() {
		return 33;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
		Shape shape = g2d.getClip();
		if (image0 != null) {
			g2d.setClip(0, (int)Math.ceil(image0.getHeight() * pc), image0.getWidth(), image0.getHeight());
			g2d.drawImage(image0, 0, 0, null);
		}
		if (this.type == Transition.Type.IN && image1 != null) {
			g2d.setClip(0, 0, image1.getWidth(), (int)Math.ceil(image1.getHeight() * pc));
			g2d.drawImage(image1, 0, 0, null);
		}
		g2d.setClip(shape);
	}
}
