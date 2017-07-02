/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
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
package org.praisenter.javafx.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.async.AsyncTaskExecutor;
import org.praisenter.javafx.configuration.Display;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.javafx.configuration.Resolution;
import org.praisenter.javafx.configuration.ResolutionSet;
import org.praisenter.javafx.configuration.Setting;

import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * Represents the application's display screen manager.
 * <p>
 * This class manages the displays for presentation.
 * @author William Bittle
 * @version 3.0.0
 */
public final class DisplayManager {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The configuration */
	private final ObservableConfiguration configuration;
	
	/** The global executor */
	private final AsyncTaskExecutor executor;
	
	/** True if debug mode is enabled */
	private final boolean debugMode;
	
	// data (for now, only to outputs)
	
	/** The main display */
	private DisplayScreen main;
	
	/** The musician display */
	private DisplayScreen musician;
	
	/** True if the screens are being mutated */
	private boolean mutating = false;
	
	/**
	 * Minimal constructor.
	 * @param configuration the configuration
	 * @param executor the executor
	 */
	public DisplayManager(ObservableConfiguration configuration, AsyncTaskExecutor executor) {
		this.configuration = configuration;
		this.executor = executor;
		this.debugMode = configuration.getBoolean(Setting.APP_DEBUG_MODE, false);
	}
	
	/**
	 * Initializes the screen manager and returns true if automatic assignment of screens was performed.
	 * <p>
	 * NOTE: This method should be called on the Java FX UI thread.
	 * @return boolean
	 */
	public boolean initialize() {
		// listen for screen changes
		Screen.getScreens().addListener(new ListChangeListener<Screen>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Screen> c) {
				screensChanged();
			}
		});
		
		this.configuration.getSettings().addListener(new MapChangeListener<Setting, Object>() {
			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends Setting, ? extends Object> change) {
				if (change.getKey() == Setting.DISPLAY_OPERATOR ||
					change.getKey() == Setting.DISPLAY_MAIN ||
					change.getKey() == Setting.DISPLAY_MUSICIAN) {
					// make sure this isn't being called as a result of
					// the screens being changed at the OS level
					if (mutating) return;
					// update the display screens
					updateDisplays();
				}
			}
		});
		
		return screensChanged();
	}
	
	/**
	 * Releases any pre-allocated screens or any other resources.
	 */
	public void release() {
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
	}

	/**
	 * Returns the display that will be used for presenation.
	 * <p>
	 * Typically this will be the MAIN screen, but can be the
	 * OPERATOR or PRIMARY when the MAIN screen is not set
	 * or invalid.
	 * @return {@link Display}
	 */
	public Display getPresentationDisplay() {
		Display main = this.configuration.getObject(Setting.DISPLAY_MAIN, Display.class, null);
		Display operator = this.configuration.getObject(Setting.DISPLAY_OPERATOR, Display.class, null);
		Display primary = this.configuration.getObject(Setting.DISPLAY_PRIMARY, Display.class, null);
		if (main != null && main.getId() >= 0) {
			return main;
		} else if (operator != null && operator.getId() >= 0) {
			return operator;
		}
		return primary;
	}
	
	/**
	 * Helper method to perform the automatic screen assignment and adjustment when the screens
	 * change, move, added, etc.
	 * <p>
	 * Returns true if this process automatically assigns a screen.
	 * @return boolean
	 */
	private boolean screensChanged() {
		mutating = true;
		
		boolean screensChanged = false;
		Screen primary = Screen.getPrimary();
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int size = screens.size();
		
		// get the configured displays
		int n = this.configuration.getInt(Setting.DISPLAY_COUNT, -1);
		Display primaryDisplay = this.configuration.getObject(Setting.DISPLAY_PRIMARY, Display.class, null);
		Display operatorDisplay = this.configuration.getObject(Setting.DISPLAY_OPERATOR, Display.class, null);
		Display mainDisplay = this.configuration.getObject(Setting.DISPLAY_MAIN, Display.class, null);
		Display musicianDisplay = this.configuration.getObject(Setting.DISPLAY_MUSICIAN, Display.class, null);
		
		// log begin state
		LOGGER.debug("Current Screen Assignment:");
		LOGGER.debug("Screens:  {}", n);
		LOGGER.debug("Primary:  {}", primaryDisplay);
		LOGGER.debug("Operator: {}", operatorDisplay);
		LOGGER.debug("Main:     {}", mainDisplay);
		LOGGER.debug("Musician: {}", musicianDisplay);

		// if the number of screens changed, then log it
		// they may have only removed an unused screen
		if (n != -1 && n != size) {
			LOGGER.info("The number of screens has changed from {} to {}.", n, size);
		}
		
		// get the primary display state
		DisplayState primaryState = this.getScreenState(primaryDisplay, screens);
		
		// check if the primary screen moved, doesn't exist, or has not been assigned
		if (primaryState == DisplayState.POSITION_CHANGED || 
			primaryState == DisplayState.SCREEN_INDEX_DOESNT_EXIT ||
			primaryState == DisplayState.SCREEN_NOT_ASSIGNED) {
			// if any of these occur, we need to reset the screen assignment
			LOGGER.info("Resetting screen assignment.");
			primaryDisplay = null;
			operatorDisplay = null;
			mainDisplay = null;
			musicianDisplay = null;
		} else if (primaryState == DisplayState.RESOLUTION_CHANGED) {
			// just reset the primary display
			primaryDisplay = null;
		}
		
		// get the states for the other screens (now that the primary has been validated or invalidated)
		DisplayState operatorState = this.getScreenState(operatorDisplay, screens);
		DisplayState mainState = this.getScreenState(mainDisplay, screens);
		DisplayState musicianState = this.getScreenState(musicianDisplay, screens);
		
		// verify that the screens are still the way we left them
		if (operatorState == DisplayState.POSITION_CHANGED ||
			operatorState == DisplayState.SCREEN_INDEX_DOESNT_EXIT) {
			LOGGER.info("The operator display is invalid.");
			operatorDisplay = null;
		}
		
		if (mainState == DisplayState.POSITION_CHANGED ||
			mainState == DisplayState.RESOLUTION_CHANGED ||
			mainState == DisplayState.SCREEN_INDEX_DOESNT_EXIT ||
			mainState == DisplayState.SCREEN_NOT_ASSIGNED) {
			LOGGER.info("The main display is invalid.");
			mainDisplay = null;
			// we need to invalidate the musician assignment
			// if we have 2 or less screens
			if (screens.size() <= 2 && musicianDisplay != null) {
				LOGGER.info("The main display was invalid and the display count is 2 or less, unassigning the musician display.");
				musicianDisplay = null;
				screensChanged = true;
			}
			if (screens.size() <= 1 && operatorDisplay != null) {
				LOGGER.info("The main display was invalid and the display count is 1 or less, unassigning the operator display.");
				operatorDisplay = null;
				screensChanged = true;
			}
		}
		
		if (musicianState == DisplayState.POSITION_CHANGED ||
			musicianState == DisplayState.RESOLUTION_CHANGED ||
			musicianState == DisplayState.SCREEN_INDEX_DOESNT_EXIT) {
			LOGGER.info("The musician display is invalid.");
			musicianDisplay = null;
		}
		
		// now verify that no screen is reused
		Map<Integer, Boolean> assigned = new HashMap<>();
		
		// then the main display
		if (mainDisplay != null) {
			// always reserve the main display if it hasn't been invalidated
			assigned.put(mainDisplay.getId(), true);
		}
		
		// then the operator display
		if (operatorDisplay != null) {
			// reserve the operator display
			// so that the musician display doesn't take it
			// the operator display can be assigned to the same screen as the main display
			assigned.put(operatorDisplay.getId(), true);
		}
		
		// then the musician display
		if (musicianDisplay != null) {
			if (assigned.containsKey(musicianDisplay.getId())) {
				LOGGER.info("The display assigned to {} is already assigned. Removing assignment.", musicianDisplay);
				musicianDisplay = null;
			} else {
				assigned.put(musicianDisplay.getId(), true);
			}
		}
		
		// now, iterate all screens and make sure any unassigned screens are 
		// assigned if there are any displays to assign
		ResolutionSet resolutions = new ResolutionSet();
		resolutions.addAll(this.configuration.getResolutions());
		for (int i = 0; i < size; i++) {
			Screen screen = screens.get(i);
			Rectangle2D bounds = screen.getBounds();
			
			// add the resolution
			Resolution resolution = new Resolution(
					(int)bounds.getWidth(), 
					(int)bounds.getHeight());
			resolutions.add(resolution);
			
			// regardless always update the primary display
			if (primary.equals(screen)) {
				primaryDisplay = new Display(i, screen);
			}
			
			// the first one we come across will be the operator
			// display if it isn't already assigned
			if (operatorDisplay == null) {
				operatorDisplay = new Display(i, screen);
				screensChanged = true;
				LOGGER.info("Assigning the operator screen to " + operatorDisplay + ".");
			}
			
			// skip if already assigned
			if (assigned.containsKey(i)) {
				continue;
			}
			
			// set it to assigned
			assigned.put(i, true);
			
			// is the operator screen assigned?
			if (mainDisplay == null) {
				mainDisplay = new Display(i, screen);
				screensChanged = true;
				LOGGER.info("Assigning the main screen to " + mainDisplay + ".");
			// is the musician screen assigned?
			} else if (musicianDisplay == null) {
				musicianDisplay = new Display(i, screen);
				screensChanged = true;
				LOGGER.info("Assigning the musician screen to " + musicianDisplay + ".");
			}
		}
		
		// log new state
		LOGGER.debug("New Screen Assignment:");
		LOGGER.debug("Screens:  {}", size);
		LOGGER.debug("Primary:  {}", primaryDisplay);
		LOGGER.debug("Operator: {}", operatorDisplay);
		LOGGER.debug("Main:     {}", mainDisplay);
		LOGGER.debug("Musician: {}", musicianDisplay);
		
		// set the configuration
		this.configuration.createBatch()
			.setObject(Setting.DISPLAY_PRIMARY, primaryDisplay)
			.setObject(Setting.DISPLAY_MAIN, mainDisplay)
			.setObject(Setting.DISPLAY_OPERATOR, operatorDisplay)
			.setObject(Setting.DISPLAY_MUSICIAN, musicianDisplay)
			.setObject(Setting.DISPLAY_RESOLUTIONS, resolutions)
			.setInt(Setting.DISPLAY_COUNT, size)
		.commitBatch()
		.execute(this.executor);
		
		// update the displays
		this.updateDisplays();
		
		mutating = false;
		
		return screensChanged;
	}
	
	/**
	 * Updates the current set of screens for presentation based on the current screen assignment.
	 */
	private void updateDisplays() {
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		Display dDisplay = this.configuration.getObject(Setting.DISPLAY_MAIN, Display.class, null);
		Display mDisplay = this.configuration.getObject(Setting.DISPLAY_MUSICIAN, Display.class, null);

		// release any existing stages
		this.release();
		
		// create new ones
		LOGGER.info("Creating new displays.");
		if (dDisplay != null && dDisplay.getId() < screens.size()) {
			this.main = new DisplayScreen(dDisplay.getId(), ScreenRole.MAIN, screens.get(dDisplay.getId()), this.debugMode);
		}
		if (mDisplay != null && mDisplay.getId() < screens.size()) {
			this.musician = new DisplayScreen(mDisplay.getId(), ScreenRole.MUSICIAN, screens.get(mDisplay.getId()), this.debugMode);
		}
	}
	
	/**
	 * Returns the state of the given display.
	 * @param display the display
	 * @param screens the list of all screens
	 * @return {@link DisplayState}
	 */
	private DisplayState getScreenState(Display display, List<Screen> screens) {
		// if it's not assigned
		if (display == null || display.getId() < 0) {
			LOGGER.info("Display has not been assigned.");
			return DisplayState.SCREEN_NOT_ASSIGNED;
		}
		
		// check if the screen index still exists
		if (display.getId() >= screens.size()) {
			LOGGER.info("Display {} no longer exists.", display);
			return DisplayState.SCREEN_INDEX_DOESNT_EXIT;
		}
		
		// otherwise, it needs to be in the list of screens with the
		// same index and must have the same dimensions and position
		Screen screen = screens.get(display.getId());
		Rectangle2D bounds = screen.getBounds();
		if ((int)bounds.getMinX() != display.getX() ||
			(int)bounds.getMinY() != display.getY()) {
			LOGGER.info("Display {} position has changed.", display);
			return DisplayState.POSITION_CHANGED;
		}
		
		if ((int)bounds.getWidth() != display.getWidth() ||
			(int)bounds.getHeight() != display.getHeight()) {
			LOGGER.info("Display {} resolution has changed.", display);
			return DisplayState.RESOLUTION_CHANGED;
		}
		
		return DisplayState.VALID;
	}
}
