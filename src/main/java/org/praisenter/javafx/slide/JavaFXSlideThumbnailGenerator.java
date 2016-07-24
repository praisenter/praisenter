package org.praisenter.javafx.slide;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Reference;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideThumbnailGenerator;
import org.praisenter.utility.ImageManipulator;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public final class JavaFXSlideThumbnailGenerator extends SlideThumbnailGenerator {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final int width;
	private final int height;
	private final ObservableSlideContext context;
	
	public JavaFXSlideThumbnailGenerator(int width, int height, ObservableSlideContext context) {
		this.width = width;
		this.height = height;
		this.context = context;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideThumbnailGenerator#generate(org.praisenter.slide.Slide)
	 */
	@Override
	public <T extends Slide> BufferedImage generate(T slide) {
		if (slide == null) return null;
		
		// create a callable for generating the slide thumbnail
		Callable<Image> r = () -> {
			ObservableSlide<?> nSlide = new ObservableSlide<>(slide, context, SlideMode.SNAPSHOT);
			
			SnapshotParameters sp = new SnapshotParameters();
			sp.setFill(Color.TRANSPARENT);
			
			return nSlide.getDisplayPane().snapshot(sp, null);
		};
		
		// since we are using Java FX to render the slides, we need to make sure
		// we render them on the Java FX thread
		if (Platform.isFxApplicationThread()) {
			try {
				// if we are already on the Java FX thread then just generate the thumbnail
				return SwingFXUtils.fromFXImage(r.call(), null);
			} catch (Exception ex) {
				LOGGER.warn("Failed to generate image", ex);
				return null;
			} 
		}
		
		// if we are not on the Java FX thread, we need to execute the generation
		// code on it and then wait for it to complete
		final Reference<Image> imageRef = new Reference<Image>();
		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {
			try {
				imageRef.set(r.call());
			} catch (Exception ex) {
				LOGGER.warn("Failed to generate image", ex);
			} finally {
				// regardless of it working or not, we need to coundDown
				// so we don't spin forever
				latch.countDown();
			}
		});
		
		try {
			// wait until the Java FX thread generates the thumbnail
			latch.await();
			// then convert it to an BufferedImage and return
			Image image = imageRef.get();
			if (image != null) {
				BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
				// scale it down
				return ImageManipulator.getUniformScaledImage(bi, this.width, this.height, AffineTransformOp.TYPE_BICUBIC);
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed to generate image", ex);
		}
		
		return null;
	}
}
