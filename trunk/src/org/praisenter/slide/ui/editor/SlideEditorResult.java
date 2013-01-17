/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.slide.ui.editor;

import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideThumbnail;

/**
 * Represents the result from slide editor.
 * <p>
 * A user can make modifications to a slide or template which update the slide and its thumbnail.
 * In addition, the user can cancel, save or save as a slide or template.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SlideEditorResult {
	/** The user's choice (cancel, save, save as) */
	protected SlideEditorOption choice;
	
	/** The updated slide; null on cancel */
	protected Slide slide;
	
	/** The updated thumbnail; null on cancel */
	protected SlideThumbnail thumbnail;
	
	/**
	 * Returns the user's choice: cancel, save, or save as.
	 * @return {@link SlideEditorOption}
	 */
	public SlideEditorOption getChoice() {
		return this.choice;
	}
	
	/**
	 * Returns the updated slide.
	 * <p>
	 * This will be null if the user canceled the action.
	 * @return {@link Slide}
	 */
	public Slide getSlide() {
		return this.slide;
	}
	
	/**
	 * Returns the updated slide thumbnail.
	 * <p>
	 * This will be null if the user canceled the action.
	 * @return {@link SlideThumbnail}
	 */
	public SlideThumbnail getThumbnail() {
		return this.thumbnail;
	}
}
