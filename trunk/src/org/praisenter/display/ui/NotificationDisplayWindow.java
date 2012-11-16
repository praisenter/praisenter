package org.praisenter.display.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.praisenter.display.NotificationDisplay;
import org.praisenter.display.TextComponent;
import org.praisenter.slide.transitions.TransitionAnimator;

/**
 * Represents a screen that is used to display custom graphics.
 * <p>
 * By default the rendering surface will be the size of the given GraphicsDevice and will
 * be invisible.  The mechanism for invisibility differs depending on the supported
 * WindowTranslucency.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class NotificationDisplayWindow extends DisplayWindow<NotificationDisplaySurface> implements PropertyChangeListener {
	/** The transition state property name */
	protected static final String PROPERTY_TRANSITION_STATE = "TransitionState";
	
	/**
	 * Creates a new display window for the given device.
	 * @param device the device
	 */
	public NotificationDisplayWindow(GraphicsDevice device) {
		super(device);

		// setup the display surface
		Container container = this.dialog.getContentPane();
		container.setLayout(new BorderLayout());
		
		this.surface = new NotificationDisplaySurface();
		this.surface.addPropertyChangeListener(PROPERTY_TRANSITION_STATE, this);
		container.add(this.surface, BorderLayout.CENTER);
		
		// set to always on top
		this.dialog.setAlwaysOnTop(true);
		
		// make sure the panel is resized to fit the layout
		this.dialog.pack();
		
		// prepare for display
		this.prepareForDisplay();
	}
	
	/**
	 * Sends a notification to this window.
	 * @param display the notification display
	 * @param in the transition in
	 * @param out the transition out
	 * @param waitPeriod the wait period
	 */
	public void send(NotificationDisplay display, TransitionAnimator in, TransitionAnimator out, int waitPeriod) {
		// set the position
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		
		TextComponent text = display.getTextComponent();
		Dimension size = new Dimension(text.getWidth(), text.getHeight());
		// set the size
		this.dialog.setMinimumSize(size);
		this.dialog.setPreferredSize(size);
		this.dialog.setLocation(r.x + text.getX(), r.y + text.getY());
		
		// make sure the panel is resized to fit the layout
		this.dialog.pack();

		if (this.setVisible(true)) {
			// set the display of the window
			this.surface.send(display, in, out, waitPeriod);
		} else {
			// set the display of the window
			this.surface.send(display, waitPeriod);
		}
	}
	
	/**
	 * Clears the current notification using the given animator.
	 * <p>
	 * If the notification is already transitioning out, this method will
	 * do nothing.
	 * @param animator the animator to use
	 */
	public void clear(TransitionAnimator animator) {
		this.surface.clear(animator);
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		WindowTranslucency translucency = this.getWindowTranslucency();
		// listen for notification done state
		if (evt.getNewValue() == NotificationState.DONE) {
			// hiding of the window depends on the translucency support
			if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
				// nothing to do here
			} else if (translucency == WindowTranslucency.TRANSLUCENT) {
				// then we can set the opacity
				// no transitions sadly
				this.dialog.setOpacity(0.0f);
			} else {
				// then we have to set the window to not visible
				// no transitions sadly
				this.dialog.setVisible(false);
			}
			this.visible = false;
		}
	}
}
