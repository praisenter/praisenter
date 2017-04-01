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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
			Map<String, Book> bookMap = null;
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().equalsIgnoreCase(bookFileName)) {
						LOGGER.debug("Reading UnboundBible .zip file contents: " + bookFileName);
						bookMap = readBooks(bible, bookFileName, zis);
						LOGGER.debug("UnboundBible .zip file contents read successfully: " + bookFileName);
					}
				}
			}

			// check for books
			if (bible.books.size() == 0 || bookMap == null) {
				LOGGER.error("The file did not contain any books. Import failed.");
				throw new NoContentException();
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
						readVerses(bible, bookMap, verseFileName, zis);
						LOGGER.debug("UnboundBible .zip file contents read successfully: " + verseFileName);
					}
				}
			}
			
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
	private Map<String, Book> readBooks(Bible bible, String fileName, ZipInputStream zis) throws InvalidFormatException, IOException {
		Map<String, Book> bookMap = new HashMap<String, Book>();
		// load up the book names
		BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
		// read them line by line
		String line = null;
		short order = 1;
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
							// the name
							data[1].trim().equalsIgnoreCase("Acts of the Apostles") ? "Acts" : data[1].trim(),
							// the order
							order++);
					bible.books.add(book);
					bookMap.put(data[0].trim(), book);
				}
			}
			i++;
		}
		return bookMap;
	}
	
	/**
	 * Reads the verses and assigns some bible fields.
	 * @param bible the bible to add to
	 * @param bookMap the mapping of bookcode to book
	 * @param fileName the file name
	 * @param zis the ZipInputStream
	 * @throws InvalidFormatException if the data is in an unexpected format
	 * @throws IOException if an IO error occurs
	 */
	private void readVerses(Bible bible, Map<String, Book> bookMap, String fileName, ZipInputStream zis) throws InvalidFormatException, IOException {
		// load up the verses
		BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
		// read them line by line
		String line = null;
		int[] columnMapping = new int[6];
		Arrays.fill(columnMapping, -1);
		int i = 0;
		
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
						String bc = data[columnMapping[0]].trim();
						short cn = Short.parseShort(data[columnMapping[1]].trim());
						
						// get the book
						Book book = bookMap.get(bc);
						// get the chapter
						Chapter chapter = book.getChapter(cn);
						// check for chapter not exists
						if (chapter == null) {
							chapter = new Chapter(cn);
							book.chapters.add(chapter);
						}
						
						short verse = Short.parseShort(data[columnMapping[2]].trim());
						
						String text = null;

						// sometimes there's "subverses" we'll just import these as
						// normal verses with duplicate verse numbers
						
						// make sure the text is there
						if (data.length > columnMapping[5]) {
							text = data[columnMapping[5]].trim();
						} else {
							text = "";
							bible.hadImportWarning = true;
							// continue, but log a warning
							LOGGER.warn("Verse [{}|{}|{}] is missing text on line {} in {}.", bc, cn, verse, i, fileName);
						}
						
						if (chapter != null) {
							chapter.verses.add(new Verse(verse, text));
						}
					} catch (NumberFormatException e) {
						LOGGER.error("Failed to parse chapter, verse or order as integers at line " + i + " of " + fileName);
						throw new InvalidFormatException(fileName + ":" + i);
					}
				}
			}
		}
		
		// sort since the chapters/verses could be out of order
		for (Book book : bible.books) {
			Collections.sort(book.chapters);
			for (Chapter chapter : book.chapters) {
				Collections.sort(chapter.verses);
			}
		}
	}
}
