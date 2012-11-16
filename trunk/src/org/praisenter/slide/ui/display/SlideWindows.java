package org.praisenter.slide.ui.display;

import java.awt.GraphicsDevice;
import java.util.HashMap;

import org.praisenter.settings.GeneralSettings;
import org.praisenter.utilities.WindowUtilities;

/**
 * Static class for managing {@link SlideWindow}s that are shared among the
 * application.
 * @param <E> the {@link SlideWindow} type
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings("serial")
public abstract class SlideWindows<E extends SlideWindow<?>> extends HashMap<String, E> {
	/** The device to {@link StandardSlideWindow} mapping */
	private static final SlideWindows<StandardSlideWindow> STANDARD_DISPLAY_WINDOWS = new SlideWindows<StandardSlideWindow>() {
		@Override
		protected StandardSlideWindow createDisplayWindow(GraphicsDevice device) {
			return new StandardSlideWindow(device);
		}
	};
	
	// static interface
	
	/**
	 * Returns the instance of {@link SlideWindow} for the given GraphicsDevice.
	 * <p>
	 * Returns null if the given GraphicsDevice is no longer valid.
	 * @param device the device
	 * @param windows the mapping of cached display windows
	 * @return {@link SlideWindow}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	private static final <E extends SlideWindow<?>> E getSlideWindow(GraphicsDevice device, SlideWindows<E> windows) {
		if (device == null) return null;
		// check if the given device is still valid
		boolean valid = WindowUtilities.isValid(device);
		// get the cached window using device id
		String id = device.getIDstring();
		// modify the windows map
		synchronized (windows) {
			E window = windows.get(id);
			// doing this will handle new devices being added
			if (window == null && valid) {
				// the device is valid so create a new window for it
				window = windows.createDisplayWindow(device);
				windows.put(id, window);
			} else if (window != null && !valid) {
				// the display is no longer valid so we need to remove it
				windows.remove(window.device.getIDstring());
				// set the window to invisible and release resources
				window.dialog.setVisible(false);
				window.dialog.dispose();
				// we also need to clear the cached GraphicsDevice from the GeneralSettings
				// so that when/if the device is restored it will get the new config
				GeneralSettings.getInstance().clearPrimaryDisplayCache();
				
				return null;
			}
			return window;
		}
	}

	/**
	 * Returns the primary {@link SlideWindow} determined by the {@link GeneralSettings}.
	 * <p>
	 * Returns null if the primary GraphicsDevice is no longer valid.
	 * @return {@link SlideWindow}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	public static final StandardSlideWindow getPrimarySlideWindow() {
		GeneralSettings settings = GeneralSettings.getInstance();
		GraphicsDevice device = settings.getPrimaryDisplay();
		return getSlideWindow(device, STANDARD_DISPLAY_WINDOWS);
	}
	
	/**
	 * Hidden constructor.
	 */
	private SlideWindows() {}
	
	/**
	 * Method to create a display of the given type.
	 * @param device the device to create the display for
	 * @return E
	 */
	protected abstract E createDisplayWindow(GraphicsDevice device);
}
