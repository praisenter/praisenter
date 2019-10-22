package org.praisenter.ui.display;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.configuration.Display;
import org.praisenter.data.configuration.DisplayRole;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.MappedList;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;

public final class DisplayManager {
	private static final Logger LOGGER = LogManager.getLogger();

	private final GlobalContext context;
	
	private FilteredList<Display> filtered;
	private MappedList<DisplayTarget, Display> targets;
	private final ObservableList<DisplayTarget> targets2 = FXCollections.observableArrayList();

	public DisplayManager(GlobalContext context) {
		this.context = context;
		
		
	}
	
	public void initialize() {

		filtered = context.getConfiguration().getDisplays().filtered(d -> d.getRole() != DisplayRole.NONE);
		this.targets = new MappedList<DisplayTarget, Display>(filtered, (display) -> {
			//System.out.println("+ Creating display target: " + display);
			return new DisplayTarget(context, display);
		});
		Bindings.bindContent(this.targets2, this.targets);
		
		this.targets.addListener((Change<? extends DisplayTarget> c) -> {
			while (c.next()) {
				for (DisplayTarget target : c.getRemoved()) {
					System.out.println("- Releasing display target: " + target.getDisplay());
					target.release();
				}
			}
		});
		
		Screen.getScreens().addListener((Change<? extends Screen> c) -> {
			this.onScreensChanged();
		});
		
		this.onScreensChanged();
		
//		// listen for screen changes
//		Screen.getScreens().addListener(new ListChangeListener<Screen>() {
//			@Override
//			public void onChanged(ListChangeListener.Change<? extends Screen> c) {
//				DesktopState state = screensChanged();
//				if (state != DesktopState.NO_CHANGE) {
//					notifyOfScreenAssignmentChange(scene, state);
//				}
//			}
//		});
//		
////		this.configuration.getDisplays().addListener(new ListChangeListener<Display>() {
////			@Override
////			public void onChanged(ListChangeListener.Change<? extends Display> c) {
////				// make sure this isn't being called as a result of
////				// the screens being changed at the OS level
////				if (mutating) return;
////				// update the display screens
////				screensChanged();
////			}
////		});
//		
//		DesktopState state = screensChanged();
//		if (state != DesktopState.NO_CHANGE) {
//			notifyOfScreenAssignmentChange(scene, state);
//		}
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
			screen.release();
		}
//		this.targets.clear();
	}

//	/**
//	 * Returns the display that will be used for presenation.
//	 * <p>
//	 * Typically this will be the MAIN screen, but can be the
//	 * OPERATOR or PRIMARY when the MAIN screen is not set
//	 * or invalid.
//	 * @return {@link Display}
//	 */
//	public Display getPresentationDisplay() {
//		List<Display> displays = this.configuration.getDisplays();
//		for (Display display : displays) {
//			if (display.getRole() == DisplayRole.MAIN) {
//				return display;
//			}
//		}
//		Screen primary = Screen.getPrimary();
//		Rectangle2D bounds = primary.getBounds();
//		return new Display(-1, null, "Primary", (int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
//	}
	
	private List<Display> autoAssignDisplays() {
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		int size = screens.size();
		
		if (size == 0) return List.of();
		
		if (size == 1) {
			// then it's the presentation (MAIN) display
			return List.of(
					this.toDisplay(screens.get(0), 0, DisplayRole.MAIN));
		}
		
		if (size == 2) {
			// the second screen is the presentation (MAIN) display
			return List.of(
					this.toDisplay(screens.get(0), 0, DisplayRole.NONE),
					this.toDisplay(screens.get(1), 1, DisplayRole.MAIN));
		}
		
		// the second screen is the presentation (MAIN) display
		// the third is the TELEPROMPT
		// any others are OTHER
		List<Display> displays = new ArrayList<Display>();
		for (int i = 0; i < size; i++) {
			DisplayRole role = DisplayRole.NONE;
			if (i == 1) role = DisplayRole.MAIN;
			else if (i == 2) role = DisplayRole.TELEPROMPT;
			else if (i > 2) role = DisplayRole.OTHER;
			displays.add(this.toDisplay(screens.get(i), i, role));
		}
		
		return displays;
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
		
//		int n = this.configuration.getInt(Setting.DISPLAY_COUNT, -1);
//		List<Display> displays = new ArrayList<Display>(this.configuration.getDisplays());
//		DisplayList ds = this.configuration.getObject(Setting.DISPLAY_ASSIGNMENTS, DisplayList.class, new DisplayList());
		
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
//				Rectangle2D bounds = screens.get(i).getBounds();
//				Display display = new Display(i, role, role == DisplayRole.OTHER ? role.toString() + (i - 2) : role.toString(), (int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
				displays.add(this.toDisplay(screens.get(i), i, role));
//				if (role != DisplayRole.NONE) {
//					this.targets.add(new DisplayTarget(this.configuration, display));
//				}
			}
			whatHappened = DesktopState.NO_INITIAL_CONFIGURATION;
		} else {
			// verify each display's state
			for (final Display display : displays) {
//				Display newDisplay = display;
				int index = display.getId();
				Screen screen = index >= 0 && index < size ? screens.get(index) : null;
				
				DisplayState state = this.getDisplayState(display, screen);
//				Optional<DisplayTarget> target = this.targets.stream().filter(s -> s.getDisplay().getId() == display.getId()).findFirst();
				
				switch (state) {
					case SCREEN_INDEX_DOESNT_EXIST:
						displays.remove(display);
						whatHappened = DesktopState.DISPLAY_COUNT_DECREASED;
						break;
					case POSITION_CHANGED:
					case RESOLUTION_CHANGED:
					case POSITION_AND_RESOLUTION_CHANGED:
						Display replacement = this.toDisplay(screens.get(index), index, display.getRole());
						displays.remove(display);
						displays.add(replacement);
						whatHappened = DesktopState.DISPLAY_POSITION_OR_RESOLUTION_CHANGED;
						break;
					case VALID:
					case SCREEN_NOT_ASSIGNED:
					default:
						// do nothing?
						break;
				}
				
				
//				if (state == DisplayState.SCREEN_INDEX_DOESNT_EXIST) {
//					// then remove the display
//					displays.remove(display);
//					whatHappened = DesktopState.DISPLAY_COUNT_DECREASED;
////					this.targets.removeIf(s -> s.getDisplay().getId() == index);
////					newDisplay = null;
//				} else if (state == DisplayState.POSITION_CHANGED ||
//						state == DisplayState.RESOLUTION_CHANGED ||
//						state == DisplayState.POSITION_AND_RESOLUTION_CHANGED) {
//					Display replacement = this.toDisplay(screens.get(index), index, display.getRole());
//					displays.remove(display);
					
//				} else if (state == DisplayState.POSITION_CHANGED) {
//					// update the display screen
//					newDisplay = this.toDisplay(screens.get(display.getId()), display.getId(), display.getRole()); //display.withBounds(screens.get(display.getId()).getBounds());
//					
//					displays.remove(display);
//					displays.add(newDisplay);
//					whatHappened = DesktopState.DISPLAY_POSITION_CHANGED;
//				} else if (state == DisplayState.RESOLUTION_CHANGED) {
//					// update the display screen
//					newDisplay = display.withBounds(screens.get(display.getId()).getBounds());
//					ds.remove(display);
//					ds.add(newDisplay);
//				} else if (display.getRole() == DisplayRole.NONE) {
//					newDisplay = null;
//				}
//				
//				if (newDisplay != null) {
//					if (newDisplay.getRole() != DisplayRole.NONE) {
//						if (screen.isPresent()) {
//							screen.get().setDisplay(newDisplay);
//						} else {
//							this.targets.add(new DisplayTarget(this.configuration, display));
//						}
//					}
//				} else {
//					if (screen.isPresent()) {
//						DisplayTarget s = screen.get();
//						s.release();
//						this.targets.remove(s);
//					}
//				}
			}
		}
		
		LOGGER.info("Screen update result: " + whatHappened);
		LOGGER.info("New Screen Assignment: ");
		for (Display display : displays) {
			LOGGER.info(display);
		}

//		this.context.getConfiguration().setDisplays(displays);
		
//		this.configuration.createBatch()
//			.setObject(Setting.DISPLAY_ASSIGNMENTS, ds)
//			.setInt(Setting.DISPLAY_COUNT, size)
//			.commitBatch()
//			.execute(this.executor);
		
//		mutating = false;
		
		return whatHappened;
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
		return FXCollections.unmodifiableObservableList(this.targets2);
	}
}
