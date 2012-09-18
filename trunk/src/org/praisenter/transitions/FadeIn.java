package org.praisenter.transitions;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a fade-in {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FadeIn extends Transition {
	/**
	 * Default constructor.
	 */
	public FadeIn() {
		super(Messages.getString("transition.fadeIn"), Type.IN);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getTransitionId() {
		return 20;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
		g2d.drawImage(image0, 0, 0, null);
		// apply alpha composite
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)Math.min(pc, 1.0));
		Composite composite = g2d.getComposite();
		g2d.setComposite(ac);
		g2d.drawImage(image1, 0, 0, null);
		g2d.setComposite(composite);
	}
}
