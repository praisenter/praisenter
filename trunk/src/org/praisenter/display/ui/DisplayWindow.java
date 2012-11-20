package org.praisenter.display.ui;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.praisenter.transitions.Transitions;

/**
 * Represents a window that is used to display custom graphics.
 * @param <E> the surface type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class DisplayWindow<E extends DisplaySurface> {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(DisplayWindow.class);
	
	/** The device this display is for */
	protected GraphicsDevice device;

	/** The window used to display the Display */
	protected JDialog dialog;

	/** True if the surface is visible */
	protected boolean visible;
	
	/** The rendering surface */
	protected E surface;
	
	/**
	 * Creates a new display window for the given device.
	 * @param device the device
	 */
	public DisplayWindow(GraphicsDevice device) {
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
		// we need to enable per-pixel translucency if available
		this.dialog.getRootPane().setOpaque(false);
		
		// get the device's default config
		GraphicsConfiguration gc = this.device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		
		// set the dialog location to the top left corner of the
		// target display device
		this.dialog.setLocation(r.x, r.y);
	}
	
	/**
	 * Returns the translucency support for this window.
	 * @return {@link WindowTranslucency}
	 */
	protected WindowTranslucency getWindowTranslucency() {
		if (this.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			return WindowTranslucency.PERPIXEL_TRANSLUCENT;
		} else if (this.device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
			return WindowTranslucency.TRANSLUCENT;
		} else if (this.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSPARENT)) {
			return WindowTranslucency.PERPIXEL_TRANSPARENT;
		} else {
			return null;
		}
	}
	
	/**
	 * This should be done at construction time of the {@link DisplayWindow}.
	 * <p>
	 * This prepares the {@link DisplayWindow} for display given the supported translucency of the
	 * device.
	 */
	protected void prepareForDisplay() {
		WindowTranslucency translucency = this.getWindowTranslucency();
		if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
			// this is the best since all transitions will work
			this.dialog.setBackground(new Color(0, 0, 0, 0));
			this.dialog.setVisible(true);
			LOGGER.info("Per-pixel translucency supported (best).");
		} else if (translucency == WindowTranslucency.TRANSLUCENT) {
			// no transition support but at least we can go ahead
			// and set the dialog to visible to save some time
			this.dialog.setOpacity(0.0f);
			this.dialog.setVisible(true);
			LOGGER.info("Only uniform translucency supported.");
		} else {
			// no support so don't show the dialog
			LOGGER.info("No translucency supported.");
		}
	}
	
	/**
	 * Sets the display window to visible or invisible.
	 * <p>
	 * Returns true if transitions are supported.
	 * @param flag true if the window should be visible
	 * @return boolean
	 */
	protected boolean setVisible(boolean flag) {
		WindowTranslucency translucency = this.getWindowTranslucency();
		boolean transitionsSupported = Transitions.isTransitionSupportAvailable(this.device);
		if (flag) {
			// showing the window depends on the translucency support
			if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
				// do nothing
			} else if (translucency == WindowTranslucency.TRANSLUCENT) {
				// see if the window is currently visible
				if (!this.visible) {
					// set the opacity to fully opaque
					// no transitions sadly
					this.dialog.setOpacity(1.0f);
				}
			} else {
				// see if the window is currently visible
				if (!this.visible) {
					// set the dialog to visible
					// no transitions sadly
					this.dialog.setVisible(true);
				}
			}
			// set the window visible flag
			this.visible = true;
			
			// if you re-send the display then make sure it goes on
			// top of all other windows
			this.dialog.toFront();
		} else {
			// hiding of the window depends on the translucency support
			if (translucency == WindowTranslucency.PERPIXEL_TRANSLUCENT) {
				// then we can just clear the display
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
		
		return transitionsSupported;
	}
	
	/**
	 * Returns the GraphicsDevice this {@link DisplayWindow} is displaying on.
	 * @return GraphicsDevice
	 */
	public GraphicsDevice getDevice() {
		return this.device;
	}

	/**
	 * Returns true if this screen is currently visible.
	 * <p>
	 * The underlying surface may be visible but may have nothing
	 * rendered to it.  This method will only return true if there
	 * is something rendered to the surface.
	 * @return boolean
	 */
	public boolean isVisible() {
		return visible;
	}
}
