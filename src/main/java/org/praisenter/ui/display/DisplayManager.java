package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.workspace.Display;
import org.praisenter.data.workspace.DisplayRole;
import org.praisenter.ui.GlobalContext;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

// TODO it would be nice if we could cache the screen changes (removes, adds) and then operate on them more gracefully

public final class DisplayManager {
	private static final Logger LOGGER = LogManager.getLogger();

	private final GlobalContext context;
	
	private final ObservableList<DisplayTarget> targets;
	private final ObservableList<DisplayTarget> targetsUnmodifiable;
	
	private final ListChangeListener<? super Screen> screenListener;

	public DisplayManager(GlobalContext context) {
		this.context = context;
		this.targets = FXCollections.observableArrayList();
		this.targetsUnmodifiable = FXCollections.unmodifiableObservableList(this.targets);
		this.screenListener = (Change<? extends Screen> c) -> {
			this.onScreensChanged();
		};
	}
	
	public void initialize() {
		// listen for screen changes
		Screen.getScreens().addListener(this.screenListener);
		
		// seed the display targets
		this.onScreensChanged();
	}
	
	/**
	 * Releases any pre-allocated screens or any other resources.
	 */
	public void dispose() {
		Screen.getScreens().removeListener(this.screenListener);
		LOGGER.info("Releasing existing displays.");
		for (DisplayTarget screen : this.targets) {
			screen.dispose();
		}
		this.targets.clear();
	}

	private DesktopState onScreensChanged() {
		DesktopState whatHappened = DesktopState.NO_CHANGE;
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int sSize = screens.size();
		
		// get the configured displays
		ObservableList<Display> displays = this.context.getConfiguration().getDisplays();
		int dSize = displays.size();
		
		LOGGER.info("Current Screen Assignment: ");
		for (Display display : displays) {
			LOGGER.info(display);
		}
		
		// check if the screen count changed
		if (dSize != sSize) {
			whatHappened = sSize < dSize ? DesktopState.DISPLAY_COUNT_DECREASED : DesktopState.DISPLAY_COUNT_INCREASED;
		}
		
		if (dSize == 0) {
			// screens have never been assigned so auto-assign them
			for (int i = 0; i < sSize; i++) {
				DisplayRole role = this.getDisplayRoleForScreenNumber(i, sSize);

				Display display = this.toDisplay(screens.get(i), i, role);
				displays.add(display);

				this.targets.add(new DisplayTarget(this.context, display));
			}
			whatHappened = DesktopState.NO_INITIAL_CONFIGURATION;
		} else {
			List<Display> toRemove = new ArrayList<>();
			List<Display> toAdd = new ArrayList<>();
			
			// map the screens by index
			Map<Integer, Screen> s = new HashMap<>();
			for (int i = 0; i < sSize; i++) {
				s.put(i, screens.get(i));
			}
			
			// verify each display's state
			int n = Math.max(sSize, dSize);
			for (int i = 0; i < n; i++) {
				Display display = null;
				if (i < dSize) {
					display = displays.get(i);
				}
				
				int index = -1;
				Screen screen = null;
				if (display != null) {
					index = display.getId();
					screen = s.remove(index);
				} else {
					for (Integer j : s.keySet()) 
					{
						index = j;
						screen = s.get(j);
						break;
					}
					if (index >= 0) {
						s.remove(index);
					}
				}
				
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
						Display replacement = this.toDisplay(screen, index, display.getRole());
						toRemove.add(display);
						toAdd.add(replacement);
						
						this.removeDisplayTargetForDisplay(display);
						this.targets.add(new DisplayTarget(this.context, replacement));
						
						whatHappened = DesktopState.DISPLAY_POSITION_OR_RESOLUTION_CHANGED;
						break;
					case VALID:
						// do we have target for this display?
//						if (display.getRole() != DisplayRole.NONE) {
							DisplayTarget target = this.getDisplayTargetForDisplay(display);
							if (target == null) {
								this.targets.add(new DisplayTarget(this.context, display));
							}
//						}
						break;
					case SCREEN_NOT_ASSIGNED:
						DisplayRole role = this.getDisplayRoleForScreenNumber(i, sSize);
						Display newDisplay = this.toDisplay(screen, index, role);
						toAdd.add(newDisplay);
						
						this.targets.add(new DisplayTarget(this.context, newDisplay));
						
						whatHappened = DesktopState.DISPLAY_COUNT_INCREASED;
						break;
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
	
	private DisplayRole getDisplayRoleForScreenNumber(int index, int screenCount) {
		DisplayRole role = DisplayRole.OTHER;
		if (index == 0 && screenCount == 1) role = DisplayRole.MAIN;
		if (index == 0 && screenCount == 2) role = DisplayRole.NONE;
		if (index == 1 && screenCount == 2) role = DisplayRole.MAIN;
		if (index == 2 && screenCount >= 3) role = DisplayRole.TELEPROMPT;
		return role;
	}
	
	private Display toDisplay(Screen screen, int index, DisplayRole role) {
		Display display = new Display();
		display.setHeight((int)screen.getBounds().getHeight());
		display.setId(index);
		display.setName("SCREEN" + index);
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
