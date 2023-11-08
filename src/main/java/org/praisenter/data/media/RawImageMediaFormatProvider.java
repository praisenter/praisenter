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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.media.tools.MediaTools;
import org.praisenter.utility.MimeType;

/**
 * {@link MediaLoader} that loads image media.
 * @author William Bittle
 * @version 3.0.0
 */
final class RawImageMediaFormatProvider extends AbstractMediaFormatProvider {
	/** The class-level loader */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public RawImageMediaFormatProvider(
			MediaConfiguration configuration,
			MediaTools tools) {
		super(configuration, tools);
	}
	
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
	
	@Override
	public boolean isSupported(Path path) {
		String mimeType = MimeType.get(path);
		return this.isSupported(mimeType);
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) throws IOException {
		String mimeType = MimeType.get(stream, name);
		return this.isSupported(mimeType);
	}
	
	@Override
	public void exp(PersistAdapter<Media> adapter, OutputStream stream, Media data) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		Path path = mpr.getMediaPath(data);
		Files.copy(path, stream);
	}
	
	@Override
	public void exp(PersistAdapter<Media> adapter, Path path, Media data) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		Path sourcePath = mpr.getMediaPath(data);
		Files.copy(sourcePath, path, StandardCopyOption.REPLACE_EXISTING);
	}
	
	@Override
	public void exp(PersistAdapter<Media> adapter, ZipArchiveOutputStream stream, Media data) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		// TODO this will export the file as "{GUID}.{ext}" we should change this to export as the item name (which could cause collisions)
		Path targetPath = mpr.getExportMediaPath(data);
		Path sourcePath = mpr.getMediaPath(data);
		ZipArchiveEntry entry = new ZipArchiveEntry(FilenameUtils.separatorsToUnix(targetPath.toString()));
		stream.putArchiveEntry(entry);
		Files.copy(sourcePath, stream);
		stream.closeArchiveEntry();
	}
	
	@Override
	public DataImportResult<Media> imp(PersistAdapter<Media> adapter, Path path) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		
		UUID id = UUID.randomUUID();
		
		// default the target location
		String mimeType = MimeType.get(path);
		String extension = this.getExtension(path);
		Path target = mpr.getMediaPath().resolve(mpr.getFileName(id, extension));
				
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
					
					media.setMediaPath(mpr.getMediaPath(media));
					media.setMediaImagePath(mpr.getMediaPath(media));
					media.setMediaThumbnailPath(mpr.getThumbPath(media));

					try {
						// write the JSON data
						JsonIO.write(mpr.getPath(media), media);
					} catch (Exception ex) {
						this.delete(target);
						throw new MediaImportException("Failed to store media metadata for '" + media.getName() + "'.", ex);
					}
					
//					try {
//						// write the image
//						ImageIO.write(image, fmt, mpr.getImagePath(media).toFile());
//					} catch (Exception ex) {
//						this.delete(target, mpr.getPath(media));
//						throw new MediaImportException("Failed to store image for media '" + media.getName() + "'.", ex);
//					}
					
					try {
						// write the thumbnail
						BufferedImage thumb = this.createThumbnail(image);
						ImageIO.write(thumb, mpr.getThumbExtension(), mpr.getThumbPath(media).toFile());
					} catch (Exception ex) {
						this.delete(target, mpr.getPath(media), mpr.getImagePath(media));
						throw new MediaImportException("Failed to store thumbnail for media '" + media.getName() + "'.", ex);
					}
					
					LOGGER.debug("Image media '{}' loaded", path);
					
					DataImportResult<Media> result = new DataImportResult<>();
					result.getCreated().add(media);
					
					return result;
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
