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

import org.praisenter.utility.ImageManipulator;

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.ResampleOp;

/**
 * An abstract implementation of the {@link MediaLoader} interface.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class AbstractMediaLoader implements MediaLoader {
	/** The context */
	protected final MediaLibraryContext context;
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public AbstractMediaLoader(MediaLibraryContext context) {
		this.context = context;
	}
	
	/**
	 * Creates a thumbnail from the given image using the current
	 * thumbnail settings.
	 * @param image the full size image
	 * @return BufferedImage
	 */
	protected final BufferedImage createThumbnail(BufferedImage image) {
		// convert the image to an image type with transparency first
		BufferedImage withTransparency = ImageUtil.toBuffered(image, BufferedImage.TYPE_INT_ARGB);
		// then down scale
		return ImageManipulator.getUniformScaledImage(
				withTransparency, 
				this.context.getThumbnailSettings().getWidth(), 
				this.context.getThumbnailSettings().getHeight(), 
				ResampleOp.FILTER_LANCZOS);
	}
}
