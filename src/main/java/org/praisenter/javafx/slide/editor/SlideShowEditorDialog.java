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

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.SlideShow;

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
 * A dialog for editing slide shows.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideShowEditorDialog extends BorderPane {
	/** The dialog */
	private final Stage dialog;
	
	/** The slide show editor pane */
	private final SlideShowEditorPane slideShowEditorPane;

	/** The callback to call when the slide show is saved or the user cancels */
	private Consumer<SlideShow> callback = null;
	
	/**
	 * Full constructor.
	 * @param owner the owner of this dialog
	 * @param context the {@link PraisenterContext}
	 */
	public SlideShowEditorDialog(
			Window owner,
			PraisenterContext context) {
		// build the dialog
		this.dialog = new Stage();
		if (owner != null) {
			this.dialog.initOwner(owner);
		}
		this.dialog.setTitle(Translations.get("slide.show.edit.title"));
		this.dialog.initModality(Modality.WINDOW_MODAL);
		this.dialog.initStyle(StageStyle.UTILITY);
		this.dialog.setWidth(600);
		
		// build the media library pane
		this.slideShowEditorPane = new SlideShowEditorPane(context);

		Button btnAccept = new Button(Translations.get("ok"));
		Button btnCancel = new Button(Translations.get("cancel"));
		
		btnAccept.setOnAction(e -> {
			if (this.callback != null) {
				this.callback.accept(this.slideShowEditorPane.getSlideShow());
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
		
		this.setCenter(this.slideShowEditorPane);
		this.setBottom(bottom);
		this.dialog.setScene(Fx.newSceneInheritCss(this, owner));
	}

	/**
	 * Shows this dialog.
	 * @param callback called when the dialog is closed
	 */
	public void show(Consumer<SlideShow> callback) {
		this.callback = callback;
		this.dialog.show();
		
		Fx.centerOnParent(this.dialog);
	}
	
	/**
	 * Returns the selected value.
	 * @return {@link SlideShow}
	 */
	public SlideShow getValue() {
		return this.slideShowEditorPane.getSlideShow();
	}
	
	/**
	 * Sets the selected value.
	 * @param show the show
	 */
	public void setValue(SlideShow show) {
		this.slideShowEditorPane.setSlideShow(show);
	}
}
