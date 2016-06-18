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
package org.praisenter.javafx;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * Used to save on loads from the file system, this class will cache JavaFX images
 * based on their path.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ImageCache {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The cache */
	private final Map<Path, SoftReference<Image>> images;
	
	/**
	 * Constructor.
	 */
	public ImageCache() {
		this.images = new HashMap<Path, SoftReference<Image>>();
	}
	
	/**
	 * Returns the image for the given path, loading it if necessary.
	 * <p>
	 * Returns null in the event that an error occurs.
	 * @param path the path
	 * @return Image
	 */
	public synchronized Image get(Path path) {
		this.evict();
		if (this.images.containsKey(path)) {
			Image image = this.images.get(path).get();
			if (image != null) {
				return image;
			}
		}
		try {
			Image image = this.load(path);
			this.images.put(path, new SoftReference<Image>(image));
			return image;
		} catch (IOException ex) {
			LOGGER.error("Failed to load image " + path.toAbsolutePath().toString(), ex);
		}
		return null;
	}
	
	/**
	 * Removes keys from the cache whose soft references have been collected.
	 */
	private void evict() {
		Iterator<SoftReference<Image>> it = this.images.values().iterator();
		while (it.hasNext()) {
			SoftReference<Image> ref = it.next();
			if (ref.get() == null) {
				it.remove();
			}
		}
	}
	
	/**
	 * Loads an Image from the given path.
	 * @param path the path
	 * @return Image
	 * @throws IOException 
	 */
	private Image load(Path path) throws IOException {
		// using ImageIO and twelvemonkeys lib allows for more supported formats
		return SwingFXUtils.toFXImage(ImageIO.read(path.toFile()), null);
	}
}
