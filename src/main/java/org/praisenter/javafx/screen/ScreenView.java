package org.praisenter.javafx.screen;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.praisenter.javafx.utility.Fx;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

public final class ScreenView extends StackPane {
	private static final double DEFAULT_WIDTH = 200;
	private static final double DEFAULT_HEIGHT = 150;
	private static final double BORDER_WIDTH = 10;
	private static final double SCALE = 200.0 / 1080.0;
	
	private final ScreenViewDragDropManager manager;
	
	private final Display display;
	private final Screen screen;
	
	private ScreenView(ScreenViewDragDropManager manager, Display display, Screen screen) {
		this.manager = manager;
		this.display = display;
		this.screen = screen;
		
		this.build();
	}
	
	public static final Map<Integer, ScreenView> createScreenViews(ScreenViewDragDropManager manager) {
		List<Screen> screens = new ArrayList<Screen>(Screen.getScreens());
		Map<Integer, ScreenView> views = new HashMap<Integer, ScreenView>();
		
		for (int i = 0; i < screens.size(); i++) {
			Screen screen = screens.get(i);
			views.put(i, new ScreenView(manager, new Display(i, screen), screen));
		}
		
		return views;
	}
	
	public static final ScreenView createUnassignedScreenView(ScreenViewDragDropManager manager) {
		return new ScreenView(manager, null, null);
	}
	
	private void build() {
		if (this.screen == null) { 
			Label label = new Label("Screen Not Assigned");
			this.getChildren().add(label);
			this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, new Insets(BORDER_WIDTH * 0.5))));
			Fx.setSize(this, DEFAULT_WIDTH + BORDER_WIDTH * 2, DEFAULT_HEIGHT + BORDER_WIDTH * 2);
			this.getStyleClass().add("screen-snapshot");
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
				
				// scale the bounds
				final double w = Math.ceil(fximage.getWidth() * SCALE);
				final double h = Math.ceil(fximage.getHeight() * SCALE);
				
				img.setFitHeight(h);
				img.setFitWidth(w);
				img.setPreserveRatio(true);
				img.setLayoutX(BORDER_WIDTH);
				img.setLayoutY(BORDER_WIDTH);
				
				this.getChildren().add(img);
				this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, new Insets(BORDER_WIDTH * 0.5))));
				Fx.setSize(this, w + BORDER_WIDTH * 2, h + BORDER_WIDTH * 2);
				this.getStyleClass().add("screen-snapshot");
			} catch (Exception ex) {
				// TODO handle error
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
	
	@Override
	public String toString() {
		if (this.display != null) {
			return "Screen#" + this.display.getId();
		} else {
			return "NotAvailable";
		}
	}
	
	public Display getDisplay() {
		return this.display;
	}
}
