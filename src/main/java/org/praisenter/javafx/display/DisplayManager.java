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
package org.praisenter.javafx.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.configuration.Display;
import org.praisenter.configuration.DisplayRole;
import org.praisenter.configuration.DisplayList;
import org.praisenter.configuration.Setting;
import org.praisenter.javafx.async.AsyncTaskExecutor;
import org.praisenter.javafx.configuration.ObservableConfiguration;
import org.praisenter.javafx.controls.Alerts;
import org.praisenter.resources.translations.Translations;

import javafx.collections.ListChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
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
	
	private final List<DisplayTarget> screens;
	
	/** True if the screens are being mutated */
	private boolean mutating = false;
	
	private Alert screenChangedWarning = null;
	
	/**
	 * Minimal constructor.
	 * @param configuration the configuration
	 * @param executor the executor
	 */
	public DisplayManager(ObservableConfiguration configuration, AsyncTaskExecutor executor) {
		this.configuration = configuration;
		this.executor = executor;
		this.debugMode = configuration.getBoolean(Setting.APP_DEBUG_MODE, false);
		this.screens = new ArrayList<>();
	}
	
	/**
	 * Initializes the screen manager.
	 * <p>
	 * NOTE: This method should be called on the Java FX UI thread.
	 * @param scene the scene to use as the parent for showing screen changed messages
	 */
	public void initialize(Scene scene) {
		// listen for screen changes
		Screen.getScreens().addListener(new ListChangeListener<Screen>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Screen> c) {
				DesktopState state = screensChanged();
				if (state != DesktopState.NO_CHANGE) {
					notifyOfScreenAssignmentChange(scene, state);
				}
			}
		});
		
		this.configuration.getDisplays().addListener(new ListChangeListener<Display>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Display> c) {
				// make sure this isn't being called as a result of
				// the screens being changed at the OS level
				if (mutating) return;
				// update the display screens
				screensChanged();
			}
		});
		
		DesktopState state = screensChanged();
		if (state != DesktopState.NO_CHANGE) {
			notifyOfScreenAssignmentChange(scene, state);
		}
	}
	
	/**
	 * Shows a non-blocking information dialog notifying the user that we detected a change in the
	 * screens and made an automatic adjustment to the screen assignments and that they should review
	 * by going to Preferences.
	 * @param scene the scene for modality
	 * @param state the state
	 */
	private void notifyOfScreenAssignmentChange(Scene scene, DesktopState state) {
		// close it if there's already one showing
		if (this.screenChangedWarning != null && this.screenChangedWarning.isShowing()) {
			this.screenChangedWarning.close();
		}
		// show the new one
		this.screenChangedWarning = Alerts.info(
				scene.getWindow(),
				Modality.WINDOW_MODAL,
				state == DesktopState.NO_INITIAL_CONFIGURATION ? Translations.get("init.displaysSet.title") : Translations.get("init.displaysChanged.title"), 
				state == DesktopState.NO_INITIAL_CONFIGURATION ? Translations.get("init.displaysSet.header") : Translations.get("init.displaysChanged.header"),
				state == DesktopState.NO_INITIAL_CONFIGURATION ? Translations.get("init.displaysSet.content") : Translations.get("init.displaysChanged.content"));
		this.screenChangedWarning.show();
	}
	
	/**
	 * Releases any pre-allocated screens or any other resources.
	 */
	public void release() {
		LOGGER.info("Releasing existing displays.");
		for (DisplayTarget screen : this.screens) {
			screen.release();
		}
		this.screens.clear();
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
		List<Display> displays = this.configuration.getDisplays();
		for (Display display : displays) {
			if (display.getRole() == DisplayRole.MAIN) {
				return display;
			}
		}
		Screen primary = Screen.getPrimary();
		Rectangle2D bounds = primary.getBounds();
		return new Display(-1, null, "Primary", (int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
	}
	
	/**
	 * Helper method to perform the automatic screen assignment and adjustment when the screens
	 * change, move, added, etc.
	 * <p>
	 * Returns true if this process automatically assigns a screen.
	 * @return DisplayCollectionState
	 */
	private DesktopState screensChanged() {
		mutating = true;
		
		DesktopState whatHappened = DesktopState.NO_CHANGE;
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int size = screens.size();
		
		// get the configured displays
		int n = this.configuration.getInt(Setting.DISPLAY_COUNT, -1);
		List<Display> displays = new ArrayList<Display>(this.configuration.getDisplays());
		DisplayList ds = this.configuration.getObject(Setting.DISPLAY_ASSIGNMENTS, DisplayList.class, new DisplayList());
		
		LOGGER.info("Current Screen Assignment: ");
		for (Display display : displays) {
			LOGGER.info(display);
		}
		
		// check if the screen count changed
		if (n != size) {
			whatHappened = size < n ? DesktopState.DISPLAY_COUNT_DECREASED : DesktopState.DISPLAY_COUNT_INCREASED;
		}
		
		if (n == -1) {
			// screens have never been assigned so auto-assign them
			for (int i = 0; i < size; i++) {
				DisplayRole role = DisplayRole.NONE;
				if (i == 0 && size == 1) role = DisplayRole.MAIN;
				if (i == 0 && size == 2) role = DisplayRole.NONE;
				if (i == 1 && size == 2) role = DisplayRole.MAIN;
				if (i == 2 && size >= 3) role = DisplayRole.TELEPROMPT;
				if (i > 2) role = DisplayRole.OTHER;
				Rectangle2D bounds = screens.get(i).getBounds();
				Display display = new Display(i, role, role == DisplayRole.OTHER ? role.toString() + (i - 2) : role.toString(), (int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
				ds.add(display);
				if (role != DisplayRole.NONE) {
					this.screens.add(new DisplayTarget(display, this.debugMode));
				}
			}
			whatHappened = DesktopState.NO_INITIAL_CONFIGURATION;
		} else {
			// just verify each display's state
			for (final Display display : displays) {
				Display newDisplay = display;
				LOGGER.debug("Before: " + display);
				DisplayState state = this.getDisplayState(display, screens);
				Optional<DisplayTarget> screen = this.screens.stream().filter(s -> s.getDisplay().getId() == display.getId()).findFirst();
				if (state == DisplayState.SCREEN_INDEX_DOESNT_EXIST) {
					// then remove the display
					ds.remove(display);
					whatHappened = DesktopState.DISPLAY_COUNT_DECREASED;
					this.screens.removeIf(s -> s.getDisplay().getId() == display.getId());
					newDisplay = null;
				} else if (state == DisplayState.POSITION_CHANGED) {
					// update the display screen
					newDisplay = display.withBounds(screens.get(display.getId()).getBounds());
					ds.remove(display);
					ds.add(newDisplay);
					whatHappened = DesktopState.DISPLAY_POSITION_CHANGED;
				} else if (state == DisplayState.RESOLUTION_CHANGED) {
					// update the display screen
					newDisplay = display.withBounds(screens.get(display.getId()).getBounds());
					ds.remove(display);
					ds.add(newDisplay);
				} else if (display.getRole() == DisplayRole.NONE) {
					newDisplay = null;
				}
				
				if (newDisplay != null) {
					if (newDisplay.getRole() != DisplayRole.NONE) {
						if (screen.isPresent()) {
							screen.get().setDisplay(newDisplay);
						} else {
							this.screens.add(new DisplayTarget(newDisplay, this.debugMode));
						}
					}
				} else {
					if (screen.isPresent()) {
						DisplayTarget s = screen.get();
						s.release();
						this.screens.remove(s);
					}
				}
			}
		}
		
		LOGGER.info("New Screen Assignment: ");
		for (Display display : displays) {
			LOGGER.info(display);
		}
		
		this.configuration.createBatch()
			.setObject(Setting.DISPLAY_ASSIGNMENTS, ds)
			.setInt(Setting.DISPLAY_COUNT, size)
			.commitBatch()
			.execute(this.executor);
		
		mutating = false;
		
		return whatHappened;
	}
	
	/**
	 * Returns the state of the given display.
	 * @param display the display
	 * @param screens the list of all screens
	 * @return {@link DisplayState}
	 */
	private DisplayState getDisplayState(Display display, List<Screen> screens) {
		// if it's not assigned
		if (display == null || display.getId() < 0) {
			LOGGER.info("Display has not been assigned.");
			return DisplayState.SCREEN_NOT_ASSIGNED;
		}
		
		// check if the screen index still exists
		if (display.getId() >= screens.size()) {
			LOGGER.info("Display {} no longer exists.", display);
			return DisplayState.SCREEN_INDEX_DOESNT_EXIST;
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
