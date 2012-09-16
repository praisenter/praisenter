package org.praisenter.transitions;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.praisenter.resources.Messages;

/**
 * Represents a fade-out {@link Transition}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class FadeOut extends Transition {
	/**
	 * Minimal constructor.
	 * @param duration the fade duration in milliseconds
	 */
	public FadeOut(int duration) {
		super(Messages.getString("transition.fadeOut"), Type.OUT);
		this.setDuration(duration);
	}
	
	/**
	 * Copy constructor.
	 * @param fadeOut the {@link FadeOut} to copy
	 */
	public FadeOut(FadeOut fadeOut) {
		this(nanoToMilli(fadeOut.duration));
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#render(java.awt.Graphics2D, java.awt.image.BufferedImage, java.awt.image.BufferedImage)
	 */
	@Override
	public void render(Graphics2D g2d, BufferedImage image0, BufferedImage image1) {
		long t1 = System.nanoTime();
		long dt = t1 - this.time;
		// apply alpha composite
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)Math.max(1.0f - ((float)dt / (float)duration), 0.0f));
		Composite composite = g2d.getComposite();
		g2d.setComposite(ac);
		g2d.drawImage(image0, 0, 0, null);
		if (dt >= this.duration) {
			this.stop();
		}
		g2d.setComposite(composite);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.transitions.Transition#clone()
	 */
	@Override
	public Transition clone() {
		return new FadeOut(this);
	}
}
