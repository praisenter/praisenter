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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.data.Database;

// FEATURE add support for http://www.opensong.org/home/download bible formats
// FEATURE add support for Zefania XML Bibles

/**
 * Abstract class for importing bibles into the bible library.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class AbstractBibleImporter implements BibleImporter {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The prepared statement SQL for inserting a bible */
	private static final String INSERT_BIBLE = "INSERT INTO BIBLE (DATA_SOURCE,NAME,LANGUAGE,IMPORT_DATE,COPYRIGHT,VERSE_COUNT,HAS_APOCRYPHA,HAD_IMPORT_WARNING) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	
	/** The prepared statement SQL for inserting a book */
	private static final String INSERT_BOOK = "INSERT INTO BIBLE_BOOK (BIBLE_ID,CODE,NAME) VALUES(?, ?, ?)";

	/** The prepared statement SQL for inserting a verse */
	private static final String INSERT_VERSE = "INSERT INTO BIBLE_VERSE (BIBLE_ID,BOOK_CODE,CHAPTER,VERSE,SUB_VERSE,ORDER_BY,TEXT) VALUES(?, ?, ?, ?, ?, ?, ?)";
	
	/** The prepared statement SQL for setting the import warning for a bible */
	private static final String UPDATE_IMPORT_WARNING = "UPDATE BIBLE SET HAD_IMPORT_WARNING = ? WHERE ID = ?";
	
	// index rebuilding
	
	/** SQL for dropping the BO_A index */
	private static final String DROP_INDEX_BO_A = "DROP INDEX BO_A";
	
	/** SQL for recreating the BO_A index */
	private static final String CREATE_INDEX_BO_A = "CREATE INDEX BO_A ON BIBLE_VERSE(BIBLE_ID,ORDER_BY)";
	
	/** SQL for dropping the BO_D index */
	private static final String DROP_INDEX_BO_D = "DROP INDEX BO_D";
	
	/** SQL for recreating the BO_D index */
	private static final String CREATE_INDEX_BO_D = "CREATE INDEX BO_D ON BIBLE_VERSE(BIBLE_ID,ORDER_BY DESC)";
	
	/** SQL for dropping the BBCV index */
	private static final String DROP_INDEX_BBCV = "DROP INDEX BBCV";
	
	/** SQL for recreating the BBCV index */
	private static final String CREATE_INDEX_BBCV = "CREATE INDEX BBCV ON BIBLE_VERSE(BIBLE_ID,BOOK_CODE,CHAPTER,VERSE)";

	/** The database */
	private final Database database;
	
	/**
	 * Minimal constructor.
	 * @param library the bible library to import into
	 */
	public AbstractBibleImporter(BibleLibrary library) {
		this.database = library.database;
	}
	
	/**
	 * Imports the given bible, books and verses.
	 * @param bible the bible
	 * @param books the books of the bible
	 * @param verses the verses of the bible
	 * @return {@link Bible} the newly inserted bible
	 * @throws SQLException if an error occurs while saving the data to the database
	 * @throws BibleAlreadyExistsException if the bible already exists in the library
	 */
	protected final Bible insert(Bible bible, List<Book> books, List<Verse> verses) throws SQLException, BibleAlreadyExistsException {
		LOGGER.debug("Importing new bible: " + bible.name);
		// insert all the data into the tables
		try (Connection connection = this.database.getConnection()) {
			// begin the transaction
			connection.setAutoCommit(false);
			
			// verify the bible doesn't exist already
			try (Statement bibleQuery = connection.createStatement();
				 ResultSet bqResult = bibleQuery.executeQuery("SELECT COUNT(*) FROM bible WHERE name = '" + bible.name + "'");) {
				// make sure we didn't get anything
				if (bqResult.next() && bqResult.getInt(1) > 0) {
					connection.rollback();
					LOGGER.error("The bible already exists in the database.");
					throw new BibleAlreadyExistsException(bible.name);
				}
			} catch (SQLException e) {
				connection.rollback();
				LOGGER.error("Failed to query existing bibles before import.");
				throw e;
			}
			
			int bibleId = -1;
			Date date = new Date();
			boolean insertWarnings = false;
			
			// insert the bible
			try (PreparedStatement bibleInsert = connection.prepareStatement(INSERT_BIBLE, Statement.RETURN_GENERATED_KEYS)) {
				// set the parameters
				bibleInsert.setString(1, bible.source);
				bibleInsert.setString(2, bible.name);
				bibleInsert.setString(3, bible.language);
				bibleInsert.setTimestamp(4, new Timestamp(date.getTime()));
				bibleInsert.setString(5, bible.copyright);
				bibleInsert.setInt(6, bible.verseCount);
				bibleInsert.setBoolean(7, bible.hasApocrypha);
				bibleInsert.setBoolean(8, bible.hadImportWarning);
				// insert the bible
				bibleInsert.executeUpdate();
				// get the generated id
				ResultSet result = bibleInsert.getGeneratedKeys();
				if (result.next()) {
					// get the bible id
					int id = result.getInt(1);
					bibleId = id;
				}
			} catch (SQLException e) {
				connection.rollback();
				throw e;
			}
			
			// make sure the bible was saved first
			if (bibleId > 0) {
				LOGGER.debug("Bible inserted successfully: " + bible.name);
				
				// insert the books
				try (PreparedStatement bookInsert = connection.prepareStatement(INSERT_BOOK)) {
					for (Book book : books) {
						// set the parameters
						bookInsert.setInt(1, bibleId);
						bookInsert.setString(2, book.code);
						bookInsert.setString(3, book.name);
						// execute the insert
						bookInsert.executeUpdate();
					}
				} catch (SQLException e) {
					connection.rollback();
					throw e;
				}
				
				LOGGER.debug("Bible books inserted successfully: " + bible.name);
				
				// insert the verses
				try (PreparedStatement verseInsert = connection.prepareStatement(INSERT_VERSE)) {
					for (Verse verse : verses) {
						// set the parameters
						verseInsert.setInt(1, bibleId);
						verseInsert.setString(2, verse.book.code);
						verseInsert.setInt(3, verse.chapter);
						verseInsert.setInt(4, verse.verse);
						verseInsert.setInt(5, verse.subVerse);
						verseInsert.setInt(6, verse.order);
						verseInsert.setClob(7, new StringReader(verse.text));
						// execute the insert
						try {
							verseInsert.executeUpdate();
						} catch (SQLIntegrityConstraintViolationException e) {
							insertWarnings |= true;
							// its possible that the dumps have duplicate keys (book, chapter, verse, subverse)
							// in this case we will ignore these and continue but log them as warnings
							LOGGER.warn("Duplicate verse in file [" + verse.book.code + "|" + verse.chapter + "|" + verse.verse + "|" + verse.subVerse + "] in " + bible.name + ". Dropping verse.");
						}
						// let the outer try/catch handle other exceptions
					}
				} catch (SQLException e) {
					connection.rollback();
					throw e;
				}
				
				LOGGER.debug("Bible verses inserted successfully: " + bible.name);
			}
			
			// commit all the changes
			connection.commit();

			if (insertWarnings) {
				// update the import warning field on the bible
				try (PreparedStatement bibleUpdate = connection.prepareStatement(UPDATE_IMPORT_WARNING)) {
					bibleUpdate.setBoolean(0, true);
					bibleUpdate.setInt(1, bibleId);
					bibleUpdate.executeUpdate();
					connection.commit();
				} catch (SQLException e) {
					// just log this
					LOGGER.error("Failed to update the import warnings field for the bible " + bible.name);
				}
			}
			
			LOGGER.debug("Bible imported successfully: " + bible.name);
			
			// rebuild the indexes after a bible has been imported
			LOGGER.debug("Rebuilding bible indexes.");
			rebuildIndexes();
			
			// return a new bible with the unique id
			return new Bible(bibleId, 
					bible.name, 
					bible.language, 
					bible.source, 
					date,
					bible.copyright,
					bible.verseCount,
					bible.hasApocrypha,
					bible.hadImportWarning || insertWarnings);
		}
	}

	/**
	 * Rebuilds the indexes for the verses tables.
	 */
	private final void rebuildIndexes() {
		try (Connection connection = this.database.getConnection()) {
			connection.setAutoCommit(false);
			try (Statement statement = connection.createStatement();) {
				statement.execute(DROP_INDEX_BO_A);
				statement.execute(CREATE_INDEX_BO_A);
				
				statement.execute(DROP_INDEX_BO_D);
				statement.execute(CREATE_INDEX_BO_D);
				
				statement.execute(DROP_INDEX_BBCV);
				statement.execute(CREATE_INDEX_BBCV);
				
				connection.commit();
				
				LOGGER.debug("Bible indexes rebuilt successfully.");
			} catch (SQLException e) {
				// roll back any index changes we have done thus far
				connection.rollback();
				// just log this error
				LOGGER.warn("An error occurred when rebuilding the indexes after a successful import of a bible:", e);
			}
		} catch (SQLException e) {
			// just log this error
			LOGGER.warn("An error occurred when rebuilding the indexes after a successful import of a bible:", e);
		}
	}
}
