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
package org.praisenter.javafx.configuration;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.Option;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Represents a view for showing a screenshot of a screen.
 * @author William Bittle
 * @version 3.0.0
 */
public final class DisplayView extends VBox {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The maximum width of the screen view images */
	private static final double MAX_WIDTH = 250;
	
	/** The border width of screen */
	private static final double BORDER_WIDTH = 10;
	
	// data
	
	/** The display detail */
	private final ObjectProperty<Display> display = new SimpleObjectProperty<Display>();
	
	// controls
	
	/** The display role */
	private final ChoiceBox<Option<DisplayRole>> cbRole;
	
	/** The user defined display name */
	private final TextField txtName;
	
	/**
	 * Constructor.
	 * @param display the display
	 */
	public DisplayView(Display display) {
		this.getStyleClass().add("display-view");
		
		this.display.set(display);
		
		ObservableList<Option<DisplayRole>> roles = FXCollections.observableArrayList();
		for (DisplayRole role : DisplayRole.values()) {
			roles.add(new Option<DisplayRole>(Translations.get(DisplayRole.class.getName() + "." + role.name()), role));
		}
		
		Label lblRole = new Label(Translations.get("setup.display.role"));
		this.cbRole = new ChoiceBox<Option<DisplayRole>>(roles);
		this.cbRole.setValue(new Option<DisplayRole>(null, display.getRole()));
		
		Label lblName = new Label(Translations.get("setup.display.name"));
		this.txtName = new TextField();
		this.txtName.setText(display.getName());
		
		// get its coordinates
		final int sx = display.getX();
		final int sy = display.getY();
		
		// use AWT to take a screenshot of the desktop
		LOGGER.debug("Using AWT to read the device configuration.");
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
		Image fximage = null;
		if (device != null) {
			try {
				LOGGER.debug("Taking a screenshot of display '{}'.", device.getIDstring());
				Robot robot = new Robot(device);
				BufferedImage image = robot.createScreenCapture(device.getDefaultConfiguration().getBounds());
				fximage = SwingFXUtils.toFXImage(image, null);
			} catch (Exception ex) {
				LOGGER.warn("Failed to generate screenshot for screen " + device.getIDstring(), ex);
			}
		}
		
		final double w = fximage != null ? fximage.getWidth() : display.getWidth();
		final double h = fximage != null ? fximage.getHeight() : display.getHeight();
		
		// compute a scale factor
		final double s = MAX_WIDTH / w;
		final double sw = Math.ceil(w * s);
		final double sh = Math.ceil(h * s);
		
		ImageView img = new ImageView(fximage);
		img.setFitHeight(sh);
		img.setFitWidth(sw);
		img.setPreserveRatio(true);
		img.setLayoutX(BORDER_WIDTH);
		img.setLayoutY(BORDER_WIDTH);
		
		Label label = new Label(String.valueOf(display.getId() + 1));
		label.getStyleClass().add("display-view-number");

		StackPane ui = new StackPane(img, label);
		ui.getStyleClass().add("display-view-monitor");
		Fx.setSize(ui, sw + 2 * BORDER_WIDTH, sh + 2 * BORDER_WIDTH);
				
		// LAYOUT

		GridPane grid = new GridPane();
		grid.setHgap(5);
		grid.setVgap(5);
		grid.add(lblRole, 0, 0);
		grid.add(this.cbRole, 1, 0);
		grid.add(lblName, 0, 1);
		grid.add(this.txtName, 1, 1);
		this.getChildren().addAll(ui, grid);
		
		// EVENTS
		
		// when the role changes
		this.cbRole.valueProperty().addListener((obs, ov, nv) -> {
			Display d = this.display.get();
			this.display.set(null);
			this.display.set(d.withRole(nv.getValue()));
		});
		
		// on focus lost
		this.txtName.focusedProperty().addListener((obs, ov, nv) -> {
			if (!nv) {
				Display d = this.display.get();
				this.display.set(null);
				this.display.set(d.withName(this.txtName.getText()));
			}
		});
		
		// on ENTER key
		this.txtName.setOnAction(e -> {
			Display d = this.display.get();
			this.display.set(null);
			this.display.set(d.withName(this.txtName.getText()));
		});
	}
	
	/**
	 * Returns the display.
	 * @return {@link Display}
	 */
	public Display getDisplay() {
		return this.display.get();
	}
	
	/**
	 * The display property.
	 * @return ObjectProperty&lt;{@link Display}&gt;
	 */
	public ReadOnlyObjectProperty<Display> displayProperty() {
		return this.display;
	}
}
