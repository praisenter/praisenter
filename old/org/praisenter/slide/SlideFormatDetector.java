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
package org.praisenter.slide;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.data.InvalidFormatException;
import org.praisenter.data.UnknownFormatException;
import org.praisenter.data.json.JsonIO;
import org.praisenter.data.json.PraisenterFormat;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;

/**
 * A slide importer that attempts to determine the format of the given path using
 * some basic knowledge of each format and use the appropriate {@link SlideImporter} for that format.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class SlideFormatDetector {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Examines the given path and determines how to import the data.
	 * <p>
	 * If the path is a zip, the zip is inspected for the contents. Otherwise the file is imported as a single file.
	 * @param path the path to the file
	 * @return List&lt;{@link Slide}&gt;
	 * @throws IOException if an IO error occurs
	 * @throws FileNotFoundException if the given path no longer exists
	 * @throws InvalidFormatException if the format detected is invalid
	 * @throws UnknownFormatException if the format could not be detected
	 */
	public SlideImportResult execute(Path path) throws IOException, FileNotFoundException, InvalidFormatException, UnknownFormatException {
		if (!Files.exists(path)) throw new FileNotFoundException(path.toAbsolutePath().toString());
		
		List<Slide> slides = new ArrayList<Slide>();
		List<SlideShow> shows = new ArrayList<SlideShow>();
		boolean isZip = false;
		
		// we'll try by the file extension first
		if (MimeType.ZIP.check(path)) {
			// otherwise check for mixed content (each file could be a different format)
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory()) {
						// if we get here, that means that this file is a zip
						isZip = true;
						byte[] content = Streams.read(zis);
						ByteArrayInputStream bais = new ByteArrayInputStream(content);
						SlideImporter importer = this.getImporter(entry.getName().toLowerCase().toString(), bais);
						if (importer != null) {
							String name = Paths.get(entry.getName()).getFileName().toString();
							try {
								SlideImportResult sir = importer.execute(name, bais);
								slides.addAll(sir.getSlides());
								shows.addAll(sir.getSlideShows());
							} catch (Exception ex) {
								LOGGER.warn("Failed to import file '" + entry.getName() + "' using importer '" + importer.getClass().getName() + "'.", ex);
							}
						}
					}
				}
			}
		}
		
		// if it wasn't really a zip, then lets try to read it as a single file
		if (!isZip) {
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis)) {
				byte[] content = Streams.read(bis);
				ByteArrayInputStream bais = new ByteArrayInputStream(content);
				SlideImporter importer = this.getImporter(path.getFileName().toString().toLowerCase(), bais);
				if (importer != null) {
					return importer.execute(path.getFileName().toString(), bais);
				} else {
					throw new UnknownFormatException("The file '" + path.toAbsolutePath().toString() + "' was not in a recognized slide file format.");
				}
			}
		}
		
		// check for some slides
		if (slides.isEmpty() && shows.isEmpty()) {
			throw new UnknownFormatException("The file '" + path.toAbsolutePath().toString() + "' was not in a recognized slide file format.");
		}
		
		return new SlideImportResult(slides, shows);
	}
	
	/**
	 * Attempts to return a {@link SlideImporter} for the given stream and file name.
	 * <p>
	 * Returns null if the format is unknown or cannot be determined.
	 * @param fileName the file name
	 * @param stream the stream
	 * @return {@link SlideImporter}
	 */
	private SlideImporter getImporter(String fileName, InputStream stream) {
		// get it based on the stream data
		String mimeType = MimeType.get(stream, fileName);
		
		// check for null
		if (mimeType == null) {
			// if it's null, then use the file name
			mimeType = MimeType.get(fileName);
		}
		
		// BASED ON DETECTED MIME TYPE
		
		// check for XML
		if (MimeType.JSON.is(mimeType)) {
			// Praisenter or Unknown
			try {
				PraisenterFormat format = JsonIO.getPraisenterFormat(stream);
				if (format != null && (format.is(Slide.class) || format.is(SlideShow.class))) {
					stream.reset();
					return new PraisenterSlideImporter();
				} else {
					// unknown
					return null;
				}
			} catch (Exception ex) {
				LOGGER.warn("Failed to determine if JSON file '" + fileName + "' is in the Praisenter format.", ex);
			}
		}
		
		// BASED ON FILE EXTENSION
		
		// if we get here our file mime type detection likely failed
		// so try to go off of extension
		if (fileName.endsWith(Constants.SLIDE_FILE_EXTENSION)) {
			try {
				PraisenterFormat format = JsonIO.getPraisenterFormat(stream);
				if (format != null && (format.is(Slide.class) || format.is(SlideShow.class))) {
					stream.reset();
					return new PraisenterSlideImporter();
				} else {
					// unknown
					return null;
				}
			} catch (Exception ex) {
				LOGGER.warn("Failed to determine if JSON file '" + fileName + "' is in the Praisenter format.", ex);
			}
		}
		
		// BASED ON FIRST LINE OF FILE
		
		// if we get here, mime type and extension detection failed
		// try to read the first line of the file to determine what
		// to do
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			// check for JSON
			if (line.startsWith("{")) {
				// check for Praisenter
				PraisenterFormat format = JsonIO.getPraisenterFormat(stream);
				if (format != null && (format.is(Slide.class) || format.is(SlideShow.class))) {
					stream.reset();
					return new PraisenterSlideImporter();
				}
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed to detect file format for '" + fileName + "' based on first line of file.", ex);
		}
		
		LOGGER.warn("Unable to determine or unknown file format '{}'.", fileName);
		
		return null;
	}
}
