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
import org.praisenter.javafx.configuration.Resolutions;
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
	 * Initializes the screen manager.
	 * <p>
	 * NOTE: This method should be called on the Java FX UI thread.
	 */
	public void initialize() {
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
		
		screensChanged();
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
		Display main = this.configuration.getObject(Setting.DISPLAY_MAIN, null);
		Display operator = this.configuration.getObject(Setting.DISPLAY_OPERATOR, null);
		Display primary = this.configuration.getObject(Setting.DISPLAY_PRIMARY, null);
		if (main != null && main.getId() >= 0) {
			return main;
		} else if (operator != null && operator.getId() >= 0) {
			return operator;
		}
		return primary;
	}
	
	private void screensChanged() {
		mutating = true;
		
		Screen primary = Screen.getPrimary();
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int size = screens.size();
		
		// get the configured displays
		Display pDisplay = this.configuration.getObject(Setting.DISPLAY_PRIMARY, null);
		Display oDisplay = this.configuration.getObject(Setting.DISPLAY_OPERATOR, null);
		Display dDisplay = this.configuration.getObject(Setting.DISPLAY_MAIN, null);
		Display mDisplay = this.configuration.getObject(Setting.DISPLAY_MUSICIAN, null);
		
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
		// then the main display
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
		Resolutions resolutions = new Resolutions();
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
		this.configuration.createBatch()
			.setObject(Setting.DISPLAY_PRIMARY, pDisplay)
			.setObject(Setting.DISPLAY_MAIN, dDisplay)
			.setObject(Setting.DISPLAY_OPERATOR, oDisplay)
			.setObject(Setting.DISPLAY_MUSICIAN, mDisplay)
			.setObject(Setting.DISPLAY_RESOLUTIONS, resolutions)
		.commitBatch()
		.execute(this.executor);
		
		// update the displays
		this.updateDisplays();
		
		mutating = false;
	}
	
	private void updateDisplays() {
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		Display dDisplay = this.configuration.getObject(Setting.DISPLAY_MAIN, null);
		Display mDisplay = this.configuration.getObject(Setting.DISPLAY_MUSICIAN, null);
		
		LOGGER.info("Releasing existing displays.");
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
}
