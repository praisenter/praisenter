package org.praisenter.ui;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.scene.image.Image;

public final class ImageCache {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Map<ImageCacheKey, SoftReference<Image>> images;

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
				LOGGER.trace("Image for key: {} found in cache.", key);
				return image;
			}
		}
		LOGGER.debug("Image for key: {} was not found in the cache. Loading...", key);
		Image image = supplier.get();
		if (image != null) {
			LOGGER.debug("Image loaded for key: {}", key);
			this.images.put(key, new SoftReference<Image>(image));
		} else {
			LOGGER.warn("Image was loaded but was null.", key);
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
	public Image getOrLoadThumbnail(UUID id, Path path) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.THUMBNAIL, id.toString());
		return getOrLoad(key, () -> {
			try {
				LOGGER.debug("Converting BufferedImage into Image.");
				return this.load(path.toUri().toURL().toExternalForm());
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
	public Image getOrLoadImage(UUID id, Path path) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.MEDIA_IMAGE, id.toString());
		return getOrLoad(key, () -> {
			try {
				return this.load(path.toUri().toURL().toExternalForm());
			} catch (Exception ex) {
				LOGGER.error("Failed to load image from path '" + path.toAbsolutePath().toString() + "'", ex);
			}
			return null;
		});
	}

	/**
	 * Returns true if the given image id is present in the image cache.
	 * @param id the id
	 * @return true if the id exists, and it's image is non-null
	 */
	public boolean isImageCached(UUID id) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.MEDIA_IMAGE, id.toString());
		if (this.images.containsKey(key)) {
			Image image = this.images.get(key).get();
			if (image != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the cached image for the given image or loads the image given the classpath
	 * path if the image is not in the cache.
	 * <p>
	 * This should be used for application images.
	 * @param classpath the classpath path to the image
	 * @return Image
	 */
	public Image getOrLoadClasspathImage(String classpath) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.APPLICATION_IMAGE, classpath);
		return getOrLoad(key, () -> {
			try {
				LOGGER.debug("Loading image from classpath '{}'.", classpath);
				return this.load(ImageCache.class.getResource(classpath).toExternalForm());
			} catch (Exception ex) {
				LOGGER.error("Failed to load image from path '" + classpath + "'", ex);
			}
			return null;
		});
	}
	
	/**
	 * Performs the same actions as {@link #getOrLoadImage(UUID, Path)} but returns a CompletableFuture
	 * to allow the caller to wait for the loading of the images before continuing.
	 * @param images the images to load
	 * @return CompletableFuture
	 */
	public CompletableFuture<Void> getOrLoadImagesAndWait(Map<UUID, Path> images) {
		// load the images
		List<Image> imgs = new ArrayList<>();
		for (var entry : images.entrySet()) {
			imgs.add(this.getOrLoadImage(entry.getKey(), entry.getValue()));
		}
		
		// now try to wait on them
		if (Platform.isFxApplicationThread()) {
			return watchImageLoading(imgs);
		}
		
		CompletableFuture<Void> future = new CompletableFuture<>();
		Platform.runLater(() -> {
			CompletableFuture<Void> lf = watchImageLoading(imgs);
			lf.thenRun(() -> {
				future.complete(null);
			});
		});
		return future;
	}
	
	/**
	 * Builds a CompletableFuture for the given list of images.
	 * @param images
	 * @return CompletableFuture
	 */
	private CompletableFuture<Void> watchImageLoading(List<Image> images) {
		List<CompletableFuture<Void>> tasks = new ArrayList<>();
		for (Image image : images) {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			tasks.add(future);
			if (image.isBackgroundLoading()) {
				image.progressProperty().addListener((obs, ov, nv) -> {
					if (nv.doubleValue() == 1.0) {
						// then it's done
						LOGGER.trace("Image '{}' has completed loading.", image.getUrl());
						future.complete(null);
					}
				});
				image.errorProperty().addListener((obs, ov, nv) -> {
					if (nv) {
						LOGGER.error("Image '" + image.getUrl() + "' has failed to load: ", image.getException());
						future.complete(null);
					}
				});
			} else {
				future.complete(null);
			}
		}
		return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
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
	 * Removes everything from the cache.
	 */
	public void clear() {
		this.images.clear();
	}
	
	/**
	 * Loads an Image from the given path.
	 * @param path the path
	 * @return Image
	 * @throws IOException 
	 */
	private Image load(String url) throws IOException {
		// NOTE: as of JavaFX 24, it falls back to ImageIO when it sees a format it doesn't know
		// https://bugs.openjdk.org/browse/JDK-8306707
		LOGGER.debug("Loading image at path '{}'", url);
		return new Image(url, true);
	}
}
