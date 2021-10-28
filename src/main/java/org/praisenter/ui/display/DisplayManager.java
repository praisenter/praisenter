package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.ui.GlobalContext;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

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
		
		// reset the focus on the primary stage
		this.context.getStage().requestFocus();
	}
	
	/**
	 * Releases any pre-allocated screens or any other resources.
	 */
	public void dispose() {
		Screen.getScreens().removeListener(this.screenListener);
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
		ObservableList<DisplayConfiguration> configurations = this.context.getWorkspaceConfiguration().getDisplayConfigurations();
		int dSize = configurations.size();
		
		LOGGER.info("Last screen state: ");
		for (DisplayConfiguration configuration : configurations) {
			LOGGER.info(configuration);
		}
		
		// check if the screen count changed
		if (dSize != sSize) {
			whatHappened = sSize < dSize ? DesktopState.DISPLAY_COUNT_DECREASED : DesktopState.DISPLAY_COUNT_INCREASED;
		}
		
		if (dSize == 0) {
			// screens have never been assigned so auto-assign them
			for (int i = 0; i < sSize; i++) {
				boolean isPrimary = this.isPrimaryScreen(i, sSize);

				DisplayConfiguration newConfiguration = this.createDisplayConfiguration(screens.get(i), i, isPrimary);
				configurations.add(newConfiguration);

				this.targets.add(new DisplayTarget(this.context, newConfiguration));
			}
			whatHappened = DesktopState.NO_INITIAL_CONFIGURATION;
		} else {
			// map the screens by index
			Map<Integer, Screen> s = new HashMap<>();
			for (int i = 0; i < sSize; i++) {
				s.put(i, screens.get(i));
			}
			
			// verify each display's state
			int n = Math.max(sSize, dSize);
			for (int i = 0; i < n; i++) {
				DisplayConfiguration configuration = null;
				if (i < dSize) {
					configuration = configurations.get(i);
				}
				
				int index = -1;
				Screen screen = null;
				if (configuration != null) {
					index = configuration.getId();
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
				
				boolean isPrimary = this.isPrimaryScreen(i, sSize);
				DisplayState state = this.getDisplayState(configuration, screen);
				
				switch (state) {
					case SCREEN_INDEX_DOESNT_EXIST:
						// remove the display target, but keep the configuration
						this.removeDisplayTargetForDisplayConfiguration(configuration);
						
						whatHappened = DesktopState.DISPLAY_COUNT_DECREASED;
						break;
					case RESOLUTION_CHANGED:
					case POSITION_AND_RESOLUTION_CHANGED:
						// if the resolutino changes, we have to scale the slide or do
						// something to readjust - it's better to just destroy the target
						// and re-create it
						
						// remove the target (if it exists)
						this.removeDisplayTargetForDisplayConfiguration(configuration);
						
						// update the configuration
						this.updateConfiguration(screen, configuration);
						
						// add a new target for the updated configuration
						this.targets.add(new DisplayTarget(this.context, configuration));
						
						whatHappened = DesktopState.DISPLAY_POSITION_OR_RESOLUTION_CHANGED;
						break;
					case VALID:
					case POSITION_CHANGED:
						// update the configuration
						this.updateConfiguration(screen, configuration);
						
						// check to make sure a target exists
						DisplayTarget target = this.getDisplayTargetForDisplayConfiguration(configuration);
						if (target == null) {
							target = new DisplayTarget(this.context, configuration);
							this.targets.add(target);
						}
						
						// check if the target is at the correct position
						this.updateLocation(target);
						break;
					case SCREEN_NOT_ASSIGNED:
						// create a new configuration for the new target
						DisplayConfiguration newConfiguration = this.createDisplayConfiguration(screen, index, isPrimary);
						configurations.add(newConfiguration);
						
						// add the new target
						this.targets.add(new DisplayTarget(this.context, newConfiguration));
						
						whatHappened = DesktopState.DISPLAY_COUNT_INCREASED;
						break;
					default:
						// do nothing?
						break;
				}
			}
		}
		
		LOGGER.info("Screen update result: " + whatHappened);
		LOGGER.info("New screen state: ");
		for (DisplayConfiguration configuration : configurations) {
			LOGGER.info(configuration);
		}

		return whatHappened;
	}
	
	private boolean isPrimaryScreen(int index, int screenCount) {
		if (index == 0 && screenCount == 1) return true;
		if (index == 1 && screenCount == 2) return true;
		return false;
	}
	
	private DisplayConfiguration createDisplayConfiguration(Screen screen, int index, boolean isPrimary) {
		DisplayConfiguration display = new DisplayConfiguration();
		display.setHeight((int)screen.getBounds().getHeight());
		display.setId(index);
		display.setPrimary(isPrimary);
		display.setWidth((int)screen.getBounds().getWidth());
		display.setX((int)screen.getBounds().getMinX());
		display.setY((int)screen.getBounds().getMinY());
		return display;
	}
	
	private void updateConfiguration(Screen screen, DisplayConfiguration configuration) {
		configuration.setHeight((int)screen.getBounds().getHeight());
		configuration.setWidth((int)screen.getBounds().getWidth());
		configuration.setX((int)screen.getBounds().getMinX());
		configuration.setY((int)screen.getBounds().getMinY());
	}
	
	private DisplayTarget getDisplayTargetForDisplayConfiguration(DisplayConfiguration configuration) {
		for (DisplayTarget target : this.targets) {
			if (target.getDisplayConfiguration() == configuration) {
				return target;
			}
		}
		return null;
	}
	
	private void removeDisplayTargetForDisplayConfiguration(DisplayConfiguration configuration) {
		DisplayTarget toRemove = this.getDisplayTargetForDisplayConfiguration(configuration);
		if (toRemove != null) {
			toRemove.dispose();
			this.targets.remove(toRemove);
		}
	}
	
	private void updateLocation(DisplayTarget target) {
		int tx = target.getDisplayConfiguration().getX();
		int ty = target.getDisplayConfiguration().getY();
		int cx = (int)target.getX();
		int cy = (int)target.getY();
		if (cx != tx || cy != ty) {
			// then move the target
			target.setX(tx);
			target.setY(ty);
		}
	}
	
	private DisplayState getDisplayState(DisplayConfiguration configuration, Screen screen) {
		// if it's not assigned
		if (configuration == null || configuration.getId() < 0) {
			LOGGER.info("Display has not been initialized.");
			return DisplayState.SCREEN_NOT_ASSIGNED;
		}
		
		// check if the screen index still exists
		if (screen == null) {
			LOGGER.info("Display {} no longer exists.", configuration.getId());
			return DisplayState.SCREEN_INDEX_DOESNT_EXIST;
		}
		
		// otherwise, it needs to be in the list of screens with the
		// same index and must have the same dimensions and position
		Rectangle2D bounds = screen.getBounds();
		boolean positionChanged = false;
		if ((int)bounds.getMinX() != configuration.getX() ||
			(int)bounds.getMinY() != configuration.getY()) {
			LOGGER.info("Display {} position has changed.", configuration.getId());
			positionChanged = true;
		}
		
		boolean resolutionChanged = false;
		if ((int)bounds.getWidth() != configuration.getWidth() ||
			(int)bounds.getHeight() != configuration.getHeight()) {
			LOGGER.info("Display {} resolution has changed.", configuration.getId());
			resolutionChanged = true;
		}
		
		if (resolutionChanged && positionChanged) {
			return DisplayState.POSITION_AND_RESOLUTION_CHANGED;
		} else if (positionChanged) {
			return DisplayState.POSITION_CHANGED;
		} else if (resolutionChanged) {
			return DisplayState.RESOLUTION_CHANGED;
		}
		
		return DisplayState.VALID;
	}
	
	public ObservableList<DisplayTarget> getDisplayTargets() {
		return this.targetsUnmodifiable;
	}
}
