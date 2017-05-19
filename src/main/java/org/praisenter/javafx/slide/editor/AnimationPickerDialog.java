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

import java.util.function.Consumer;

import org.praisenter.javafx.utility.Fx;
import org.praisenter.slide.animation.Animation;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * A dialog for building an animation.
 * @author William Bittle
 * @version 3.0.0
 */
final class AnimationPickerDialog extends BorderPane {
	/** The dialog */
	private final Stage dialog;
	
	/** The animation picker pane */
	private final AnimationPickerPane animationPickerPane;
	
	/** The configured animation */
	private final ObjectProperty<Animation> value = new SimpleObjectProperty<Animation>();
	
	private Consumer<Animation> callback = null;
	
	/**
	 * Full constructor.
	 * @param owner the owner of this dialog
	 */
	public AnimationPickerDialog(Window owner) {
		// build the dialog
		this.dialog = new Stage();
		if (owner != null) {
			this.dialog.initOwner(owner);
		}
		// TODO translate
		this.dialog.setTitle("Animation Picker");
		this.dialog.initModality(Modality.WINDOW_MODAL);
		this.dialog.initStyle(StageStyle.UTILITY);
//		this.dialog.setWidth(800);
//		this.dialog.setHeight(450);
		// NOTE: this makes the title portion of the modal shorter
		this.dialog.setResizable(false);
		
		// build the animation picker pane
		this.animationPickerPane = new AnimationPickerPane();
		
		this.value.addListener((obs, ov, nv) -> {
			this.animationPickerPane.setValue(nv);
		});
		
		Button btnAccept = new Button("OK");
		btnAccept.setOnAction(e -> {
			this.value.setValue(this.animationPickerPane.getValue());
			if (this.callback != null) {
				this.callback.accept(this.value.get());
			}
		});
		
		this.setCenter(this.animationPickerPane);
		this.setBottom(btnAccept);
		this.dialog.setScene(Fx.newSceneInheritCss(this, owner));
	}
	
	/**
	 * Shows this dialog.
	 * @param callback called when the dialog is closed
	 */
	public void show(Consumer<Animation> callback) {
		this.callback = callback;
		this.dialog.show();
	}
	
	/**
	 * Returns the selected value property.
	 * @return ObjectProperty&lt;{@link Animation}&gt;
	 */
	public ObjectProperty<Animation> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the selected value.
	 * @return {@link Animation}
	 */
	public Animation getValue() {
		return this.value.getValue();
	}
	
	/**
	 * Sets the selected value.
	 * @param animation the animation
	 */
	public void setValue(Animation animation) {
		this.value.setValue(animation);
	}
}
