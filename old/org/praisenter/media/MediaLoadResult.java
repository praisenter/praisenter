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
package org.praisenter.media;

import java.awt.image.BufferedImage;

/**
 * The results of loading a media item.
 * @author William Bittle
 * @version 3.0.0
 */
final class MediaLoadResult {
	/** The loaded media */
	final Media media;
	
	/** The video frame */
	final BufferedImage frame;
	
	/**
	 * Minimal constructor.
	 * @param media the loaded media
	 */
	public MediaLoadResult(Media media) {
		this.media = media;
		this.frame = null;
	}
	
	/**
	 * Optional constructor (primarily for video).
	 * @param media the loaded media
	 * @param frame the video frame
	 */
	public MediaLoadResult(Media media, BufferedImage frame) {
		this.media = media;
		this.frame = frame;
	}

	/**
	 * Returns the loaded media.
	 * @return {@link Media}
	 */
	public Media getMedia() {
		return this.media;
	}

	/**
	 * Returns the video frame.
	 * @return BufferedImage
	 */
	public BufferedImage getFrame() {
		return frame;
	}
	
}
