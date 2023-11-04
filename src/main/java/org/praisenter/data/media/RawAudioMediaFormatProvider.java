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
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.PersistAdapter;
import org.praisenter.data.media.tools.FFProbeMediaMetadata;
import org.praisenter.data.media.tools.MediaTools;
import org.praisenter.utility.ClasspathLoader;
import org.praisenter.utility.MimeType;

/**
 * {@link MediaLoader} that loads audio media.
 * @author William Bittle
 * @version 3.0.0
 */
final class RawAudioMediaFormatProvider extends AbstractMediaFormatProvider {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param context the context
	 */
	public RawAudioMediaFormatProvider(
			MediaConfiguration configuration,
			MediaTools tools) {
		super(configuration, tools);
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		if (mimeType != null && mimeType.startsWith("audio")) {
			// ffmpeg/humble does not support midi
			if (mimeType.contains("midi")) {
				return false;
			}
			// handle any mimetype
			return true;
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
	public void exp(PersistAdapter<Media> adapter, ZipOutputStream stream, Media data) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		// TODO this will export the file as "{GUID}.{ext}" we should change this to export as the item name (which could cause collisions)
		Path targetPath = mpr.getExportMediaPath(data);
		Path sourcePath = mpr.getMediaPath(data);
		ZipEntry entry = new ZipEntry(FilenameUtils.separatorsToUnix(targetPath.toString()));
		stream.putNextEntry(entry);
		Files.copy(sourcePath, stream);
		stream.closeEntry();
	}
	
	@Override
	public DataImportResult<Media> imp(PersistAdapter<Media> adapter, Path path) throws IOException {
		MediaPathResolver mpr = (MediaPathResolver)adapter.getPathResolver();
		UUID id = UUID.randomUUID();
		
		// default the target location
		String extension = this.getExtension(path);
		Path target = mpr.getMediaPath().resolve(mpr.getFileName(id, extension));
		
		// are we doing transcoding?
		if (this.configuration.isAudioTranscodingEnabled() && this.isValidTranscodeCommand(MediaType.AUDIO)) {
			extension = this.configuration.getAudioTranscodeExtension();
			// get the proper target path
			target = mpr.getMediaPath().resolve(mpr.getFileName(id, extension));
			// transcode the file
			this.transcode(path, target, MediaType.AUDIO);
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
			if (!metadata.hasAudio()) {
				throw new MediaImportException("No audio stream was found in the file '" + path.toAbsolutePath().toString() + "'.");
			}
		} catch (InterruptedException e) {
			this.delete(target);
			throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' was interrupted.", e);
		} catch (IOException e) {
			this.delete(target);
			throw new MediaImportException("The process to extract metadata from '" + path.toAbsolutePath().toString() + "' failed.", e);
		}
		
		media = new Media();
		media.setAudioAvailable(true);
		media.setExtension(extension);
		media.setHeight(0);
		media.setId(id);
		media.setLength(metadata.getLength());
		media.setMediaFormat(metadata.getFormat());
		media.setMediaType(MediaType.AUDIO);
		media.setMimeType(MimeType.get(target));
		media.setName(path.getFileName().toString());
		media.setWidth(0);
		media.setSize(this.getFileSize(target));
		
		media.setMediaPath(mpr.getMediaPath(media));
		media.setMediaImagePath(mpr.getThumbPath(media));
		media.setMediaThumbnailPath(mpr.getThumbPath(media));

		try {
			// write the JSON data
			adapter.create(media);
		} catch (Exception ex) {
			this.delete(target);
			throw new MediaImportException("Failed to store media metadata for '" + media.getName() + "'.", ex);
		}
		
		// images
		BufferedImage image = ClasspathLoader.getBufferedImage("/org/praisenter/images/audio-default-thumbnail.png");
		
		// FEATURE (L-H) consider calling an external service to get album art  https://musicbrainz.org/doc/MusicBrainz_API/Search
//		try {
//			// write the image
//			ImageIO.write(image, this.pathResolver.getImageExtension(), this.pathResolver.getImagePath(media).toFile());
//		} catch (Exception ex) {
//			this.delete(target, this.pathResolver.getPath(media));
//			throw new MediaImportException("Failed to store image for audio media from the classpath default.", ex);
//		}
		
		try {
			// write the thumbnail
			BufferedImage thumb = this.createThumbnail(image);
			ImageIO.write(thumb, mpr.getThumbExtension(), mpr.getThumbPath(media).toFile());
		} catch (Exception ex) {
			this.delete(target, mpr.getPath(media), mpr.getImagePath(media));
			throw new MediaImportException("Failed to store image for audio media from the classpath default.", ex);
		}
		
		DataImportResult<Media> result = new DataImportResult<>();
		result.getCreated().add(media);
		
		LOGGER.debug("Audio media '{}' loaded", path);
		return result;
	}
}
