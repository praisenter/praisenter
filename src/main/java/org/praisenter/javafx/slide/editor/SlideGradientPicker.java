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
package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.slide.JavaFXTypeConverter;
import org.praisenter.slide.graphics.SlideGradient;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * A custom button to allow selection of a gradient.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideGradientPicker extends SplitMenuButton {
	/** The gradient dialog */
	private SlideGradientPickerDialog dialog;
	
	/** The selected value */
	private final ObjectProperty<SlideGradient> value = new SimpleObjectProperty<SlideGradient>();
	
	/**
	 * Default constructor.
	 */
	public SlideGradientPicker() {
		// attempt to mimic the color picker styling
		StackPane gradBox = new StackPane();
		gradBox.getStyleClass().add("picker-color");
		Rectangle rect = new Rectangle(12, 12);
		rect.getStyleClass().add("picker-color-rect");
		rect.setStyle("-fx-stroke: -fx-box-border;");
		gradBox.getChildren().add(rect);
		this.setGraphic(gradBox);
		
		// actions
		this.showingProperty().addListener((obs, ov, nv) -> {
			onAction();
		});
		this.setOnAction((e) -> {
			onAction();
		});
		
		// update the rect background
		this.value.addListener((obs, ov, nv) -> {
			if (nv != null) {
				rect.setFill(JavaFXTypeConverter.toJavaFX(nv));
			}
		});
	}
	
	private void onAction() {
		if (dialog == null) {
			// create the dialog
			// passing the owner (we don't create
			// it until the user request for it since
			// 	1. we don't know the owner at creation time
			//  2. we don't know if the user will request it at all
			dialog = new SlideGradientPickerDialog(getScene().getWindow());
			// set the value
			dialog.valueProperty().set(value.get());
			// bind the values
			value.bindBidirectional(this.dialog.valueProperty());
		}
		// show the dialog
		dialog.show();
	}
	
	/**
	 * Returns the value property.
	 * @return ObjectProperty&lt;{@link SlideGradient}&gt;
	 */
	public ObjectProperty<SlideGradient> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the current value of this picker.
	 * @return {@link SlideGradient}
	 */
	public SlideGradient getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the current value of this picker.
	 * @param gradient the desired value
	 */
	public void setValue(SlideGradient gradient) {
		this.value.set(gradient);
	}
}
