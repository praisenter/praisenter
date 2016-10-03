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

import org.praisenter.javafx.FlowListItem;
import org.praisenter.javafx.FlowListView;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.media.MediaType;

import javafx.embed.swing.SwingFXUtils;
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
import javafx.util.Callback;

/**
 * Cell factory for the {@link FlowListView} specifically for showing media items.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaListViewCellFactory implements Callback<MediaListItem, FlowListItem<MediaListItem>> {
	/** The thumbnail settings */
	private final MediaThumbnailSettings settings;
	
	/** The default image thumbnail */
	private final Image defaultImageThumbnail;
	
	/** The default video thumbnail */
	private final Image defaultVideoThumbnail;
	
	/** The default audio thumbnail */
	private final Image defaultAudioThumbnail;
	
	/**
	 * Creates a new cell factory for media items.
	 * @param settings the thumbnail settings
	 */
	public MediaListViewCellFactory(MediaThumbnailSettings settings) {
		this.settings = settings;
		
		int w = settings.getWidth();
		int h = settings.getHeight();
		this.defaultImageThumbnail = new Image("/org/praisenter/resources/image-default-thumbnail.png", w, h, true, true, false);
		this.defaultVideoThumbnail = new Image("/org/praisenter/resources/video-default-thumbnail.png", w, h, true, true, false);
		this.defaultAudioThumbnail = new Image("/org/praisenter/resources/music-default-thumbnail.png", w, h, true, true, false);
	}
	
	/* (non-Javadoc)
	 * @see javafx.util.Callback#call(java.lang.Object)
	 */
	@Override
	public FlowListItem<MediaListItem> call(MediaListItem item) {
		FlowListItem<MediaListItem> cell = new FlowListItem<MediaListItem>(item);

		cell.setPrefWidth(110);
		cell.setAlignment(Pos.TOP_CENTER);
		
		String name = null;
		
		int maxHeight = this.settings.getHeight();
		
		if (item.loaded) {
			name = item.name;
	    	// setup the thumbnail image
			Image image = null;
			if (item.media.getThumbnail() == null) {
				if (item.media.getType() == MediaType.IMAGE) {
					image = this.defaultImageThumbnail;
				} else if (item.media.getType() == MediaType.VIDEO) {
					image = this.defaultVideoThumbnail;
				} else if (item.media.getType() == MediaType.AUDIO) {
					image = this.defaultAudioThumbnail;
				}
			} else {
				image = SwingFXUtils.toFXImage(item.media.getThumbnail(), null);
			}
	    	final ImageView thumb = new ImageView(image);
	    	// place it in a VBox for good positioning
	    	final VBox wrapper = new VBox(thumb);
	    	wrapper.setAlignment(Pos.BOTTOM_CENTER);
	    	wrapper.setPrefHeight(maxHeight);
	    	wrapper.setMaxHeight(maxHeight);
	    	wrapper.setMinHeight(maxHeight);
	    	// only show a drop shadow effect on images that aren't using the default thumbnail
	    	if (item.media.getType() == MediaType.IMAGE) {
	    		thumb.setEffect(new DropShadow(2, 2, 2, Color.rgb(0, 0, 0, 0.25)));
	    	}
	    	cell.getChildren().add(wrapper);
		} else {
			name = item.name;
			// setup an indeterminant progress bar
			ProgressIndicator progress = new ProgressIndicator();
			cell.getChildren().add(progress);
		}
    	
    	// setup the media name label
    	final Label label = new Label();
    	label.setText(name);
    	label.setWrapText(true);
    	label.setTextAlignment(TextAlignment.CENTER);
    	label.setPadding(new Insets(5, 0, 0, 0));
		
    	// add the image and label to the cell
    	cell.getChildren().addAll(label);
    	
		return cell;
	}
}
