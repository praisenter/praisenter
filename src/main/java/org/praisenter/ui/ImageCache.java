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
package org.praisenter.ui;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

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
	private final Map<ImageCacheKey, SoftReference<Image>> images;
	
	/**
	 * Constructor.
	 */
	public ImageCache() {
		this.images = new HashMap<ImageCacheKey, SoftReference<Image>>();
	}
	
	/**
	 * Returns the image for the given path, loading it if necessary.
	 * <p>
	 * Returns null in the event that an error occurs.
	 * @param key the image cache key
	 * @param supplier the function to load the image if it doesn't exist
	 * @return Image
	 */
	private synchronized Image getOrLoad(ImageCacheKey key, Supplier<Image> supplier) {
		this.evict();
		if (this.images.containsKey(key)) {
			Image image = this.images.get(key).get();
			if (image != null) {
				LOGGER.debug("Image for key: {} found in cache.", key);
				return image;
			}
		}
		LOGGER.debug("Image for key: {} was not found in the cache. Loading...", key);
		Image image = supplier.get();
		if (image != null) {
			LOGGER.debug("Image loaded for key: {}", key);
			this.images.put(key, new SoftReference<Image>(image));
		} else {
			LOGGER.debug("Image was loaded but was null.", key);
		}
		return image;
	}
	
	// helpers
	
	/**
	 * Returns the cached image for the given buffered image or converts the
	 * given image to a Java FX image.
	 * @param id the unique identifer
	 * @param thumbnail the image
	 * @return Image
	 */
	public synchronized Image getOrLoadThumbnail(UUID id, Path path) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.THUMBNAIL, id.toString());
		return getOrLoad(key, () -> {
			try {
				LOGGER.debug("Converting BufferedImage into Image.");
				return this.load(path);
			} catch (Exception ex) {
				LOGGER.error("Failed to convert buffered image for '" + id + "'", ex);
			}
			return null;
		});
	}
	
	/**
	 * Returns the cached image for the given image media or loads the image given the path
	 * if the image is not in the cache.
	 * @param id the id
	 * @param path the path to the image
	 * @return Image
	 */
	public synchronized Image getOrLoadImage(UUID id, Path path) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.MEDIA_IMAGE, id.toString());
		return getOrLoad(key, () -> {
			try {
				return this.load(path);
			} catch (Exception ex) {
				LOGGER.error("Failed to load image from path '" + path.toAbsolutePath().toString() + "'", ex);
			}
			return null;
		});
	}

	/**
	 * Returns the cached image for the given image or loads the image given the classpath
	 * path if the image is not in the cache.
	 * <p>
	 * This should be used for application images.
	 * @param classpath the classpath path to the image
	 * @return Image
	 */
	public synchronized Image getOrLoadClasspathImage(String classpath) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.APPLICATION_IMAGE, classpath);
		return getOrLoad(key, () -> {
			try {
				LOGGER.debug("Loading image from classpath '{}'.", classpath);
				return new Image(classpath);
			} catch (Exception ex) {
				LOGGER.error("Failed to load image from path '" + classpath + "'", ex);
			}
			return null;
		});
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
				LOGGER.debug("Image with key '{}' has been evicted from the image cache.");
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
		LOGGER.debug("Loading image at path '{}'", path.toAbsolutePath().toString());
		// using ImageIO and twelvemonkeys lib allows for more supported formats
		return SwingFXUtils.toFXImage(ImageIO.read(path.toFile()), null);
	}
}
