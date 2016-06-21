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
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.InvalidFormatException;
import org.praisenter.NoContentException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// https://sourceforge.net/projects/zefania-sharp/?source=typ_redirect
// http://www.bgfdb.de/zefaniaxml/bml/
public final class ZefaniaBibleImporter extends AbstractBibleImporter implements BibleImporter {
	/** The class level-logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final String SOURCE = "https://sourceforge.net/projects/zefania-sharp/files/Bibles/";
	
	private static final int O_BOOK_COUNT = 39;
	private static final int N_BOOK_COUNT = O_BOOK_COUNT + 27;
	private static final int A_BOOK_COUNT = N_BOOK_COUNT + 20;
	
	private Bible bible;
	
	private List<Book> books;
	
	/** The list of verses */
	private List<Verse> verses;

	// temp
	
	private String name;
	private String language;
	private String copyright;
	
	private boolean hasApocrypha;
	
	private boolean hadImportWarning;
	
	private int bookNumber;
	private String bookCode;
	
	private int chapter;
	private int verse;
	
	private int order;
	
	/** Buffer for tag contents */
	private StringBuilder dataBuilder;
	
	private boolean verseContent;
	private boolean ignoreContent;
	
	/**
	 * Minimal constructor.
	 * @param library the library to import into
	 */
	public ZefaniaBibleImporter(BibleLibrary library) {
		super(library);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.bible.BibleImporter#execute(java.nio.file.Path)
	 */
	@Override
	public List<Bible> execute(Path path) throws IOException, SQLException, FileNotFoundException, BibleAlreadyExistsException, InvalidFormatException {
		final String name = path.getFileName().toString().toLowerCase();
		
		this.reset();
		
		List<Bible> bibles = new ArrayList<Bible>();
		
		// make sure the file exists
		if (Files.exists(path)) {
			LOGGER.debug("Reading OpenSong Bible file: " + path.toAbsolutePath().toString());

			// first try to open it as a zip
			try (FileInputStream fis = new FileInputStream(path.toFile());
					 BufferedInputStream bis = new BufferedInputStream(fis);
					 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries (each should be a .xmm file)
				ZipEntry entry = null;
				boolean read = false;
				while ((entry = zis.getNextEntry()) != null) {
					read = true;
					LOGGER.debug("Reading as zip file: " + path.toAbsolutePath().toString());
					this.name = entry.getName().substring(0, entry.getName().lastIndexOf('.'));
					this.parse(zis);
					Bible bbl = this.insert(entry.getName());
					if (bbl != null) {
						bibles.add(bbl);
					}
					this.reset();
				}
				// check if we read an entry
				// if not, that may mean the file was not a zip so try it as a normal flat file
				if (!read) {
					LOGGER.debug("Reading as XML file: " + path.toAbsolutePath().toString());
					if (!name.endsWith(".xml")) {
						LOGGER.warn("File " + path.toAbsolutePath().toString() + " does not end with either .zip or .xml file extensions.  Attempting to read anyway.");
					}
					// this indicates the file is not a zip or invalid
					this.name = name.substring(0, name.lastIndexOf('.'));
					// hopefully its an .xmm
					// just read it
					this.parse(new FileInputStream(path.toFile()));
					Bible bbl = this.insert(path.toAbsolutePath().toString());
					if (bbl != null) {
						bibles.add(bbl);
					}
				}
			}

			return bibles;
		} else {
			// throw an exception
			throw new FileNotFoundException(path.toAbsolutePath().toString());
		}
	}
	
	private void reset() {
		this.bible = null;
		this.books = new ArrayList<Book>();
		this.verses = new ArrayList<Verse>();
		
		this.name = null;
		this.language = "N/A";
		this.copyright = null;
		this.hasApocrypha = false;
		this.hadImportWarning = false;
		this.bookNumber = 0;
		this.bookCode = null;
		this.chapter = 0;
		this.verse = 0;
		this.order = 0;
		this.dataBuilder = null;
		this.verseContent = false;
		this.ignoreContent = false;
	}
	
	private void parse(InputStream stream) throws IOException, InvalidFormatException {
		try {
			byte[] content = read(stream);
			// read the bytes
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();			
			parser.parse(new ByteArrayInputStream(content), new SaxParser());
		} catch (SAXException | ParserConfigurationException e) {
			throw new InvalidFormatException(e);
		}
	}
	
	private Bible insert(String name) throws SQLException, BibleAlreadyExistsException {
		// check for missing files
		if (books.size() == 0 && verses.size() == 0) {
			LOGGER.error("The file '" + name + "' did not contain any books or verses. Import failed.");
			return null;
		}
		
		this.bible = new Bible(-1, this.name, this.language, SOURCE, null, this.copyright, this.verses.size(), this.hasApocrypha, this.hadImportWarning);
		
		// import into the database
		return this.insert(bible, books, verses);
	}
	
	private String getBookCode(int number) {
		if (number <= O_BOOK_COUNT) {
			return (number < 10 ? "0" : "") + number + "O";
		} else if (number > O_BOOK_COUNT && number <= N_BOOK_COUNT) {
			return number + "N";
		} else {
			this.hasApocrypha = true;
			return number + "A";
		}
	}
	
	private class SaxParser extends DefaultHandler {
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// inspect the tag name
			if (qName.equalsIgnoreCase("xmlbible") ||
				qName.equalsIgnoreCase("x")) {
				// get the name if present
				String name = attributes.getValue("biblename");
				if (name != null) {
					ZefaniaBibleImporter.this.name = name.trim();
				}
			} else if (qName.equalsIgnoreCase("biblebook") ||
					   qName.equalsIgnoreCase("b")) {
				String bnumber = attributes.getValue("bnumber");
				try {
					bookNumber = Integer.parseInt(bnumber);
				} catch (NumberFormatException ex) {
					LOGGER.warn("Failed to parse book number '" + bnumber + "' for '" + name + "'. Using next book number in sequence instead.");
					bookNumber++;
				}
				bookCode = getBookCode(bookNumber);
				books.add(new Book(null, bookCode, attributes.getValue("bname")));
				chapter = 0;
			} else if (qName.equalsIgnoreCase("chapter") ||
					   qName.equalsIgnoreCase("c")) {
				String cnumber = attributes.getValue("cnumber");
				try {
					chapter = Integer.parseInt(cnumber);
				} catch (NumberFormatException ex) {
					LOGGER.warn("Failed to parse chapter number '" + cnumber + "' for '" + name + "'. Using next chapter number in sequence instead.");
					chapter++;
				}
				verse = 0;
			} else if (qName.equalsIgnoreCase("vers") ||
					   qName.equalsIgnoreCase("v")) {
				String v = attributes.getValue("v");
				String vnumber = v == null || v.length() == 0 ? attributes.getValue("vnumber") : v;
				try {
					verse = Integer.parseInt(vnumber);
				} catch (NumberFormatException ex) {
					LOGGER.warn("Failed to parse verse number '" + vnumber + "' for '" + name + "'. Using next verse number in sequence instead.");
					verse++;
				}
				order += 10;
				verseContent = true;
			} else if (qName.equalsIgnoreCase("note") ||
					   qName.equalsIgnoreCase("n") ||
					   qName.equalsIgnoreCase("gram") ||
					   qName.equalsIgnoreCase("gr") ||
					   qName.equalsIgnoreCase("g") ||
					   qName.equalsIgnoreCase("xref") ||
					   qName.equalsIgnoreCase("xr")) {
				ignoreContent = true;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			// this method can be called a number of times for the contents of a tag
			// this is done to improve performance so we need to append the text before
			// using it
			String s = new String(ch, start, length);
			if (dataBuilder == null) {
				dataBuilder = new StringBuilder();
			}
			if (!ignoreContent) {
				dataBuilder.append(s);
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equalsIgnoreCase("vers") ||
				qName.equalsIgnoreCase("v")) {
				verses.add(new Verse(null, new Book(null, bookCode, null), -1, chapter, verse, -1, order, dataBuilder.toString().trim()));
				verseContent = false;
			} else if (qName.equalsIgnoreCase("title")) {
				String name = dataBuilder.toString();
				if (name != null) {
					ZefaniaBibleImporter.this.name = name.trim();
				}
			} else if (qName.equalsIgnoreCase("language")) {
				String language = dataBuilder.toString();
				if (language != null) {
					ZefaniaBibleImporter.this.language = language.trim();
				}
			} else if (qName.equalsIgnoreCase("rights")) {
				copyright = dataBuilder.toString().trim();
			} else if (qName.equalsIgnoreCase("note") ||
					   qName.equalsIgnoreCase("n") ||
					   qName.equalsIgnoreCase("gram") ||
					   qName.equalsIgnoreCase("gr") ||
					   qName.equalsIgnoreCase("g") ||
					   qName.equalsIgnoreCase("xref") ||
					   qName.equalsIgnoreCase("xr")) {
				ignoreContent = false;
			}
			if (!verseContent) {
				dataBuilder = null;
			}
		}
	}
}
