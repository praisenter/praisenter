package org.praisenter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDialog;

import org.apache.log4j.Logger;
import org.praisenter.display.Display;
import org.praisenter.panel.TransitionDisplayPanel;
import org.praisenter.settings.GeneralSettings;
import org.praisenter.transitions.Transition;
import org.praisenter.utilities.WindowUtilities;

/**
 * Represents a frame that is used to display custom graphics.
 * <p>
 * In this applications case, this frame will be used to display bible verses, song lyrics, etc.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class DisplayWindow {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(DisplayWindow.class);
	
	/** The device to window mapping */
	private static final Map<String, DisplayWindow> WINDOWS = new HashMap<String, DisplayWindow>();
	
	/**
	 * An enumeration of return types from the show method.
	 * @author William Bittle
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static enum ShowResult {
		/** The normal return */
		NORMAL,
		
		/** The device is no longer valid */
		DEVICE_NOT_VALID
	}
	
	// static interface
	
	/**
	 * Returns the instance of {@link DisplayWindow} from the given GraphicsDevice.
	 * <p>
	 * Returns null if the given GraphicsDevice is no longer valid.
	 * @param device the device
	 * @return {@link DisplayWindow}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	public static final synchronized DisplayWindow getDisplay(GraphicsDevice device) {
		if (device == null) return null;
		// check if the given device is still valid
		boolean valid = WindowUtilities.isValid(device);
		// get the cached window using device id
		String id = device.getIDstring();
		DisplayWindow window = WINDOWS.get(id);
		// doing this will handle new devices being added
		if (window == null && valid) {
			// the device is valid so create a new window for it
			window = new DisplayWindow(device);
			WINDOWS.put(id, window);
		} else if (window != null && !valid) {
			invalidate(window);
			return null;
		}
		return window;
	}
	
	/**
	 * Returns the primary {@link DisplayWindow} determined by the {@link GeneralSettings}.
	 * <p>
	 * Returns null if the assigned GraphicsDevice is no longer valid.
	 * @return {@link DisplayWindow}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	public static final DisplayWindow getPrimaryDisplay() {
		GeneralSettings settings = GeneralSettings.getInstance();
		GraphicsDevice device = settings.getPrimaryDisplay();
		return getDisplay(device);
	}
	
	/**
	 * Hides the given {@link DisplayWindow}.
	 * @param window the display window
	 * @param transition the transition
	 */
	public static final void hide(DisplayWindow window, Transition transition) {
		// check for null
		if (window == null) return;
		
		// hiding of the window depends on the translucency support
		if (window.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			// then we can just clear the display
			window.pnlDisplay.clear(transition);
		} else if (window.device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
			// then we can set the opacity
			window.dialog.setOpacity(0.0f);
		} else {
			// then we have to set the window to not visible
			window.dialog.setVisible(false);
		}
		window.visible = false;
	}
	
	/**
	 * Hides all the {@link DisplayWindow}s.
	 * @param transition the transition
	 */
	public static final synchronized void hide(Transition transition) {
		Iterator<String> keys = WINDOWS.keySet().iterator();
		while (keys.hasNext()) {
			DisplayWindow window = WINDOWS.get(keys.next());
	
			if (window.visible) {
				hide(window, transition);
			}
			
			// make sure the display is still valid
			if (!isValid(window)) {
				invalidate(window);
			}
		}
	}
	
	/**
	 * Shows the given {@link DisplayWindow} with the given {@link Display} attached.
	 * <p>
	 * If the window is already visible the display is simply changed.
	 * @param window the display window to show
	 * @param display the display to show
	 * @param transition the transition
	 * @return boolean true if the display was successfully shown
	 */
	public static final ShowResult show(DisplayWindow window, Display display, Transition transition) {
		// check for null
		if (window == null) return ShowResult.DEVICE_NOT_VALID;
		
		// see if the window is currently visible
		if (!window.visible) {
			// showing the window depends on the translucency support
			if (window.device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
				// do nothing
			} else if (window.device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
				// set the opacity to fully opaque
				window.dialog.setOpacity(1.0f);
			} else {
				// set the dialog to visible
				window.dialog.setVisible(true);
			}
			// set the window visible flag
			window.visible = true;
		}
		
		// if you re-send the display then make sure it goes on
		// top of all other windows
		window.dialog.toFront();
		
		// set the display of the window
		window.pnlDisplay.send(display, transition);
		
		return ShowResult.NORMAL;
	}
	
	/**
	 * Returns true if the given {@link DisplayWindow} is valid.
	 * <p>
	 * Valid means that the display is still available.
	 * @param window the window
	 * @return boolean
	 */
	private static final boolean isValid(DisplayWindow window) {
		return WindowUtilities.isValid(window.device);
	}
	
	/**
	 * Invalidates the given {@link DisplayWindow}.
	 * <p>
	 * This should be called when the window's device is no longer valid.
	 * @param window the window
	 */
	private static final synchronized void invalidate(DisplayWindow window) {
		// the display is no longer valid so we need to remove it
		WINDOWS.remove(window.device.getIDstring());
		// set the window to invisible and release resources
		window.dialog.setVisible(false);
		window.dialog.dispose();
		// we also need to clear the cached GraphicsDevice from the GeneralSettings
		// so that when/if the device is restored it will get the new config
		GeneralSettings.getInstance().clearPrimaryDisplayCache();
	}
	
	// DisplayWindow class
	
	/** The window used to display the Display */
	private JDialog dialog;
	
	/** The device this display is for */
	private GraphicsDevice device;
	
	/** The component */
	private TransitionDisplayPanel pnlDisplay;
	
	/** True if the surface is visible */
	private boolean visible;
	
	/**
	 * Creates a new display frame for the given device.
	 * @param device the device
	 */
	private DisplayWindow(GraphicsDevice device) {
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
		GraphicsConfiguration gc = device.getDefaultConfiguration();
		// get its position and dimensions
		Rectangle r = gc.getBounds();
		
		// set the size
		Dimension size = new Dimension(r.width, r.height);
		this.dialog.setMinimumSize(size);
		this.dialog.setPreferredSize(size);
		// set the location
		this.dialog.setLocation(r.x, r.y);
		
		// we need to enable per-pixel translucency if available
		this.dialog.getRootPane().setOpaque(false);
		if (device.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
			this.dialog.setBackground(new Color(0, 0, 0, 0));
			this.dialog.setVisible(true);
			LOGGER.info("Per-pixel translucency supported (best).");
		} else if (device.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
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
		
		this.pnlDisplay = new TransitionDisplayPanel();
		container.add(this.pnlDisplay, BorderLayout.CENTER);
		
		// make sure the panel is resized to fit the layout
		this.dialog.pack();
		
		// TODO add nursery overlay
	}
}
