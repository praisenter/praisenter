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
package org.praisenter.javafx.bible;

import org.praisenter.bible.BibleSearchResult;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * A custom button to allow searching for bible verses.
 * @author William Bittle
 * @version 3.0.0
 */
final class BibleSearchButton extends Button {
	/** The bible search dialog */
	private Stage dialog;
	
	/** The selected value */
	private final ObjectProperty<BibleSearchResult> value = new SimpleObjectProperty<BibleSearchResult>();
	
	/**
	 * Full constructor.
	 * @param context the praisenter context
	 */
	public BibleSearchButton(PraisenterContext context) {
		this.setText("Search...");
		this.setOnAction((e) -> {
			if (dialog == null) {
				Window owner = getScene().getWindow();
				// create the media dialog
				// passing the owner (we don't create
				// it until the user request for it since
				// 	1. we don't know the owner at creation time
				//  2. we don't know if the user will request it at all
				dialog = new Stage();
				// TODO set the value
				// TODO bind the value of this button to the value of the pane
				dialog.initOwner(owner);
				dialog.setTitle("Bible Search");
				dialog.initModality(Modality.NONE);
				dialog.initStyle(StageStyle.UTILITY);
				dialog.setWidth(800);
				dialog.setHeight(450);
				dialog.setResizable(false);
				
				// build the media library pane
				BibleSearchPane bsp = new BibleSearchPane(context);

				dialog.setScene(Fx.newSceneInheritCss(bsp, getScene().getWindow()));
			}
			// show the dialog
			dialog.show();
		});
	}
	
	/**
	 * Returns the value property.
	 * @return ObjectProperty&lt;{@link BibleSearchResult}&gt;
	 */
	public ObjectProperty<BibleSearchResult> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the current value of this picker.
	 * @return {@link BibleSearchResult}
	 */
	public BibleSearchResult getValue() {
		return this.value.get();
	}
	
	// TODO this shouldn't be valid, instead you should supply a string for the text search
	/**
	 * Sets the current value of this picker.
	 * @param result the desired value
	 */
	public void setValue(BibleSearchResult result) {
		this.value.set(result);
	}
}
