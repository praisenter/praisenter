/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.slide.ui.present;

import java.awt.GraphicsDevice;
import java.util.HashMap;
import java.util.Map;

import org.praisenter.preferences.Preferences;
import org.praisenter.utilities.WindowUtilities;

/**
 * Static class for managing {@link SlideWindow}s that are shared among the application.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class SlideWindows {
	/** The device to {@link SlideWindow} mapping */
	private static final Map<String, SlideWindow> WINDOWS = new HashMap<String, SlideWindow>();

	/** The device to {@link SlideWindow} mapping (for notification windows) */
	private static final Map<String, SlideWindow> NOTIFICATION_WINDOWS = new HashMap<String, SlideWindow>();
	
	// static interface
	
	/**
	 * Returns the instance of {@link SlideWindow} for the given GraphicsDevice.
	 * <p>
	 * Returns null if the given GraphicsDevice is no longer valid.
	 * @param device the device
	 * @param notification true if a notification window is desired
	 * @return {@link SlideWindow}
	 * @see WindowUtilities#isValid(GraphicsDevice)
	 */
	private static final SlideWindow getSlideWindow(GraphicsDevice device, boolean notification) {
		if (device == null) return null;
		// check if the given device is still valid
		boolean valid = WindowUtilities.isValid(device);
		// get the cached window using device id
		String id = device.getIDstring();
		
		Map<String, SlideWindow> windows = WINDOWS;
		if (notification) {
			windows = NOTIFICATION_WINDOWS;
		}
		
		// modify the windows map
		synchronized (windows) {
			SlideWindow window = windows.get(id);
			// doing this will handle new devices being added
			if (window == null && valid) {
				// the device is valid so create a new window for it
				if (notification) {
					window = new SlideWindow(device, false, true);
				} else {
					window = new SlideWindow(device, true, false);
				}
				windows.put(id, window);
			} else if (window != null && !valid) {
				// the device is no longer valid so we need to remove it
				windows.remove(window.device.getIDstring());
				// set the window to invisible and release resources
				window.setVisible(false);
				window.dispose();
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
		Preferences preferences = Preferences.getInstance();
		return getSlideWindow(preferences.getPrimaryOrDefaultDevice(), false);
	}
	
	/**
	 * Returns the primary {@link SlideWindow} determined by the {@link Preferences}.
	 * <p>
	 * Returns null if the primary GraphicsDevice is no longer valid.
	 * <p>
	 * This method returns a window specifically for notifications.
	 * @return {@link SlideWindow}
	 */
	public static final SlideWindow getPrimaryNotificationWindow() {
		Preferences preferences = Preferences.getInstance();
		return getSlideWindow(preferences.getPrimaryOrDefaultDevice(), true);
	}
	
	/**
	 * Disposes of all {@link SlideWindow}s.
	 */
	public static final void disposeWindows() {
		for (SlideWindow window : WINDOWS.values()) {
			if (window != null) {
				window.setVisible(false);
				window.dispose();
			}
		}
		for (SlideWindow window : NOTIFICATION_WINDOWS.values()) {
			if (window != null) {
				window.setVisible(false);
				window.dispose();
			}
		}
	}
	
	/**
	 * Hidden constructor.
	 */
	private SlideWindows() {}
}
