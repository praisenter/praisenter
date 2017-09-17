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
package org.praisenter.javafx.media;

import org.praisenter.MediaType;
import org.praisenter.ThumbnailSettings;
import org.praisenter.javafx.controls.FlowListCell;
import org.praisenter.media.Media;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Represents a {@link FlowListCell} for {@link MediaListItem}s.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaFlowListCell extends FlowListCell<MediaListItem> {
	/** The media */
	private final ObjectProperty<Media> media = new SimpleObjectProperty<Media>(null);
	
	/**
	 * Minimal constructor.
	 * @param item the media list item
	 * @param thumbnailSettings the thumbnail settings
	 * @param defaultThumbnails the default thumbnails
	 */
	public MediaFlowListCell(MediaListItem item, ThumbnailSettings thumbnailSettings, DefaultMediaThumbnails defaultThumbnails) {
		super(item);
		
		final int maxHeight = thumbnailSettings.getHeight();
		
		this.getStyleClass().add("media-list-cell");
		
		// setup an image view for loaded items
    	final ImageView thumb = new ImageView();
    	thumb.getStyleClass().add("media-list-cell-thumbnail");
    	thumb.managedProperty().bind(thumb.visibleProperty());
    	
		// setup an indeterminant progress bar for pending items
		final ProgressIndicator progress = new ProgressIndicator();
		progress.getStyleClass().add("media-list-cell-progress");
		progress.managedProperty().bind(progress.visibleProperty());
		
		// place it in a VBox for good positioning
		final VBox wrapper = new VBox(thumb, progress);
		wrapper.getStyleClass().add("media-list-cell-wrapper");
    	wrapper.setPrefHeight(maxHeight);
    	wrapper.setMaxHeight(maxHeight);
    	wrapper.setMinHeight(maxHeight);
		this.getChildren().add(wrapper);
		
		this.media.addListener((obs, ov, nv) -> {
			// setup the thumbnail image
			String clazz = null;
			Image image = null;
			if (nv != null) {
				if (nv.getType() == MediaType.IMAGE) {
					clazz = "media-list-cell-thumbnail-image";
				} else if (nv.getType() == MediaType.VIDEO) {
					clazz = "media-list-cell-thumbnail-video";
				} else if (nv.getType() == MediaType.AUDIO) {
					clazz = "media-list-cell-thumbnail-audio";
				}
				if (nv.getThumbnail() == null) {
					if (nv.getType() == MediaType.IMAGE) {
						image = defaultThumbnails.getDefaultImageThumbnail();
					} else if (nv.getType() == MediaType.VIDEO) {
						image = defaultThumbnails.getDefaultVideoThumbnail();
					} else if (nv.getType() == MediaType.AUDIO) {
						image = defaultThumbnails.getDefaultAudioThumbnail();
					}
				} else {
					image = SwingFXUtils.toFXImage(nv.getThumbnail(), null);
				}
			}
			
			thumb.getStyleClass().setAll("media-list-cell-thumbnail");
			if (clazz != null) {
				thumb.getStyleClass().add(clazz);
			}
			thumb.setImage(image);
		});
		
		thumb.visibleProperty().bind(item.loadedProperty());
		progress.visibleProperty().bind(item.loadedProperty().not());
		this.media.bind(item.mediaProperty());
		
    	// setup the media name label
    	final Label label = new Label();
    	label.getStyleClass().add("media-list-cell-name");
    	label.textProperty().bind(item.nameProperty());
		
    	// add the image and label to the cell
    	this.getChildren().addAll(label);
	}
}
