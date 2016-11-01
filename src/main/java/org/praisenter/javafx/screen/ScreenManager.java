package org.praisenter.javafx.screen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.xml.XmlIO;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public final class ScreenManager {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ScreenConfiguration configuration;
	
	private DisplayScreen main;
	private DisplayScreen musician;
	
	private boolean mutating = false;
	
	public ScreenManager() {
		// load the screen config
		this.configuration = this.loadConfiguration();
	}
	
	public void initialize() {
		// listen for screen changes
		Screen.getScreens().addListener(new ListChangeListener<Screen>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Screen> c) {
				screensChanged();
			}
		});
		
		// save the screen config whenever something changes it
		InvalidationListener listener = obs -> {
			if (mutating) return;
			
			// save the configuration
			this.saveConfiguration();
			
			// update the display screens
			this.updateDisplays();
		};
		this.configuration.musicianScreenProperty().addListener(listener);
		this.configuration.operatorScreenProperty().addListener(listener);
		this.configuration.mainScreenProperty().addListener(listener);
		this.configuration.primaryScreenProperty().addListener(listener);
		this.configuration.getResolutions().addListener(listener);
		
		screensChanged();
	}
	
	private void screensChanged() {
		mutating = true;
		
		Screen primary = Screen.getPrimary();
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int size = screens.size();
		
		// get the configured displays
		Display pDisplay = this.configuration.getPrimaryScreen();
		Display oDisplay = this.configuration.getOperatorScreen();
		Display dDisplay = this.configuration.getMainScreen();
		Display mDisplay = this.configuration.getMusicianScreen();
		
		// verify that the primary display is still the same
		boolean primaryChanged = false;
		if (!isValid(pDisplay, screens)) {
			// this indicates that all of them should be invalidated
			primaryChanged = true;
			LOGGER.info("The primary screen changed. Resetting screen assignment.");
		}
		
		// verify that the screens are still the way we left them
		if (primaryChanged || !isValid(oDisplay, screens)) {
			oDisplay = null;
		}
		if (primaryChanged || !isValid(dDisplay, screens)) {
			dDisplay = null;
		}
		if (primaryChanged || !isValid(mDisplay, screens)) {
			mDisplay = null;
		}
		
		// now verify that no screen is reused
		Map<Integer, Boolean> assigned = new HashMap<>();
		// favor the operator screen first
		if (oDisplay != null) {
			assigned.put(oDisplay.getId(), true);
		}
		// then the primary display
		if (dDisplay != null) {
			if (assigned.containsKey(dDisplay.getId())) {
				LOGGER.info("The display assigned to " + dDisplay + " is already assigned. Removing assignment.");
				dDisplay = null;
			} else {
				assigned.put(dDisplay.getId(), true);
			}
		}
		// then the musician display
		if (mDisplay != null) {
			if (assigned.containsKey(mDisplay.getId())) {
				LOGGER.info("The display assigned to " + mDisplay + " is already assigned. Removing assignment.");
				mDisplay = null;
			} else {
				assigned.put(mDisplay.getId(), true);
			}
		}
		
		// now we need to assign displays if they aren't already
		for (int i = 0; i < size; i++) {
			Screen screen = screens.get(i);
			Rectangle2D bounds = screen.getBounds();
			this.configuration.getResolutions().add(new Resolution((int)bounds.getWidth(), (int)bounds.getHeight()));
			
			// regardless always update the primary display
			if (primary.equals(screen)) {
				pDisplay = new Display(i, screen);
			}
			
			// skip if already assigned
			if (assigned.containsKey(i)) {
				continue;
			}
			
			// set it to assigned
			assigned.put(i, true);
			
			// check for the primary screen
			if (oDisplay == null) {
				// set it to the primary screen index
				oDisplay = new Display(i, screen);
				LOGGER.info("Assigning the operator screen to " + oDisplay + ".");
			} else if (dDisplay == null) {
				dDisplay = new Display(i, screen);
				LOGGER.info("Assigning the main screen to " + dDisplay + ".");
			} else if (mDisplay == null) {
				mDisplay = new Display(i, screen);
				LOGGER.info("Assigning the musician screen to " + mDisplay + ".");
			}
		}
		
		// set the configuration
		this.configuration.setPrimaryScreen(pDisplay);
		this.configuration.setMainScreen(dDisplay);
		this.configuration.setOperatorScreen(oDisplay);
		this.configuration.setMusicianScreen(mDisplay);
		
		// save the new configuration
		this.saveConfiguration();
		
		// update the displays
		this.updateDisplays();
		
		mutating = false;
	}
	
	private void updateDisplays() {
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		Display dDisplay = this.configuration.getMainScreen();
		Display mDisplay = this.configuration.getMusicianScreen();
		
		LOGGER.info("Releasing existing displays.");
		// release any existing stages
		if (this.main != null) {
			this.main.release();
			this.main = null;
		}
		if (this.musician != null) {
			this.musician.release();
			this.musician = null;
		}
		
		// create new ones
		LOGGER.info("Creating new displays.");
		if (dDisplay != null && dDisplay.getId() < screens.size()) {
			this.main = new DisplayScreen(dDisplay.getId(), ScreenRole.MAIN, screens.get(dDisplay.getId()));
		}
		if (mDisplay != null && mDisplay.getId() < screens.size()) {
			this.musician = new DisplayScreen(mDisplay.getId(), ScreenRole.MUSICIAN, screens.get(mDisplay.getId()));
		}
	}
	
	private boolean isValid(Display display, List<Screen> screens) {
		// if it's not assigned, then its valid by default
		if (display == null || display.getId() < 0 || screens.size() <= display.getId()) {
			return true;
		}
		
		// otherwise, it needs to be in the list of screens with the
		// same index and must have the same dimensions
		Screen screen = screens.get(display.getId());
		Rectangle2D bounds = screen.getBounds();
		if ((int)bounds.getMinX() == display.getX() &&
			(int)bounds.getMinY() == display.getY() &&
			(int)bounds.getWidth() == display.getWidth() &&
			(int)bounds.getHeight() == display.getHeight()) {
			return true;
		}
		
		LOGGER.info("Display " + display + " no longer valid. It's resolution or position has changed.");
		return false;
	}
	
	private ScreenConfiguration loadConfiguration() {
		Path path = Paths.get(Constants.SCREENS_ABSOLUTE_FILE_PATH);
		if (Files.exists(path)) {
			try {
				return XmlIO.read(path, ScreenConfiguration.class);
			} catch (Exception ex) {
				LOGGER.info("Failed to load configuration. Using default configuration.", ex);
			}
		} else {
			LOGGER.info("No configuration found. Using default configuration.");
		}
		
		ScreenConfiguration conf = new ScreenConfiguration();
		// add the default resolutions
		conf.getResolutions().addAll(Arrays.asList(Resolution.DEFAULT_RESOLUTIONS));
		
		this.saveConfiguration();
		
		return conf;
	}
	
	private void saveConfiguration() {
		Path path = Paths.get(Constants.SCREENS_ABSOLUTE_FILE_PATH);
		try {
			XmlIO.save(path, this.configuration);
			LOGGER.info("Screen configuration saved.");
		} catch (Exception ex) {
			LOGGER.warn("Failed to save new configuration file.", ex);
		}
	}
	
	// public interface
	
	public ScreenConfiguration getScreenConfiguration() {
		return this.configuration;
	}
}
