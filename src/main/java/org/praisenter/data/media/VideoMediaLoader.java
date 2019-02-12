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
package org.praisenter.data.media;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.media.tools.FFProbeMediaMetadata;
import org.praisenter.data.media.tools.MediaTools;
import org.praisenter.utility.MimeType;

/**
 * {@link MediaLoader} that loads video media.
 * @author William Bittle
 * @version 3.0.0
 */
final class VideoMediaLoader extends AbstractMediaLoader implements MediaLoader {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public VideoMediaLoader(
			MediaPathResolver pathResolver, 
			MediaConfiguration configuration,
			MediaTools tools) {
		super(pathResolver, configuration, tools);
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
	 * @see org.praisenter.data.media.MediaLoader#load(java.nio.file.Path)
	 */
	@Override
	public Media load(Path path) throws IOException {
		UUID id = UUID.randomUUID();
		
		// default the target location
		String extension = this.getExtension(path);
		Path target = this.pathResolver.getMediaPath().resolve(this.pathResolver.getFileName(id, extension));
		
		// are we doing transcoding?
		if (this.configuration.isVideoTranscodingEnabled() && this.isValidTranscodeCommand(MediaType.VIDEO)) {
			extension = this.configuration.getVideoTranscodeExtension();
			// get the proper target path
			target = this.pathResolver.getMediaPath().resolve(this.pathResolver.getFileName(id, extension));
			// transcode the file
			this.transcode(path, target, MediaType.VIDEO);
		} else {
			// just copy the file
			Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
		}
		
		// now that the media is the proper location and in the proper format
		// we need to load the media metadata
		
		// get the metadata
		Media media = null;
		FFProbeMediaMetadata metadata = null;
		
		try {
			metadata = this.tools.ffprobeExtractMetadata(target);
			if (!metadata.hasVideo()) {
				throw new MediaImportException("No video stream was found in the file '" + path.toAbsolutePath().toString() + "'.");
			}
		} catch (InterruptedException e) {
			this.delete(target);
			throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' was interrupted.", e);
		} catch (IOException e) {
			this.delete(target);
			throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' failed.", e);
		}
		
		// try to produce a frame capture and thumbnail
		BufferedImage image = null;
		
		try {
			String command = this.configuration.getVideoFrameExtractCommand();
			
			LOGGER.debug("Video media '{}' - searching for best frame.", path);
			image = this.tools.ffmpegExtractFrame(command, path);
		} catch (Exception ex) {
			LOGGER.warn("Failed to extract frame from video '" + path.toAbsolutePath().toString() + "'.");
		}

		media = new Media();
		media.setAudioAvailable(metadata.hasAudio());
		media.setExtension(extension);
		media.setHeight(metadata.getHeight());
		media.setId(id);
		media.setLength(metadata.getLength());
		media.setMediaFormat(metadata.getFormat());
		media.setMediaType(MediaType.VIDEO);
		media.setMimeType(MimeType.get(target));
		media.setName(path.getFileName().toString());
		media.setWidth(metadata.getWidth());
		media.setSize(this.getFileSize(target));
		
		media.setMediaPath(this.pathResolver.getMediaPath(media));
		media.setMediaImagePath(this.pathResolver.getImagePath(media));
		media.setMediaThumbnailPath(this.pathResolver.getThumbPath(media));

		try {
			// write the JSON data
			JsonIO.write(this.pathResolver.getPath(media), media);
		} catch (Exception ex) {
			this.delete(target);
			throw new MediaImportException("Failed to store media metadata for '" + media.getName() + "'.", ex);
		}
		
		try {
			// write the image
			ImageIO.write(image, this.pathResolver.getImageExtension(), this.pathResolver.getImagePath(media).toFile());
		} catch (Exception ex) {
			this.delete(target, this.pathResolver.getPath(media));
			throw new MediaImportException("Failed to store image for media '" + media.getName() + "'.", ex);
		}
		
		try {
			// write the thumbnail
			BufferedImage thumb = null;
			if (image != null) {
				LOGGER.debug("Video media '{}' - creating thumbnail.", path);
				thumb = this.createThumbnail(image);
				this.drawFilmOnFrame(thumb);
			}
			ImageIO.write(thumb, this.pathResolver.getThumbExtension(), this.pathResolver.getThumbPath(media).toFile());
		} catch (Exception ex) {
			this.delete(target, this.pathResolver.getPath(media), this.pathResolver.getImagePath(media));
			throw new MediaImportException("Failed to store thumbnail for media '" + media.getName() + "'.", ex);
		}
		
		return media;
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
