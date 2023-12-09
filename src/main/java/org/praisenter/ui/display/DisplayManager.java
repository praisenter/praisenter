package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.data.workspace.DisplayType;
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
	private final ListChangeListener<? super DisplayConfiguration> ndiListener;
	
	public DisplayManager(GlobalContext context) {
		this.context = context;
		
		this.targets = FXCollections.observableArrayList();
		this.targetsUnmodifiable = this.targets.sorted();

		this.screenListener = (Change<? extends Screen> c) -> {
			this.onScreensChanged();
		};
		
		this.ndiListener = (Change<? extends DisplayConfiguration> c) -> {
			this.onDisplayConfigurationChanged(c);
		};
	}
	
	public void initialize() {
		// listen for screen changes
		Screen.getScreens().addListener(this.screenListener);
		
		// listen for NDI changes
		this.context.getWorkspaceConfiguration().getDisplayConfigurations().addListener(this.ndiListener);
		
		// seed the screen display targets
		this.onScreensChanged();
		
		// seed the NDI display targets
		for (DisplayConfiguration ndiDisplayConfiguration : this.context.getWorkspaceConfiguration().getDisplayConfigurations()) {
			if (ndiDisplayConfiguration.getType() == DisplayType.NDI) {
				NDIDisplayTarget target = new NDIDisplayTarget(this.context, ndiDisplayConfiguration);
				this.targets.add(target);
			}
		}
		
		// reset the focus on the primary stage
		this.context.getStage().requestFocus();
	}
	
	/**
	 * Releases any pre-allocated screens or any other resources.
	 */
	public void dispose() {
		Screen.getScreens().removeListener(this.screenListener);
		this.context.getWorkspaceConfiguration().getDisplayConfigurations().removeListener(this.ndiListener);
		
		for (DisplayTarget target : this.targets) {
			target.dispose();
		}
		
		this.targets.clear();
	}

	// ScreenDisplayTarget
	
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

				DisplayConfiguration newConfiguration = this.createDisplayConfigurationFromScreen(screens.get(i), i, isPrimary);
				if (isPrimary && sSize > 1) {
					newConfiguration.setActive(true);
				}
				
				configurations.add(newConfiguration);

				this.targets.add(new ScreenDisplayTarget(this.context, newConfiguration));
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
				DisplayState state = this.getScreenDisplayState(configuration, screen);
				
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
						this.updateScreenConfiguration(screen, configuration);
						
						// add a new target for the updated configuration
						this.targets.add(new ScreenDisplayTarget(this.context, configuration));
						
						whatHappened = DesktopState.DISPLAY_POSITION_OR_RESOLUTION_CHANGED;
						break;
					case VALID:
					case POSITION_CHANGED:
						// update the configuration
						this.updateScreenConfiguration(screen, configuration);
						
						// check to make sure a target exists
						ScreenDisplayTarget target = (ScreenDisplayTarget)this.getDisplayTargetForDisplayConfiguration(configuration);
						if (target == null) {
							target = new ScreenDisplayTarget(this.context, configuration);
							this.targets.add(target);
						}
						
						// check if the target is at the correct position
						this.updateScreenLocation(target);
						break;
					case SCREEN_NOT_ASSIGNED:
						// create a new configuration for the new target
						DisplayConfiguration newConfiguration = this.createDisplayConfigurationFromScreen(screen, index, isPrimary);
						configurations.add(newConfiguration);
						
						// add the new target
						this.targets.add(new ScreenDisplayTarget(this.context, newConfiguration));
						
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
		if (index == 1 && screenCount >= 2) return true;
		return false;
	}
	
	private DisplayConfiguration createDisplayConfigurationFromScreen(Screen screen, int index, boolean isPrimary) {
		DisplayConfiguration display = new DisplayConfiguration();
		display.setHeight((int)screen.getBounds().getHeight());
		display.setId(index);
		display.setPrimary(isPrimary);
		display.setWidth((int)screen.getBounds().getWidth());
		display.setX((int)screen.getBounds().getMinX());
		display.setY((int)screen.getBounds().getMinY());
		display.setType(DisplayType.SCREEN);
		display.setFramesPerSecond(-1);
		return display;
	}
	
	private void updateScreenConfiguration(Screen screen, DisplayConfiguration configuration) {
		configuration.setHeight((int)screen.getBounds().getHeight());
		configuration.setWidth((int)screen.getBounds().getWidth());
		configuration.setX((int)screen.getBounds().getMinX());
		configuration.setY((int)screen.getBounds().getMinY());
	}

	private void updateScreenLocation(ScreenDisplayTarget target) {
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
	
	private DisplayState getScreenDisplayState(DisplayConfiguration configuration, Screen screen) {
		int id = configuration.getId() + 1;
		
		// if it's not assigned
		if (configuration == null || configuration.getId() < 0) {
			LOGGER.info("Display has not been initialized.");
			return DisplayState.SCREEN_NOT_ASSIGNED;
		}
		
		// check if the screen index still exists
		if (screen == null) {
			LOGGER.info("Display {} no longer exists.", id);
			return DisplayState.SCREEN_INDEX_DOESNT_EXIST;
		}
		
		// otherwise, it needs to be in the list of screens with the
		// same index and must have the same dimensions and position
		Rectangle2D bounds = screen.getBounds();
		boolean positionChanged = false;
		if ((int)bounds.getMinX() != configuration.getX() ||
			(int)bounds.getMinY() != configuration.getY()) {
			LOGGER.info("Display {} position has changed.", id);
			positionChanged = true;
		}
		
		boolean resolutionChanged = false;
		if ((int)bounds.getWidth() != configuration.getWidth() ||
			(int)bounds.getHeight() != configuration.getHeight()) {
			LOGGER.info("Display {} resolution has changed.", id);
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
	
	// NDIDisplayTarget
	
	private void onDisplayConfigurationChanged(Change<? extends DisplayConfiguration> c) {
		while (c.next()) {
			// first check if the slide we currently have selected was
			// added (updated)
			List<? extends DisplayConfiguration> as = c.getAddedSubList();
			for (DisplayConfiguration add : as) {
				NDIDisplayTarget target = new NDIDisplayTarget(this.context, add);
				this.targets.add(target);
			}
			
			// next check if the slide we currently have selected was
			// removed (deleted)
			List<? extends DisplayConfiguration> rs = c.getRemoved();
			for (DisplayConfiguration rm : rs) {
				DisplayTarget found = this.getDisplayTargetForDisplayConfiguration(rm);
				
				if (found != null) {
					this.targets.remove(found);
					found.dispose();
				}
			}
		}
	}
	
	// general
	
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
	
	public ObservableList<DisplayTarget> getDisplayTargets() {
		return this.targetsUnmodifiable;
	}
}
