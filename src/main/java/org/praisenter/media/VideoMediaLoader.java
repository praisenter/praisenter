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
package org.praisenter.media;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.configuration.Setting;
import org.praisenter.tools.ToolExecutionException;
import org.praisenter.tools.ffmpeg.FFprobeMediaMetadata;

/**
 * {@link MediaLoader} that loads video media.
 * @author William Bittle
 * @version 3.0.0
 */
public final class VideoMediaLoader extends AbstractMediaLoader implements MediaLoader {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The default command */
	public static final String DEFAULT_VIDEO_FRAME_EXTRACT_COMMAND = "{ffmpeg} -v fatal -ss 3 -i {media} -vf \"select=gt(scene\\,0.2)\" -frames:v 10 -vsync vfr {frame}"; 
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public VideoMediaLoader(MediaLibraryContext context) {
		super(context);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#isSupported(java.lang.String)
	 */
	@Override
	public boolean isSupported(String mimeType) {
		if (mimeType != null && mimeType.startsWith("video")) {
			// handle any mimetype
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.nio.file.Path)
	 */
	@Override
	public Media load(Path path) throws MediaImportException {
		LOGGER.debug("Video media '{}' loading", path);
		if (Files.exists(path) && Files.isRegularFile(path)) {
			BufferedImage frame = null;
			BufferedImage thumb = null;
			
			try {
				// get the command
				String command = this.context.getConfiguration().getString(Setting.MEDIA_VIDEO_FRAME_EXTRACT_COMMAND, DEFAULT_VIDEO_FRAME_EXTRACT_COMMAND);
				
				// get the best frame
				LOGGER.debug("Video media '{}' - searching for best frame.", path);
				frame = this.context.getTools().ffmpegExtractFrame(command, path);
			} catch (Exception ex) {
				LOGGER.warn("Failed to extract frame from video '" + path.toAbsolutePath().toString() + "'.");
			}

			// create a thumbnail for it
			if (frame != null) {
				LOGGER.debug("Video media '{}' - creating thumbnail.", path);
				thumb = this.createThumbnail(frame);
				this.drawFilmOnFrame(thumb);
			}
			
			// get the metadata
			try {
				FFprobeMediaMetadata metadata = this.context.getTools().ffprobeExtractMetadata(path);

				if (!metadata.hasVideo()) {
					LOGGER.error("No video stream present in file: '{}'", path.toAbsolutePath().toString());
					throw new MediaImportException("No video stream was found in the file '" + path.toAbsolutePath().toString() + "'.");
				}
				
				final Media media = Media.forVideo(
						path, 
						metadata.getFormat(), 
						metadata.getWidth(), 
						metadata.getHeight(), 
						metadata.getLength(), 
						metadata.hasAudio(), 
						null, 
						thumb, 
						frame);
				
				LOGGER.debug("Video media '{}' loaded", path);
				return media;
			} catch (InterruptedException e) {
				LOGGER.error("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' was interrupted.", e);
				throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' was interrupted.", e);
			} catch (ToolExecutionException | IOException e) {
				LOGGER.error("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' failed.", e);
				throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' failed.", e);
			}
		} else {
			throw new MediaImportException(new FileNotFoundException(path.toAbsolutePath().toString()));
		}
	}
	
	/**
	 * Draws onto the given image to make it look like film.
	 * @param image the image to draw on
	 */
	private void drawFilmOnFrame(BufferedImage image) {
		final int w = image.getWidth();
		final int h = image.getHeight();
		// FEATURE (L-L) Make video "film" settings dependent on size of thumbnails
		final int lineWidth = 2;
		final int edgeWidth = 5;
		final int blockHeight = 5;
		final int dividerWidth = 4;
		final int n = h / (blockHeight + dividerWidth);
		final int s = (h - n * (blockHeight + dividerWidth) + dividerWidth) / 2;
		
		Graphics2D g = image.createGraphics();
		
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, w, lineWidth);
		g.fillRect(0, h - lineWidth, w, lineWidth);
		g.drawRect(lineWidth * 2 + edgeWidth, lineWidth, w - (lineWidth * 2 + edgeWidth) * 2 - 1, h - lineWidth * 2 - 1);
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, lineWidth * 2 + edgeWidth, h);
		g.fillRect(w - lineWidth * 2 - edgeWidth, 0, lineWidth * 2 + edgeWidth, h);
		
		g.setBackground(new Color(0,0,0,0));
		for (int i = 0; i < n; i++) {
			int y = s + (blockHeight + dividerWidth) * i;
			g.clearRect(lineWidth, y, edgeWidth, blockHeight);
			g.clearRect(w - lineWidth - edgeWidth, y, edgeWidth, blockHeight);
		}
		
		g.dispose();
	}
}
