package org.praisenter.javafx.screen;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.utility.Fx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;

// TODO translate
public final class ScreenView extends StackPane {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The maximum width of the screen view images */
	private static final double MAX_WIDTH = 250;
	
	/** The border width of screen */
	private static final double BORDER_WIDTH = 10;
	
	// interaction
	
	/** The drag and drop manager */
	private final ScreenViewDragDropManager manager;
	
	// data
	
	/** The display detail */
	private final Display display;
	
	/** The screen assigned to the display */
	private final Screen screen;
	
	/**
	 * Constructor.
	 * @param manager the drag and drop manager
	 * @param display the display; can be null
	 * @param screen the screen; can be null
	 */
	private ScreenView(ScreenViewDragDropManager manager, Display display, Screen screen) {
		this.getStyleClass().add("screen-view");
		
		this.manager = manager;
		this.display = display;
		this.screen = screen;
		
		this.build();
	}
	
	/**
	 * Returns a list of {@link ScreenView}s for all the screens on this system.
	 * @param manager the drag and drop manager
	 * @return Map&lt;Integer, {@link ScreenView}&gt;
	 */
	public static final Map<Integer, ScreenView> createScreenViews(ScreenViewDragDropManager manager) {
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		Map<Integer, ScreenView> views = new HashMap<Integer, ScreenView>();
		
		for (int i = 0; i < screens.size(); i++) {
			Screen screen = screens.get(i);
			views.put(i, new ScreenView(manager, new Display(i, screen), screen));
		}
		
		return views;
	}
	
	/**
	 * Returns a new {@link ScreenView} for an unassigned screen.
	 * @param manager the drag and drop manager
	 * @return {@link ScreenView}
	 */
	public static final ScreenView createUnassignedScreenView(ScreenViewDragDropManager manager) {
		return new ScreenView(manager, null, null);
	}
	
	/**
	 * Builds the view for a unassigned display or for a display that we couldn't generate
	 * a screenshot for.
	 * @param text the text to display on the screen
	 */
	private void buildUnassignedOrError(String text) {
		// have it mirror the default screen
		Screen screen = Screen.getPrimary();
		final double s = MAX_WIDTH / screen.getBounds().getWidth();
		final double w = MAX_WIDTH;
		final double h = Math.ceil(screen.getBounds().getHeight() * s);
		
		Label label = new Label(text);
		this.getChildren().add(label);
		Fx.setSize(this, w + BORDER_WIDTH * 2, h + BORDER_WIDTH * 2);
	}
	
	/**
	 * Builds this view.
	 */
	private void build() {
		if (this.screen == null || this.display == null) { 
			buildUnassignedOrError("Display Not Assigned");
		} else {
			// get its coordinates
			final int sx = (int)this.screen.getBounds().getMinX();
			final int sy = (int)this.screen.getBounds().getMinY();
			
			// use AWT to take a screenshot of the desktop
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] devices = ge.getScreenDevices();
			
			// find a matching device for this screen by x,y coordinates
			GraphicsDevice device = null;
			for (int i = 0; i < devices.length; i++) {
				GraphicsDevice dev = devices[i];
				java.awt.Rectangle bounds = dev.getDefaultConfiguration().getBounds();
				final int dx = bounds.x;
				final int dy = bounds.y;
				if (sx == dx && sy == dy) {
					device = dev;
					break;
				}
			}
			
			// take a screenshot of the device
			try {
				Robot robot = new Robot(device);
				BufferedImage image = robot.createScreenCapture(device.getDefaultConfiguration().getBounds());
				Image fximage = SwingFXUtils.toFXImage(image, null);
				ImageView img = new ImageView(fximage);
				
				final double s = MAX_WIDTH / fximage.getWidth();
				
				// scale the bounds
				final double w = Math.ceil(fximage.getWidth() * s);
				final double h = Math.ceil(fximage.getHeight() * s);
				
				img.setFitHeight(h);
				img.setFitWidth(w);
				img.setPreserveRatio(true);
				img.setLayoutX(BORDER_WIDTH);
				img.setLayoutY(BORDER_WIDTH);
				
				this.getChildren().add(img);
				Fx.setSize(this, w + BORDER_WIDTH * 2, h + BORDER_WIDTH * 2);
			} catch (Exception ex) {
				LOGGER.warn("Failed to generate screenshot for screen " + device.getIDstring(), ex);
				buildUnassignedOrError(this.display.toString());
			}
		}

		this.setOnDragDetected(e -> {
			manager.dragDetected(this, e);
		});
		this.setOnDragEntered(e -> {
			manager.dragEntered(this, e);
		});
		this.setOnDragExited(e -> {
			manager.dragExited(this, e);
		});
		this.setOnDragOver(e -> {
			manager.dragOver(this, e);
		});
		this.setOnDragDropped(e -> {
			manager.dragDropped(this, e);
		});
		this.setOnDragDone(e -> {
			manager.dragDone(this, e);
		});
	}
	
	/* (non-Javadoc)
	 * @see javafx.scene.Node#toString()
	 */
	@Override
	public String toString() {
		if (this.display != null) {
			return this.display.toString();
		} else {
			return "NotAvailable";
		}
	}
	
	/**
	 * Returns the display.
	 * @return {@link Display}
	 */
	public Display getDisplay() {
		return this.display;
	}
}
