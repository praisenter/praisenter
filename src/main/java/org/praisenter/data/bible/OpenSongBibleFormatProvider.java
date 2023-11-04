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
package org.praisenter.data.bible;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.ImportExportProvider;
import org.praisenter.data.InvalidImportExportFormatException;
import org.praisenter.data.PersistAdapter;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A bible importer for the OpenSong bible format.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
final class OpenSongBibleFormatProvider implements ImportExportProvider<Bible> {
	/** The class level-logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The source */
	private static final String SOURCE = "OpenSong (http://www.opensong.org/)";
	
	@Override
	public boolean isSupported(Path path) {
		return this.isSupported(MimeType.get(path));
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.XML.is(mimeType) || mimeType.toLowerCase().startsWith("text");
	}
	
	@Override
	public boolean isSupported(String name, InputStream stream) {
		return this.isSupported(MimeType.get(stream, name));
	}
	
	@Override
	public void exp(PersistAdapter<Bible> adapter, OutputStream stream, Bible data) throws IOException {
		throw new UnsupportedOperationException();
	}
	@Override
	public void exp(PersistAdapter<Bible> adapter, Path path, Bible data) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void exp(PersistAdapter<Bible> adapter, ZipOutputStream stream, Bible data) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DataImportResult<Bible> imp(PersistAdapter<Bible> adapter, Path path) throws IOException {
		DataImportResult<Bible> result = new DataImportResult<>();
		
		if (!this.isOpenSongBible(path)) {
			return result;
		}
		
		String name = path.getFileName().toString();
		int i = name.lastIndexOf('.');
		if (i >= 0) {
			name = name.substring(0, i);
		}
		
		List<DataReadResult<Bible>> results = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(path.toFile());
			BufferedInputStream bis = new BufferedInputStream(fis)) {
			results.add(this.parse(bis, name));
		} catch (SAXException | ParserConfigurationException ex) {
			throw new InvalidImportExportFormatException(ex);
		}
		
		for (DataReadResult<Bible> drr : results) {
			if (drr == null) continue;
			Bible bible = drr.getData();
			if (bible == null) continue;
			try {
				boolean isUpdate = adapter.upsert(bible);
				if (isUpdate) {
					result.getUpdated().add(bible);
				} else {
					result.getCreated().add(bible);
				}
				result.getWarnings().addAll(drr.getWarnings());
			} catch (Exception ex) {
				result.getErrors().add(ex);
			}
		}
		
		return result;
	}
	
	private boolean isOpenSongBible(Path path) {
		try (FileInputStream stream = new FileInputStream(path.toFile())) {
			XMLInputFactory f = XMLInputFactory.newInstance();
			// prevent XXE attacks
			// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLInputFactory_.28a_StAX_parser.29
			f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			XMLStreamReader r = f.createXMLStreamReader(stream);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().equalsIgnoreCase("bible")) {
			    		return true;
			    	}
			    }
			}
		} catch (Exception ex) {
			LOGGER.trace("Failed to read the path as an XML document.", ex);
		}
		return false;
	}
	
	/**
	 * Attempts to parse the given input stream into the internal bible format.
	 * @param stream the input stream
	 * @param name the name
	 * @throws IOException if an IO error occurs
	 * @throws InvalidImportExportFormatException if the stream was not in the expected format
	 * @return {@link Bible}
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private DataReadResult<Bible> parse(InputStream stream, String name) throws ParserConfigurationException, SAXException, IOException {
		byte[] content = Streams.read(stream);
		// read the bytes
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// prevent XXE attacks 
		// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser parser = factory.newSAXParser();
		OpenSongHandler handler = new OpenSongHandler(name);
		parser.parse(new ByteArrayInputStream(content), handler);
		return new DataReadResult<Bible>(handler.getBible(), handler.warnings);
	}
	
	/**
	 * A SAX parser handler for the OpenSong Bible format.
	 * @author William Bittle
	 * @version 3.0.0
	 * @since 3.0.0
	 */
	private final class OpenSongHandler extends DefaultHandler {
		// imported data
		
		/** The bible */
		private Bible bible;
		
		// temp data
		
		/** The current book */
		private Book book;
		
		/** The book number */
		private int bookNumber;
		
		/** The current chapter */
		private Chapter chapter;
		
		/** The chapter number */
		private int chapterNumber;
		
		/** The verse number */
		private int number;
		
		/** The verse range */
		private int verseTo;
		
		/** Buffer for tag contents */
		private StringBuilder dataBuilder;
		
		private List<String> warnings;
		
		/**
		 * Full constructor.
		 * @param name the bible name
		 */
		public OpenSongHandler(String name) {
			this.bible = new Bible();
			this.bible.setName(name);
			this.bible.setSource(SOURCE);
			this.bookNumber = 1;
			this.chapterNumber = 1;
			this.number = 1;
			this.warnings = new ArrayList<>();
		}
		
		/**
		 * Returns the bible.
		 * @return {@link Bible}
		 */
		public Bible getBible() {
			return this.bible;
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// inspect the tag name
			if (qName.equalsIgnoreCase("bible")) {
				// get the name if present
				String name = attributes.getValue("n");
				if (name != null) {
					this.bible.setName(name.trim());
				}
			} else if (qName.equalsIgnoreCase("b")) {
				book = new Book(bookNumber, attributes.getValue("n"));
				this.bible.getBooks().add(book);
				bookNumber++;
			} else if (qName.equalsIgnoreCase("c")) {
				String number = attributes.getValue("n");
				try {
					this.chapterNumber = Short.parseShort(number);
				} catch (NumberFormatException ex) {
					LOGGER.warn("Failed to parse chapter number '" + number + "' for '" + this.book.getName() + "' in '" + this.bible.getName() + "'. Using next chapter number in sequence instead.");
					this.chapterNumber++;
				}
				this.chapter = new Chapter(this.chapterNumber);
				this.book.getChapters().add(this.chapter);
				this.number = 0;
			} else if (qName.equalsIgnoreCase("v")) {
				this.verseTo = -1;
				String number = attributes.getValue("n");
				String to = attributes.getValue("t");
				try {
					this.number = Short.parseShort(number);
				} catch (NumberFormatException ex) {
					LOGGER.warn("Failed to parse verse number '" + number + "' for '" + this.book.getName() + "' chatper '"  + this.chapter.getNumber() + "' in '" + this.bible.getName() + "'. Using next verse number in sequence instead.");
					this.number++;
				}
				if (to != null) {
					try {
						this.verseTo = Short.parseShort(to);
					} catch (NumberFormatException ex) {
						LOGGER.warn("Failed to parse the to verse number '" + to + "' for '" + this.bible.getName() + "'. Skipping.");
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			// this method can be called a number of times for the contents of a tag
			// this is done to improve performance so we need to append the text before
			// using it
			String s = new String(ch, start, length);
			if (this.dataBuilder == null) {
				this.dataBuilder = new StringBuilder();
			}
			this.dataBuilder.append(s);
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("v".equalsIgnoreCase(qName)) {
				// check for embedded verses (n="1" t="4") ...why oh why...
				if (this.verseTo > 0 && this.verseTo > this.number) {
					String warning = "The bible '" + this.bible.getName() + "' included a verse that is a collection of verses with a range of " + this.number + " to " + this.verseTo + ". These were imported as separate verses, all with the same text.";
					this.warnings.add(warning);
					LOGGER.warn(warning);
					// just duplicate the verse content for each
					for (int i = this.number; i <= this.verseTo; i++) {
						this.chapter.getVerses().add(new Verse(i, this.dataBuilder.toString().trim()));
					}
				} else {
					// add as normal
					this.chapter.getVerses().add(new Verse(this.number, this.dataBuilder.toString().trim()));
				}
				
			}
			this.dataBuilder = null;
		}
	}
}
