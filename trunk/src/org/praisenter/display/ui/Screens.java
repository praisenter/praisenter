package org.praisenter.display.ui;

import java.awt.GraphicsDevice;
import java.util.HashMap;
import java.util.Map;

import org.praisenter.settings.GeneralSettings;
import org.praisenter.utilities.WindowUtilities;

/**
 * Static class for managing {@link Screen}s that are shared among the
 * application.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Screens {
	/** The device to screen mapping */
	private static final Map<String, Screen> SCREENS = new HashMap<String, Screen>();
	
	// static interface
	
	/**
	 * Returns the instance of {@link Screen} for the given GraphicsDevice.
	 * <p>
	 * Returns null if the given GraphicsDevice is no longer valid.
	 * @param device the device
	 * @return {@link Screen}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	public static final Screen getScreen(GraphicsDevice device) {
		if (device == null) return null;
		// check if the given device is still valid
		boolean valid = WindowUtilities.isValid(device);
		// get the cached window using device id
		String id = device.getIDstring();
		// modify the windows map
		synchronized (SCREENS) {
			Screen window = SCREENS.get(id);
			// doing this will handle new devices being added
			if (window == null && valid) {
				// the device is valid so create a new window for it
				window = new Screen(device);
				SCREENS.put(id, window);
			} else if (window != null && !valid) {
				invalidate(window);
				return null;
			}
			return window;
		}
	}

	/**
	 * Invalidates the given {@link Screen}.
	 * <p>
	 * This should be called when the screen's device is no longer valid.
	 * @param window the {@link Screen} to invalidate
	 */
	private static final void invalidate(Screen window) {
		synchronized (SCREENS) {
			// the display is no longer valid so we need to remove it
			SCREENS.remove(window.device.getIDstring());
		}
		// set the window to invisible and release resources
		window.dialog.setVisible(false);
		window.dialog.dispose();
		// we also need to clear the cached GraphicsDevice from the GeneralSettings
		// so that when/if the device is restored it will get the new config
		GeneralSettings.getInstance().clearPrimaryDisplayCache();
	}
	
	/**
	 * Returns the primary {@link Screen} determined by the {@link GeneralSettings}.
	 * <p>
	 * Returns null if the primary GraphicsDevice is no longer valid.
	 * @return {@link Screen}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	public static final Screen getPrimary() {
		GeneralSettings settings = GeneralSettings.getInstance();
		GraphicsDevice device = settings.getPrimaryDisplay();
		return getScreen(device);
	}
	
	public static final NotificationOverlay getPrimaryNotificationOverlay() {
		GeneralSettings settings = GeneralSettings.getInstance();
		GraphicsDevice device = settings.getPrimaryDisplay();
		return new NotificationOverlay(device);
	}
}
