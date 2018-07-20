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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataFormatProvider;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.InvalidFormatException;
import org.praisenter.utility.MimeType;
import org.praisenter.utility.Streams;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A bible importer for the Zefania XML Bible format.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 * @see <a href="https://sourceforge.net/projects/zefania-sharp/?source=typ_redirect">Source Forge Project</a>
 * @see <a href="http://www.bgfdb.de/zefaniaxml/bml/">Format Documentation</a>
 */
final class ZefaniaBibleFormatProvider implements DataFormatProvider<Bible> {
	/** The class level-logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The source */
	private static final String SOURCE = "Zefania XML Bible (https://sourceforge.net/projects/zefania-sharp/files/Bibles/)";
	
	@Override
	public boolean isSupported(Path path) {
		try (Reader reader = new BufferedReader(new FileReader(path.toFile()))) {
			XMLInputFactory f = XMLInputFactory.newInstance();
			// prevent XXE attacks
			// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLInputFactory_.28a_StAX_parser.29
			f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			XMLStreamReader r = f.createXMLStreamReader(reader);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().equalsIgnoreCase("xmlbible") ||
			    		r.getLocalName().equalsIgnoreCase("x")) {
			    		return true;
			    	}
			    }
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed to read the path as an XML document.", ex);
		}
		return false;
	}

	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.XML.is(mimeType) || mimeType.toLowerCase().startsWith("text");
	}
	
	@Override
	public boolean isSupported(String resourceName, InputStream stream) {
		try {
			XMLInputFactory f = XMLInputFactory.newInstance();
			// prevent XXE attacks
			// https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#XMLInputFactory_.28a_StAX_parser.29
			f.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			f.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			XMLStreamReader r = f.createXMLStreamReader(stream);
			while(r.hasNext()) {
			    r.next();
			    if (r.isStartElement()) {
			    	if (r.getLocalName().equalsIgnoreCase("xmlbible") ||
			    		r.getLocalName().equalsIgnoreCase("x")) {
			    		return true;
			    	}
			    }
			}
		} catch (Exception ex) {
			LOGGER.warn("Failed to read the input stream as an XML document.", ex);
		}
		return false;
	}
	
	@Override
	public List<DataReadResult<Bible>> read(Path path) throws IOException {
		try (FileInputStream stream = new FileInputStream(path.toFile())) {
			return this.read(path.getFileName().toString(), stream);
		}
	}
	
	@Override
	public List<DataReadResult<Bible>> read(String resourceName, InputStream stream) throws IOException {
		String name = resourceName;
		int i = resourceName.lastIndexOf('.');
		if (i >= 0) {
			name = resourceName.substring(0, i);
		}
		
		List<DataReadResult<Bible>> results = new ArrayList<>();
		try {
			results.add(this.parse(stream, name));
		} catch (SAXException | ParserConfigurationException ex) {
			throw new InvalidFormatException(ex);
		}
		return results;
	}
	
	@Override
	public void write(OutputStream stream, Bible bible) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void write(Path path, Bible bible) throws IOException {
		throw new UnsupportedOperationException();
	}
	
//	/* (non-Javadoc)
//	 * @see org.praisenter.bible.BibleImporter#execute(java.lang.String, java.io.InputStream)
//	 */
//	@Override
//	public List<Bible> execute(String fileName, InputStream stream) throws IOException {
//		List<Bible> bibles = new ArrayList<Bible>();
//		String name = fileName;
//		int i = name.lastIndexOf('.');
//		if (i >= 0) {
//			name = name.substring(0, i);
//		}
//		try {
//			Bible bible = this.parse(stream, name);
//			bibles.add(bible);
//		} catch (ParserConfigurationException | SAXException ex) {
//			throw new InvalidFormatException("Failed to import file '" + fileName + "' as a Zefania bible file.", ex);
//		}
//		return bibles;
//	}
//	
	/**
	 * Attempts to parse the given input stream into the internal bible format.
	 * @param stream the input stream
	 * @param name the name
	 * @return {@link Bible}
	 * @throws IOException if an IO error occurs
	 * @throws ParserConfigurationException
	 * @throws SAXException 
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
		ZefaniaHandler handler = new ZefaniaHandler(name);
		parser.parse(new ByteArrayInputStream(content), handler);
		DataReadResult<Bible> result = new DataReadResult<Bible>(handler.getBible(), handler.warnings);
		return result;
	}
	
	/**
	 * A SAX parser for the Zefania XML Bible format.
	 * @author William Bittle
	 * @version 3.0.0
	 * @since 3.0.0
	 */
	private final class ZefaniaHandler extends DefaultHandler {

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
		private int verse;
		
		/** Buffer for tag contents */
		private StringBuilder dataBuilder;
		
		/** True if we are reading the verse content */
		private boolean verseContent;
		
		/** True if we need to ignore the current content */
		private boolean ignoreContent;
		
		/** The list of warnings */
		private List<String> warnings;
		
		/**
		 * Minimal constructor.
		 * @param name the bible name
		 */
		public ZefaniaHandler(String name) {
			this.bible = new Bible();
			this.bible.setName(name);
			this.bible.setSource(SOURCE);
			this.bookNumber = 1;
			this.chapterNumber = 1;
			this.verse = 1;
			this.warnings = new ArrayList<>();
		}
		
		/**
		 * Returns the parsed bible.
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
			if (qName.equalsIgnoreCase("xmlbible") ||
				qName.equalsIgnoreCase("x")) {
				// get the name if present
				String name = attributes.getValue("biblename");
				if (name != null) {
					this.bible.setName(name.trim());
				}
			} else if (qName.equalsIgnoreCase("biblebook") ||
					   qName.equalsIgnoreCase("b")) {
				String bnumber = attributes.getValue("bnumber");
				String bname = attributes.getValue("bname");
				try {
					this.bookNumber = Short.parseShort(bnumber);
				} catch (NumberFormatException ex) {
					String warning = "Failed to parse book number '" + bnumber + "' for '" + bname + "' in '" + this.bible.getName() + "'. Using next book number in sequence instead.";
					warnings.add(warning);
					LOGGER.warn(warning);
					this.bookNumber++;
				}
				this.book = new Book(bookNumber, bname);
				this.bible.getBooks().add(book);
				this.chapterNumber = 0;
			} else if (qName.equalsIgnoreCase("chapter") ||
					   qName.equalsIgnoreCase("c")) {
				String cnumber = attributes.getValue("cnumber");
				try {
					this.chapterNumber = Short.parseShort(cnumber);
				} catch (NumberFormatException ex) {
					String warning = "Failed to parse chapter number '" + cnumber + "' for '" + this.book.getName() + "' in '" + this.bible.getName() + "'. Using next chapter number in sequence instead.";
					warnings.add(warning);
					LOGGER.warn(warning);
					this.chapterNumber++;
				}
				this.chapter = new Chapter(this.chapterNumber);
				this.book.getChapters().add(this.chapter);
				this.verse = 0;
			} else if (qName.equalsIgnoreCase("vers") ||
					   qName.equalsIgnoreCase("v")) {
				String v = attributes.getValue("v");
				String vnumber = v == null || v.length() == 0 ? attributes.getValue("vnumber") : v;
				try {
					this.verse = Short.parseShort(vnumber);
				} catch (NumberFormatException ex) {
					String warning = "Failed to parse verse number '" + vnumber + "' for '" + this.book.getName() + "' chapter '" + this.chapter.getNumber() + "' in '" + this.bible.getName() + "'. Using next verse number in sequence instead.";
					warnings.add(warning);
					LOGGER.warn(warning);
					this.verse++;
				}
				this.verseContent = true;
			} else if (qName.equalsIgnoreCase("note") ||
					   qName.equalsIgnoreCase("n") ||
					   qName.equalsIgnoreCase("xref") ||
					   qName.equalsIgnoreCase("xr")) {
				this.ignoreContent = true;
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
			if (!this.ignoreContent) {
				this.dataBuilder.append(s);
			}
		}
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
		 */
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equalsIgnoreCase("vers") ||
				qName.equalsIgnoreCase("v")) {
				this.chapter.getVerses().add(new Verse(this.verse, this.dataBuilder.toString().replaceAll("\\n|\\r", "").replaceAll("\\s+", " ").trim()));
				this.verseContent = false;
			} else if (qName.equalsIgnoreCase("title")) {
				String name = this.dataBuilder.toString();
				if (name != null) {
					this.bible.setName(name.trim());
				}
			} else if (qName.equalsIgnoreCase("language")) {
				String language = this.dataBuilder.toString();
				if (language != null) {
					this.bible.setLanguage(language.trim());
				}
			} else if (qName.equalsIgnoreCase("rights")) {
				this.bible.setCopyright(this.dataBuilder.toString().trim());
			} else if (qName.equalsIgnoreCase("note") ||
					   qName.equalsIgnoreCase("n") ||
					   qName.equalsIgnoreCase("xref") ||
					   qName.equalsIgnoreCase("xr")) {
				this.ignoreContent = false;
			}
			if (!this.verseContent) {
				this.dataBuilder = null;
			}
		}
	}
}
