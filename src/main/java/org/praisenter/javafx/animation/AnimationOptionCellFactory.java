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
package org.praisenter.javafx.animation;

import org.praisenter.javafx.FlowListItem;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

/**
 * A custom cell factory for animation options.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class AnimationOptionCellFactory implements Callback<AnimationOption, FlowListItem<AnimationOption>> {
	/* (non-Javadoc)
	 * @see javafx.util.Callback#call(java.lang.Object)
	 */
	@Override
	public FlowListItem<AnimationOption> call(AnimationOption option) {
		FlowListItem<AnimationOption> cell = new FlowListItem<AnimationOption>(option);
		
		cell.setPrefSize(100, 80);
		
		String name = null;
		
		name = option.name;
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView(option.image);
    	cell.getChildren().add(thumb);
    	
    	// setup the media name label
    	final Label label = new Label();
    	label.setText(name);
    	label.setWrapText(true);
    	label.setTextAlignment(TextAlignment.CENTER);
    	label.setTooltip(new Tooltip(name));
    	label.setPadding(new Insets(5, 0, 0, 0));
		
    	// add the image and label to the cell
    	cell.getChildren().addAll(label);
    	
		return cell;
	}
}
