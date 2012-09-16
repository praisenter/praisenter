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
public class Swap extends Transition implements Cloneable {
	/**
	 * Minimal constructor.
	 * @param type the transition type
	 */
	public Swap(Type type) {
		super(Messages.getString("transition.swap"), type);
	}
	
	/**
	 * Copy constructor.
	 * @param swap the {@link Swap} to copy
	 */
	public Swap(Swap swap) {
		this(swap.type);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1) {
		// swap immediately displays the next slide
		g2d.drawImage(image1, 0, 0, null);
		this.stop();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#clone()
	 */
	@Override
	public Transition clone() {
		return new Swap(this);
	}
}
