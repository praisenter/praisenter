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
package org.praisenter.javafx.slide.editor.controls;

import org.controlsfx.control.PopOver;
import org.praisenter.slide.graphics.SlideGradient;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

/**
 * A dialog for selecting a gradient.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideGradientPickerDialog extends BorderPane {
	/** The dialog */
//	private final Stage dialog;
	
	private final PopOver dialog;
	
	/** The media library pane */
	private final SlideGradientPickerPane gradientPane;

	/**
	 * Full constructor.
	 * @param owner the owner of this dialog
	 */
	public SlideGradientPickerDialog(Window owner) {
		// build the dialog
		this.dialog = new PopOver();
		this.dialog.setDetachable(false);
		this.dialog.setAutoFix(false);
		
		// build the media library pane
		this.gradientPane = new SlideGradientPickerPane();
		this.setCenter(this.gradientPane);
		
		this.dialog.setContentNode(this);
	}
	
	/**
	 * Shows this dialog.
	 * @param owner the owning node
	 */
	public void show(Node owner) {
		this.dialog.show(owner);
	}
	
	/**
	 * Returns the selected gradient property.
	 * @return ObjectProperty&lt;{@link SlideGradient}&gt;
	 */
	public ObjectProperty<SlideGradient> valueProperty() {
		return this.gradientPane.gradientProperty();
	}
	
	/**
	 * Returns the selected gradient.
	 * @return {@link SlideGradient}
	 */
	public SlideGradient getValue() {
		return this.gradientPane.getGradient();
	}
	
	/**
	 * Sets the selected gradient.
	 * @param gradient the gradient
	 */
	public void setValue(SlideGradient gradient) {
		this.gradientPane.setGradient(gradient);
	}
}