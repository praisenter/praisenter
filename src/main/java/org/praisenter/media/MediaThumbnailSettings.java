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

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.praisenter.utility.ImageManipulator;

/**
 * Media library thumbnail settings.
 * <p>
 * Used to specify the size of the thumbnails to generate and the default
 * thumbnails when a thumbnail cannot be created.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaThumbnailSettings {
	/** The desired thumbnail width */
	final int width;
	
	/** The desired thumbnail height */
	final int height;
	
	/** The scaled default image thumbnail */
	final BufferedImage imageDefaultThumbnail;
	
	/** The scaled default audio thumbnail */
	final BufferedImage audioDefaultThumbnail;
	
	/** The scaled default video thumbnail */
	final BufferedImage videoDefaultThumbnail;
	
	/**
	 * Full constructor.
	 * <p>
	 * Generated thumbnails will retain their aspect ratio and fit within
	 * the given width and height.
	 * <p>
	 * The default thumbnails will be sized appropriately if they aren't already.
	 * @param width the desired thumbnail width
	 * @param height the desired thumbnail height
	 * @param imageDefaultThumbnail the default image thumbnail
	 * @param audioDefaultThumbnail the default audio thumbnail
	 * @param videoDefaultThumbnail the default video thumbnail
	 */
	public MediaThumbnailSettings(
			int width, 
			int height,
			BufferedImage imageDefaultThumbnail, 
			BufferedImage audioDefaultThumbnail,
			BufferedImage videoDefaultThumbnail) {
		super();
		this.width = width;
		this.height = height;
		
		// make sure they are the right size
		this.imageDefaultThumbnail = ImageManipulator.getUniformScaledImage(imageDefaultThumbnail, width, height, AffineTransformOp.TYPE_BICUBIC);
		this.audioDefaultThumbnail = ImageManipulator.getUniformScaledImage(audioDefaultThumbnail, width, height, AffineTransformOp.TYPE_BICUBIC);
		this.videoDefaultThumbnail = ImageManipulator.getUniformScaledImage(videoDefaultThumbnail, width, height, AffineTransformOp.TYPE_BICUBIC);
	}

	/**
	 * Returns the desired thumbnail width.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Returns the desired thumbnail height.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Returns the scaled default image thumbnail.
	 * @return int
	 */
	public BufferedImage getImageDefaultThumbnail() {
		return this.imageDefaultThumbnail;
	}

	/**
	 * Returns the scaled default audio thumbnail.
	 * @return int
	 */
	public BufferedImage getAudioDefaultThumbnail() {
		return this.audioDefaultThumbnail;
	}

	/**
	 * Returns the scaled default video thumbnail.
	 * @return int
	 */
	public BufferedImage getVideoDefaultThumbnail() {
		return this.videoDefaultThumbnail;
	}
}
