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
	/** The fade in id */
	protected static final int ID = 10;
	
	/**
	 * Minimal constructor.
	 * @param duration the fade duration in milliseconds
	 */
	public FadeIn(int duration) {
		super(Messages.getString("transition.fadeIn"), Type.IN);
		this.setDuration(duration);
	}
	
	/**
	 * Copy constructor.
	 * @param fadeIn the {@link FadeIn} to copy
	 */
	public FadeIn(FadeIn fadeIn) {
		this(nanoToMilli(fadeIn.duration));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1) {
		long t1 = System.nanoTime();
		long dt = t1 - this.time;
		g2d.drawImage(image0, 0, 0, null);
		// apply alpha composite
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)Math.min(((float)dt / (float)duration), 1.0f));
		Composite composite = g2d.getComposite();
		g2d.setComposite(ac);
		g2d.drawImage(image1, 0, 0, null);
		if (dt >= this.duration) {
			this.stop();
		}
		g2d.setComposite(composite);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#getTransitionId()
	 */
	@Override
	public int getTransitionId() {
		return ID;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#clone()
	 */
	@Override
	public Transition clone() {
		return new FadeIn(this);
	}
}
