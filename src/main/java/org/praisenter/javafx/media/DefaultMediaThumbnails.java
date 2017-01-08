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

import org.praisenter.ThumbnailSettings;

import javafx.scene.image.Image;

/**
 * Represents a class that stores default thumbnails for media items
 * that failed to generate a thumbnail or cannot (audio or streaming video).
 * @author William Bittle
 * @version 3.0.0
 */
final class DefaultMediaThumbnails {
	/** The default image thumbnail */
	private final Image defaultImageThumbnail;
	
	/** The default video thumbnail */
	private final Image defaultVideoThumbnail;
	
	/** The default audio thumbnail */
	private final Image defaultAudioThumbnail;
	
	/**
	 * Creates a set of default thumbnails based on the given settings.
	 * @param thumbnailSettings the thumbnail settings
	 */
	public DefaultMediaThumbnails(ThumbnailSettings thumbnailSettings) {
		final int w = thumbnailSettings.getWidth();
		final int h = thumbnailSettings.getHeight();
		this.defaultImageThumbnail = new Image("/org/praisenter/resources/image-default-thumbnail.png", w, h, true, true, false);
		this.defaultVideoThumbnail = new Image("/org/praisenter/resources/video-default-thumbnail.png", w, h, true, true, false);
		this.defaultAudioThumbnail = new Image("/org/praisenter/resources/music-default-thumbnail.png", w, h, true, true, false);
	}
	
	/**
	 * Returns the default image thumbnail.
	 * @return Image
	 */
	public Image getDefaultImageThumbnail() {
		return this.defaultImageThumbnail;
	}
	
	/**
	 * Returns the default video thumbnail.
	 * @return Image
	 */
	public Image getDefaultVideoThumbnail() {
		return this.defaultVideoThumbnail;
	}
	
	/**
	 * Returns the default audio thumbnail.
	 * @return Image
	 */
	public Image getDefaultAudioThumbnail() {
		return this.defaultAudioThumbnail;
	}
}
