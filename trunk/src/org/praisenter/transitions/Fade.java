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
public class Fade extends Transition {
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public Fade(Type type) {
		super(Messages.getString("transition.fade"), type);
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
		// apply alpha composite
		Composite composite = g2d.getComposite();
		if (this.type == Transition.Type.IN) {
			// draw the old
			g2d.drawImage(image0, 0, 0, null);
			// fade in the new
			float alpha = (float)Math.min(pc, 1.0);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2d.drawImage(image1, 0, 0, null);
		} else {
			float alpha = (float)Math.min(pc, 1.0);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - alpha));
			g2d.drawImage(image0, 0, 0, null);
		}
		g2d.setComposite(composite);
	}
}
