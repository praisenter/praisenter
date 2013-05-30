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
package org.praisenter.common.utilities;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

/**
 * Utility class to help working with windows.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public final class WindowUtilities {
	/** Hidden default constructor */
	private WindowUtilities() {}
	
	/**
	 * Returns the parent window for the given component.
	 * @param component the component
	 * @return Window
	 * @since 1.0.1
	 */
	public static final Window getParentWindow(Component component) {
		// get the parent frame
		Frame frame = JOptionPane.getFrameForComponent(component);
		// first check for a parent dialog component
		while (component != null) {
			component = component.getParent();
			if (component instanceof Dialog) {
				return (Dialog) component;
			}
		}
		// if nothing was found, then use the frame for the component
		return frame;
	}
	
	/**
	 * Returns a dimension for the given display mode.
	 * @param mode the display mode
	 * @return Dimension
	 */
	public static final Dimension getDimension(DisplayMode mode) {
		return new Dimension(mode.getWidth(), mode.getHeight());
	}
	
	/**
	 * Returns the list of screen devices for the current graphics environment.
	 * @return GraphicsDevice[]
	 */
	public static final GraphicsDevice[] getDevices() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return env.getScreenDevices();
	}
	
	/**
	 * Returns the default graphics device.
	 * @return GraphicsDevice
	 */
	public static final GraphicsDevice getDefaultDevice() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}
	
	/**
	 * Returns the first device that is not the default device.
	 * <p>
	 * If only one device is present, the default device is returned.
	 * @return GraphicsDevice
	 */
	public static final GraphicsDevice getSecondaryDevice() {
		// get all the screen devices
		GraphicsDevice[] devices = WindowUtilities.getDevices();
		// get the default screen device
		GraphicsDevice defaultDevice = WindowUtilities.getDefaultDevice();
		// check the length of the devices array
		if (devices.length > 1) {
			// if we have more than one display, then find
			// the first display that isn't the default
			for (GraphicsDevice device : devices) {
				if (device != defaultDevice) {
					return device;
				}
			}
		}
		// just return the default device then
		return defaultDevice;
	}
	
	/**
	 * Returns the GraphicsDevice that matches the given id.
	 * <p>
	 * Returns null if the device is not found.
	 * @param id the id
	 * @return GraphicsDevice
	 */
	public static final GraphicsDevice getDeviceForId(String id) {
		GraphicsDevice[] devices = WindowUtilities.getDevices();
		for (GraphicsDevice device : devices) {
			if (device.getIDstring().equals(id)) {
				return device;
			}
		}
		return null;
	}
	
	/**
	 * Returns true if the given GraphicsDevice instance is still valid.
	 * <p>
	 * GraphicsDevice objects may be stored for various reasons.  If the user
	 * removes or adds a screen the device can become invalid.  (What actually
	 * happens is that the device is reused and set to the primary display).
	 * <p>
	 * This method will check with the currently available devices to see
	 * if the given device still exists.
	 * @param device the device to validate
	 * @return boolean
	 */
	public static final boolean isValid(GraphicsDevice device) {
		GraphicsDevice[] devices = WindowUtilities.getDevices();
		for (GraphicsDevice cDevice : devices) {
			if (cDevice.getIDstring().equals(device.getIDstring())) {
				// determine if the bounds are the same
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a device name for the given device.
	 * <p>
	 * The format should be a string that contains {0} where 0 is the index of a value.
	 * The indices 0 through 4 respresent the device index, width, height, color depth,
	 * and refresh rate respectively.
	 * @param device the device
	 * @param index the index of the display
	 * @param format the format to use for the device name
	 * @return String
	 */
	public static final String getDeviceName(GraphicsDevice device, int index, String format) {
		DisplayMode mode = device.getDisplayMode();
		int rate = mode.getRefreshRate();
		if (rate <= 0) {
			// assume its 60 (mac os x and LCD monitors)
			rate = 60;
		}
		return MessageFormat.format(format,
				index + 1,
				mode.getWidth(),
				mode.getHeight(),
				mode.getBitDepth(),
				rate);
	}
	
	/**
     * Returns a GraphicsConfiguration that supports translucency for the given device.
     * <p>
     * If a configuration does not exist, the default configuration will be returned.
     * @param device the device
     * @return GraphicsConfiguration
     * @since 2.0.1
     */
    public static final GraphicsConfiguration getTranslucentConfiguration(GraphicsDevice device) {
        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        // check the default configuration for translucency
        if (configuration.isTranslucencyCapable()) {
            return configuration;
        }
        // if not, search through the other configurations
        for (GraphicsConfiguration gc : device.getConfigurations()) {
            if (gc.isTranslucencyCapable()) {
                // return the first one found
                return gc;
            }
        }
        // if we dont find any, then just return the default configuration
        return configuration;
    }
}
