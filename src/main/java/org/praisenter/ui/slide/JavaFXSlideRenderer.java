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
package org.praisenter.ui.slide;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Reference;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideRenderer;
import org.praisenter.ui.GlobalContext;
import org.praisenter.utility.ImageManipulator;

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.ResampleOp;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Represents a {@link SlideThumbnailGenerator} for Java FX.
 * <p>
 * This class will generate a thumbnail using the Node.snapshot method.
 * @author William Bittle
 * @version 3.0.0
 */
public final class JavaFXSlideRenderer implements SlideRenderer {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The context */
	private final GlobalContext context;
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 * @param settings the thumbnail settings
	 */
	public JavaFXSlideRenderer(GlobalContext context) {
		this.context = context;
	}
	
	@Override
	public Image render(Slide slide) {
		if (slide == null) return null;
		
		// create a callable for generating the slide thumbnail
		Callable<Image> snapshot = () -> {
			SlideNode nSlide = new SlideNode(this.context, slide);
			nSlide.setMode(SlideMode.VIEW);
			nSlide.setCacheHint(CacheHint.QUALITY);
			
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
			return new Pane(nSlide).snapshot(sp, null);
		};
		
		// since we are using Java FX to render the slides, we need to make sure
		// we render them on the Java FX thread
		if (Platform.isFxApplicationThread()) {
			try {
				// if we are already on the Java FX thread then just generate the thumbnail
				return snapshot.call();
			} catch (Exception ex) {
				LOGGER.warn("Failed to generate snapshot of slide '" + slide.getName() + "'", ex);
				return null;
			} 
		}
		
		// if we are not on the Java FX thread, we need to execute the generation
		// code on it and then wait for it to complete
		final Reference<Image> imageRef = new Reference<Image>();
		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {
			try {
				imageRef.set(snapshot.call());
			} catch (Exception ex) {
				LOGGER.warn("Failed to generate snapshot of slide '" + slide.getName() + "'", ex);
			} finally {
				// regardless of it working or not, we need to coundDown
				// so we don't wait forever
				latch.countDown();
			}
		});
		
		try {
			// wait until the Java FX thread generates the thumbnail
			latch.await();
			// then convert it to an BufferedImage and return
			return imageRef.get();
		} catch (Exception ex) {
			LOGGER.warn("Failed to convert snapshot of slide '" + slide.getName() + "' to a buffered image.", ex);
		}
		
		return null;
	}
	
	@Override
	public BufferedImage renderThumbnail(Slide slide, int width, int height) {
		Image fxImage = this.render(slide);
		// convert to a buffered image so we can manipulate it
		BufferedImage image = SwingFXUtils.fromFXImage(fxImage, null);
		// make sure its a in the right format for scaling
		image = ImageUtil.toBuffered(image, BufferedImage.TYPE_INT_ARGB);
		// we use the FILTER_LANCZOS because it gives good results for text at high resize ratios
		return ImageManipulator.getUniformScaledImage(image, width, height, ResampleOp.FILTER_LANCZOS);
	}
}
