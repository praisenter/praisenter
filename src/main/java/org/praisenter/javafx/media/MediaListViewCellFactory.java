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

import org.praisenter.javafx.FlowListItemView;
import org.praisenter.javafx.FlowListView;
import org.praisenter.media.MediaType;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * Cell factory for the {@link FlowListView} specifically for showing media items.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaListViewCellFactory implements Callback<MediaListItem, FlowListItemView<MediaListItem>> {
	/* (non-Javadoc)
	 * @see javafx.util.Callback#call(java.lang.Object)
	 */
	@Override
	public FlowListItemView<MediaListItem> call(MediaListItem item) {
		FlowListItemView<MediaListItem> cell = new FlowListItemView<MediaListItem>(item);
		
		cell.setPrefSize(130, 130);
		
		String name = null;
		
		if (item.loaded) {
			name = item.name;
	    	// setup the thumbnail image
	    	final ImageView thumb = new ImageView(SwingFXUtils.toFXImage(item.media.getThumbnail(), null));
	    	// only show a drop shadow effect on images that aren't using the default thumbnail
	    	if (item.media.getMetadata().getType() == MediaType.IMAGE) {
	    		thumb.setEffect(new DropShadow(2, 2, 2, Color.rgb(0, 0, 0, 0.25)));
	    	}
	    	cell.getChildren().add(thumb);
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
