package org.praisenter.display.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.praisenter.display.Display;
import org.praisenter.transitions.Transition;
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
public class NotificationPanel extends JPanel implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -944011695590655744L;
	
	/** The cached image */
	protected BufferedImage image;
	
	/** A cached blank image */
	protected BufferedImage blank;
	
	/** The transition coming in */
	protected TransitionAnimator in;
	
	/** The transition going out */
	protected TransitionAnimator out;
	
	protected Timer waitTimer;
	
	protected NotificationState state;
	
	/**
	 * Default constructor.
	 */
	protected NotificationPanel() {
		this.image = null;
		this.in = null;
		this.out = null;
		this.state = NotificationState.IN;
		this.waitTimer = new Timer(0, this);
		this.waitTimer.setActionCommand("waitComplete");
		this.waitTimer.setRepeats(false);
		this.setOpaque(false);
		this.setBackground(new Color(0, 0, 0, 0));
	}
	
	public void send(Display display, TransitionAnimator in, TransitionAnimator out, int waitPeriod) {
		this.state = NotificationState.IN;
		this.waitTimer.stop();
		
		// stop the old transitions just in case they are still in progress
		if (this.in != null) {
			this.in.stop();
		}
		if (this.out != null) {
			this.out.stop();
		}
		
		// set the transitions
		this.in = in;
		this.out = out;
		this.waitTimer.setInitialDelay(waitPeriod);
		
		// make sure our offscreen image is still the correct size
		this.validateOffscreenImage();
		
		// paint the display to the image
		Graphics2D tg2d = this.image.createGraphics();
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
		if (this.in != null) {
			// start it
			this.in.start(this);
		} else {
			this.repaint();
		}
	}
	
	/**
	 * Verifies the offscreen image is created an sized appropriately.
	 */
	protected void validateOffscreenImage() {
		Dimension size = this.getSize();

		if (this.image == null || size.width != this.image.getWidth() || size.height != this.image.getHeight()) {
			this.image = this.getGraphicsConfiguration().createCompatibleImage(this.getSize().width, this.getSize().height, Transparency.TRANSLUCENT);
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
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("waitComplete".equals(e.getActionCommand())) {
			this.waitTimer.stop();
			this.state = NotificationState.OUT;
			if (this.out != null) {
				this.out.start(this);
			} else {
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
		
		if (this.state == NotificationState.IN) {
			if (this.in == null) {
				this.state = NotificationState.WAIT;
				this.waitTimer.start();
			} else {
				if (!this.in.isComplete()) {
					Transition transition = this.in.getTransition();
					transition.render((Graphics2D)g, this.blank, this.image, this.in.getPercentComplete());
				} else {
					this.state = NotificationState.WAIT;
					this.waitTimer.start();
				}
			}
		}
		
		if (this.state == NotificationState.WAIT) {
			g.drawImage(this.image, 0, 0, null);
		}
		
		if (this.state == NotificationState.OUT) {
			if (this.out == null) {
				this.state = NotificationState.DONE;
				this.firePropertyChange("TransitionStateChanged", null, NotificationState.DONE);
			} else {
				if (!this.out.isComplete()) {
					Transition transition = this.out.getTransition();
					transition.render((Graphics2D)g, this.image, this.blank, this.out.getPercentComplete());
				} else {
					this.state = NotificationState.DONE;
					this.firePropertyChange("TransitionStateChanged", null, NotificationState.DONE);
				}
			}
		}
	}
}
