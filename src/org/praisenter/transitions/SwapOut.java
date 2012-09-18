package org.praisenter.transitions;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a flip transition.
 * <p>
 * The beginning image is immediately swapped with the ending image.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SwapOut extends Transition {
	/**
	 * Default constructor.
	 */
	public SwapOut() {
		super(Messages.getString("transition.swap"), Type.OUT);
	}

	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getTransitionId() {
		return 11;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage, double)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1, double pc) {
		// swap immediately displays the next slide
		g2d.drawImage(image1, 0, 0, null);
	}
}
