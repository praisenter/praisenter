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
package org.praisenter.media;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.praisenter.images.Images;
import org.praisenter.utilities.ImageUtilities;
import org.praisenter.utilities.WindowUtilities;

/**
 * Concrete class for image media.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class ImageMedia extends AbstractMedia {
	/** The image */
	protected BufferedImage image;
	
	/**
	 * Full constructor.
	 * @param file the file information
	 * @param image the image
	 */
	public ImageMedia(ImageMediaFile file, BufferedImage image) {
		super(file, MediaType.IMAGE);
		this.image = image;
	}
	
	/**
	 * Returns the image.
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		return this.image;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.Media#getThumbnail(java.awt.Dimension)
	 */
	@Override
	public MediaThumbnail getThumbnail(Dimension size) {
		// uniformly scale the image and compute the size and position
		double sw = (double)size.width / (double)this.image.getWidth();
		double sh = (double)size.height / (double)this.image.getHeight();
		double s = sw < sh ? sw : sh;
		int w = (int)Math.ceil((double)this.image.getWidth() * s);
		int h = (int)Math.ceil((double)this.image.getHeight() * s);
		int x = (size.width - w) / 2;
		int y = (size.height - h) / 2;
		
		// use the default device to create an image to render to
		GraphicsDevice device = WindowUtilities.getDefaultDevice();
		GraphicsConfiguration conf = device.getDefaultConfiguration();
		BufferedImage image = conf.createCompatibleImage(size.width, size.height, Transparency.BITMASK);
		
		// render the scaled image
		Graphics2D g = image.createGraphics();
		// render a tiled transparent background first
		if (this.image.getTransparency() != Transparency.OPAQUE) {
			// only render this if the image is translucent or bitmask
			ImageUtilities.renderTiledImage(Images.TRANSPARENT_BACKGROUND, g, 0, 0, size.width, size.height);
		}
		// render the scaled image to the graphics
		g.drawImage(this.image, x, y, w, h, null);
		g.dispose();
		
		// return the thumbnail
		return new MediaThumbnail(this.file, image, this.type);
	}
}
