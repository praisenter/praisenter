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
package org.praisenter.javafx.slide.editor;

import org.praisenter.javafx.controls.FlowListCell;
import org.praisenter.slide.animation.Animation;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * Represents an animation option list cell.
 * @author William Bittle
 * @version 3.0.0
 */
final class AnimationOptionListCell extends FlowListCell<AnimationOption> {
	/**
	 * Constructor.
	 * @param option the option
	 */
	public AnimationOptionListCell(AnimationOption option) {
		super(option);
		
		this.getStyleClass().add("animation-option-list-cell");
		
    	// setup the thumbnail image
    	final ImageView thumb = new ImageView();
    	thumb.getStyleClass().add("animation-option-list-cell-thumbnail");
    	thumb.getStyleClass().add("animation-option-" + (Animation.class.isAssignableFrom(option.type) ? "animation-" : "easing-") + option.type.getSimpleName());
    	this.getChildren().add(thumb);
    	
    	// setup the media name label
    	final Label label = new Label();
    	label.setText(option.name);
    	label.getStyleClass().add("animation-option-list-cell-name");
    	this.getChildren().addAll(label);
	}
}
