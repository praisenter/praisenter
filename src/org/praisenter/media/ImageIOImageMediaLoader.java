/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Represents an Image loader using ImageIO.
 * @author William Bittle
 * @version 2.0.2
 * @since 2.0.0
 */
public class ImageIOImageMediaLoader implements ImageMediaLoader, MediaLoader<ImageMedia> {
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
	 * @see org.praisenter.media.MediaLoader#getMediaType()
	 */
	@Override
	public Class<ImageMedia> getMediaType() {
		return ImageMedia.class;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#load(java.lang.String, java.lang.String)
	 */
	@Override
	public ImageMedia load(String basePath, String filePath) throws MediaException {
		Path path = FileSystems.getDefault().getPath(filePath);
		if (Files.exists(path) && Files.isRegularFile(path)) {
			try (ImageInputStream in = ImageIO.createImageInputStream(path.toFile())) {
				Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
				// loop through the readers until we find one that works
				while (readers.hasNext()) {
					ImageReader reader = readers.next();
					reader.setInput(in);
					ImageMediaFile file = new ImageMediaFile(
							basePath,
							filePath, 
							reader.getFormatName(), 
							reader.getWidth(0), 
							reader.getHeight(0));
					try {
						return new ImageMedia(file, reader.read(0));
					} finally {
						reader.dispose();
					}
				}
				// no readers
				throw new NoMediaLoaderException();
			} catch (IOException e) {
				throw new MediaException(e);
			}
		} else {
			throw new MediaException(new FileNotFoundException(path.toString()));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.media.MediaLoader#getSupportedFormats()
	 */
	@Override
	public List<Pair<String, String>> getSupportedContainerFormats() {
		List<Pair<String, String>> out = new ArrayList<Pair<String, String>>();
		String[] formats = ImageIO.getReaderFormatNames();
		// filter duplicates
		HashSet<String> set = new HashSet<String>();
		for (String format : formats) {
			format = format.toLowerCase();
			if (set.add(format)) {
				// try to come up with a description since ImageIO doesn't 
				// give this to us
				String description = "";
				if (format.equals("png")) {
					description = "Portable Network Graphic";
				} else if (format.equals("bmp")) {
					description = "Bitmap Uncompressed Graphic";
				} else if (format.equals("wbmp")) {
					description = "Wireless Bitmap Graphic (1 bit color depth)";
				} else if (format.equals("jpg") || format.equals("jpeg")) {
					description = "JPEG Compressed Graphic";
				} else if (format.equals("gif")) {
					description = "Graphical Interchange Format";
				}
				out.add(Pair.of(format, description));
			}
		}
		return out;
	}
}
