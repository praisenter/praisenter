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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.InvalidFormatException;
import org.praisenter.NoContentException;

/**
 * A bible importer for the bible data files hosted on The Unbound Bible at www.unboundbible.org.
 * <p>
 * This class will attempt to read the .zip file supplied according to the format as of 9/13/12.
 * @author William Bittle
 * @version 3.0.0
 */
public final class UnboundBibleImporter extends AbstractBibleImporter implements BibleImporter {
	/** The class level-logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/**
	 * Minimal constructor.
	 * @param library the library to import into
	 */
	public UnboundBibleImporter(BibleLibrary library) {
		super(library);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.bible.BibleImporter#execute(java.nio.file.Path)
	 */
	@Override
	public List<Bible> execute(Path path) throws IOException, JAXBException, FileNotFoundException, InvalidFormatException {
		// get the file name
		String fileName = path.getFileName().toString();
		int d = fileName.lastIndexOf(".");
		String name = fileName.substring(0, d);
		
		// set the important file names
		final String bookFileName = "book_names.txt";
		final String verseFileName = name + "_utf8.txt";
		
		Bible bible = new Bible();
		bible.source = "THE UNBOUND BIBLE (www.unboundbible.org)";

		// make sure the file exists
		if (Files.exists(path)) {
			LOGGER.debug("Reading UnboundBible .zip file: " + path.toAbsolutePath().toString());
			
			// read the zip file for Books
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().equalsIgnoreCase(bookFileName)) {
						LOGGER.debug("Reading UnboundBible .zip file contents: " + bookFileName);
						readBooks(bible, bookFileName, zis);
						LOGGER.debug("UnboundBible .zip file contents read successfully: " + bookFileName);
					}
				}
			}
			
			// read the zip file for Verses
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().equalsIgnoreCase(verseFileName)) {
						LOGGER.debug("Reading UnboundBible .zip file contents: " + verseFileName);
						readVerses(bible, verseFileName, zis);
						LOGGER.debug("UnboundBible .zip file contents read successfully: " + verseFileName);
					}
				}
			}
			
			// check for missing files
			if (bible.books.size() == 0) {
				LOGGER.error("The file did not contain any books or verses. Import failed.");
				throw new NoContentException();
			}
			
			// add to the library
			this.library.save(bible);
			
			// return
			List<Bible> bibles = new ArrayList<Bible>();
			bibles.add(bible);
			return bibles;
		} else {
			// throw an exception
			throw new FileNotFoundException(path.toAbsolutePath().toString());
		}
	}
	
	/**
	 * Reads the books file.
	 * @param bible the bible to add to
	 * @param fileName the file name
	 * @param zis the ZipInputStream
	 * @throws InvalidFormatException if the data is in an unexpected format
	 * @throws IOException if an IO error occurs
	 */
	private void readBooks(Bible bible, String fileName, ZipInputStream zis) throws InvalidFormatException, IOException {
		// load up the book names
		BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
		// read them line by line
		String line = null;
		int order = 0;
		int i = 1;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				// ignore the line, its a comment
			} else {
				// split the line by tabs
				String[] data = line.split("\\t");
				if (data.length != 2) {
					LOGGER.error("Expected 2 columns of tab delimited data, but found " + data.length + " columns at line " + i + " of " + fileName);
					throw new InvalidFormatException(fileName + ":" + i);
				} else {
					Book book = new Book(
							// the code
							data[0].trim(),
							// the name
							data[1].trim().equalsIgnoreCase("Acts of the Apostles") ? "Acts" : data[1].trim(),
							// the order
							order++);
					bible.books.add(book);
				}
			}
			i++;
		}
	}
	
	/**
	 * Reads the verses and assigns some bible fields.
	 * @param bible the bible to add to
	 * @param fileName the file name
	 * @param zis the ZipInputStream
	 * @throws InvalidFormatException if the data is in an unexpected format
	 * @throws IOException if an IO error occurs
	 */
	private void readVerses(Bible bible, String fileName, ZipInputStream zis) throws InvalidFormatException, IOException {
		// load up the verses
		BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
		// read them line by line
		String line = null;
		int[] columnMapping = new int[6];
		Arrays.fill(columnMapping, -1);
		final int increment = 10;
		int order = increment;
		int lastSubVerse = -1;
		int i = 0;
		
//		String name = null;
//		String language = null;
//		String copyright = null;
//		boolean hadImportWarning = false;
//		boolean hasApocrypha = false;
		
		while ((line = reader.readLine()) != null) {
			i++;
			if (line.startsWith("#")) {
				// it's a comment, but some comments will provide data
				if (line.startsWith("#name")) {
					bible.name = line.replaceFirst("#name\\s+", "").trim();
				} else if (line.startsWith("#language")) {
					bible.language = line.replaceFirst("#language\\s+", "").trim().toUpperCase();
				} else if (line.startsWith("#copyright")) {
					bible.copyright = line.replaceFirst("#copyright\\s+", "").trim();
				} else if (line.startsWith("#columns")) {
					// not all bibles support the same fields so we need to setup a 
					// column mapping for the columns
					String[] columns = line.replaceFirst("#columns\\s+", "").split("\\t");
					int k = 0;
					for (String column : columns) {
						if ("orig_book_index".equals(column)) {
							columnMapping[0] = k;
						} else if ("orig_chapter".equals(column)) {
							columnMapping[1] = k;
						} else if ("orig_verse".equals(column)) {
							columnMapping[2] = k;
						} else if ("orig_subverse".equals(column)) {
							columnMapping[3] = k;
						} else if ("order_by".equals(column)) {
							columnMapping[4] = k;
						} else if ("text".equals(column)) {
							columnMapping[5] = k;
						}
						k++;
					}
				}
			} else {
				// split the line by tabs
				String[] data = line.split("\\t");
				// we need at least 4 columns to continue (book,chapter,verse,text)
				if (data.length < 4) {
					LOGGER.error("Expected at least 4 columns of tab delimited data, but found " + data.length + " columns at line " + i + " of " + fileName);
					throw new InvalidFormatException(fileName + ":" + i);
				} else {
					try {
						// dont bother checking the mapping on these since they are necessary
						String bookCode = data[columnMapping[0]].trim();
						int chapter = Integer.parseInt(data[columnMapping[1]].trim());
						int verse = Integer.parseInt(data[columnMapping[2]].trim());
						
						int subVerse = -1;
						int verseOrder = -1;
						String text = null;
						
						// the sub verse is rarely populated (and sometimes not present)
						if (columnMapping[3] != -1 && data[columnMapping[3]].trim().length() > 0) {
							String subVerseText = data[columnMapping[3]].trim();
							// some sub verses start with a . and have numbers (these are pre-verse sub verses)
							if (subVerseText.startsWith(".")) {
								subVerseText = subVerseText.replaceAll("\\.", "");
								// convert the subverse to an int
								try {
									// we will reverse order them
									subVerse = -Integer.parseInt(subVerseText);
								} catch (NumberFormatException e) {
									bible.hadImportWarning = true;
									LOGGER.warn("Unknown sub-verse format [{}|{}|{}|{}] on line {} in {}. Dropping verse.", bookCode, chapter, verse, data[columnMapping[3]].trim(), i, fileName);
									continue;
								}
							} else {
								// apparently the sub-verse field is a character field rather than a number
								if (lastSubVerse < 0) {
									lastSubVerse = 1;
								} else {
									lastSubVerse++;
								}
								subVerse = lastSubVerse;
							}
						} else {
							lastSubVerse = -1;
						}
						
						// order isn't always present
						if (columnMapping[4] != -1) {
							verseOrder = Integer.parseInt(data[columnMapping[4]].trim());
						} else {
							// if order isn't present then attempt to generate the ordering
							// NOTE this assumes that the verses are in the correct order in the file
							// and are read in the same order (which should be the case)
							verseOrder = order;
							order += increment;
						}
						
						// make sure the text is there
						if (data.length > columnMapping[5]) {
							text = data[columnMapping[5]].trim();
						} else {
							text = "";
							bible.hadImportWarning = true;
							// continue, but log a warning
							LOGGER.warn("Verse [{}|{}|{}|{}] is missing text on line {} in {}.", bookCode, chapter, verse, subVerse, i, fileName);
						}
						
						for (Book book : bible.books) {
							if (book.code.equals(bookCode)) {
								book.verses.add(new Verse(chapter, verse, subVerse, verseOrder, text));
								break;
							}
						}
					} catch (NumberFormatException e) {
						LOGGER.error("Failed to parse chapter, verse or order as integers at line " + i + " of " + fileName);
						throw new InvalidFormatException(fileName + ":" + i);
					}
				}
			}
		}
	}
}
