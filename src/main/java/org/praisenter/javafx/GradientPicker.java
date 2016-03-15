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
package org.praisenter.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Paint;

import org.praisenter.resources.translations.Translations;

/**
 * A custom button to allow selection of a gradient.
 * @author William Bittle
 * @version 3.0.0
 */
public final class GradientPicker extends Button {
	/** The gradient dialog */
	private GradientPickerDialog dialog;
	
	/** The selected value */
	private final ObjectProperty<Paint> value = new SimpleObjectProperty<Paint>();
	
	/**
	 * Full constructor.
	 * @param gradient the initial value
	 */
	public GradientPicker(Paint gradient) {
		this.setText(Translations.get("choose"));
		this.setOnAction((e) -> {
			if (dialog == null) {
				// create the dialog
				// passing the owner (we don't create
				// it until the user request for it since
				// 	1. we don't know the owner at creation time
				//  2. we don't know if the user will request it at all
				dialog = new GradientPickerDialog(getScene().getWindow(), gradient);
				// set the value
				dialog.valueProperty().set(value.get());
				// bind the values
				value.bindBidirectional(this.dialog.valueProperty());
			}
			// show the dialog
			dialog.show();
		});
	}
	
	/**
	 * Returns the value property.
	 * @return ObjectProperty&lt;Paint&gt;
	 */
	public ObjectProperty<Paint> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the current value of this picker.
	 * @return Paint
	 */
	public Paint getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the current value of this picker.
	 * @param gradient the desired value
	 */
	public void setValue(Paint gradient) {
		this.value.set(gradient);
	}
}