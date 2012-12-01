package org.praisenter.slide.ui.present;

import java.awt.GraphicsDevice;
import java.util.HashMap;
import java.util.Map;

import org.praisenter.preferences.Preferences;
import org.praisenter.utilities.WindowUtilities;

/**
 * Static class for managing {@link SlideWindow}s that are shared among the application.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlideWindows {
	/** The device to {@link SlideWindow} mapping */
	private static final Map<String, SlideWindow> WINDOWS = new HashMap<String, SlideWindow>();

	// static interface
	
	/**
	 * Returns the instance of {@link SlideWindow} for the given GraphicsDevice.
	 * <p>
	 * Returns null if the given GraphicsDevice is no longer valid.
	 * @param device the device
	 * @return {@link SlideWindow}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	private static final SlideWindow getSlideWindow(GraphicsDevice device) {
		if (device == null) return null;
		// check if the given device is still valid
		boolean valid = WindowUtilities.isValid(device);
		// get the cached window using device id
		String id = device.getIDstring();
		// modify the windows map
		synchronized (WINDOWS) {
			SlideWindow window = WINDOWS.get(id);
			// doing this will handle new devices being added
			if (window == null && valid) {
				// the device is valid so create a new window for it
				window = new SlideWindow(device);
				WINDOWS.put(id, window);
			} else if (window != null && !valid) {
				// the device is no longer valid so we need to remove it
				WINDOWS.remove(window.device.getIDstring());
				// set the window to invisible and release resources
				window.dialog.setVisible(false);
				window.dialog.dispose();
				return null;
			}
			return window;
		}
	}

	/**
	 * Returns the primary {@link SlideWindow} determined by the {@link Preferences}.
	 * <p>
	 * Returns null if the primary GraphicsDevice is no longer valid.
	 * @return {@link SlideWindow}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	public static final SlideWindow getPrimarySlideWindow() {
		GraphicsDevice device = WindowUtilities.getSecondaryDevice();
		Preferences preferences = Preferences.getInstance();
		device = WindowUtilities.getScreenDeviceForId(preferences.getPrimaryDeviceId());
		return getSlideWindow(device);
	}
	
	/**
	 * Hidden constructor.
	 */
	private SlideWindows() {}
}
