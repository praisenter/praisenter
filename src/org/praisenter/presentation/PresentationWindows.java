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
package org.praisenter.presentation;

import java.awt.GraphicsDevice;
import java.util.HashMap;
import java.util.Map;

import org.praisenter.common.utilities.WindowUtilities;

/**
 * Static class for managing {@link PresentationWindow}s that are shared among the application.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public final class PresentationWindows {
	/** The device to {@link PresentationWindow} mapping for fullscreen windows */
	private static final Map<String, PresentationWindow> FULLSCREEN = new HashMap<String, PresentationWindow>();

	/** The device to {@link PresentationWindow} mapping for non-fullscreen windows */
	private static final Map<String, PresentationWindow> WINDOWED = new HashMap<String, PresentationWindow>();
	
	// public interface

	/**
	 * Initializes the {@link PresentationWindow}s for faster display.
	 * <p>
	 * This method does not need to be called before using this class, but should be to improve performance.
	 * <p>
	 * This method should be called on the EDT.
	 * @param deviceIds the devices to initialize
	 */
	protected static final void initialize(String... deviceIds) {
		// dispose of any existing windows
		dispose();
		// we want to initialize devices passed
		for (String id : deviceIds) {
			getPresentationWindow(id, PresentationWindowType.FULLSCREEN);
			getPresentationWindow(id, PresentationWindowType.WINDOWED);
		}
	}
	
	/**
	 * Returns a {@link PresentationWindow} for the given device.
	 * @param device the device
	 * @param type the window type
	 * @return {@link PresentationWindow}
	 */
	protected static final PresentationWindow getPresentationWindow(GraphicsDevice device, PresentationWindowType type) {
		String id = null;
		if (device != null) {
			id = device.getIDstring();
		}
		return getPresentationWindow(id, type);
	}

	/**
	 * Returns a {@link PresentationWindow} for the given device id.
	 * <p>
	 * Returns the secondary device, or primary if a second doesn't exist, if the given device id is null or
	 * not found.
	 * <p>
	 * Returns null if no device exists.
	 * @param deviceId the device id
	 * @param type the window type
	 * @return {@link PresentationWindow}
	 */
	protected static final PresentationWindow getPresentationWindow(String deviceId, PresentationWindowType type) {
		// get the graphics device
		GraphicsDevice device = null;
		if (deviceId == null) {
			// if we were given a null device id, then return the default device for the give type
			device = WindowUtilities.getSecondaryDevice();
			deviceId = device.getIDstring();
		} else {
			// get the graphics device
			device = WindowUtilities.getScreenDeviceForId(deviceId);
			// check if the device exists
			if (device == null) {
				// see if the we have any windows for it and remove them
				// and release any resources they may be using
				PresentationWindow window = FULLSCREEN.remove(deviceId);
				if (window != null) {
					window.setVisible(false);
					window.dispose();
				}
				// attempt to return a default device
				device = WindowUtilities.getSecondaryDevice();
				deviceId = device.getIDstring();
			}
		}
		// one final check for null
		if (device == null || deviceId == null) {
			return null;
		}
		
		Map<String, PresentationWindow> windows = FULLSCREEN;
		if (type == PresentationWindowType.WINDOWED) {
			windows = WINDOWED;
		}
		
		// modify the windows map
		synchronized (windows) {
			PresentationWindow window = windows.get(deviceId);
			// doing this will handle new devices being added
			if (window == null) {
				// the device is valid so create a new window for it
				if (type == PresentationWindowType.WINDOWED) {
					window = new PresentationWindow(device, false, true);
				} else {
					window = new PresentationWindow(device, true, false);
				}
				windows.put(deviceId, window);
			}
			return window;
		}
	}
	
	/**
	 * Returns the {@link PresentationWindow} for the given event.
	 * <p>
	 * Depending on the event window configuration, this method will return the same window that another
	 * event having the same window configuration returned.
	 * @param event the event
	 * @return {@link PresentationWindow}
	 */
	protected static final PresentationWindow getPresentationWindowForEvent(PresentationEvent event) {
		PresentationEventConfiguration configuration = event.getConfiguration();
		return PresentationWindows.getPresentationWindow(
				configuration.presentationWindowDeviceId, 
				configuration.presentationWindowType);
	}
	
	/**
	 * Disposes of all {@link PresentationWindow}s.
	 * <p>
	 * You can still use this class after this has been called.
	 */
	protected static final void dispose() {
		synchronized (FULLSCREEN) {
			for (PresentationWindow window : FULLSCREEN.values()) {
				if (window != null) {
					window.setVisible(false);
					window.dispose();
				}
			}
			FULLSCREEN.clear();
		}
		
		synchronized (WINDOWED) {
			for (PresentationWindow window : WINDOWED.values()) {
				if (window != null) {
					window.setVisible(false);
					window.dispose();
				}
			}
			WINDOWED.clear();
		}
	}
	
	/**
	 * Hidden constructor.
	 */
	private PresentationWindows() {}
}
