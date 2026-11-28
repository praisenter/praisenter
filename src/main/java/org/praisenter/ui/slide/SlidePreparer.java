package org.praisenter.ui.slide;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.async.AsyncHelper;
import org.praisenter.data.TextStore;
import org.praisenter.data.media.Media;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.Slide;
import org.praisenter.ui.GlobalContext;
import org.praisenter.utility.RuntimeInterruptException;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public final class SlidePreparer {
    private static final Logger LOGGER = LogManager.getLogger();

    private final GlobalContext context;
    private final CacheHint renderHint;

    public SlidePreparer(GlobalContext context) {
        this(context, CacheHint.DEFAULT);
    }

    public SlidePreparer(GlobalContext context, CacheHint renderHint) {
        this.context = context;
        this.renderHint = renderHint;
    }
    
	/**
	 * Prepares the given slide for viewing by preloading its images and creating a PreparedSlide.
	 * @param slide the slide to prepare
	 * @param data the text store containing slide data
	 * @param mode the slide mode
	 * @return 
	 */
	public CompletableFuture<PreparedSlide> prepareAsync(Slide slide, TextStore data, SlideMode mode) {
		Instant time = Instant.now();
		LOGGER.trace("Preparing slide '{}' for view", slide);
		
		// look for images to preload
		return this.getImagesToPreLoadAsync(slide, mode)
			// preload the images
			.thenCompose(this::preLoadImagesAsync)
			// create the slide node
			.thenCompose((v) -> this.createSlideNodeAsync(slide, mode))
			// wait for media to be ready
			.thenCompose((sn) -> {
				return this.waitForMediaReadyAsync(sn).thenApply((ready) -> {
					// it's ready or we gave up, so return a prepared slide
					return new PreparedSlide(slide, data, sn, time);
				}).exceptionally((ex) -> {
					// we failed during wait, so clean up the slide node
					Platform.runLater(() -> { 
						sn.close(); 
					});
					throw new CompletionException(ex);
				});
			});
	}

    /**
     * Prepares the slide, then renders it on the Java FX thread.
     * @param slide
     * @return
     */
    public CompletableFuture<Image> generateImageAsync(Slide slide) {
        return this.generateImageAsync(slide, SlideMode.VIEW);
    }

    /**
     * Prepares the slide, then renders it on the Java FX thread.
     * @param slide
     * @param mode
     * @return
     */
    public CompletableFuture<Image> generateImageAsync(Slide slide, SlideMode mode) {
        return this.prepareAsync(slide, null, mode)
            .thenCompose(AsyncHelper.onJavaFXThreadAndWait(p -> {
                if (p == null)
                    return null;

                SlideNode node = p.getNode();
                if (node == null)
                    return null;

                SnapshotParameters sp = new SnapshotParameters();
                // make sure the snapshot's background is transparent
                sp.setFill(Color.TRANSPARENT);
                // make sure we only render the slide width/height since components can spill
                // over the width, but won't be shown when displayed
                sp.setViewport(new Rectangle2D(0, 0, slide.getWidth(), slide.getHeight()));
                
                // use a Pane to contain the slide's display pane so that we avoid the
                // issue of positioning based on what the display pane's type is
                // NOTE: there's also a callback version of the snapshot method
                LOGGER.debug("Taking snapshot of slide");
                Image image = new Pane(node).snapshot(sp, null);
                return image;
            }))
            .exceptionally((ex) -> {
                LOGGER.error("Failed to take snapshot of slide", ex);
                return null;
            });
    }

	/**
	 * Converts a Slide into a SlideNode on the Java FX thread.
	 * @param slide
	 * @return
	 */
	private CompletableFuture<SlideNode> createSlideNodeAsync(Slide slide, SlideMode mode) {
		if (Platform.isFxApplicationThread()) {
			SlideNode slideNode = this.createSlideNode(slide, mode);
			return CompletableFuture.completedFuture(slideNode);
		} else {
			CompletableFuture<SlideNode> future = new CompletableFuture<>();
			Platform.runLater(() -> {
				try {
					SlideNode slideNode = this.createSlideNode(slide, mode);
					future.complete(slideNode);
				} catch (Exception ex) {
					future.completeExceptionally(ex);
				}
			});
			return future;
		}
	}

    /**
     * Returns a new SlideNode for the given slide and mode.
     * @param slide
     * @param mode
     * @return
     */
	private SlideNode createSlideNode(Slide slide, SlideMode mode) {
		LOGGER.trace("Creating JavaFX nodes");
		SlideNode node = new SlideNode(this.context, slide);
		
		LOGGER.trace("Changing mode to: {}", mode);
		node.setMode(mode);
		node.setCacheHint(this.renderHint);

		return node;
	}
	
	/**
	 * Gets a map of image media that needs to be preloaded for the given slide.
	 * <p>
	 * Can be called from any thread and will ensure Java FX interaction happens on the JavaFX application thread.
	 * The continuation returned from this method will always be OFF the Java FX thread.
	 * @param slide
	 * @return
	 */
	private CompletableFuture<Map<UUID, Path>> getImagesToPreLoadAsync(Slide slide, SlideMode mode) {
		if (Platform.isFxApplicationThread()) {
			return CompletableFuture.completedFuture(this.getImagesToPreLoad(slide, mode));
		}

		return CompletableFuture.completedFuture(slide)
			.thenCompose(AsyncHelper.onJavaFXThreadAndWait((v) -> {
                return this.getImagesToPreLoad(slide, mode);
            }));
	}
	
	/**
	 * Gets a map of image media that needs to be preloaded for the given slide.
	 * <p>
	 * MUST be called on the JavaFX application thread.
	 * @param slide
	 * @return 
	 */
	private Map<UUID, Path> getImagesToPreLoad(Slide slide, SlideMode mode) {
		final Set<UUID> mediaIds = slide.getReferencedMedia();
		final Map<UUID, Path> mediaToLoad = new HashMap<>();
		
		LOGGER.trace("Checking for media that hasn't been loaded");
		for (UUID mediaId : mediaIds) {
			Media media = this.context.getWorkspaceManager().getItem(Media.class, mediaId);
			if (media != null) {
				if (media.getMediaType() == MediaType.IMAGE) {
					if (!this.context.getImageCache().isImageCached(mediaId)) {
						LOGGER.trace("Image media '{}' has not been loaded yet", media.getName());
						mediaToLoad.put(media.getId(), media.getMediaPath());
					}
				} else if (media.getMediaType() == MediaType.VIDEO && mode != SlideMode.PRESENT) {
					if (!this.context.getImageCache().isImageCached(mediaId)) {
						LOGGER.trace("Video media image '{}' has not been loaded yet", media.getName());
						mediaToLoad.put(media.getId(), media.getMediaImagePath());
					}
				}
			}
		}
		
		return mediaToLoad;
	}
	
	/**
	 * Loads the given images asynchronously on a background thread.
	 * @param mediaToLoad
	 * @return
	 */
	private CompletableFuture<Void> preLoadImagesAsync(Map<UUID, Path> mediaToLoad) {
		if (mediaToLoad.size() > 0) {
			LOGGER.trace("Loading {} images", mediaToLoad.size());
			return this.context.getImageCache().getOrLoadImagesAndWait(mediaToLoad);
		} else {
			LOGGER.trace("No media to load, returning");
			return CompletableFuture.completedFuture(null);
		}
	}
	
	/**
	 * Returns true if the given SlideNode is ready.
	 * <p>
	 * This method can be called from any thread, but will delegate the readiness
	 * check to the JavaFX application thread.
	 * @param node
	 * @return
	 */
	private CompletableFuture<Boolean> isReadyAsync(SlideNode node) {
		CompletableFuture<Boolean> future = new CompletableFuture<>();

		if (Platform.isFxApplicationThread()) {
			future.complete(node.isReady());
		} else {
			Platform.runLater(() -> {
				future.complete(node.isReady());
			});
		}
		return future;
	}
	
	/**
	 * Uses a background thread to wait for all media in the given SlideNode to be ready.
	 * This method periodically checks if the media is ready on the Java FX thread, but will
	 * give up after a maximum wait time has been reached.
	 * @param node
	 * @return
	 */
	private CompletableFuture<Boolean> waitForMediaReadyAsync(SlideNode node) {
		final int waitTimeIncrementInMS = 100;
		final int maxWaitTimeInMS = 5 * 1000;
		
		LOGGER.trace("Staring wait for media players");
		return CompletableFuture.supplyAsync(() -> {
			int waitTimeInMS = 0;
			boolean isReady = false;
			while (!isReady) {
				try {
					// Check if the media is ready on the JavaFX thread
					// and wait for it's response
					LOGGER.trace("Checking if SlideNode media players are ready...");
					isReady = this.isReadyAsync(node).get();
				} catch (InterruptedException ex) {
					LOGGER.warn("Checking if the SlideNode media is ready was interrupted");
					throw new RuntimeInterruptException(ex);
				} catch (ExecutionException | CancellationException ex) {
					LOGGER.warn("Checking if the SlideNode media is ready completed exceptionally or was cancelled");
					throw new CompletionException(ex);
				}

				if (!isReady) {
					// see if we should give up
					if (waitTimeInMS >= maxWaitTimeInMS) {
						LOGGER.warn("Waiting for media to get ready took too long {} ms", waitTimeInMS);
						break;
					}

					try {
						Thread.sleep(waitTimeIncrementInMS);
						waitTimeInMS += waitTimeIncrementInMS;
					} catch (InterruptedException ex) {
						LOGGER.warn("Waiting for media to be ready on SlideNode was interrupted");
						throw new RuntimeInterruptException(ex);
					}
				} else {
					LOGGER.debug("Media players are ready");
					return true;
				}
			}

			return false;
		});
	}
}
