package org.praisenter.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.praisenter.display.Display;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.Transition.Type;
import org.praisenter.transitions.TransitionAnimator;

/**
 * Panel used for display on a selected device.
 * <p>
 * This panel accepts a display and renders it to a local buffered image.
 * From thereon, the image is used to render the panel.  If the underlying
 * display is updated, this panel will not update.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class TransitionDisplayPanel extends JPanel {
	// TODO this could be an issue when we begin to use video backgrounds
	
	/** The version id */
	private static final long serialVersionUID = -944011695590655744L;
	
	/** The cached before image */
	protected BufferedImage image0;
	
	/** The cached current image */
	protected BufferedImage image1;
	
	/** The transition to apply from display to display */
	protected TransitionAnimator transitionAnimator;
	
	/** True if the panel is clear */
	protected boolean clear;
	
	/**
	 * Default constructor.
	 */
	public TransitionDisplayPanel() {
		this.image0 = null;
		this.image1 = null;
		this.clear = true;
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
	}
	
	/**
	 * Shows the given display using the given transition.
	 * @param display the display to send
	 * @param transitionAnimator the transition; can be null
	 */
	public void send(Display display, TransitionAnimator transitionAnimator) {
		this.clear = false;
		
		// stop the old transition just in case it's still in progress
		if (this.transitionAnimator != null) {
			this.transitionAnimator.stop();
		}
		
		// set the transition
		this.transitionAnimator = transitionAnimator;
		
		// make sure our offscreen images are still the correct size
		this.validateOffscreenImages();
		
		// render whats currently in image1 to image0
		// this saves the last display's rendering so we
		// can apply transitions
		Graphics2D tg2d = this.image0.createGraphics();
		tg2d.drawImage(this.image1, 0, 0, null);
		tg2d.dispose();
		
		// paint the display to the image
		tg2d = this.image1.createGraphics();
		// make the rendering the best quality
		tg2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		tg2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		tg2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		tg2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		tg2d.setBackground(new Color(0, 0, 0, 0));
		tg2d.clearRect(0, 0, this.getSize().width, this.getSize().height);
		display.render(tg2d);
		tg2d.dispose();
		
		// make sure the transition is not null
		if (this.transitionAnimator != null) {
			// start it
			this.transitionAnimator.start(this);
		} else {
			this.repaint();
		}
	}
	
	/**
	 * Clears the panel using the given transition.
	 * @param transitionAnimator the transition; can be null
	 */
	public void clear(TransitionAnimator transitionAnimator) {
		if (!this.clear) {
			// stop the old transition just in case it's still in progress
			if (this.transitionAnimator != null) {
				this.transitionAnimator.stop();
			}
	
			// set the transition
			this.transitionAnimator = transitionAnimator;
			
			// make sure the transition is not null
			if (this.transitionAnimator != null) {
				// render what's currently in image1 to image0
				// this saves the last display's rendering so we
				// can apply transitions
				Graphics2D tg2d = this.image0.createGraphics();
				tg2d.drawImage(this.image1, 0, 0, null);
				tg2d.dispose();
				
				this.clearImage(this.image1);
				// start it
				this.transitionAnimator.start(this);
			} else {
				this.clearImage(this.image0);
				this.clearImage(this.image1);
				this.clear = true;
				this.repaint();
			}
		}
	}
	
	/**
	 * Verifies the offscreen images are created an sized appropriately.
	 */
	protected void validateOffscreenImages() {
		Dimension size = this.getSize();

		if (this.image0 == null || size.width != this.image0.getWidth() || size.height != this.image0.getHeight()) {
			this.image0 = this.getGraphicsConfiguration().createCompatibleImage(this.getSize().width, this.getSize().height, Transparency.TRANSLUCENT);
		}
		
		// if the image is null or the panel has been resized then we need to create a new compatible image
		if (this.image1 == null || size.width != this.image1.getWidth() || size.height != this.image1.getHeight()) {
			// create the image
			this.image1 = this.getGraphicsConfiguration().createCompatibleImage(this.getSize().width, this.getSize().height, Transparency.TRANSLUCENT);
		}
	}
	
	/**
	 * Clears the given image.
	 * @param image the image
	 */
	protected void clearImage(BufferedImage image) {
		Graphics2D g2d = image.createGraphics();
		// set the background color to 100% transparent so that it clears
		// the image when we call clearRect
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2d.dispose();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (this.transitionAnimator != null) {
			Transition transition = transitionAnimator.getTransition();
			if (this.transitionAnimator.isComplete()) {
				if (transition.getType() == Type.IN) {
					g.drawImage(this.image1, 0, 0, null);
				} else {
					if (!this.clear) {
						clearImage(this.image0);
						this.clear = true;
					}
				}
			} else {
				transition.render((Graphics2D)g, this.image0, this.image1, this.transitionAnimator.getPercentComplete());
			}
		} else {
			if (!this.clear) {
				g.drawImage(this.image1, 0, 0, null);
			}
		}
	}
}
