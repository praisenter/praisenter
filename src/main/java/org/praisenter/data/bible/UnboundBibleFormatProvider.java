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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.DataFormatProvider;
import org.praisenter.data.DataReadResult;
import org.praisenter.data.InvalidFormatException;
import org.praisenter.utility.MimeType;

import javafx.collections.FXCollections;

/**
 * A bible importer for the bible data files hosted on The Unbound Bible at www.unboundbible.org.
 * <p>
 * This class will attempt to read the .zip file supplied according to the format as of 9/13/12.
 * @author William Bittle
 * @version 3.0.0
 */
final class UnboundBibleFormatProvider implements DataFormatProvider<Bible> {
	/** The class level-logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public boolean isSupported(Path path) {
		if (MimeType.ZIP.check(path)) {
			try (FileInputStream fis = new FileInputStream(path.toFile());
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().equalsIgnoreCase("book_names.txt")) {
						return true;
					}
				}
			} catch (Exception ex) {
				LOGGER.warn("Failed to determine if '" + path.toAbsolutePath() + "' was a unbound bible zip format.", ex);
			}
		}
		return false;
	}
	
	@Override
	public boolean isSupported(String mimeType) {
		return MimeType.ZIP.is(mimeType);
	}
	
	@Override
	public boolean isSupported(String resourceName, InputStream stream) {
		// zips in zips not supported
		return false;
	}
	
	@Override
	public List<DataReadResult<Bible>> read(Path path) throws IOException {
		List<String> warnings = null;
		String resourceName = path.getFileName().toString();
		int d = resourceName.lastIndexOf(".");
		String name = resourceName.substring(0, d);
		
		// set the important file names
		final String bookFileName = "book_names.txt";
		final String verseFileName = name + "_utf8.txt";
		
		Bible bible = new Bible();
		bible.setSource("THE UNBOUND BIBLE (www.unboundbible.org)");

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path.toFile()));
		bis.mark(Integer.MAX_VALUE);
		
		// find the book first
		Map<String, Book> bookMap = null;
		ZipInputStream zis = new ZipInputStream(bis);
		ZipEntry entry = null;
		while ((entry = zis.getNextEntry()) != null) {
			if (entry.getName().equalsIgnoreCase(bookFileName)) {
				LOGGER.debug("Reading UnboundBible .zip file contents: " + bookFileName);
				bookMap = readBooks(bible, bookFileName, zis);
				LOGGER.debug("UnboundBible .zip file contents read successfully: " + bookFileName);
				break;
			}
		}
		
		// check for books
		if (bible.getBookCount() == 0 || bookMap == null) {
			LOGGER.error("The file did not contain any books. Import failed.");
			throw new InvalidFormatException("A book_names.txt file was not found '" + resourceName + "'.");
		}
		
		bis.reset();
		// read the zip file for Verses
		zis = new ZipInputStream(bis);
		entry = null;
		while ((entry = zis.getNextEntry()) != null) {
			if (entry.getName().equalsIgnoreCase(verseFileName) || entry.getName().toLowerCase().endsWith("_utf8.txt")) {
				LOGGER.debug("Reading UnboundBible .zip file contents: " + verseFileName);
				warnings = readVerses(bible, bookMap, verseFileName, zis);
				LOGGER.debug("UnboundBible .zip file contents read successfully: " + verseFileName);
			}
		}
		
		List<DataReadResult<Bible>> results = new ArrayList<>();
		results.add(new DataReadResult<Bible>(bible, warnings));
		return results;
	}
	
	@Override
	public List<DataReadResult<Bible>> read(String resourceName, InputStream stream) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void write(OutputStream stream, Bible bible) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void write(Path path, Bible bible) throws IOException {
		throw new UnsupportedOperationException();
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
		int order = 1;
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
					Book book = new Book();
					book.setName(data[1].trim().equalsIgnoreCase("Acts of the Apostles") ? "Acts" : data[1].trim());
					book.setNumber(order++);
					bible.getBooks().add(book);
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
	private List<String> readVerses(Bible bible, Map<String, Book> bookMap, String fileName, ZipInputStream zis) throws InvalidFormatException, IOException {
		List<String> warnings = new ArrayList<>();
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
					bible.setName(line.replaceFirst("#name\\s+", "").trim());
				} else if (line.startsWith("#language")) {
					bible.setLanguage(line.replaceFirst("#language\\s+", "").trim().toUpperCase());
				} else if (line.startsWith("#copyright")) {
					bible.setCopyright(line.replaceFirst("#copyright\\s+", "").trim());
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
						int cn = Integer.parseInt(data[columnMapping[1]].trim());
						
						// get the book
						Book book = bookMap.get(bc);
						// get the chapter
						Chapter chapter = book.getChapter(cn);
						// check for chapter not exists
						if (chapter == null) {
							chapter = new Chapter(cn);
							book.getChapters().add(chapter);
						}
						
						int verse = Integer.parseInt(data[columnMapping[2]].trim());
						
						String text = null;

						// sometimes there's "subverses" we'll just import these as
						// normal verses with duplicate verse numbers
						
						// make sure the text is there
						if (data.length > columnMapping[5]) {
							text = data[columnMapping[5]].trim();
						} else {
							// continue, but log a warning
							text = "";
							String warning = "Verse [" + bc + "|" + cn + "|" + verse + "] is missing text on line " + i + " in " + fileName + ".";
							warnings.add(warning);
							LOGGER.warn(warning);
						}
						
						if (chapter != null) {
							chapter.getVerses().add(new Verse(verse, text));
						}
					} catch (NumberFormatException e) {
						LOGGER.error("Failed to parse chapter, verse or order as integers at line " + i + " of " + fileName);
						throw new InvalidFormatException(fileName + ":" + i);
					}
				}
			}
		}
		
		// sort since the chapters/verses could be out of order
		for (Book book : bible.getBooks()) {
			FXCollections.sort(book.getChapters());
			for (Chapter chapter : book.getChapters()) {
				FXCollections.sort(chapter.getVerses());
			}
		}
		
		return warnings;
	}
}
