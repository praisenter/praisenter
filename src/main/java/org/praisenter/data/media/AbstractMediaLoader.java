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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.media.tools.MediaTools;
import org.praisenter.data.media.MediaImportException;
import org.praisenter.utility.ImageManipulator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.twelvemonkeys.image.ImageUtil;
import com.twelvemonkeys.image.ResampleOp;

/**
 * An abstract implementation of the {@link MediaLoader} interface.
 * @author William Bittle
 * @version 3.0.0
 */
abstract class AbstractMediaLoader implements MediaLoader {
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The context */
	protected final MediaPathResolver pathResolver;
	protected final MediaConfiguration configuration;
	protected final MediaTools tools;
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public AbstractMediaLoader(
			MediaPathResolver pathResolver, 
			MediaConfiguration configuration,
			MediaTools tools) {
		this.pathResolver = pathResolver;
		this.configuration = configuration;
		this.tools = tools;
	}
	
	/**
	 * Returns the extension for the given file.
	 * @param path
	 * @return
	 */
	protected String getExtension(Path path) {
		return FilenameUtils.getExtension(path.getFileName().toString());
	}
	
	/**
	 * Returns the files size of the given path.
	 * @param path the path
	 * @return long
	 * @throws IOException
	 */
	protected long getFileSize(Path path) throws IOException {
		try {
			return Files.size(path);	
		} catch (IOException ex) {
			LOGGER.warn("Failed to get file size of '{}'.", path.toAbsolutePath().toString());
		}
		return Media.UNKNOWN;
	}
	
	/**
	 * Copies the source image to the target path correcting the orientation
	 * as part of the copy.
	 * @param source the source
	 * @param target the target
	 * @param orientation the orientation
	 * @throws IOException if an IO error occurs 
	 */
	protected void copyAndCorrectOrientation(Path source, Path target, int orientation) throws IOException {
		// read the image
		try (ImageInputStream in = ImageIO.createImageInputStream(source.toFile())) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			// loop through the readers until we find one that works
			while (readers.hasNext()) {
				ImageReader reader = readers.next();
				reader.setInput(in);
				// read and correct the image
				LOGGER.debug("Correcting orientation of '{}'.", source);
				BufferedImage image = ImageManipulator.correctExifOrientation(reader.read(0), orientation);
				
				// convert so that saving a JPG with alpha doesn't alter the image
				if (image.getColorModel().hasAlpha() && ("JPG".equals(reader.getFormatName().toUpperCase()) || "JPEG".equals(reader.getFormatName().toUpperCase()))) {
					LOGGER.debug("Converting color model of '{}'.", source);
					BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = bi.createGraphics();
					g2d.drawImage(image, 0, 0, null);
					g2d.dispose();
					image = bi;
				}
				
				LOGGER.debug("Writing corrected image to media library.");
				ImageIO.write(image, reader.getFormatName(), target.toFile());
				break;
			}
		}
	}
	
	/**
	 * Returns the orientation stored in a EXIF header or -1 if
	 * it doesnt exist or cannot be read.
	 * @param source the file
	 * @return int
	 */
	protected int getExifOrientation(Path source) {
		// attempt to read the EXIF orientation
		// and just log any exceptions
		int orientation = -1;
		try {
			Metadata meta = ImageMetadataReader.readMetadata(source.toFile());
			ExifIFD0Directory directory = meta.getFirstDirectoryOfType(ExifIFD0Directory.class);
			if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			}
		} catch (Exception e) {
			LOGGER.warn("Failed to read EXIF orientation for '{}': {}.", source.toAbsolutePath().toString(), e.getMessage());
		}
		return orientation;
	}
	
	/**
	 * Creates a thumbnail from the given image using the current
	 * thumbnail settings.
	 * @param image the full size image
	 * @return BufferedImage
	 */
	protected final BufferedImage createThumbnail(BufferedImage image) {
		// convert the image to an image type with transparency first
		BufferedImage withTransparency = ImageUtil.toBuffered(image, BufferedImage.TYPE_INT_ARGB);
		// then down scale
		return ImageManipulator.getUniformScaledImage(
				withTransparency, 
				this.configuration.getThumbnailWidth(), 
				this.configuration.getThumbnailHeight(), 
				ResampleOp.FILTER_LANCZOS);
	}

	/**
	 * Transcodes the given source file from its current format to a supported format using FFmpeg CLI.
	 * <p>
	 * This method blocks until transcoding is complete.
	 * @param source the source file
	 * @param target the target file
	 * @param type the media type
	 * @throws MediaImportException if an error occurs during transcoding
	 */
	protected final void transcode(Path source, Path target, MediaType type) throws MediaImportException {
		// split the command by whitespace
		String command = (type == MediaType.VIDEO 
				 ? this.configuration.getVideoTranscodeCommand()
				 : this.configuration.getAudioTranscodeCommand());
		
		try {
			this.tools.ffmpegTranscode(command, source, target);
		} catch (IOException ex) {
			throw new MediaImportException("Failed to transcode media '" + source.toAbsolutePath().toString() + "'.", ex);
		} catch (InterruptedException ex) {
			throw new MediaImportException("Failed to transcode media '" + source.toAbsolutePath().toString() + "' due to the process being interrupted.", ex);
		}
	}
	
	/**
	 * Deletes the given paths and logs any errors.
	 * @param paths
	 */
	protected final void delete(Path... paths) {
		for (Path path : paths) {
			try {
				Files.deleteIfExists(path);
			} catch (Exception ex) {
				LOGGER.error("Failed to clean up copied media '" + path.toAbsolutePath() + "' after failed load.", ex);
			}
		}
	}
}
