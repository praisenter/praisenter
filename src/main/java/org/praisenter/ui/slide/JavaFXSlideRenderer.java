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
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.slide.Slide;
import org.praisenter.data.slide.SlideRenderer;
import org.praisenter.ui.GlobalContext;
import org.praisenter.utility.ImageManipulator;

import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.ResampleOp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;

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

	private final SlidePreparer slidePreparer;
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public JavaFXSlideRenderer(GlobalContext context) {
		this.slidePreparer = new SlidePreparer(context, CacheHint.QUALITY);
	}
	
	/**	(non-Javadoc)
	 * Renders the given slide to a Java FX image.
	 * This method MUST be called OFF of the Java FX Application Thread.
	 * @see org.praisenter.data.slide.SlideRenderer#render(org.praisenter.data.slide.Slide)
	 */
	@Override
	public Image render(Slide slide) {
		if (slide == null) 
			return null;
		
		try {
			return this.slidePreparer.generateImageAsync(slide, SlideMode.VIEW).get();
		} catch (InterruptedException | ExecutionException ex) {
			LOGGER.warn("Failed to render slide '{}'", slide.getName(), ex);
			return null;
		}
	}
	
	/**	(non-Javadoc)
	 * Renders a thumbnail of the given slide with the specified width and height.
	 * This method MUST be called OFF of the Java FX Application Thread.
	 * @see org.praisenter.data.slide.SlideRenderer#renderThumbnail(org.praisenter.data.slide.Slide, int, int)
	 */
	@Override
	public BufferedImage renderThumbnail(Slide slide, int width, int height) {
		LOGGER.debug("Taking slide snapshot");
		Image fxImage = this.render(slide);
		if (fxImage == null) 
			return null;

		// convert to a buffered image so we can manipulate it
		LOGGER.debug("Converting Java FX image to buffered image");
		BufferedImage image = SwingFXUtils.fromFXImage(fxImage, null);
		if (image == null)
			return null;

		// make sure its a in the right format for scaling
		LOGGER.debug("Making sure image format is correct");
		image = ImageUtil.toBuffered(image, BufferedImage.TYPE_INT_ARGB);
		
		LOGGER.debug("Creating thumbnail from slide snapshot");
		// we use the FILTER_LANCZOS because it gives good results for text at high resize ratios
		return ImageManipulator.getUniformScaledImage(image, width, height, ResampleOp.FILTER_LANCZOS, true);
	}
}
