package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.configuration.Display;
import org.praisenter.data.configuration.DisplayRole;
import org.praisenter.ui.GlobalContext;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;

// TODO it would be nice if we could cache the screen changes (removes, adds) and then operate on them more gracefully

public final class DisplayManager {
	private static final Logger LOGGER = LogManager.getLogger();

	private final GlobalContext context;
	
	private final ObservableList<DisplayTarget> targets;
	private final ObservableList<DisplayTarget> targetsUnmodifiable;

	public DisplayManager(GlobalContext context) {
		this.context = context;
		this.targets = FXCollections.observableArrayList();
		this.targetsUnmodifiable = FXCollections.unmodifiableObservableList(this.targets);
	}
	
	public void initialize() {
		// listen for screen changes
		Screen.getScreens().addListener((Change<? extends Screen> c) -> {
			this.onScreensChanged();
		});
		
		// seed the display targets
		this.onScreensChanged();
	}
	
	/**
	 * Shows a non-blocking information dialog notifying the user that we detected a change in the
	 * screens and made an automatic adjustment to the screen assignments and that they should review
	 * by going to Preferences.
	 * @param scene the scene for modality
	 * @param state the state
	 */
	private void notifyOfScreenAssignmentChange(Scene scene, DesktopState state) {
//		// close it if there's already one showing
//		if (this.screenChangedWarning != null && this.screenChangedWarning.isShowing()) {
//			this.screenChangedWarning.close();
//		}
//		// show the new one
//		this.screenChangedWarning = Alerts.warn(
//				scene.getWindow(),
//				Modality.WINDOW_MODAL,
//				state == DesktopState.NO_INITIAL_CONFIGURATION ? Translations.get("init.displaysSet.title") : Translations.get("init.displaysChanged.title"), 
//				state == DesktopState.NO_INITIAL_CONFIGURATION ? Translations.get("init.displaysSet.header") : Translations.get("init.displaysChanged.header"),
//				state == DesktopState.NO_INITIAL_CONFIGURATION ? Translations.get("init.displaysSet.content") : Translations.get("init.displaysChanged.content"));
//		this.screenChangedWarning.show();
	}
	
	/**
	 * Releases any pre-allocated screens or any other resources.
	 */
	public void release() {
		LOGGER.info("Releasing existing displays.");
		for (DisplayTarget screen : this.targets) {
			screen.dispose();
		}
		this.targets.clear();
	}

	/**
	 * Helper method to perform the automatic screen assignment and adjustment when the screens
	 * change, move, added, etc.
	 * <p>
	 * Returns true if this process automatically assigns a screen.
	 * @return DisplayCollectionState
	 */
	private DesktopState onScreensChanged() {
		DesktopState whatHappened = DesktopState.NO_CHANGE;
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int size = screens.size();
		
		// get the configured displays
		ObservableList<Display> displays = this.context.getConfiguration().getDisplays();
		int n = displays.size();
		
		LOGGER.info("Current Screen Assignment: ");
		for (Display display : displays) {
			LOGGER.info(display);
		}
		
		// check if the screen count changed
		if (n != size) {
			whatHappened = size < n ? DesktopState.DISPLAY_COUNT_DECREASED : DesktopState.DISPLAY_COUNT_INCREASED;
		}
		
		if (n == 0) {
			// screens have never been assigned so auto-assign them
			for (int i = 0; i < size; i++) {
				DisplayRole role = DisplayRole.NONE;
				if (i == 0 && size == 1) role = DisplayRole.MAIN;
				if (i == 0 && size == 2) role = DisplayRole.NONE;
				if (i == 1 && size == 2) role = DisplayRole.MAIN;
				if (i == 2 && size >= 3) role = DisplayRole.TELEPROMPT;
				if (i > 2) role = DisplayRole.OTHER;

				Display display = this.toDisplay(screens.get(i), i, role);
				displays.add(display);
				
				if (role != DisplayRole.NONE) {
					this.targets.add(new DisplayTarget(this.context, display));
				}
			}
			whatHappened = DesktopState.NO_INITIAL_CONFIGURATION;
		} else {
			List<Display> toRemove = new ArrayList<>();
			List<Display> toAdd = new ArrayList<>();
			
			// verify each display's state
			for (final Display display : displays) {
				int index = display.getId();
				Screen screen = index >= 0 && index < size ? screens.get(index) : null;
				
				DisplayState state = this.getDisplayState(display, screen);
				
				switch (state) {
					case SCREEN_INDEX_DOESNT_EXIST:
						toRemove.add(display);
						
						this.removeDisplayTargetForDisplay(display);
						
						whatHappened = DesktopState.DISPLAY_COUNT_DECREASED;
						break;
					case POSITION_CHANGED:
					case RESOLUTION_CHANGED:
					case POSITION_AND_RESOLUTION_CHANGED:
						Display replacement = this.toDisplay(screens.get(index), index, display.getRole());
						toRemove.add(display);
						toAdd.add(replacement);
						
						this.removeDisplayTargetForDisplay(display);
						this.targets.add(new DisplayTarget(this.context, replacement));
						
						whatHappened = DesktopState.DISPLAY_POSITION_OR_RESOLUTION_CHANGED;
						break;
					case VALID:
						// do we have target for this display?
						if (display.getRole() != DisplayRole.NONE) {
							DisplayTarget target = this.getDisplayTargetForDisplay(display);
							if (target == null) {
								this.targets.add(new DisplayTarget(this.context, display));
							}
						}
						break;
					case SCREEN_NOT_ASSIGNED:
					default:
						// do nothing?
						break;
				}
			}
			
			displays.removeAll(toRemove);
			displays.addAll(toAdd);
		}
		
		LOGGER.info("Screen update result: " + whatHappened);
		LOGGER.info("New Screen Assignment: ");
		for (Display display : displays) {
			LOGGER.info(display);
		}

		return whatHappened;
	}

	private Display toDisplay(Screen screen, int index, DisplayRole role) {
		Display display = new Display();
		display.setHeight((int)screen.getBounds().getHeight());
		display.setId(index);
		display.setName(screen.toString());
		display.setRole(role);
		display.setWidth((int)screen.getBounds().getWidth());
		display.setX((int)screen.getBounds().getMinX());
		display.setY((int)screen.getBounds().getMinY());
		return display;
	}
	
	private DisplayTarget getDisplayTargetForDisplay(Display display) {
		for (DisplayTarget target : this.targets) {
			if (target.getDisplay() == display) {
				return target;
			}
		}
		return null;
	}
	
	private void removeDisplayTargetForDisplay(Display display) {
		DisplayTarget toRemove = this.getDisplayTargetForDisplay(display);
		if (toRemove != null) {
			toRemove.dispose();
			this.targets.remove(toRemove);
		}
	}
	
	private DisplayState getDisplayState(Display display, Screen screen) {
		// if it's not assigned
		if (display == null || display.getId() < 0) {
			LOGGER.info("Display has not been assigned.");
			return DisplayState.SCREEN_NOT_ASSIGNED;
		}
		
		// check if the screen index still exists
		if (screen == null) {
			LOGGER.info("Display {} no longer exists.", display);
			return DisplayState.SCREEN_INDEX_DOESNT_EXIST;
		}
		
		// otherwise, it needs to be in the list of screens with the
		// same index and must have the same dimensions and position
		Rectangle2D bounds = screen.getBounds();
		boolean positionChanged = false;
		if ((int)bounds.getMinX() != display.getX() ||
			(int)bounds.getMinY() != display.getY()) {
			LOGGER.info("Display {} position has changed.", display);
			positionChanged = true;
		}
		
		boolean resolutionChanged = false;
		if ((int)bounds.getWidth() != display.getWidth() ||
			(int)bounds.getHeight() != display.getHeight()) {
			LOGGER.info("Display {} resolution has changed.", display);
			resolutionChanged = true;
		}
		
		if (resolutionChanged && positionChanged) {
			return DisplayState.POSITION_AND_RESOLUTION_CHANGED;
		} else if (positionChanged) {
			return DisplayState.POSITION_CHANGED;
		} else if (positionChanged) {
			return DisplayState.RESOLUTION_CHANGED;
		}
		
		return DisplayState.VALID;
	}
	
	public ObservableList<DisplayTarget> getDisplayTargets() {
		return this.targetsUnmodifiable;
	}
}
