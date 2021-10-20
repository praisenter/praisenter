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

import java.awt.image.BufferedImage;
import java.util.UUID;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.controls.FlowListCell;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideShow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Custom list cell for {@link SlideShowListItem}s.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class SlideShowFlowListCell extends FlowListCell<SlideShowListItem> {
	/** The slide for this cell */
	private final ObjectProperty<SlideShow> show = new SimpleObjectProperty<SlideShow>(null);
	
	/**
	 * Minimal constructor.
	 * @param context the praisenter context
	 * @param item the slide item
	 * @param maxHeight the max height for the cell
	 */
	public SlideShowFlowListCell(PraisenterContext context, SlideShowListItem item, int maxHeight) {
		super(item);
		
		this.getStyleClass().add("slide-show-list-cell");
		
		final Pane pane = new Pane();
		
    	final ImageView thumb1 = new ImageView();
    	final ImageView thumb2 = new ImageView();
    	final ImageView thumb3 = new ImageView();
    	final Pane pane1 = new Pane(thumb1);
    	final Pane pane2 = new Pane(thumb2);
    	final Pane pane3 = new Pane(thumb3);
    	pane3.setLayoutY(-10);
    	pane3.setLayoutX(0);
    	pane2.setLayoutY(0);
    	pane2.setLayoutX(10);
    	pane1.setLayoutY(10);
    	pane1.setLayoutX(20);
    	
		pane1.getStyleClass().add("slide-show-list-cell-thumbnail");
		pane2.getStyleClass().add("slide-show-list-cell-thumbnail");
		pane3.getStyleClass().add("slide-show-list-cell-thumbnail");
    	pane.getChildren().addAll(pane3, pane2, pane1);
    	
    	// place it in a VBox for good positioning
    	final VBox wrapper = new VBox(pane);
    	wrapper.getStyleClass().add("slide-show-list-cell-wrapper");
    	wrapper.setPrefHeight(maxHeight);
    	wrapper.setMaxHeight(maxHeight);
    	wrapper.setMinHeight(maxHeight);
    	wrapper.managedProperty().bind(wrapper.visibleProperty());
    	this.getChildren().add(wrapper);

		// setup an indeterminant progress bar
		ProgressIndicator progress = new ProgressIndicator();
		progress.getStyleClass().add("slide-show-list-cell-progress");
		progress.managedProperty().bind(progress.visibleProperty());
		this.getChildren().add(progress);
		
		this.show.addListener((obs, ov, nv) -> {
			// setup the thumbnail images
			Image image1 = null;
			Image image2 = null;
			Image image3 = null;
			if (nv != null) {
				int size = nv.getSlides().size();
				for (int i = 0; i < size; i++) {
					if (i >= 3) break;
					UUID slideId = nv.getSlides().get(i).getSlideId();
					Slide slide = context.getSlideLibrary().getSlide(slideId);
					BufferedImage image = slide != null ? slide.getThumbnail() : null;
					if (i == 0) image1 = context.getImageCache().getOrLoadThumbnail(slideId, image);
					if (i == 1) image2 = context.getImageCache().getOrLoadThumbnail(slideId, image);
					if (i == 2) image3 = context.getImageCache().getOrLoadThumbnail(slideId, image);
				}
			}
			
			thumb1.setImage(image1);
			thumb2.setImage(image2);
			thumb3.setImage(image3);
		});
		
		wrapper.visibleProperty().bind(item.loadedProperty());
		progress.visibleProperty().bind(item.loadedProperty().not());
		this.show.bind(item.slideShowProperty());
		
    	// setup the slide name label
    	final Label label = new Label();
    	label.getStyleClass().add("slide-show-list-cell-name");
    	label.textProperty().bind(item.nameProperty());
		
    	// add the image and label to the cell
    	this.getChildren().addAll(label);
	}
}
