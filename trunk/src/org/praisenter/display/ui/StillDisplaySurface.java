package org.praisenter.display.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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
public class StillDisplaySurface extends StandardDisplaySurface {
	/** The version id */
	private static final long serialVersionUID = -944011695590655744L;
	
	/** The cached before image */
	protected BufferedImage image0;
	
	/** The cached current image */
	protected BufferedImage image1;
	
	/** The transition to apply from display to display */
	protected TransitionAnimator animator;
	
	/** True if the panel is clear */
	protected boolean clear;
	
	/**
	 * Default constructor.
	 */
	protected StillDisplaySurface() {
		super();
		this.image0 = null;
		this.image1 = null;
		this.clear = true;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.StandardDisplaySurface#send(org.praisenter.display.Display)
	 */
	@Override
	public void send(Display display) {
		this.send(display, null);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.StandardDisplaySurface#send(org.praisenter.display.Display, org.praisenter.transitions.TransitionAnimator)
	 */
	@Override
	public void send(Display display, TransitionAnimator animator) {
		this.clear = false;
		
		// stop the old transition just in case it's still in progress
		if (this.animator != null) {
			this.animator.stop();
		}
		
		// set the transition
		this.animator = animator;
		
		// make sure our offscreen images are still the correct size
		this.image0 = DisplaySurface.validateOffscreenImage(this.image0, this);
		this.image1 = DisplaySurface.validateOffscreenImage(this.image1, this);
		
		// render whats currently in image1 to image0
		// this saves the last display's rendering so we
		// can apply transitions
		DisplaySurface.copyImage(this.image1, this.image0);
		
		// paint the display to the image
		DisplaySurface.renderDisplay(display, this.image1);
		
		// make sure the transition is not null
		if (this.animator != null) {
			// start it
			this.animator.start(this);
		} else {
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.StandardDisplaySurface#clear()
	 */
	@Override
	public void clear() {
		this.clear(null);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.display.ui.StandardDisplaySurface#clear(org.praisenter.transitions.TransitionAnimator)
	 */
	@Override
	public void clear(TransitionAnimator animator) {
		if (!this.clear) {
			// stop the old transition just in case it's still in progress
			if (this.animator != null) {
				this.animator.stop();
			}
	
			// set the transition
			this.animator = animator;
			
			// make sure the transition is not null
			if (this.animator != null) {
				// render what's currently in image1 to image0
				// this saves the last display's rendering so we
				// can apply transitions
				Graphics2D tg2d = this.image0.createGraphics();
				tg2d.drawImage(this.image1, 0, 0, null);
				tg2d.dispose();
				
				DisplaySurface.clearImage(this.image1);
				// start it
				this.animator.start(this);
			} else {
				DisplaySurface.clearImage(this.image0);
				DisplaySurface.clearImage(this.image1);
				this.clear = true;
				this.repaint();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (this.animator != null) {
			Transition transition = animator.getTransition();
			if (this.animator.isComplete()) {
				if (transition.getType() == Type.IN) {
					g.drawImage(this.image1, 0, 0, null);
				} else {
					if (!this.clear) {
						clearImage(this.image0);
						this.clear = true;
					}
				}
			} else {
				transition.render((Graphics2D)g, this.image0, this.image1, this.animator.getPercentComplete());
			}
		} else {
			if (!this.clear) {
				g.drawImage(this.image1, 0, 0, null);
			}
		}
	}
}
