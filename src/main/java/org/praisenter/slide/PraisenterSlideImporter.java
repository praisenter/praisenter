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
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.InvalidFormatException;
import org.praisenter.UnknownFormatException;
import org.praisenter.utility.Zip;
import org.praisenter.xml.XmlIO;

/**
 * {@link SlideImporter} for the Praisenter slide format.
 * @author William Bittle
 * @version 3.0.0
 */
public final class PraisenterSlideImporter implements SlideImporter {
	/** The class level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideImporter#execute(java.nio.file.Path)
	 */
	@Override
	public List<Slide> execute(Path path) throws IOException, JAXBException, FileNotFoundException, InvalidFormatException, UnknownFormatException {
		List<Slide> slides = new ArrayList<Slide>();
		
		// make sure the file exists
		if (Files.exists(path)) {
			LOGGER.debug("Reading file: " + path.toAbsolutePath().toString());

			boolean read = false;
			Throwable throwable = null;
			// first try to open it as a zip
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				LOGGER.debug("Reading as zip file: " + path.toAbsolutePath().toString());
				// read the entries (each should be a .xml file)
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					read = true;
					if (!entry.isDirectory()) {
						byte[] data = Zip.read(zis);
						try {
							// make a copy to ensure the id is changed
							Slide slide = XmlIO.read(new ByteArrayInputStream(data), BasicSlide.class).copy(false);
							slides.add(slide);
						} catch (Exception ex) {
							throwable = ex;
							LOGGER.warn("Failed to parse zip entry: " + entry.getName());
						}
					}
				}
			}
			
			// check if we read an entry
			// if not, that may mean the file was not a zip so try it as a normal file
			if (!read) {
				LOGGER.debug("Reading as XML file: " + path.toAbsolutePath().toString());
				// hopefully its an .xml
				// just read it
				try (FileInputStream stream = new FileInputStream(path.toFile())) {
					// make a copy to ensure the id is changed
					Slide slide = XmlIO.read(stream, BasicSlide.class).copy(false);
					slides.add(slide);
				}
			}

			// throw the exception stored during the unzip process
			// only if we didn't find any slides (if we successfully read in
			// a slide from the zip then we don't want to throw)
			if (slides.size() == 0 && throwable != null) {
				throw new InvalidFormatException(throwable.getMessage(), throwable);
			}

			return slides;
		} else {
			// throw an exception
			throw new FileNotFoundException(path.toAbsolutePath().toString());
		}
	}
}
