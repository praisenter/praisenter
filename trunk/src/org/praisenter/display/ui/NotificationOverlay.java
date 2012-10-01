package org.praisenter.display.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.praisenter.display.Display;
import org.praisenter.transitions.TransitionAnimator;

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
public class NotificationOverlay implements PropertyChangeListener {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(NotificationOverlay.class);
	
	/** The window used to display the Display */
	protected JDialog dialog;
	
	/** The device this display is for */
	protected GraphicsDevice device;
	
	/** The component */
	protected NotificationPanel pnlDisplay;
	
	/** True if the surface is visible */
	protected boolean visible;
	
	/**
	 * Creates a new display window for the given device.
	 * @param device the device
	 */
	public NotificationOverlay(GraphicsDevice device) {
		// simple assignments
		this.device = device;
		this.visible = false;
		
		// setup the dialog
		
		this.dialog = new JDialog();
		this.dialog.setUndecorated(true);
		// don't allow focus to transfer to the dialog
		this.dialog.setFocusable(false);
		this.dialog.setFocusableWindowState(false);
		this.dialog.setFocusTraversalKeysEnabled(false);
		
		// get the device's default config
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		
		// set the location
		this.dialog.setLocation(r.x, r.y);
		
		// we need to enable per-pixel translucency if available
		this.dialog.getRootPane().setOpaque(false);
		if (this.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.dialog.setBackground(new Color(0, 0, 0, 0));
			this.dialog.setVisible(true);
			LOGGER.info("Per-pixel translucency supported (best).");
		} else if (this.device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
			this.dialog.setOpacity(0.0f);
			this.dialog.setVisible(true);
			LOGGER.info("Only uniform translucency supported.");
		} else {
			// not supported so don't show the dialog
			LOGGER.info("No translucency supported.");
		}
		
		// setup the panel
		
		Container container = this.dialog.getContentPane();
		container.setLayout(new BorderLayout());
		
		this.pnlDisplay = new NotificationPanel();
		container.add(this.pnlDisplay, BorderLayout.CENTER);
		
		this.pnlDisplay.addPropertyChangeListener("TransitionStateChanged", this);
	}
	
	/**
	 * 
	 * @param display
	 * @param in
	 * @param out
	 * @param waitPeriod
	 */
	public void send(Display display, TransitionAnimator in, TransitionAnimator out, int waitPeriod) {
		// see if the window is currently visible
		if (!this.visible) {
			// showing the window depends on the translucency support
			if (this.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
				// do nothing
			} else if (this.device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
				// set the opacity to fully opaque
				this.dialog.setOpacity(1.0f);
				// transitions not supported
				in = null;
				out = null;
			} else {
				// set the dialog to visible
				this.dialog.setVisible(true);
				// transitions not supported
				in = null;
				out = null;
			}
			// set the window visible flag
			this.visible = true;
		}
		
		this.pnlDisplay.setMinimumSize(display.getDisplaySize());
		this.pnlDisplay.setPreferredSize(display.getDisplaySize());
		// make sure the panel is resized to fit the layout
		this.dialog.pack();
		
		// if you re-send the display then make sure it goes on
		// top of all other windows
		this.dialog.toFront();
		
		// set the display of the window
		this.pnlDisplay.send(display, in, out, waitPeriod);
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getNewValue() == NotificationState.DONE) {
			// hiding of the window depends on the translucency support
			if (this.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
				// nothing to do here
			} else if (this.device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
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
