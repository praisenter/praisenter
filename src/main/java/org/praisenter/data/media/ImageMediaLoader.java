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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.media.tools.MediaTools;
import org.praisenter.utility.MimeType;

/**
 * {@link MediaLoader} that loads image media.
 * @author William Bittle
 * @version 3.0.0
 */
final class ImageMediaLoader extends AbstractMediaLoader implements MediaLoader {
	/** The class-level loader */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public ImageMediaLoader(
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
		String[] supportedTypes = ImageIO.getReaderMIMETypes();
		for (String type : supportedTypes) {
			if (type.equalsIgnoreCase(mimeType)) {
				return true;
			}
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
		String mimeType = MimeType.get(path);
		String extension = this.getExtension(path);
		Path target = this.pathResolver.getMediaPath().resolve(this.pathResolver.getFileName(id, extension));
				
		// correct orientation if EXIF header is present
		int orientation = this.getExifOrientation(path);
		if (orientation != -1) {
			this.copyAndCorrectOrientation(path, target, orientation);
		} else {
			Files.copy(path, target);
		}
		
		// read the image
		try (ImageInputStream in = ImageIO.createImageInputStream(target.toFile())) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			// loop through the readers until we find one that works
			while (readers.hasNext()) {
				ImageReader reader = readers.next();
				reader.setInput(in);
				try {
					// read the image
					BufferedImage image = reader.read(0);
					
					// get the format
					String fmt = reader.getFormatName().toLowerCase();
					MediaFormat format = new MediaFormat(fmt, getDescription(fmt));
					
					Media media = new Media();
					media.setAudioAvailable(false);
					media.setExtension(extension);
					media.setHeight(image.getHeight());
					media.setId(id);
					media.setLength(0);
					media.setMediaFormat(format);
					media.setMediaType(MediaType.IMAGE);
					media.setMimeType(mimeType);
					media.setName(path.getFileName().toString());
					media.setWidth(image.getWidth());
					media.setSize(this.getFileSize(target));
					
					media.setMediaImagePath(this.pathResolver.getImagePath(media));
					media.setMediaPath(this.pathResolver.getMediaPath(media));
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
						BufferedImage thumb = this.createThumbnail(image);
						ImageIO.write(thumb, this.pathResolver.getThumbExtension(), this.pathResolver.getThumbPath(media).toFile());
					} catch (Exception ex) {
						this.delete(target, this.pathResolver.getPath(media), this.pathResolver.getImagePath(media));
						throw new MediaImportException("Failed to store thumbnail for media '" + media.getName() + "'.", ex);
					}
					
					LOGGER.debug("Image media '{}' loaded", path);
					return media;
				} finally {
					reader.dispose();
				}
			}
			
			// no readers
			this.delete(target);
			LOGGER.error("No image reader was found for the file '{}'.", path.toAbsolutePath().toString());
			throw new MediaImportException("No image reader was found for the file '" + path.toAbsolutePath().toString() + "'.");
		}
	}
	
	/**
	 * Returns the given format's long name.
	 * @param format the format all lower case
	 * @return String
	 */
	private static final String getDescription(String format) {
		if (format.equals("png")) {
			return "Portable Network Graphic";
		} else if (format.equals("bmp")) {
			return "Bitmap Uncompressed Graphic";
		} else if (format.equals("wbmp")) {
			return "Wireless Bitmap Graphic (1 bit color depth)";
		} else if (format.equals("jpg") || format.equals("jpeg")) {
			return "JPEG Compressed Graphic";
		} else if (format.equals("gif")) {
			return "Graphical Interchange Format";
		} else {
			LOGGER.warn("Missing format description for '" + format + "'");
		}
		return "";
	}
}
