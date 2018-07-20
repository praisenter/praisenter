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
package org.praisenter.javafx.slide;

import org.praisenter.javafx.ImageCache;
import org.praisenter.javafx.controls.FlowListCell;
import org.praisenter.slide.Slide;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Custom list cell for {@link SlideListItem}s.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class SlideFlowListCell extends FlowListCell<SlideListItem> {
	/** The slide for this cell */
	private final ObjectProperty<Slide> slide = new SimpleObjectProperty<Slide>(null);
	
	/**
	 * Minimal constructor.
	 * @param item the slide item
	 * @param imageCache the image cache
	 * @param maxHeight the max height for the cell
	 */
	public SlideFlowListCell(SlideListItem item, ImageCache imageCache, int maxHeight) {
		super(item);
		
		this.getStyleClass().add("slide-list-cell");
		
		final Pane pane = new Pane();
		pane.getStyleClass().add("slide-list-cell-thumbnail");
		
    	final ImageView thumb = new ImageView();
    	pane.getChildren().add(thumb);
    	
    	// place it in a VBox for good positioning
    	final VBox wrapper = new VBox(pane);
    	wrapper.getStyleClass().add("slide-list-cell-wrapper");
    	wrapper.setPrefHeight(maxHeight);
    	wrapper.setMaxHeight(maxHeight);
    	wrapper.setMinHeight(maxHeight);
    	wrapper.managedProperty().bind(wrapper.visibleProperty());
    	this.getChildren().add(wrapper);

		// setup an indeterminant progress bar
		ProgressIndicator progress = new ProgressIndicator();
		progress.getStyleClass().add("slide-list-cell-progress");
		progress.managedProperty().bind(progress.visibleProperty());
		this.getChildren().add(progress);
		
		this.slide.addListener((obs, ov, nv) -> {
			// setup the thumbnail image
			Image image = null;
			if (nv != null) {
				if (nv.getThumbnail() != null) {
					image = imageCache.getOrLoadThumbnail(nv.getId(), nv.getThumbnail());
				}
			}
			
			thumb.setImage(image);
    		pane.setMaxWidth(image.getWidth());
		});
		
		wrapper.visibleProperty().bind(item.loadedProperty());
		progress.visibleProperty().bind(item.loadedProperty().not());
		this.slide.bind(item.slideProperty());
		
    	// setup the slide name label
    	final Label label = new Label();
    	label.getStyleClass().add("slide-list-cell-name");
    	label.textProperty().bind(item.nameProperty());
		
    	// add the image and label to the cell
    	this.getChildren().addAll(label);
	}
}
