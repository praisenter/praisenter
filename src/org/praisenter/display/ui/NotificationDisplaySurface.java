package org.praisenter.display.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import org.praisenter.display.Display;
import org.praisenter.transitions.Transition;
import org.praisenter.transitions.TransitionAnimator;

/**
 * Represents a special display surface supporting notifications.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationDisplaySurface extends DisplaySurface implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = -944011695590655744L;
	
	/** The cached image */
	protected BufferedImage image;
	
	/** The transition coming in */
	protected TransitionAnimator in;
	
	/** The transition going out */
	protected TransitionAnimator out;
	
	/** The timer for the wait period */
	protected Timer waitTimer;
	
	/** The current notification state */
	protected NotificationState state;
	
	/**
	 * Default constructor.
	 */
	protected NotificationDisplaySurface() {
		super();
		this.image = null;
		this.in = null;
		this.out = null;
		// default state is in
		this.state = NotificationState.IN;
		// setup the wait timer to not repeat and to fire immediately when started
		this.waitTimer = new Timer(0, this);
		this.waitTimer.setActionCommand("waitComplete");
		this.waitTimer.setRepeats(false);
	}
	
	/**
	 * Sends the notification display and shows for the given wait period.
	 * @param display the notification display
	 * @param waitPeriod the wait period
	 */
	public void send(Display display, int waitPeriod) {
		this.send(display, null, null, waitPeriod);
	}
	
	/**
	 * Sends the notification display and shows for the given wait period.
	 * @param display the notification display
	 * @param in the transition in animator
	 * @param out the transition out animator
	 * @param waitPeriod the wait period
	 */
	public void send(Display display, TransitionAnimator in, TransitionAnimator out, int waitPeriod) {
		// set the state to the initial state
		this.state = NotificationState.IN;
		// stop the wait timer if necessary
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
		this.image = DisplaySurface.validateOffscreenImage(this.image, this);
		
		// paint the display to the image
		DisplaySurface.renderDisplay(display, this.image);
		
		// make sure the transition is not null
		if (this.in != null) {
			// start it
			this.in.start(this);
		} else {
			this.repaint();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("waitComplete".equals(e.getActionCommand())) {
			// stop the wait timer
			this.waitTimer.stop();
			// set the new state
			this.state = NotificationState.OUT;
			// see if there is an out transition
			if (this.out != null) {
				// if so, then start the out transition
				this.out.start(this);
			} else {
				// if not, then just call repaint
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
		
		// check if the state is "in"
		if (this.state == NotificationState.IN) {
			// if so, then check if the "in" transition is null
			if (this.in == null) {
				// if it is, then jump to the "wait" state
				this.state = NotificationState.WAIT;
				this.waitTimer.start();
			} else {
				// if its not, then check if the "in" transition is complete
				if (!this.in.isComplete()) {
					// if its not complete, then render it
					Transition transition = this.in.getTransition();
					transition.render((Graphics2D)g, null, this.image, this.in.getPercentComplete());
				} else {
					// if its complete then jump to the "wait" state
					this.state = NotificationState.WAIT;
					this.waitTimer.start();
				}
			}
		}
		
		// check if we are in the "wait" state
		if (this.state == NotificationState.WAIT) {
			// if so, simply render the notification
			g.drawImage(this.image, 0, 0, null);
		}
		
		// check if we are in the "out" state
		if (this.state == NotificationState.OUT) {
			// if so, then check if the "out" transition is null
			if (this.out == null) {
				// if it is, then jump to the "done" state
				this.state = NotificationState.DONE;
				// let the notification window know that we are done
				this.firePropertyChange(NotificationDisplayWindow.PROPERTY_TRANSITION_STATE, null, NotificationState.DONE);
			} else {
				// if its not, then check if the "out" transition is complete
				if (!this.out.isComplete()) {
					// if its not complete, then render it
					Transition transition = this.out.getTransition();
					transition.render((Graphics2D)g, this.image, null, this.out.getPercentComplete());
				} else {
					// if it is, then jump to the "done" state
					this.state = NotificationState.DONE;
					// let the notification window know that we are done
					this.firePropertyChange(NotificationDisplayWindow.PROPERTY_TRANSITION_STATE, null, NotificationState.DONE);
				}
			}
		}
	}
}
