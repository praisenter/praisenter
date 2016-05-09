package org.praisenter.javafx.screen;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.stage.Screen;

import org.praisenter.javafx.configuration.ScreenMapping;
import org.praisenter.javafx.configuration.ScreenRole;

public final class ScreenManager {
	private final ObservableMap<String, DisplayScreen> displays;
	
	public ScreenManager() {
		this.displays = FXCollections.observableHashMap();
	}
	
	public void setup(List<ScreenMapping> mapping) {
		// release any existing displays
		for (DisplayScreen ds : displays.values()) {
			ds.release();
		}
		displays.clear();
		
		// map the roles to their device ids
		Map<String, ScreenRole> roles = new HashMap<String, ScreenRole>();
		for (ScreenMapping map : mapping) {
			roles.put(map.getId(), map.getRole());
		}
		
		// get the screens
		ObservableList<Screen> screens = Screen.getScreens();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();
		
		for (GraphicsDevice device : devices) {
			GraphicsConfiguration conf = device.getDefaultConfiguration();
			// get the screen's role
			ScreenRole role = roles.get(device.getIDstring());
			// is the role something other than none?
			if (role != null && role != ScreenRole.NONE) {
				// if so, find the respective screen for the device
				for (Screen screen : screens) {
					if ((int)screen.getBounds().getMinX() == (int)conf.getBounds().getMinX() &&
						(int)screen.getBounds().getMinY() == (int)conf.getBounds().getMinY()) {
						// setup a reusable stage for it
						displays.put(device.getIDstring(), new DisplayScreen(device.getIDstring(), role, screen));
					}
				}
			}
		}
	}
	
	public void send() {
		// sends a slide to a particular output
	}
	
	public void close() {
		// will close all the stages and clean up
	}
}
