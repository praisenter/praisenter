package org.praisenter.ui;

import java.io.IOException;
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
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

public final class ImageCache {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final Map<ImageCacheKey, CachedImage> images;

	public ImageCache() {
		this.images = new HashMap<ImageCacheKey, CachedImage>();
	}
	
	/**
	 * Private class used to hold a strong reference to an image
	 * after being loaded from the supplier
	 */
	private final class SuppliedImage {
		final Image image;
		final CachedImage cachedImage;
		public SuppliedImage(Image image, CachedImage cachedImage) {
			this.image = image;
			this.cachedImage = cachedImage;
		}
	}

	/**
	 * Returns the image for the given path, loading it if necessary.
	 * <p>
	 * Returns null in the event that an error occurs.
	 * @param key the image cache key
	 * @param supplier the function to load the image if it doesn't exist
	 * @return Image
	 */
	private synchronized Image getOrLoad(ImageCacheKey key, String location, Supplier<SuppliedImage> supplier) {
		this.evict();
		if (this.images.containsKey(key)) {
			CachedImage cachedImage = this.images.get(key);
			Image image = cachedImage.getImage();
			if (image != null) {
				LOGGER.trace("Image for key: {} found in cache. Last modified date: {}", key, cachedImage.getLastModified());
				if (cachedImage.hasLocationChanged(location)) {
					LOGGER.debug("Image for key: {} has changed location. Reloading...", key);
					this.images.remove(key);
					cachedImage = null;
				} else if (cachedImage.hasUpdated()) {
					LOGGER.debug("Image for key: {} is outdated. Reloading...", key);
					this.images.remove(key);
					cachedImage = null;
				} else {
					return image;
				}
			}
		}

		LOGGER.debug("Image for key: {} was not found in the cache. Loading...", key);
		SuppliedImage suppliedImage = supplier.get();
		if (suppliedImage == null) {
			return null;
		}

		CachedImage cachedImage = suppliedImage.cachedImage;
		Image image = suppliedImage.image;
		if (image != null) {
			LOGGER.debug("Image loaded for key: {}", key);
			this.images.put(key, cachedImage);
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
		return getOrLoad(key, path.toAbsolutePath().toString(), () -> {
			try {
				LOGGER.debug("Converting BufferedImage into Image.");
				Image image = this.load(path);
				CachedImage cachedImage = CachedImage.fromPath(image, path);
				return new SuppliedImage(image, cachedImage);
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
		return getOrLoad(key, path.toAbsolutePath().toString(), () -> {
			try {
				Image image = this.load(path);
				CachedImage cachedImage = CachedImage.fromPath(image, path);
				return new SuppliedImage(image, cachedImage);
			} catch (Exception ex) {
				LOGGER.error("Failed to load image from path '" + path.toAbsolutePath().toString() + "'", ex);
			}
			return null;
		});
	}

	/**
	 * Returns true if the given image id is present in the image cache.
	 * <p>
	 * NOTE: This only checks for the presence of the image in the cache and 
	 * whether it is non-null. It does not check if the image has been updated
	 * on the file system.  In most cases this doesn't matter because cached
	 * images are from Images or Videos which can't be updated in Praisenter.
	 * @param id the id
	 * @return true if the id exists, and it's image is non-null
	 */
	public synchronized boolean isImageCached(UUID id) {
		ImageCacheKey key = new ImageCacheKey(ImageCacheKeyType.MEDIA_IMAGE, id.toString());
		if (this.images.containsKey(key)) {
			CachedImage ci = this.images.get(key);
			if (ci != null && ci.getImageReference().get() != null) {
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
		return getOrLoad(key, classpath, () -> {
			try {
				LOGGER.debug("Loading image from classpath '{}'.", classpath);
				Image image = this.load(ImageCache.class.getResource(classpath).toExternalForm());
				CachedImage cachedImage = CachedImage.fromString(image, classpath);
				return new SuppliedImage(image, cachedImage);
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
			Image image = this.getOrLoadImage(entry.getKey(), entry.getValue());
			if (image != null) {
				imgs.add(image);
			}
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
			
			// create listeners for progress and error events
			final ChangeListener<Number> progressListener = (obs, ov, nv) -> {
				if (nv.doubleValue() == 1.0) {
					// then it's done
					LOGGER.trace("Image '{}' has completed loading.", image.getUrl());
					future.complete(null);
				}
			};
			final ChangeListener<Boolean> errorListener = (obs, ov, nv) -> {
				if (nv) {
					LOGGER.error("Image '" + image.getUrl() + "' has failed to load: ", image.getException());
					future.complete(null);
				}
			};
			
			// when the future is completed, remove the listeners
			future.handleAsync((res, ex) -> {
				Platform.runLater(() -> {
					image.progressProperty().removeListener(progressListener);
					image.errorProperty().removeListener(errorListener);
				});
				return null;
			});

			// add the listeners
			image.progressProperty().addListener(progressListener);
			image.errorProperty().addListener(errorListener);

			// then inspect current state to see if it's already done or has an error
			if (image.getProgress() == 1.0) {
				LOGGER.trace("Image '{}' has completed loading.", image.getUrl());
				future.complete(null);
			} else if (image.isError()) {
				LOGGER.error("Image '" + image.getUrl() + "' has failed to load: ", image.getException());
				future.complete(null);
			}
		}
		return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
	}
	
	/**
	 * Removes keys from the cache whose soft references have been collected.
	 */
	private void evict() {
		Iterator<CachedImage> it = this.images.values().iterator();
		while (it.hasNext()) {
			CachedImage ref = it.next();
			if (ref.getImage() == null) {
				it.remove();
				LOGGER.debug("Image with key '{}' has been evicted from the image cache.");
			}
		}
	}

	/**
	 * Removes everything from the cache.
	 */
	public synchronized void clear() {
		this.images.clear();
	}
	
	/**
	 * Loads an Image from the given path.
	 * @param path the path
	 * @return Image
	 * @throws IOException 
	 */
	private Image load(Path path) throws IOException {
		String url = path.toUri().toURL().toExternalForm();
		Image image = this.load(url);
		return image;
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
