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
public class Swap extends Transition {
	/** The {@link Swap} transition id */
	public static final int ID = 10;
	
	/**
	 * Full constructor.
	 * @param type the transition type
	 */
	public Swap(Type type) {
		super(Messages.getString("transition.swap"), type);
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
		// swap immediately displays the next slide
		g2d.drawImage(image1, 0, 0, null);
	}
}
