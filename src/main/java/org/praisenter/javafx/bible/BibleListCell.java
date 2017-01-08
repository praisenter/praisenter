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

import org.praisenter.javafx.FlowListCell;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 * Custom list cell for {@link BibleListItem}s.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class BibleListCell extends FlowListCell<BibleListItem> {
	/** The shared icon */
	private static final Image ICON = new Image("/org/praisenter/resources/bible-icon.png");
	
	/**
	 * Minimal constructor.
	 * @param item the bible item
	 */
	public BibleListCell(BibleListItem item) {
		super(item);
		
		this.setPrefWidth(110);
		this.setAlignment(Pos.TOP_CENTER);
		
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView(ICON);
    	thumb.setFitHeight(100);
    	thumb.setPreserveRatio(true);
		thumb.setEffect(new DropShadow(2, 2, 2, Color.rgb(0, 0, 0, 0.25)));
		thumb.managedProperty().bind(thumb.visibleProperty());
		
		// setup an indeterminant progress bar
		ProgressIndicator progress = new ProgressIndicator();
		progress.managedProperty().bind(progress.visibleProperty());
		
		// place it in a VBox for good positioning
    	final VBox wrapper = new VBox(thumb, progress);
    	wrapper.setAlignment(Pos.BOTTOM_CENTER);
    	wrapper.setPrefHeight(100);
    	wrapper.setMaxHeight(100);
    	wrapper.setMinHeight(100);
    	this.getChildren().add(wrapper);
		
		thumb.visibleProperty().bind(item.loadedProperty());
		progress.visibleProperty().bind(item.loadedProperty().not());
    	
    	// setup the media name label
    	final Label label = new Label();
    	label.textProperty().bind(item.nameProperty());
    	label.setWrapText(true);
    	label.setTextAlignment(TextAlignment.CENTER);
    	label.setPadding(new Insets(5, 0, 0, 0));
		
    	// add the image and label to the cell
    	this.getChildren().addAll(label);
	}
}
