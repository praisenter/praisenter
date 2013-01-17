/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.praisenter.UnrecognizedFormatException;
import org.praisenter.data.ConnectionFactory;
import org.praisenter.data.DataImportException;
import org.praisenter.resources.Messages;

/**
 * A bible importer for the bible data files hosted on The Unbound Bible at www.unboundbible.org.
 * <p>
 * This class will attempt to read the .zip file supplied according to the format as of 9/13/12.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class UnboundBibleImporter {
	/** The class level logger */
	private static final Logger LOGGER = Logger.getLogger(UnboundBibleImporter.class);
	
	/** The prepared statement SQL for inserting a bible */
	private static final String INSERT_BIBLE = "INSERT INTO BIBLES (DATA_SOURCE,NAME,LANGUAGE) VALUES(?, ?, ?)";
	
	/** The prepared statement SQL for inserting a book */
	private static final String INSERT_BOOK = "INSERT INTO BIBLE_BOOKS (BIBLE_ID,CODE,NAME) VALUES(?, ?, ?)";

	/** The prepared statement SQL for inserting a verse */
	private static final String INSERT_VERSE = "INSERT INTO BIBLE_VERSES (BIBLE_ID,BOOK_CODE,CHAPTER,VERSE,SUB_VERSE,ORDER_BY,TEXT) VALUES(?, ?, ?, ?, ?, ?, ?)";
	
	// index rebuilding
	
	/** SQL for dropping the BO_A index */
	private static final String DROP_INDEX_BO_A = "DROP INDEX BO_A";
	
	/** SQL for recreating the BO_A index */
	private static final String CREATE_INDEX_BO_A = "CREATE INDEX BO_A ON BIBLE_VERSES(BIBLE_ID,ORDER_BY)";
	
	/** SQL for dropping the BO_D index */
	private static final String DROP_INDEX_BO_D = "DROP INDEX BO_D";
	
	/** SQL for recreating the BO_D index */
	private static final String CREATE_INDEX_BO_D = "CREATE INDEX BO_D ON BIBLE_VERSES(BIBLE_ID,ORDER_BY DESC)";
	
	/** SQL for dropping the BBCV index */
	private static final String DROP_INDEX_BBCV = "DROP INDEX BBCV";
	
	/** SQL for recreating the BBCV index */
	private static final String CREATE_INDEX_BBCV = "CREATE INDEX BBCV ON BIBLE_VERSES(BIBLE_ID,BOOK_CODE,CHAPTER,VERSE)";
	
	/**
	 * Attempts to import the selected file into the bible database.
	 * @param file the file; should be a .zip from http://unbound.biola.edu/
	 * @throws DataImportException if any exception occurs during import
	 */
	public static final void importBible(File file) throws DataImportException {
		// check for null
		if (file == null) throw new DataImportException(new NullPointerException());
		// check for directory
		if (file.isDirectory()) throw new DataImportException();
		
		// get the file name
		String fileName = file.getName();
		int d = fileName.lastIndexOf(".");
		String name = fileName.substring(0, d);
		
		// set the important file names
		final String bookFileName = "book_names.txt";
		final String verseFileName = name + "_utf8.txt";
		
		Bible bible = new Bible();
		bible.source = "THE UNBOUND BIBLE (www.unboundbible.org)";
		
		List<Book> books = new ArrayList<Book>();
		List<Verse> verses = new ArrayList<Verse>();
		
		// make sure the file exists
		if (file.exists()) {
			LOGGER.info("Reading UnboundBible .zip file: " + file.getName());
			// read the zip file
			try (FileInputStream fis = new FileInputStream(file);
				 BufferedInputStream bis = new BufferedInputStream(fis);
				 ZipInputStream zis = new ZipInputStream(bis);) {
				// read the entries
				ZipEntry entry = null;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().equalsIgnoreCase(bookFileName)) {
						try {
							LOGGER.info("Reading UnboundBible .zip file contents: " + bookFileName);
							books = readBooks(zis);
						} catch (UnrecognizedFormatException e) {
							throw new DataImportException(MessageFormat.format(Messages.getString("bible.import.unrecognizedFormat"), bookFileName), e);
						} catch (NumberFormatException e) {
							throw new DataImportException(MessageFormat.format(Messages.getString("bible.import.numberFormat"), bookFileName), e);
						}
						LOGGER.info("UnboundBible .zip file contents read successfully: " + bookFileName);
					} else if (entry.getName().equalsIgnoreCase(verseFileName)) {
						try {
							LOGGER.info("Reading UnboundBible .zip file contents: " + verseFileName);
							verses = readVerses(bible, zis);
						} catch (UnrecognizedFormatException e) {
							throw new DataImportException(MessageFormat.format(Messages.getString("bible.import.unrecognizedFormat"), verseFileName), e);
						} catch (NumberFormatException e) {
							throw new DataImportException(MessageFormat.format(Messages.getString("bible.import.numberFormat"), verseFileName), e);
						}
						LOGGER.info("UnboundBible .zip file contents read successfully: " + verseFileName);
					}
				}
			} catch (ZipException e) {
				throw new DataImportException(Messages.getString("bible.import.zip"), e);
			} catch (FileNotFoundException e) {
				throw new DataImportException(Messages.getString("bible.import.fileNotFound"), e);
			} catch (IOException e) {
				throw new DataImportException(Messages.getString("bible.import.io"), e);
			}
			
			// check for missing files
			if (books.size() == 0 && verses.size() == 0) {
				LOGGER.error("The file did not contain any books or verses. Import failed.");
				throw new DataImportException(MessageFormat.format(Messages.getString("bible.import.unrecognizedFormat"), file.getName()));
			}
			
			LOGGER.info("Importing new bible: " + bible.name);
			// insert all the data into the tables
			try (Connection connection = ConnectionFactory.getBibleConnection()) {
				// begin the transaction
				connection.setAutoCommit(false);
				
				// verify the bible doesn't exist already
				try (Statement bibleQuery = connection.createStatement();
					 ResultSet bqResult = bibleQuery.executeQuery("SELECT COUNT(*) FROM BIBLES WHERE NAME = '" + bible.name + "'");) {
					// make sure we didn't get anything
					if (bqResult.next() && bqResult.getInt(1) > 0) {
						connection.rollback();
						throw new DataImportException(Messages.getString("bible.import.bibleExists"));
					}
				} catch (SQLException e) {
					connection.rollback();
					throw new DataImportException(Messages.getString("bible.import.error"), e);
				}
				
				// insert the bible
				try (PreparedStatement bibleInsert = connection.prepareStatement(INSERT_BIBLE, Statement.RETURN_GENERATED_KEYS)) {
					// set the parameters
					bibleInsert.setString(1, bible.source);
					bibleInsert.setString(2, bible.name);
					bibleInsert.setString(3, bible.language);
					// insert the bible
					int n = bibleInsert.executeUpdate();
					// get the generated id
					if (n > 0) {
						ResultSet result = bibleInsert.getGeneratedKeys();
						if (result.next()) {
							// get the song id
							int id = result.getInt(1);
							bible.id = id;
						}
					} else {
						// throw an error
						connection.rollback();
						throw new DataImportException(Messages.getString("bible.import.error"));
					}
				} catch (SQLException e) {
					connection.rollback();
					throw new DataImportException(Messages.getString("bible.import.error"), e);
				}
				
				// make sure the bible was saved first
				if (bible.id > 0) {
					LOGGER.info("Bible inserted successfully: " + bible.name);
					// insert the books
					try (PreparedStatement bookInsert = connection.prepareStatement(INSERT_BOOK)) {
						for (Book book : books) {
							// set the parameters
							bookInsert.setInt(1, bible.id);
							bookInsert.setString(2, book.code);
							bookInsert.setString(3, book.name);
							// execute the insert
							int n = bookInsert.executeUpdate();
							// make sure it worked
							if (n <= 0) {
								// roll back anything we've done
								connection.rollback();
								// throw an error
								throw new DataImportException(MessageFormat.format(Messages.getString("bible.import.book.error"), book.name));
							}
						}
					} catch (SQLException e) {
						connection.rollback();
						throw new DataImportException(Messages.getString("bible.import.error"), e);
					}
					LOGGER.info("Bible books inserted successfully: " + bible.name);
					
					// insert the verses
					try (PreparedStatement verseInsert = connection.prepareStatement(INSERT_VERSE)) {
						for (Verse verse : verses) {
							// set the parameters
							verseInsert.setInt(1, bible.id);
							verseInsert.setString(2, verse.book.code);
							verseInsert.setInt(3, verse.chapter);
							verseInsert.setInt(4, verse.verse);
							verseInsert.setInt(5, verse.subVerse);
							verseInsert.setInt(6, verse.order);
							verseInsert.setClob(7, new StringReader(verse.text));
							// execute the insert
							int n = verseInsert.executeUpdate();
							// make sure it worked
							if (n <= 0) {
								// roll back anything we've done
								connection.rollback();
								// throw an error
								throw new DataImportException(MessageFormat.format(Messages.getString("bible.import.verse.error"), verse.book, verse.chapter, verse.verse));
							}
						}
					} catch (SQLException e) {
						connection.rollback();
						throw new DataImportException(Messages.getString("bible.import.error"), e);
					}
					LOGGER.info("Bible verses inserted successfully: " + bible.name);
				}
				
				// commit all the changes
				connection.commit();
				
				LOGGER.info("Bible imported successfully: " + bible.name);
			} catch (SQLException e) {
				throw new DataImportException(Messages.getString("bible.import.error"), e);
			}
			
			// rebuild the indexes after a bible has been imported
			rebuildIndexes();
		}
	}
	
	/**
	 * Rebuilds the indexes for the verses tables.
	 * <p>
	 * This is useful after an import of a new bible.
	 */
	private static final void rebuildIndexes() {
		LOGGER.info("Rebuilding bible indexes.");
		try (Connection connection = ConnectionFactory.getBibleConnection()) {
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement();) {
				statement.execute(DROP_INDEX_BO_A);
				statement.execute(CREATE_INDEX_BO_A);
				
				statement.execute(DROP_INDEX_BO_D);
				statement.execute(CREATE_INDEX_BO_D);
				
				statement.execute(DROP_INDEX_BBCV);
				statement.execute(CREATE_INDEX_BBCV);
				
				connection.commit();
				
				LOGGER.info("Bible indexes rebuilt successfully.");
			} catch (SQLException e) {
				// roll back any index changes we have done thus far
				connection.rollback();
				// just log this error
				LOGGER.error("An error occurred when rebuilding the indexes after a successful import of a bible:", e);
			}
		} catch (SQLException e) {
			// just log this error
			LOGGER.error("An error occurred when rebuilding the indexes after a successful import of a bible:", e);
		}
	}
	
	/**
	 * Reads the books file.
	 * @param zis the ZipInputStream
	 * @return List&lt;Book&gt;
	 * @throws UnrecognizedFormatException if the data is in an unexpected format
	 * @throws IOException if an IO error occurs
	 */
	private static final List<Book> readBooks(ZipInputStream zis) throws UnrecognizedFormatException, IOException {
		List<Book> books = new ArrayList<Book>();
		// load up the book names
		BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
		// read them line by line
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				// ignore the line, its a comment
			} else {
				// split the line by tabs
				String[] data = line.split("\\t");
				if (data.length != 2) {
					throw new UnrecognizedFormatException();
				} else {
					Book book = new Book();
					book.code = data[0];
					book.name = data[1];
					// the unbound bible sources use a long name for Acts
					if (book.name.equalsIgnoreCase("Acts of the Apostles")) {
						book.name = "Acts";
					}
					books.add(book);
				}
			}
		}
		
		return books;
	}
	
	/**
	 * Reads the verses and assigns some bible fields.
	 * @param bible the bible object
	 * @param zis the ZipInputStream
	 * @return List&tl;Verse&gt;
	 * @throws UnrecognizedFormatException if the data is in an unexpected format
	 * @throws NumberFormatException if a field failed to be parsed as an integer
	 * @throws IOException if an IO error occurs
	 */
	private static final List<Verse> readVerses(Bible bible, ZipInputStream zis) throws UnrecognizedFormatException, NumberFormatException, IOException {
		List<Verse> verses = new ArrayList<Verse>();
		// load up the verses
		BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8));
		// read them line by line
		String line = null;
		int[] columnMapping = new int[6];
		Arrays.fill(columnMapping, -1);
		final int increment = 10;
		int order = increment;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("#")) {
				// it's a comment, but some comments will provide data
				if (line.startsWith("#name")) {
					bible.name = line.replaceFirst("#name\\s+", "");
				} else if (line.startsWith("#language")) {
					bible.language = line.replaceFirst("#language\\s+", "");
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
					throw new UnrecognizedFormatException();
				} else {
					Verse verse = new Verse();
					verse.book = new Book();
					// dont bother checking the mapping on these since they are necessary
					verse.book.code = data[columnMapping[0]].trim();
					verse.chapter = Integer.parseInt(data[columnMapping[1]].trim());
					verse.verse = Integer.parseInt(data[columnMapping[2]].trim());
					// the sub verse is rarely populated (and sometimes not present)
					if (columnMapping[3] != -1 && data[columnMapping[3]].trim().length() > 0) {
						verse.subVerse = Integer.parseInt(data[columnMapping[3]].trim());
					}
					// order isn't always present
					if (columnMapping[4] != -1) {
						verse.order = Integer.parseInt(data[columnMapping[4]].trim());
					} else {
						// if order isn't present then attempt to generate the ordering
						// NOTE this assumes that the verses are in the correct order in the file
						// and are read in the same order (which should be the case)
						verse.order = order;
						order += increment;
					}
					verse.text = data[columnMapping[5]].trim();
					verses.add(verse);
				}
			}
		}
		
		return verses;
	}
}
