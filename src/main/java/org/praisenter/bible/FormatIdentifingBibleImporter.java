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
package org.praisenter.bible;

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

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.Constants;
import org.praisenter.InvalidFormatException;
import org.praisenter.UnknownFormatException;
import org.praisenter.utility.Zip;

/**
 * A bible importer that attempts to determine the format of the given path using
 * some basic knowledge of each format and use the appropriate {@link BibleImporter} for that format.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class FormatIdentifingBibleImporter implements BibleImporter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/* (non-Javadoc)
	 * @see org.praisenter.bible.BibleImporter#execute(java.nio.file.Path)
	 */
	@Override
	public List<Bible> execute(Path path) throws IOException, JAXBException, FileNotFoundException, InvalidFormatException, UnknownFormatException {
		// make sure the file exists
		if (Files.exists(path)) {
			BibleImporter importer = this.getImporter(path);
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
	 * @return {@link BibleImporter}
	 * @throws IOException if an IO error occurs
	 * @throws FileNotFoundException if the file is not found
	 */
	private BibleImporter getImporter(Path path) throws IOException, FileNotFoundException {
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
					// check for unbound bible book file
					if (entry.getName().equalsIgnoreCase("book_names.txt")) {
						return new UnboundBibleImporter();
					// check for .xmm extension (OpenSong)
					} else if (entry.getName().toLowerCase().endsWith(".xmm")) {
						return new OpenSongBibleImporter();
					// check for .xml extension, could be any kind of XML really (Zefania)
					} else if (entry.getName().toLowerCase().endsWith(".xml")) {
						byte[] content = Zip.read(zis);
						return this.getImporterForXml(new ByteArrayInputStream(content));
					// otherwise read the first line of the file to see
					// if we can determine the file type that way
					} else {
						byte[] content = Zip.read(zis);
						BibleImporter bi = this.getImporterForFile(new ByteArrayInputStream(content));
						if (bi != null) {
							return bi;
						}
					}
				}
			}
		// check for .xmm extension (OpenSong)
		} else if (fileName.endsWith(".xmm")) {
			return new OpenSongBibleImporter();
		// check for .xml extension, could be any kind of XML really (Zefania)
		} else if (fileName.endsWith(".xml")) {
			byte[] content = Zip.read(new FileInputStream(path.toFile()));
			return this.getImporterForXml(new ByteArrayInputStream(content));
		// otherwise read the first line of the file to see
		// if we can determine the file type that way
		} else {
			byte[] content = Zip.read(new FileInputStream(path.toFile()));
			BibleImporter importer = this.getImporterForFile(new ByteArrayInputStream(content));
			if (importer instanceof UnboundBibleImporter) {
				// we need two files for The Unbound Bible format, we can't just read one of them
				LOGGER.warn("The Unbound Bible format detected, but two files are required: book_names.txt and the file containing the text. Please create a zip file with these two files and import that.");
				importer = null;
			}
			return importer;
		}
		
		return null;
	}
	
	/**
	 * Returns a {@link BibleImporter} for the given stream assuming its an XML document.
	 * <p>
	 * Returns null if the file was not an XML document.
	 * @param stream the file stream
	 * @return {@link BibleImporter}
	 */
	private BibleImporter getImporterForXml(ByteArrayInputStream stream) {
		try {
			XMLInputFactory f = XMLInputFactory.newInstance();
			XMLStreamReader r = f.createXMLStreamReader(stream);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().equalsIgnoreCase("xmlbible") ||
			    		r.getLocalName().equalsIgnoreCase("x")) {
			    		return new ZefaniaBibleImporter();
			    	} else if (r.getLocalName().equalsIgnoreCase("bible")) {
			    		String format = r.getAttributeValue(null, "format");
			    		if (format != null && format.toLowerCase().equals(Constants.FORMAT_NAME)) {
			    			return new PraisenterBibleImporter();
			    		}
			    		return new OpenSongBibleImporter();
			    	}
			    }
			}
		} catch (Exception ex) {
			LOGGER.warn("An error occurred while trying read the file as an XML document.", ex);
		}
		return null;
	}
	
	/**
	 * Returns a {@link BibleImporter} for the given file.
	 * <p>
	 * This method will read the file and return as soon as it can determine
	 * a valid {@link BibleImporter} for the data contained.
	 * @param stream the file stream
	 * @return {@link BibleImporter}
	 */
	private BibleImporter getImporterForFile(ByteArrayInputStream stream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			if (line.toUpperCase().startsWith("#THE UNBOUND BIBLE")) {
				return new UnboundBibleImporter();
			} else if (line.toUpperCase().startsWith("<?XML")) {
				// then its an xml document
				stream.reset();
				return getImporterForXml(stream);
			}
		} catch (IOException ex) {
			LOGGER.warn("An error occurred while trying to determine the format of the file.", ex);
		}
		return null;
	}
}
