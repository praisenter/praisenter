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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.praisenter.data.Database;

/**
 * Access class for Bible verses.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleLibraryV1 {
	/** The database */
	final Database database;
	
	/**
	 * Minimal constructor.
	 * @param database the database to connect to
	 */
	public BibleLibraryV1(Database database) {
		this.database = database;
	}
	
	// public interface
	
	// bibles
	
	/**
	 * Returns the bible with the given id.
	 * @param id the bible id
	 * @return Bible the bible
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Bible getBible(int id) throws SQLException {
		return getBibleBySql("SELECT * FROM bible WHERE id = " + id);
	}
	
	/**
	 * Returns all the bibles.
	 * @return List&lt;{@link Bible}&gt;
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public List<Bible> getBibles() throws SQLException {
		return getBiblesBySql("SELECT * FROM bible ORDER BY name");
	}
	
	/**
	 * Returns the number of bibles.
	 * @return int
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public int getBibleCount() throws SQLException {
		return getCountBySql("SELECT COUNT(*) FROM bible");
	}
	
	/**
	 * Deletes the given bible.
	 * @param bible the bible to delete
	 * @throws SQLException if an exception occurs while deleting the bible
	 */
	public void deleteBible(Bible bible) throws SQLException {
		deleteBible(bible.id);
	}
	
	/**
	 * Deletes the bible with the given id.
	 * @param id the id of the bible to delete
	 * @throws SQLException if an exception occurs while deleting the bible
	 */
	public void deleteBible(int id) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement()) {
			// delete from the bottom up
			statement.addBatch("DELETE FROM bible_verse WHERE bible_id = " + id);
			statement.addBatch("DELETE FROM bible_book WHERE bible_id = " + id);
			statement.addBatch("DELETE FROM bible WHERE id = " + id);
			
			// execute the batch
			statement.executeBatch();
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	// books
	
	/**
	 * Returns all the books for the given {@link Bible}.
	 * <p>
	 * Returns an empty list if the bible was not found.
	 * @param bible the bible
	 * @return List&lt;{@link Book}&gt;
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public List<Book> getBooks(Bible bible) throws SQLException {
		return getBooks(bible, false);
	}
	
	/**
	 * Returns all the books for the given {@link Bible}.
	 * <p>
	 * Returns an empty list if the bible was not found.
	 * @param bible the bible
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return List&lt;{@link Book}&gt;
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public List<Book> getBooks(Bible bible, boolean includeApocrypha) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT code, name FROM bible_book WHERE bible_id = ").append(bible.id);
		if (!includeApocrypha) {
			sb.append(" AND code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("'");
		}
		sb.append(" ORDER BY code");
				
		return getBooksBySql(bible, sb.toString());
	}
	
	/**
	 * Returns the book for the given {@link Bible} and book code.
	 * <p>
	 * Returns null if the book code is not found.
	 * @param bible the bible
	 * @param code the book code
	 * @return {@link Book}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Book getBook(Bible bible, String code) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT code, name FROM bible_book WHERE bible_id = ").append(bible.getId())
		  .append(" AND code = '").append(code.replace("'", "''").trim()).append("'");
		
		return getBookBySql(bible, sb.toString());
	}
	
	/**
	 * Returns the {@link Book}s from the given {@link Bible} that match the given search.
	 * <p>
	 * Returns an empty list if no match was found.
	 * @param bible the bible to search
	 * @param search the search; (by book name)
	 * @return List&lt;{@link Book}&gt;
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public List<Book> searchBooks(Bible bible, String search) throws SQLException {
		return searchBooks(bible, search, false);
	}
	
	/**
	 * Returns the {@link Book}s from the given {@link Bible} that match the given search.
	 * <p>
	 * Returns an empty list if no match was found.
	 * @param bible the bible to search
	 * @param includeApocrypha true if the apocrypha should be included
	 * @param search the search; (by book name)
	 * @return List&lt;{@link Book}&gt;
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public List<Book> searchBooks(Bible bible, String search, boolean includeApocrypha) throws SQLException {
		String term = cleanSearchTerm(search);
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT code, name FROM bible_book WHERE bible_id = ").append(bible.id);
		if (!includeApocrypha) {
			sb.append(" AND code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("'");
		}
		sb.append(" AND searchable_name LIKE '%").append(term.toUpperCase()).append("%'")
		  .append(" ORDER BY code");
				
		return getBooksBySql(bible, sb.toString());
	}

	/**
	 * Returns the number of {@link Book}s contained in the given {@link Bible}.
	 * @param bible the bible
	 * @return int
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public int getBookCount(Bible bible) throws SQLException {
		return getBookCount(bible, false);
	}
	

	/**
	 * Returns the number of {@link Book}s contained in the given {@link Bible}.
	 * @param bible the bible
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return int
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public int getBookCount(Bible bible, boolean includeApocrypha) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(code) FROM bible_book WHERE bible_id = ? ");
		if (!includeApocrypha) {
			sb.append("AND code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("'");
		}
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	// chapters
	
	/**
	 * Returns the number of chapters contained in the given {@link Bible} and {@link Book}.
	 * @param bible the bible
	 * @param bookCode the book code
	 * @return int
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public int getChapterCount(Bible bible, String bookCode) throws SQLException {
		// create the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(DISTINCT chapter) FROM bible_verse WHERE bible_id = ? ")
		  .append("AND book_code = ?");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setString(2, bookCode);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the last chapter number in the given {@link Bible} and {@link Book}.
	 * @param bible the bible
	 * @param bookCode the book code
	 * @return int
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public int getLastChapter(Bible bible, String bookCode) throws SQLException {
		// create the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT MAX(chapter) FROM bible_verse WHERE bible_id = ? ")
		  .append("AND book_code = ?");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setString(2, bookCode);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	// verses
	
	/**
	 * Returns the verse for the given {@link Bible}, {@link Book}, chapter, and verse.
	 * @param bible the bible
	 * @param bookCode the book
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getVerse(Bible bible, String bookCode, int chapter, int verse) throws SQLException {
		// create the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append("FROM bible_verse ")
		  .append("INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id ")
		  .append("WHERE bible_verse.bible_id = ? ")
		  .append("AND book_code = ? ")
		  .append("AND chapter = ? ")
		  .append("AND verse = ? ")
		  .append("AND sub_verse = -1");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setString(2, bookCode);
			statement.setInt(3, chapter);
			statement.setInt(4, verse);
			
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return getVerse(bible, result);
			}
			
			return null;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the next verse for the given {@link Verse}.
	 * @param verse the verse
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getNextVerse(Verse verse) throws SQLException {
		return getNextVerse(verse, false);
	}
	
	/**
	 * Returns the next verse for the given {@link Verse}.
	 * @param verse the verse
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getNextVerse(Verse verse, boolean includeApocrypha) throws SQLException {
		// create the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append("FROM bible_verse ")
		  .append("INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id ")
		  .append("WHERE bible_verse.bible_id = ? ");
		if (!includeApocrypha) {
			sb.append("AND bible_book.code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("' ");
		}
		sb.append("AND sub_verse = -1 ")
		  .append("AND order_by = ")
		  .append("(SELECT MIN(order_by) FROM bible_verse WHERE bible_id = ? AND sub_verse = -1 ")
		  .append("AND order_by > ?)");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, verse.bible.id);
			statement.setInt(2, verse.bible.id);
			statement.setInt(3, verse.order);
			
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return getVerse(verse.bible, result);
			}
			
			return null;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the next verse for the given verse.
	 * @param bible the bible
	 * @param bookCode the book
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getNextVerse(Bible bible, String bookCode, int chapter, int verse) throws SQLException {
		return getNextVerse(bible, bookCode, chapter, verse, false);
	}

	/**
	 * Returns the next verse for the given verse.
	 * @param bible the bible
	 * @param bookCode the book
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getNextVerse(Bible bible, String bookCode, int chapter, int verse, boolean includeApocrypha) throws SQLException {
		// create the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append("FROM bible_verse ")
		  .append("INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id ")
		  .append("WHERE bible_verse.bible_id = ? ");
		if (!includeApocrypha) {
			sb.append("AND bible_book.code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("' ");
		}
		sb.append("AND order_by = ")
		  .append("(SELECT MIN(order_by) FROM bible_verse WHERE bible_id = ? AND sub_verse = -1 ")
		  .append("AND order_by > ")
		  .append("(SELECT order_by FROM bible_verse WHERE bible_id = ? ")
		  .append("AND book_code = ? ")
		  .append("AND chapter = ? ")
		  .append("AND verse = ? ")
		  .append("AND sub_verse = -1))");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setInt(2, bible.id);
			statement.setInt(3, bible.id);
			statement.setString(4, bookCode);
			statement.setInt(5, chapter);
			statement.setInt(6, verse);
			
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return getVerse(bible, result);
			}
			
			return null;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the previous verse for the given {@link Verse}.
	 * @param verse the verse
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getPreviousVerse(Verse verse) throws SQLException {
		return getPreviousVerse(verse, false);
	}

	/**
	 * Returns the previous verse for the given {@link Verse}.
	 * @param verse the verse
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getPreviousVerse(Verse verse, boolean includeApocrypha) throws SQLException {
		// create the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append("FROM bible_verse ")
		  .append("INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id ")
		  .append("WHERE bible_verse.bible_id = ? ");
		if (!includeApocrypha) {
			sb.append("AND bible_book.code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("' ");
		}
		sb.append("AND sub_verse = -1 ")
		  .append("AND order_by = ")
		  .append("(SELECT MAX(order_by) FROM bible_verse WHERE bible_id = ? AND sub_verse = -1 ")
		  .append("AND order_by < ?)");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, verse.bible.id);
			statement.setInt(2, verse.bible.id);
			statement.setInt(3, verse.order);
			
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return getVerse(verse.bible, result);
			}
			
			return null;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the previous verse for the given verse.
	 * @param bible the bible
	 * @param bookCode the book
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getPreviousVerse(Bible bible, String bookCode, int chapter, int verse) throws SQLException {
		return getPreviousVerse(bible, bookCode, chapter, verse, false);
	}

	/**
	 * Returns the previous verse for the given verse.
	 * @param bible the bible
	 * @param bookCode the book
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while retrieving the data
	 */
	public Verse getPreviousVerse(Bible bible, String bookCode, int chapter, int verse, boolean includeApocrypha) throws SQLException {
		// create the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append("FROM bible_verse ")
		  .append("INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id ")
		  .append("WHERE bible_verse.bible_id = ? ");
		if (!includeApocrypha) {
			sb.append("AND bible_book.code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("' ");
		}
		sb.append("AND order_by = ")
		  .append("(SELECT MAX(order_by) FROM bible_verse WHERE bible_id = ? AND sub_verse = -1 ")
		  .append("AND order_by < ")
		  .append("(SELECT order_by FROM bible_verse WHERE bible_id = ? ")
		  .append("AND book_code = ? ")
		  .append("AND chapter = ? ")
		  .append("AND verse = ? ")
		  .append("AND sub_verse = -1))");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setInt(2, bible.id);
			statement.setInt(3, bible.id);
			statement.setString(4, bookCode);
			statement.setInt(5, chapter);
			statement.setInt(6, verse);
			
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return getVerse(bible, result);
			}
			
			return null;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the matching verses for the given search.
	 * <p>
	 * This search is designed for input like: '1 cor 3: 6'.
	 * @param bible the bible
	 * @param search the search criteria
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return List&lt;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	private List<Verse> searchVersesByLocation(Bible bible, String search, boolean includeApocrypha) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append(" FROM bible_verse")
		  .append(" INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id")
		  .append(" WHERE bible_verse.bible_id = ").append(bible.id);
		if (!includeApocrypha) {
			sb.append(" AND bible_book.code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("'");
		}
		
		// replace multiple whitespaces with single whitespace
		// uppercase it, trim the ends, and escape out single quotes
		search = cleanSearchTerm(search).toUpperCase().replaceAll("\\s+", " ");
		// replace [(\\d+)\\s*:\\s*(\\d+)] with [\\1:\\2]
		search = search.replaceAll("(\\d+)\\s*:\\s*(\\d+)", "$1:$2");
		// handle the case where the search ends with chapter'space'verse instead of a :
		search = search.replaceAll("(.*)(\\d+)\\s+(\\d+)", "$1 $2:$3");
		// we should now have a string of the form [\\d_.*(_\\d)?(_\\d)?] where _ is a space
		// split the search term by space
		String[] parts = search.split("\\s+");
		// there is a possibility of 3 parts (booknum)(bookname)(chapter:verse)
		if (parts.length == 1) {
			// assume that its the book name
			sb.append(" AND bible_book.searchable_name LIKE '%").append(parts[0]).append("%'");
		} else if (parts.length == 2) {
			// what do we have (booknum)(bookname) or (bookname)(chapter:verse)?
			// if part[1] contains : or ends with a number then its the second case
			if (parts[1].matches("^\\d+(:)?(\\d+)?$")) {
				// second case
				sb.append(" AND bible_book.searchable_name LIKE '%").append(parts[0]).append("%'");
				if (parts[1].contains(":")) {
					// it has chapter and verse
					String[] cv = parts[1].split(":");
					sb.append(" AND chapter = ").append(cv[0]);
					if (cv.length > 1) {
						sb.append(" AND verse = ").append(cv[1]);
					}
				} else {
					// it doesn't contain : so assume its the chapter
					sb.append(" AND chapter = ").append(parts[1]);
				}
			} else {
				// first case
				sb.append(" AND bible_book.searchable_name LIKE '%").append(parts[0]).append(" ").append(parts[1]).append("%'");
			}
		} else {
			// we have all three pieces
			sb.append(" AND bible_book.searchable_name LIKE '%").append(parts[0]).append(" ").append(parts[1]).append("%'");
			// see what we have in the last piece
			if (parts[2].contains(":")) {
				// it has chapter and verse
				String[] cv = parts[2].split(":");
				sb.append(" AND chapter = ").append(cv[0]);
				if (cv.length > 1) {
					sb.append(" AND verse = ").append(cv[1]);
				}
			} else {
				// it doesn't contain : so assume its the chapter
				sb.append(" AND chapter = ").append(parts[2]);
			}
		}
		sb.append(" AND sub_verse = -1 ");
		sb.append("ORDER BY order_by");
		
		return getVersesBySql(bible, sb.toString());
	}
	
	/**
	 * Returns a WHERE condition string for the given search term and search type.
	 * @param search the search term
	 * @param type the search type
	 * @return String
	 */
	private String getSearchWhereCondition(String search, BibleSearchType type) {
		StringBuilder sb = new StringBuilder();
		// clean the search term
		search = cleanSearchTerm(search).toUpperCase().replaceAll("(\\s*,)?\\s+", " ");
		// check the search type
		if (type == BibleSearchType.ALL_WORDS || type == BibleSearchType.ANY_WORD) {
			sb.append("( ");
			// get the operation
			String operation = (type == BibleSearchType.ALL_WORDS) ? " AND" : " OR";
			// split the search term by whitespace
			String[] words = search.split("\\s+");
			// check the number of words
			if (words.length > 1) {
				// all words will be an AND operation
				for (int i = 0; i < words.length; i++) {
					if (i != 0) sb.append(operation);
					sb.append(" searchable_text LIKE '%").append(words[i]).append("%'");
				}
			} else {
				// one word search
				sb.append(" searchable_text LIKE '%").append(search).append("%'");
			}
			sb.append(")");
		} else {
			// phrase search
			sb.append(" searchable_text LIKE '%").append(search).append("%'");
		}
		
		return sb.toString();
	}
	
	/**
	 * Searches verses for the given search term.
	 * <p>
	 * This uses {@link BibleSearchType#PHRASE} by default.
	 * @param bible the bible
	 * @param search the search term
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String search) throws SQLException {
		return searchVerses(bible, search, BibleSearchType.PHRASE, false);
	}
	
	/**
	 * Searches verses for the given search term.
	 * <p>
	 * This uses {@link BibleSearchType#PHRASE} by default.
	 * @param bible the bible
	 * @param search the search term
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String search, boolean includeApocrypha) throws SQLException {
		return searchVerses(bible, search, BibleSearchType.PHRASE, includeApocrypha);
	}
	
	/**
	 * Searches verses for the given search term.
	 * @param bible the bible
	 * @param search the search term
	 * @param type the search type
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String search, BibleSearchType type) throws SQLException {
		return searchVerses(bible, search, type, false);
	}
	
	/**
	 * Searches verses for the given search term.
	 * @param bible the bible
	 * @param search the search term
	 * @param type the search type
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String search, BibleSearchType type, boolean includeApocrypha) throws SQLException {
		// check the search criteria
		if (search == null || search.trim().isEmpty()) {
			return Collections.emptyList();
		}
		// check for the location search type
		if (type == BibleSearchType.LOCATION) {
			return searchVersesByLocation(bible, search, includeApocrypha);
		}
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append(" FROM bible_verse")
		  .append(" INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id")
		  .append(" WHERE bible_verse.bible_id = ").append(bible.id);
		if (!includeApocrypha) {
			sb.append(" AND bible_book.code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("'");
		}
		sb.append(" AND sub_verse = -1")
		  .append(" AND ").append(getSearchWhereCondition(search, type))
		  .append(" ORDER BY order_by");
		
		return getVersesBySql(bible, sb.toString());
	}
	
	/**
	 * Searches verses for the given search term.
	 * <p>
	 * This uses {@link BibleSearchType#PHRASE} by default.
	 * @param bible the bible
	 * @param division the bible division
	 * @param search the search term
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, Division division, String search) throws SQLException {
		return searchVerses(bible, division, search, BibleSearchType.PHRASE);
	}
	
	/**
	 * Searches verses for the given search term.
	 * @param bible the bible
	 * @param division the division of the bible to search
	 * @param search the search term
	 * @param type the search type
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, Division division, String search, BibleSearchType type) throws SQLException {
		// check the search criteria
		if (search == null || search.trim().isEmpty()) {
			return Collections.emptyList();
		}
		// check the testament
		if (division == null) {
			return searchVerses(bible, search, type);
		}
		// check for the location search type
		if (type == BibleSearchType.LOCATION) {
			return searchVersesByLocation(bible, search, division == Division.APOCRYPHA);
		}
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append(" FROM bible_verse")
		  .append(" INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id")
		  .append(" WHERE bible_verse.bible_id = ").append(bible.id)
		  .append(" AND bible_book.code LIKE '%").append(division.getCode()).append("'")
		  .append(" AND sub_verse = -1")
		  .append(" AND").append(getSearchWhereCondition(search, type))
		  .append(" ORDER BY order_by");
				
		return getVersesBySql(bible, sb.toString());
	}

	/**
	 * Searches verses for the given search term.
	 * <p>
	 * This uses {@link BibleSearchType#PHRASE} by default.
	 * @param bible the bible
	 * @param bookCode the book code of the book to search
	 * @param search the search term
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String bookCode, String search) throws SQLException {
		return searchVerses(bible, bookCode, search, BibleSearchType.PHRASE);
	}

	/**
	 * Searches verses for the given search term.
	 * @param bible the bible
	 * @param bookCode the book code of the book to search
	 * @param search the search term
	 * @param type the search type
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String bookCode, String search, BibleSearchType type) throws SQLException {
		// check the search criteria
		if (search == null || search.trim().isEmpty()) {
			return Collections.emptyList();
		}
		// check for the location search type
		if (type == BibleSearchType.LOCATION) {
			return searchVersesByLocation(bible, search, bookCode.endsWith(Division.APOCRYPHA.getCode()));
		}
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append(" FROM bible_verse")
		  .append(" INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id")
		  .append(" WHERE bible_verse.bible_id = ").append(bible.id)
		  .append(" AND bible_book.code = ").append(bookCode)
		  .append(" AND sub_verse = -1")
		  .append(" AND").append(getSearchWhereCondition(search, type))
		  .append(" ORDER BY order_by");
				
		return getVersesBySql(bible, sb.toString());
	}
	
	/**
	 * Searches verses for the given search term.
	 * <p>
	 * This uses {@link BibleSearchType#PHRASE} by default.
	 * @param bible the bible
	 * @param bookCode the book code of the book to search
	 * @param chapter the chapter number
	 * @param search the search term
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String bookCode, int chapter, String search) throws SQLException {
		return searchVerses(bible, bookCode, chapter, search, BibleSearchType.PHRASE);
	}
	
	/**
	 * Searches verses for the given search term.
	 * @param bible the bible
	 * @param bookCode the book code of the book to search
	 * @param chapter the chapter number
	 * @param search the search term
	 * @param type the search type
	 * @return List&tl;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public List<Verse> searchVerses(Bible bible, String bookCode, int chapter, String search, BibleSearchType type) throws SQLException {
		// check the search criteria
		if (search == null || search.trim().isEmpty()) {
			return Collections.emptyList();
		}
		// check for the location search type
		if (type == BibleSearchType.LOCATION) {
			return searchVersesByLocation(bible, search, bookCode.endsWith(Division.APOCRYPHA.getCode()));
		}
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT id, book_code, name AS book_name, chapter, verse, sub_verse, order_by, text ")
		  .append(" FROM bible_verse")
		  .append(" INNER JOIN bible_book ON bible_verse.book_code = bible_book.code AND bible_verse.bible_id = bible_book.bible_id")
		  .append(" WHERE bible_verse.bible_id = ").append(bible.id)
		  .append(" AND bible_book.code = ").append(bookCode)
		  .append(" AND chapter = ").append(chapter)
		  .append(" AND sub_verse = -1")
		  .append(" AND").append(getSearchWhereCondition(search, type))
		  .append(" ORDER BY order_by");
				
		return getVersesBySql(bible, sb.toString());
	}

	/**
	 * Returns the total number of verses in the given {@link Bible}.
	 * @param bible the bible
	 * @return int
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public int getVerseCount(Bible bible) throws SQLException {
		return getVerseCount(bible, false);
	}

	/**
	 * Returns the total number of verses in the given {@link Bible}.
	 * @param bible the bible
	 * @param includeApocrypha true if the apocrypha should be included
	 * @return int
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public int getVerseCount(Bible bible, boolean includeApocrypha) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(id) FROM bible_verse WHERE bible_id = ? ");
		if (!includeApocrypha) {
			sb.append("AND book_code NOT LIKE '%").append(Division.APOCRYPHA.getCode()).append("'");
		}
		sb.append(" AND sub_verse = -1");

		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the total number of verses in the given {@link Book} of the {@link Bible}.
	 * @param bible the bible
	 * @param bookCode the book code
	 * @return int
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public int getVerseCount(Bible bible, String bookCode) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(id) FROM bible_verse WHERE bible_id = ? ")
		  .append("AND book_code = ? AND sub_verse = -1");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setString(2, bookCode);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	/**
	 * Returns the total number of verses in the given chapter of the {@link Bible}.
	 * @param bible the bible
	 * @param bookCode the book code
	 * @param chapter the chapter number
	 * @return int
	 * @throws SQLException if any exception occurs while retrieving the data
	 */
	public int getVerseCount(Bible bible, String bookCode, int chapter) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT COUNT(id) FROM bible_verses WHERE bible_id = ? ")
		  .append("AND book_code = ? ")
		  .append("AND chapter = ? ")
		  .append("AND sub_verse = -1");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setString(2, bookCode);
			statement.setInt(3, chapter);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Returns the last verse number in the given chapter of the {@link Bible}.
	 * @param bible the bible
	 * @param bookCode the book code
	 * @param chapter the chapter number
	 * @return int
	 * @throws SQLException if any exception occurs while retrieving the data
	 * @since 2.0.1
	 */
	public int getLastVerse(Bible bible, String bookCode, int chapter) throws SQLException {
		// build the query
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT MAX(verse) FROM bible_verse WHERE bible_id = ? ")
		  .append("AND book_code = ? ")
		  .append("AND chapter = ? ")
		  .append("AND sub_verse = -1");
		
		// execute the query
		try (Connection connection = this.database.getConnection();
			 PreparedStatement statement = connection.prepareStatement(sb.toString());) {
			statement.setInt(1, bible.id);
			statement.setString(2, bookCode);
			statement.setInt(3, chapter);

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getInt(1);
			}
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	// internal methods

	/**
	 * Escapes any single qoutes and trims the given string.
	 * @param search the search string
	 * @return String
	 */
	private static final String cleanSearchTerm(String search) {
		return search.replace("'", "''").trim();
	}
	
	/**
	 * Executes the given sql returning the count.
	 * @param sql the sql query
	 * @return int the count
	 * @throws SQLException if any exception occurs during processing
	 */
	private int getCountBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			if (result.next()) {
				// interpret the result
				return result.getInt(1);
			} 
			
			return 0;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Executes the given sql returning a list of {@link Bible}s.
	 * @param sql the sql query
	 * @return List&lt;{@link Bible}&gt;
	 * @throws SQLException if any exception occurs during processing
	 */
	private List<Bible> getBiblesBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Bible> bibles = new ArrayList<Bible>();
			while (result.next()) {
				// interpret the result
				Bible bible = getBible(result);
				bibles.add(bible);
			} 
			
			return bibles;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	/**
	 * Executes the given sql returning a {@link Bible}.
	 * @param sql the sql query
	 * @return {@link Bible}
	 * @throws SQLException if any exception occurs during processing
	 */
	private Bible getBibleBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			Bible bible = null;
			if (result.next()) {
				// interpret the result
				bible = getBible(result);
			} 
			
			return bible;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	/**
	 * Executes the given sql returning a list of {@link Book}s.
	 * @param bible the bible
	 * @param sql the sql query
	 * @return List&lt;{@link Book}&gt;
	 * @throws SQLException if any exception occurs during processing
	 */
	private List<Book> getBooksBySql(Bible bible, String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Book> books = new ArrayList<Book>();
			while (result.next()) {
				// interpret the result
				Book book = getBook(bible, result);
				books.add(book);
			} 
			
			return books;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Executes the given sql returning a {@link Book}.
	 * @param bible the bible
	 * @param sql the sql query
	 * @return {@link Book}
	 * @throws SQLException if any exception occurs during processing
	 */
	private Book getBookBySql(Bible bible, String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			if (result.next()) {
				// interpret the result
				Book book = getBook(bible, result);
				return book;
			} 
		} catch (Exception e) {
			throw new SQLException(e);
		}
		
		return null;
	}
	
	/**
	 * Executes the given sql returning the matching {@link Verse}s.
	 * @param bible the bible
	 * @param sql the sql query
	 * @return List&lt;{@link Verse}&gt;
	 * @throws SQLException if any exception occurs during processing
	 */
	private List<Verse> getVersesBySql(Bible bible, String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Verse> verses = new ArrayList<Verse>();
			while (result.next()) {
				// interpret the result
				Verse verse = getVerse(bible, result);
				verses.add(verse);
			} 
			
			return verses;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Converts the given result to a {@link Bible}.
	 * @param result the result
	 * @return {@link Bible}
	 * @throws SQLException if an exception occurs while processing the result
	 */
	private static final Bible getBible(ResultSet result) throws SQLException {
		try {
			return new Bible(
					result.getInt("id"),
					result.getString("name"),
					result.getString("language"),
					result.getString("data_source"),
					result.getTimestamp("import_date"),
					result.getString("copyright"),
					result.getInt("verse_count"),
					result.getBoolean("has_apocrypha"),
					result.getBoolean("had_import_warning"));
		} catch (SQLException e) {
			throw new SQLException("An error occurred when interpreting the bible result.", e);
		}
	}

	/**
	 * Converts the given result to a {@link Book}.
	 * @param bible the bible
	 * @param result the result
	 * @return {@link Book}
	 * @throws SQLException if an exception occurs while processing the result
	 */
	private static final Book getBook(Bible bible, ResultSet result) throws SQLException {
		try {
			return new Book(
					bible,
					result.getString("code"),
					result.getString("name"));
		} catch (SQLException e) {
			throw new SQLException("An error occurred when interpreting the book result.", e);
		}
	}
	
	/**
	 * Converts the given result to a {@link Verse}.
	 * @param bible the bible
	 * @param result the result
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while processing the result
	 */
	private static final Verse getVerse(Bible bible, ResultSet result) throws SQLException {
		try {
			return new Verse(
					bible,
					new Book(
							bible, 
							result.getString("book_code"), 
							result.getString("book_name")),
					result.getInt("id"),
					result.getInt("chapter"),
					result.getInt("verse"),
					result.getInt("sub_verse"),
					result.getInt("order_by"),
					result.getString("text"));
		} catch (SQLException e) {
			throw new SQLException("An error occurred when interpreting the verse result.", e);
		}
	}
}
