package org.praisenter.ui.display;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.TextStore;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.media.MediaComponent;
import org.praisenter.data.slide.media.MediaObject;
import org.praisenter.data.workspace.DisplayConfiguration;
import org.praisenter.ui.GlobalContext;
import org.praisenter.ui.slide.SlideMode;
import org.praisenter.ui.slide.SlideView;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import me.walkerknapp.devolay.DevolayFrameFourCCType;
import me.walkerknapp.devolay.DevolaySender;
import me.walkerknapp.devolay.DevolayVideoFrame;

public final class NDIDisplayTarget implements DisplayTarget {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final GlobalContext context;
	private final DisplayConfiguration configuration;
	
	private final Pane container;
	private final SlideView slideView;
	private final SlideView notificationView;
	
	private final DevolaySender ndiTarget;
	private final FramesPerSecondTimer frameProducer;
	private final PriorityBlockingQueue<NDIVideoFrame> frameQueue;
	private final Thread frameConsumer;
	private final ChangeListener<Boolean> activeListener;
	
    private final int width;
    private final int height;
    private final int pixelDepth;
    
    private boolean disposed;
    private boolean sentHideFrame;
    
    // NDI Consumer state
    
	// always render NDI at 60 fps
	// I noticed when we didn't send 60 fps, it had the appearance of being very laggy
    
    // create buffers to write to NDI
    // Use two frame buffers because one will typically be in flight (being used by NDI send) while the other is being filled.
    
    private final int NDIFPS;
    private final DevolayVideoFrame videoFrame;
    private final byte[] convertBuffer;
    private final ByteBuffer[] frameBuffers;
    
    private long lastFrameNumber = -1;
    private int bufferIndex = 0;
    
    private Instant lastSlide;
    private Instant lastNotification;
    private long transitionCooldownCounter;
    
	public NDIDisplayTarget(GlobalContext context, DisplayConfiguration configuration) {
		this.context = context;
		this.configuration = configuration;
		this.width = configuration.getWidth();
        this.height = configuration.getHeight();
        this.pixelDepth = 4;
		this.disposed = false;
		this.sentHideFrame = false;
		
		// NOTE: when running on the laptop, it didn't matter what the NDI frame rate was
		// but when running docked with two monitors it mattered, so instead of
		
		// NOTE: we always want NDI ahead of Java FX so that the frame queue rarely has
		// anything queued (which is why we +1 to the FPS)
		this.NDIFPS = Math.max(configuration.getFramesPerSecond() + 1, context.getWorkspaceConfiguration().getNDIFramesPerSecond());
		
		this.container = new StackPane();
		this.container.setBackground(null);
		
		this.videoFrame = new DevolayVideoFrame();
		this.videoFrame.setResolution(this.width, this.height);
		this.videoFrame.setFourCCType(DevolayFrameFourCCType.BGRA);
		this.videoFrame.setLineStride(this.width * this.pixelDepth);
		this.videoFrame.setFrameRate(this.NDIFPS, 1);
	    
	    int nbytes = this.width * this.height * this.pixelDepth;
	    this.convertBuffer = new byte[nbytes];
	    this.frameBuffers = new ByteBuffer[] { 
			ByteBuffer.allocateDirect(nbytes),
	        ByteBuffer.allocateDirect(nbytes) 
	    };
		
		// setup debug mode notification
		if (context.getWorkspaceConfiguration().isDebugModeEnabled()) {
			this.container.setBorder(new Border(new BorderStroke(
					Color.RED, 
					new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE, 10, 0, new ArrayList<Double>()), 
					null, 
					new BorderWidths(10))));
		}
		
		this.slideView = new SlideView(context);
		this.slideView.setClipEnabled(false);
		this.slideView.setFitToHeightEnabled(false);
		this.slideView.setFitToWidthEnabled(false);
		this.slideView.setCheckeredBackgroundEnabled(false);
		this.slideView.setViewMode(SlideMode.PRESENT);
		
		this.notificationView = new SlideView(context);
		this.notificationView.setClipEnabled(false);
		this.notificationView.setFitToHeightEnabled(false);
		this.notificationView.setFitToWidthEnabled(false);
		this.notificationView.setCheckeredBackgroundEnabled(false);
		this.notificationView.setViewMode(SlideMode.PRESENT);
		this.notificationView.setAutoHideEnabled(true);
		
		this.container.getChildren().addAll(this.slideView, this.notificationView);
		
		// cache hints
		this.container.setCache(true);
		this.container.setCacheHint(CacheHint.SPEED);
		
        final String ndiName = configuration.getName();
        
        this.ndiTarget = new DevolaySender(ndiName);
		this.frameQueue = new PriorityBlockingQueue<>();
		
		this.activeListener = (obs, ov, nv) -> {
			if (!nv) {
				// clear the content
				this.slideView.render(null, null, false);
				this.notificationView.render(null, null, false);
				// make sure one frame of full transparent makes it to the NDI output
				this.sentHideFrame = false;
			}
		};
		configuration.activeProperty().addListener(this.activeListener);
		
		final SnapshotParameters params = new SnapshotParameters();
		params.setViewport(new Rectangle2D(0, 0, this.width, this.height));
		params.setFill(Color.TRANSPARENT);
		
		this.frameProducer = new FramesPerSecondTimer(configuration.getFramesPerSecond(), (frame) -> {
			if (this.disposed) {
				return;
			}
			if (configuration.isActive()) {
				// optimization: try to predict if we can avoid rendering or not
				boolean render = this.renderRequired();
				if (render) {
					this.generateFrame(params, frame);
				}
			} else if (!this.sentHideFrame) {
				this.generateFrame(params, frame);
				this.sentHideFrame = true;
			}
		});
		
		this.frameConsumer = new Thread(() -> {
			ndiSendLoop();
		});
		
		this.frameProducer.start();
		this.frameConsumer.start();
	}
	
	private final boolean renderRequired() {
		boolean renderOptimizationsEnabled = this.context.getWorkspaceConfiguration().isNDIRenderOptimizationsEnabled();
		if (!renderOptimizationsEnabled) {
			return true;
		}
		
		// if either have video, we have to keep rendering
		if (this.slideView.hasAnimatedContent() || this.notificationView.hasAnimatedContent()) {
			return true;
		}
		
		// if either are transitioning, we have to keep rendering
		if (this.slideView.isTransitionInProgress() || this.notificationView.isTransitionInProgress()) {
			// set the cooldown to 24 frames
			// this will be used after transition
			// ends to make sure we capture all the
			// frames
			this.transitionCooldownCounter = this.NDIFPS;
			return true;
		}
		
		// if the slide has changed
		Instant s = this.slideView.getCurrentSlideEnqueueTime();
		if (!Objects.equals(s, this.lastSlide)) {
			this.lastSlide = s;
			return true;
		}
		
		// if the notification has changed
		Instant n = this.notificationView.getCurrentSlideEnqueueTime();
		if (!Objects.equals(n, this.lastNotification)) {
			this.lastNotification = n;
			return true;
		}
		
		// finally, check if we have a cooldown period
		// from a recent animation that we need run through
		if (this.transitionCooldownCounter > 0) {
			this.transitionCooldownCounter--;
			return true;
		}
		
		return false;
	}
	
	private final void generateFrame(SnapshotParameters params, long frameNumber) {
		WritableImage image = this.container.snapshot(params, null);
		this.frameQueue.offer(new NDIVideoFrame(image, frameNumber));
	}
	
	private final void ndiSendLoop() {
        // define the amount of time to wait for a frame from the Java FX producer thread
        final long frameWaitTime = 1000 / this.NDIFPS;
        
        // now run forever, pushing any frames we receive to NDI
        long startTime = System.nanoTime();
        while (!this.disposed) {
        	// attempt to get a frame from the frame queue
        	NDIVideoFrame frame = null;
			try {
				frame = this.frameQueue.poll(frameWaitTime, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				LOGGER.warn("Waiting for a frame was interrupted.");
			}
			
			// the frame can be null if we waited long enough and we still
			// didn't get a frame in the frame queue
        	if (frame == null) {
        		// it's fine, just start waiting again
        		continue;
        	} else if (frame.getFrameNumber() <= this.lastFrameNumber) {
        		// in the case that we get an old frame for some reason
        		// just drop the frame and report it - this shouldn't ever happen
        		LOGGER.warn("Dropping frame {} it came in after frame {}", frame.getFrameNumber(), this.lastFrameNumber);
        		continue;
        	}
        	
        	// keep track of the last frame number
        	this.lastFrameNumber = frame.getFrameNumber();
        	
            // Use the buffer that currently isn't in flight
            ByteBuffer buffer = this.frameBuffers[this.bufferIndex];

            // Fill in the buffer for one frame.
            writeImageToBuffer(frame.getImage(), buffer);
            this.videoFrame.setData(buffer);

            // Submit the frame asynchronously.
            // This call will return immediately and the API will "own" the buffer until a synchronizing event.
            // A synchronizing event is one of: DevolaySender#sendVideoFrameAsync, DevolaySender#sendVideoFrame, DevolaySender#close
            this.ndiTarget.sendVideoFrameAsync(this.videoFrame);

            // Give an FPS message every 30 frames submitted
            if(frame.getFrameNumber() % this.NDIFPS == 0) {
            	long now = System.nanoTime();
            	double seconds = (now - startTime) / (double)1e9;
            	
            	LOGGER.trace("Sent {} frames. Average FPS: {}", this.NDIFPS, (this.NDIFPS / seconds));
                startTime = now;
            }
            
            this.bufferIndex++;
            if (this.bufferIndex > 1) {
            	this.bufferIndex = 0;
            }
        }
        
        LOGGER.debug("Stopping NDI consumer thread. disposed={}", this.disposed);
	}
	
	private final void writeImageToBuffer(WritableImage image, ByteBuffer data) {
        data.position(0);

        final int w = (int)image.getWidth();
        final int h = (int)image.getHeight();
        
        PixelReader pr = image.getPixelReader();
        pr.getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), convertBuffer, 0, w * 4);
        data.put(convertBuffer);
        data.flip();
    }
	
	private final void writeTransparentToBuffer(ByteBuffer data) {
        data.position(0);
        // it's always 4 bytes per pixel so we can do this and guarantee
        // that we'll fully fill the buffer and not over/under fill it
        for (int i = 0; i < data.capacity(); i+=4) {
        	data.putInt(0);
        }
        data.flip();
    }
	
	private final void sendClearFrame() {
		if (this.ndiTarget != null) {
			LOGGER.debug("Sending clear frame to NDI");
			final DevolayVideoFrame videoFrame = new DevolayVideoFrame();
	        videoFrame.setResolution(this.width, this.height);
	        videoFrame.setFourCCType(DevolayFrameFourCCType.BGRA);
	        videoFrame.setLineStride(this.width * this.pixelDepth);
	        videoFrame.setFrameRate(this.configuration.getFramesPerSecond(), 1);
	        
	        ByteBuffer buffer = frameBuffers[bufferIndex];
	        writeTransparentToBuffer(buffer);
	        videoFrame.setData(buffer);
			this.ndiTarget.sendVideoFrame(videoFrame);
			
			this.bufferIndex++;
            if (this.bufferIndex > 1) {
            	this.bufferIndex = 0;
            }
		}
	}
	
	@Override
	public String toString() {
		return this.configuration.getName();
	}
	
	public void dispose() {
		LOGGER.debug("Marking NDI display target as disposed");
		this.disposed = true;
		
		this.slideView.dispose();
		this.container.getChildren().clear();
		this.configuration.activeProperty().removeListener(this.activeListener);
		
		// stop the frame producer
		this.frameProducer.stop();
		LOGGER.debug("NDI Frame producer stopped");
		
		CompletableFuture.runAsync(() -> {
			// stop the frame consumer
			if (this.frameConsumer != null) {
				try {
					this.frameConsumer.join();
					LOGGER.debug("NDI frame consumer stopped");
				} catch (InterruptedException e) {
					LOGGER.warn("Waiting for the NDI consumer thread to complete was interrupted.");
				}
			}
			
			// send one last clear frame
			this.sendClearFrame();
			
			// release the NDI natives
			LOGGER.debug("Releasing NDI resources");
			this.ndiTarget.close();
			
			LOGGER.debug("Clearing NDI frame queue");
			this.frameQueue.clear();
			
			LOGGER.debug("NDI clean up complete");
		});
	}


	@Override
	public void displaySlide(Slide slide, TextStore data) {
		this.displaySlide(slide, data, true);
	}

	public void displaySlide(final Slide slide, final TextStore data, boolean transtion) {
		if (slide == null) {
			this.slideView.render(null, null, transtion);
			return;
		}
		
		Slide copy = slide.copy();

		if (data != null) {
			copy.setPlaceholderData(data.copy());
		}
		
		double w = this.configuration.getWidth();
		double h = this.configuration.getHeight();
		
		copy.fit(w, h);
		this.muteAllAudio(copy);
		
		this.slideView.render(copy, copy.getPlaceholderData(), transtion);
	}
	
	@Override
	public void displayNotification(Slide slide, TextStore data) {
		this.displayNotification(slide, data, true);
	}

	public void displayNotification(final Slide slide, final TextStore data, boolean transtion) {
		if (slide == null) {
			this.notificationView.render(null, null, transtion);
			return;
		}
		
		Slide copy = slide.copy();

		if (data != null) {
			copy.setPlaceholderData(data.copy());
		}
		
		double w = this.configuration.getWidth();
		double h = this.configuration.getHeight();
		
		copy.fit(w, h);
		this.muteAllAudio(copy);

		this.notificationView.render(copy, copy.getPlaceholderData(), transtion);
	}
	
	private void muteAllAudio(Slide slide) {
		SlidePaint sp = slide.getBackground();
		if (sp != null && sp instanceof MediaObject) {
			MediaObject mo = ((MediaObject) sp);
			if (mo != null) {
				mo.setMuted(true);
			}
		}
		
		for (SlideComponent sc : slide.getComponents()) {
			if (sc == null) {
				continue;
			}
			
			SlidePaint scsp = sc.getBackground();
			if (scsp != null && scsp instanceof MediaObject) {
				MediaObject mo = ((MediaObject) sp);
				if (mo != null) {
					mo.setMuted(true);
				}
			}
			
			if (sc instanceof MediaComponent) {
				MediaObject mo = ((MediaComponent) sc).getMedia();
				if (mo != null) {
					mo.setMuted(true);
				}
			}
		}
	}
	
	@Override
	public void clear() {
		this.clear(true);
	}
	
	public void clear(boolean transition) {
		this.slideView.render(null, null, transition);
		this.notificationView.render(null, null, transition);
	}
	
	public DisplayConfiguration getDisplayConfiguration() {
		return this.configuration;
	}
}
