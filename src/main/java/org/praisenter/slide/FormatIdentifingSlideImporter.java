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
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.InvalidFormatException;
import org.praisenter.UnknownFormatException;
import org.praisenter.utility.Streams;

/**
 * A slide importer that attempts to determine the format of the given path using
 * some basic knowledge of each format and use the appropriate {@link SlideImporter} for that format.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class FormatIdentifingSlideImporter implements SlideImporter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideImporter#execute(java.nio.file.Path)
	 */
	@Override
	public List<Slide> execute(Path path) throws IOException, FileNotFoundException, InvalidFormatException, UnknownFormatException {
		// make sure the file exists
		if (Files.exists(path)) {
			SlideImporter importer = this.getImporter(path);
			if (importer == null) {
				throw new UnknownFormatException(path.toAbsolutePath().toString());
			}
			return importer.execute(path);
		} else {
			throw new FileNotFoundException(path.toAbsolutePath().toString());
		}
	}
	
	/**
	 * Attempts to read the file to determine what format the file is.
	 * @param path the path
	 * @return {@link SlideImporter}
	 * @throws IOException if an IO error occurs
	 * @throws FileNotFoundException if the file is not found
	 */
	private SlideImporter getImporter(Path path) throws IOException, FileNotFoundException {
		String fileName = path.getFileName().toString().toLowerCase();
		// we'll try by the file extension first
		if (fileName.endsWith(".zip")) {
			// read the contents to see what format we are looking at
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					// check for .xml extension, could be any kind of XML really
					if (entry.getName().toLowerCase().endsWith(".xml")) {
						byte[] content = Streams.read(zis);
						return this.getImporterForXml(new ByteArrayInputStream(content));
					// check for praisenter format
					} else if (entry.getName().toLowerCase().endsWith(Constants.SLIDE_FILE_EXTENSION)) {
						return new PraisenterSlideImporter();
					// otherwise read the first line of the file to see
					// if we can determine the file type that way
					} else {
						byte[] content = Streams.read(zis);
						SlideImporter bi = this.getImporterForFile(new ByteArrayInputStream(content));
						if (bi != null) {
							return bi;
						}
					}
				}
			}
		// check for .xml extension, could be any kind of XML really
		} else if (fileName.endsWith(".xml")) {
			byte[] content = Streams.read(new FileInputStream(path.toFile()));
			return this.getImporterForXml(new ByteArrayInputStream(content));
		// check for praisenter format
		} else if (fileName.endsWith(Constants.SLIDE_FILE_EXTENSION)) {
			return new PraisenterSlideImporter();
		// otherwise read the first line of the file to see
		// if we can determine the file type that way
		} else {
			byte[] content = Streams.read(new FileInputStream(path.toFile()));
			SlideImporter importer = this.getImporterForFile(new ByteArrayInputStream(content));
			return importer;
		}
		
		return null;
	}
	
	/**
	 * Returns a {@link SlideImporter} for the given stream assuming its an XML document.
	 * <p>
	 * Returns null if the file was not an XML document or the format is not known.
	 * @param stream the file stream
	 * @return {@link SlideImporter}
	 */
	private SlideImporter getImporterForXml(ByteArrayInputStream stream) {
		return null;
	}
	
	/**
	 * Returns a {@link SlideImporter} for the given file.
	 * <p>
	 * This method will read the file and return as soon as it can determine
	 * a valid {@link SlideImporter} for the data contained.
	 * @param stream the file stream
	 * @return {@link SlideImporter}
	 */
	private SlideImporter getImporterForFile(ByteArrayInputStream stream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			if (line.toUpperCase().startsWith("<?XML")) {
				// then its an xml document
				stream.reset();
				return getImporterForXml(stream);
			} else if (line.startsWith("{")) {
				// it's a json file
				return new PraisenterSlideImporter();
			}
		} catch (IOException ex) {
			LOGGER.warn("An error occurred while trying to determine the format of the file.", ex);
		}
		return null;
	}
}
