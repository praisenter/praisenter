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

/**
 * Represents the modes that a slide can be in for different processes.
 * @author William Bittle
 * @version 3.0.0
 */
public enum SlideMode {
	/** 
	 * For editing a slide.
	 * <p>
	 * This will show additional borders/UI elements on the slide components
	 * specific to editing.
	 * <p>
	 * All video media will use the single frame. Audio media and 
	 * animations will not be shown.
	 * <p>
	 * Quality: SPEED
	 */
	EDIT,
	
	/**
	 * For taking a snapshot of a slide to use as a thumb nail or image. 
	 * <p>
	 * All video media will use the single frame. Audio media and 
	 * animations will not be included.
	 * <p>
	 * Quality: DEFAULT
	 */
	SNAPSHOT,
	
	/**
	 * For previewing a slide before presentation.
	 * <p>
	 * All video media will use the single frame. Audio media and 
	 * animations will not be included.
	 * <p>
	 * Quality: DEFAULT
	 */
	PREVIEW,
	
	/**
	 * For previewing a slide before presentation.
	 * <p>
	 * This will show all animations and video, but will not play audio.
	 * <p>
	 * Quality: DEFAULT
	 */
	PREVIEW_NO_AUDIO,
		
	/**
	 * Normal presentation.
	 * <p>
	 * This will show all animations and videos and play audio.
	 * <p>
	 * Quality: DEFAULT
	 */
	PRESENT,

	/** TODO not sure how this will be used just yet */
	MUSICIAN
}
