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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.utility.ImageManipulator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * The default media import filter which simply copies the source to the target.
 * @author William Bittle
 * @version 3.0.0
 */
public class DefaultMediaImportFilter implements MediaImportFilter {
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaImportFilter#getTarget(java.nio.file.Path, java.lang.String, org.praisenter.media.MediaType)
	 */
	@Override
	public Path getTarget(Path location, String name, MediaType type) {
		// by default it should be the file name in the location
		return location.resolve(name);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaImportFilter#filter(java.nio.file.Path, java.nio.file.Path, org.praisenter.media.MediaType)
	 */
	@Override
	public void filter(Path source, Path target, MediaType type) throws TranscodeException, FileAlreadyExistsException, IOException {
		// just copy from source to target
		
		// see if we can use the same name in the destination file
		if (Files.exists(target)) {
			throw new FileAlreadyExistsException(target.toAbsolutePath().toString());
		}
		
		// for image media we want to go ahead and fix any rotation stuff
		if (type == MediaType.IMAGE) {
			// attempt to get the orientation of the image
			int orientation = getExifOrientation(source);
			// if we did, fix it if necessary
			if (orientation != -1 && orientation != 1) {
				try {
					copyAndCorrectOrientation(source, target, orientation);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
		}
		
		Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Copies the source image to the target path correcting the orientation
	 * as part of the copy.
	 * @param source the source
	 * @param target the target
	 * @param orientation the orientation
	 * @throws IOException if an IO error occurs
	 * @throws InterruptedException 
	 */
	private void copyAndCorrectOrientation(Path source, Path target, int orientation) throws IOException, InterruptedException {
		// read the image
		try (ImageInputStream in = ImageIO.createImageInputStream(source.toFile())) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			// loop through the readers until we find one that works
			while (readers.hasNext()) {
				ImageReader reader = readers.next();
				reader.setInput(in);
				// read and correct the image
				BufferedImage image = ImageManipulator.correctExifOrientation(reader.read(0), orientation);
				
				if (image.getColorModel().hasAlpha() && ("JPG".equals(reader.getFormatName().toUpperCase()) || "JPEG".equals(reader.getFormatName().toUpperCase()))) {
					BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
					Graphics2D g2d = bi.createGraphics();
					g2d.drawImage(image, 0, 0, null);
					g2d.dispose();
					image = bi;
				}
				
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
	private int getExifOrientation(Path source) {
		// attempt to read the EXIF orientation
		// and just log any exceptions
		int orientation = -1;
		try {
			Metadata meta = ImageMetadataReader.readMetadata(source.toFile());
			ExifIFD0Directory directory = meta.getFirstDirectoryOfType(ExifIFD0Directory.class);
			if (directory != null) {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			}
		} catch (Exception e) {
			LOGGER.warn("Failed to read EXIF orientation for '{}': {}.", source.toAbsolutePath().toString(), e.getMessage());
		}
		return orientation;
	}
}
