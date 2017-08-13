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

import java.util.function.Consumer;

import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.graphics.SlideGradient;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * A dialog for selecting a gradient.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideGradientPickerDialog extends BorderPane {
	/** The dialog */
	private final Stage dialog;
	
	/** The media library pane */
	private final SlideGradientPickerPane gradientPane;

	/** The selected gradient */
	private final ObjectProperty<SlideGradient> value = new SimpleObjectProperty<SlideGradient>();
	
	/** The callback to call when the gradient is selected */
	private Consumer<SlideGradient> callback = null;
	
	/**
	 * Full constructor.
	 * @param owner the owner of this dialog
	 */
	public SlideGradientPickerDialog(Window owner) {
		// build the dialog
		this.dialog = new Stage();
		if (owner != null) {
			this.dialog.initOwner(owner);
		}
		this.dialog.setTitle(Translations.get(""));
		this.dialog.initModality(Modality.WINDOW_MODAL);
		this.dialog.initStyle(StageStyle.UTILITY);
		// NOTE: this makes the title portion of the modal shorter
		this.dialog.setResizable(false);
		
		// build the media library pane
		this.gradientPane = new SlideGradientPickerPane();

		// TODO translate
		Button btnAccept = new Button("OK");
		Button btnCancel = new Button("Cancel");
		
		// when our value changes, update the media pane
		this.value.addListener((obs, ov, nv) -> {
			this.gradientPane.setGradient(nv);
		});
		
		btnAccept.setOnAction(e -> {
			this.value.setValue(this.gradientPane.getGradient());
			if (this.callback != null) {
				this.callback.accept(this.value.get());
			}
			this.dialog.close();
		});

		btnCancel.setOnAction(e -> {
			this.dialog.close();
		});
		
		btnAccept.setPadding(new Insets(5, 15, 5, 15));
		btnCancel.setPadding(new Insets(5, 15, 5, 15));
		HBox bottom = new HBox(5, btnAccept, btnCancel);
		bottom.setAlignment(Pos.BASELINE_RIGHT);
		bottom.setPadding(new Insets(5));
		
		this.setCenter(this.gradientPane);
		this.setBottom(bottom);
		this.dialog.setScene(Fx.newSceneInheritCss(this, owner));
	}
	
	/**
	 * Shows this dialog.
	 * @param x the screen x for the dialog
	 * @param y the screen y for the dialog
	 * @param callback called when the dialog is closed
	 */
	public void show(double x, double y, Consumer<SlideGradient> callback) {
		this.callback = callback;
		this.dialog.setX(x);
		this.dialog.setY(y);
		this.dialog.show();
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