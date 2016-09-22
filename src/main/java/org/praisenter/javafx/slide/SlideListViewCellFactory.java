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

import org.praisenter.javafx.FlowListItem;
import org.praisenter.javafx.FlowListView;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * Cell factory for the {@link FlowListView} specifically for showing slide items.
 * @author William Bittle
 * @version 3.0.0
 */
final class SlideListViewCellFactory implements Callback<SlideListItem, FlowListItem<SlideListItem>> {
	/** The max height for the thumbnails */
	private final int maxHeight;
	
	/**
	 * Creates a new cell factory for slide items.
	 * @param maxHeight the maximum height for the thumbnails
	 */
	public SlideListViewCellFactory(int maxHeight) {
		this.maxHeight = maxHeight;
	}
	
	/* (non-Javadoc)
	 * @see javafx.util.Callback#call(java.lang.Object)
	 */
	@Override
	public FlowListItem<SlideListItem> call(SlideListItem item) {
		FlowListItem<SlideListItem> cell = new FlowListItem<SlideListItem>(item);

		cell.setPrefWidth(110);
		cell.setAlignment(Pos.TOP_CENTER);
		
		String name = null;
		
		if (item.loaded) {
			name = item.name;
	    	// setup the thumbnail image
			BufferedImage image = item.slide.getThumbnail();
			
			Node node = null;
			if (image != null) {
				node = new ImageView(SwingFXUtils.toFXImage(image, null));
			} else {
				Rectangle r = new Rectangle(0, 0, maxHeight / 2, maxHeight);
				r.setFill(new Color(1, 1, 1, 1));
				node = r;
			}
			
			VBox wrapper = new VBox(node);
	    	// place it in a VBox for good positioning
	    	wrapper.setAlignment(Pos.BOTTOM_CENTER);
	    	wrapper.setPrefHeight(maxHeight);
	    	wrapper.setMaxHeight(maxHeight);
	    	wrapper.setMinHeight(maxHeight);
	    	node.setEffect(new DropShadow(2, 2, 2, Color.rgb(0, 0, 0, 0.25)));
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
