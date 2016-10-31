package org.praisenter.javafx.screen;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.xml.XmlIO;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.stage.Screen;

public final class ScreenManager {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ScreenConfiguration configuration;
	
	private DisplayScreen primary;
	private DisplayScreen musician;
	private DisplayScreen notification;
	
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
				System.out.println("Screens changed.");
				screensChanged();
			}
		});
		
		// save the screen config whenever something changes it
		InvalidationListener listener = obs -> {
			if (mutating) return;
			this.saveConfiguration();
		};
		this.configuration.musicianScreenIdProperty().addListener(listener);
		this.configuration.operatorScreenIdProperty().addListener(listener);
		this.configuration.primaryScreenIdProperty().addListener(listener);
		this.configuration.getResolutions().addListener(listener);
		
		screensChanged();
	}
	
	private void screensChanged() {
		mutating = true;
		
		Screen primary = Screen.getPrimary();
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int size = screens.size();
		
		// in the event that the screens change the original configuration
		// is discarded and we need to re-auto-detect the configuration
		if (this.configuration.getOperatorScreenId() >= size) {
			this.configuration.setOperatorScreenId(ScreenConfiguration.SCREEN_NOT_AVAILABLE);
		}
		if (this.configuration.getPrimaryScreenId() >= size) {
			this.configuration.setPrimaryScreenId(ScreenConfiguration.SCREEN_NOT_AVAILABLE);
		}
		if (this.configuration.getMusicianScreenId() >= size) {
			this.configuration.setMusicianScreenId(ScreenConfiguration.SCREEN_NOT_AVAILABLE);
		}
		
		for (int i = 0; i < size; i++) {
			Screen screen = screens.get(i);
			this.configuration.getResolutions().add(new Resolution((int)screen.getBounds().getWidth(), (int)screen.getBounds().getHeight()));
			
			if (this.configuration.getOperatorScreenId() == i || this.configuration.getPrimaryScreenId() == i) {
				continue;
			}
			
			// check for the primary screen
			if (this.configuration.getOperatorScreenId() == ScreenConfiguration.SCREEN_NOT_AVAILABLE && screen.equals(primary)) {
				// set it to the primary screen index
				this.configuration.setOperatorScreenId(i);
			} else if (this.configuration.getPrimaryScreenId() == ScreenConfiguration.SCREEN_NOT_AVAILABLE) {
				this.configuration.setPrimaryScreenId(i);
			} else if (this.configuration.getMusicianScreenId() == ScreenConfiguration.SCREEN_NOT_AVAILABLE) {
				this.configuration.setMusicianScreenId(i);
			}
		}
		
		// make sure the primary screen was set
		if (this.configuration.getPrimaryScreenId() == ScreenConfiguration.SCREEN_NOT_AVAILABLE) {
			this.configuration.setPrimaryScreenId(this.configuration.getOperatorScreenId());
		}
		
		// save the new configuration
		this.saveConfiguration();
		
		// create display stages for each screen in use
		if (this.primary != null) {
			this.primary.release();
			this.primary = null;
		}
		if (this.notification != null) {
			this.notification.release();
			this.notification = null;
		}
		Screen ps = this.configuration.getPrimaryScreenId() != ScreenConfiguration.SCREEN_NOT_AVAILABLE ? screens.get(this.configuration.getPrimaryScreenId()) : null;
		if (ps != null) {
			this.primary = new DisplayScreen(this.configuration.getPrimaryScreenId(), ScreenRole.PRESENTATION, ps);
			this.notification = new DisplayScreen(this.configuration.getPrimaryScreenId(), ScreenRole.PRESENTATION, ps);
		}
		
		if (this.musician != null) {
			this.musician.release();
			this.musician = null;
		}
		Screen ms = this.configuration.getMusicianScreenId() != ScreenConfiguration.SCREEN_NOT_AVAILABLE ? screens.get(this.configuration.getMusicianScreenId()) : null;
		if (ms != null) {
			this.musician = new DisplayScreen(this.configuration.getMusicianScreenId(), ScreenRole.MUSICIAN, ms);
		}
		
		mutating = false;
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
		} catch (Exception ex) {
			LOGGER.warn("Failed to save new configuration file.", ex);
		}
	}
	
	// public interface
	
	public ScreenConfiguration getScreenConfiguration() {
		return this.configuration;
	}
}
