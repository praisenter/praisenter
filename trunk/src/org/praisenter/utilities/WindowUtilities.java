package org.praisenter.utilities;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import javax.swing.JOptionPane;

/**
 * Utility class to help working with windows.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class WindowUtilities {
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
	public static final GraphicsDevice[] getScreenDevices() {
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
	 * @return GraphicsDevice
	 */
	public static final GraphicsDevice getSecondaryDevice() {
		// get all the screen devices
		GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
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
	 * @param id the id
	 * @return GraphicsDevice
	 */
	public static final GraphicsDevice getScreenDeviceForId(String id) {
		GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
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
		GraphicsDevice[] devices = WindowUtilities.getScreenDevices();
		for (GraphicsDevice cDevice : devices) {
			if (cDevice.getIDstring().equals(device.getIDstring())) {
				// determine if the bounds are the same
				return true;
			}
		}
		return false;
	}
}
