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

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.resources.translations.Translations;
import org.praisenter.utility.ImageManipulator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * {@link MediaLoader} that loads image media.
 * @author William Bittle
 * @version 3.0.0
 */
public final class ImageMediaLoader extends AbstractMediaLoader implements MediaLoader {
	/** The class-level loader */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param settings the thumbnail settings
	 */
	public ImageMediaLoader(MediaThumbnailSettings settings) {
		super(settings);
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
	 * @see org.praisenter.media.MediaLoader#load(java.nio.file.Path)
	 */
	@Override
	public LoadedMedia load(Path path) throws IOException, FileNotFoundException, MediaFormatException {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			
			// attempt to read the EXIF orientation
			// and just log any exceptions
			int orientation = -1;
			try {
				Metadata meta = ImageMetadataReader.readMetadata(path.toFile());
				ExifIFD0Directory directory = meta.getFirstDirectoryOfType(ExifIFD0Directory.class);
				if (directory != null) {
					orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
				}
			} catch (Exception e) {
				LOGGER.warn("Failed to read EXIF orientation for '{}': {}.", path.toAbsolutePath().toString(), e.getMessage());
			}
			
			// read the image
			try (ImageInputStream in = ImageIO.createImageInputStream(path.toFile())) {
				Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
				// loop through the readers until we find one that works
				while (readers.hasNext()) {
					ImageReader reader = readers.next();
					reader.setInput(in);
					try {
						// read and correct the image
						BufferedImage image = ImageManipulator.correctExifOrientation(reader.read(0), orientation);
						BufferedImage thumb = createThumbnail(image);
						String fmt = reader.getFormatName().toLowerCase();
						
						MediaFormat format = new MediaFormat(fmt, getDescription(fmt));
						MediaMetadata metadata = MediaMetadata.forImage(path, format, (int)image.getWidth(), (int)image.getHeight(), null);
						Media media = new Media(metadata, thumb);
						
						return new LoadedMedia(media, image);
					} finally {
						reader.dispose();
					}
				}
				
				// no readers
				throw new MediaFormatException(MessageFormat.format(Translations.get("media.import.error.image.reader.missing"), path.toAbsolutePath().toString()));
			}
		} else {
			throw new FileNotFoundException(MessageFormat.format(Translations.get("error.file.missing"), path.toAbsolutePath().toString()));
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
		}
		return "";
	}
}
