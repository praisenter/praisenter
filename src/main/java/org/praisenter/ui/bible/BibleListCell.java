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
package org.praisenter.ui.bible;

import org.praisenter.data.bible.Bible;
import org.praisenter.ui.controls.FlowListCell;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Custom list cell for {@link BibleListItem}s.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class BibleListCell extends FlowListCell<Bible> {
	/**
	 * Minimal constructor.
	 * @param item the bible item
	 */
	public BibleListCell(Bible item) {
		super(item);
		this.getStyleClass().add("bible-list-cell");
		
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView();
    	thumb.getStyleClass().add("bible-list-cell-thumbnail");
    	thumb.setFitHeight(100);
    	thumb.setPreserveRatio(true);
		//thumb.managedProperty().bind(thumb.visibleProperty());
		
		// setup an indeterminant progress bar
//		ProgressIndicator progress = new ProgressIndicator();
//		progress.getStyleClass().add("bible-list-cell-progress");
//		progress.managedProperty().bind(progress.visibleProperty());
		
		// place it in a VBox for good positioning
    	final VBox wrapper = new VBox(thumb);
    	wrapper.getStyleClass().add("bible-list-cell-wrapper");
    	this.getChildren().add(wrapper);
		
//		thumb.visibleProperty().bind(item.loadedProperty());
//		progress.visibleProperty().bind(item.loadedProperty().not());
    	
    	// setup the media name label
    	final Label label = new Label();
    	label.getStyleClass().add("bible-list-cell-name");
    	label.textProperty().bind(item.nameProperty());
		
    	// add the image and label to the cell
    	this.getChildren().addAll(label);
	}
}
