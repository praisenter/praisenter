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
		float alpha = 0.0f;
		if (this.type == Transition.Type.IN) {
			g2d.drawImage(image0, 0, 0, null);
			alpha = (float)Math.min(pc, 1.0);
		} else {
			alpha = (float)Math.max(1.0 - pc, 0.0);
		}
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		Composite composite = g2d.getComposite();
		g2d.setComposite(ac);
		if (this.type == Transition.Type.IN) {
			g2d.drawImage(image1, 0, 0, null);
		} else  {
			g2d.drawImage(image0, 0, 0, null);
		}
		g2d.setComposite(composite);
	}
}
