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

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.resources.translations.Translations;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableStringValue;
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
public final class BibleSearchButton extends Button {
	/** The bible search dialog */
	private Stage dialog;
	
	/** The search text */
	private final StringProperty searchText = new SimpleStringProperty();
	
	/** The selected value */
	private final ObjectProperty<SelectedBibleSearchResult> value = new SimpleObjectProperty<SelectedBibleSearchResult>();
	
	/**
	 * Full constructor.
	 * @param context the praisenter context
	 */
	public BibleSearchButton(PraisenterContext context) {
		this.setText(Translations.get("search"));
		this.setOnAction((e) -> {
			if (dialog == null) {
				this.value.unbind();
				this.searchText.unbind();
				
				Window owner = getScene().getWindow();
				// create the media dialog
				// passing the owner (we don't create
				// it until the user request for it since
				// 	1. we don't know the owner at creation time
				//  2. we don't know if the user will request it at all
				dialog = new Stage();
				dialog.initOwner(owner);
				dialog.setTitle(Translations.get("bible.search.title"));
				dialog.initModality(Modality.NONE);
				dialog.initStyle(StageStyle.UTILITY);
				dialog.setWidth(800);
				dialog.setHeight(450);
				dialog.setResizable(false);
				
				// build the media library pane
				BibleSearchPane bsp = new BibleSearchPane(context);
				this.value.bind(bsp.valueProperty());
				
				this.searchText.addListener((obs, ov, nv) -> {
					bsp.setText(nv);
				});
				
				dialog.setScene(Fx.newSceneInheritCss(bsp, getScene().getWindow()));
			}
			// show the dialog
			dialog.show();
		});
	}
	
	/**
	 * Returns the value property.
	 * @return ReadOnlyObjectProperty&lt;{@link SelectedBibleSearchResult}&gt;
	 */
	public ReadOnlyObjectProperty<SelectedBibleSearchResult> valueProperty() {
		return this.value;
	}
	
	/**
	 * Returns the current value of this picker.
	 * @return {@link SelectedBibleSearchResult}
	 */
	public SelectedBibleSearchResult getValue() {
		return this.value.get();
	}
	
	/**
	 * Sets the search text.
	 * @param search the search text
	 */
	public void setSearchText(String search) {
		this.searchText.set(search);
	}
	
	/**
	 * Returns the search text property.
	 * @return StringProperty
	 */
	public WritableStringValue searchTextProperty() {
		return this.searchText;
	}
}
