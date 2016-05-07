package org.praisenter.javafx.screen;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javafx.collections.ObservableList;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class ScreenManager {
	public ScreenManager() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = ge.getScreenDevices();
		
		GraphicsConfiguration conf = devices[0].getDefaultConfiguration();
		for (GraphicsDevice device : devices) {
			if (device.getIDstring().equals(deviceId)) {
				conf = device.getDefaultConfiguration();
				break;
			}
		}
		
		ObservableList<Screen> screens = Screen.getScreens();
		for (Screen screen : screens) {
			if ((int)screen.getBounds().getMinX() == (int)conf.getBounds().getMinX() &&
				(int)screen.getBounds().getMinY() == (int)conf.getBounds().getMinY()) {
				return screen.
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
