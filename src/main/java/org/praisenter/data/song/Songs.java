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
package org.praisenter.data.song;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.praisenter.data.Database;

/**
 * Data access class for {@link Song}s.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Songs {
	
	private final Database database;
	
	public Songs(Database database) {
		this.database = database;
	}
	
	// public interface
	
	/**
	 * Returns the number of songs in the data store.
	 * @return int
	 * @throws SQLException if an exception occurs during execution
	 */
	public int getSongCount() throws SQLException {
		return getCountBySql("SELECT COUNT(*) FROM song");
	}
	
	/**
	 * Returns the song for the given id.
	 * @param id the song id
	 * @return {@link Song}
	 * @throws SQLException if an exception occurs during execution
	 */
	public Song getSong(int id) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM song WHERE id = ").append(id);
		// get the song
		Song song = getSongBySql(sb.toString());
		
		if (song != null) {
			// get authors
			sb = new StringBuilder();
			sb.append("SELECT * FROM song_author WHERE song_id = ").append(id);
			song.properties.authors = getAuthorsBySql(sb.toString());
			
			// get songbooks
			sb = new StringBuilder();
			sb.append("SELECT * FROM song_book WHERE song_id = ").append(id);
			song.properties.songbooks = getSongbooksBySql(sb.toString());
			
			// get titles
			sb = new StringBuilder();
			sb.append("SELECT * FROM song_title WHERE song_id = ").append(id);
			song.properties.titles = getTitlesBySql(sb.toString());
			
			// get comments
			sb = new StringBuilder();
			sb.append("SELECT * FROM song_comment WHERE song_id = ").append(id);
			song.properties.comments = getCommentsBySql(sb.toString());
			
			// get themes
			sb = new StringBuilder();
			sb.append("SELECT * FROM song_theme WHERE song_id = ").append(id);
			song.properties.themes = getThemesBySql(sb.toString());
			
			// get verses
			sb = new StringBuilder();
			sb.append("SELECT * FROM song_verse WHERE song_id = ").append(id);
			song.verses = getVersesBySql(sb.toString());
		}
		
		// return the song
		return song;
	}
	
	/**
	 * Returns all the songs.
	 * @return &lt;{@link Song}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	public List<Song> getSongs() throws SQLException {
		// get the songs
		List<Song> songs = getSongsBySql("SELECT * FROM songs ORDER BY id");

		// get authors
		List<Author> authors = getAuthorsBySql("SELECT * FROM song_author ORDER BY song_id");
		
		// get songbooks
		List<Songbook> songbooks = getSongbooksBySql("SELECT * FROM song_book ORDER BY song_id");
		
		// get titles
		List<Title> titles = getTitlesBySql("SELECT * FROM song_title ORDER BY song_id");
		
		// get comments
		List<Comment> comments = getCommentsBySql("SELECT * FROM song_comment ORDER BY song_id");
		
		// get themes
		List<Theme> themes = getThemesBySql("SELECT * FROM song_theme ORDER BY song_id");
		
		// get themes
		List<Verse> verses = getVersesBySql("SELECT * FROM song_verse ORDER BY song_id");
		
		// loop over the songs
		int a = 0, 
			b = 0, 
			t = 0, 
			c = 0, 
			m = 0, 
			v = 0;
		int as = authors.size(),
			bs = songbooks.size(),
			ts = titles.size(),
			cs = comments.size(),
			ms = themes.size(),
			vs = verses.size();
		for (Song song : songs) {
			// add authors
			for (;a < as; a++) {
				Author author = authors.get(a);
				if (song.id == author.songId) {
					song.properties.authors.add(author);
				} else {
					break;
				}
			}
			
			// add songbooks
			for (;b < bs; b++) {
				Songbook book = songbooks.get(b);
				if (song.id == book.songId) {
					song.properties.songbooks.add(book);
				} else {
					break;
				}
			}
			
			// add titles
			for (;t < ts; t++) {
				Title title = titles.get(t);
				if (song.id == title.songId) {
					song.properties.titles.add(title);
				} else {
					break;
				}
			}
			
			// add comments
			for (;c < cs; c++) {
				Comment comment = comments.get(c);
				if (song.id == comment.songId) {
					song.properties.comments.add(comment);
				} else {
					break;
				}
			}
			
			// add themes
			for (;m < ms; m++) {
				Theme theme = themes.get(m);
				if (song.id == theme.songId) {
					song.properties.themes.add(theme);
				} else {
					break;
				}
			}
			
			// add verses
			for (;v < vs; v++) {
				Verse verse = verses.get(v);
				if (song.id == verse.songId) {
					song.verses.add(verse);
				} else {
					break;
				}
			}
		}
		
		// return the songs
		return songs;
	}
	
	/**
	 * Returns the list of matching songs for the given search criteria.
	 * <p>
	 * This will search song title and song part text and return a list of matching
	 * parts or titles. This list will contain the matched text in the notes field and
	 * may return duplicate song results if more than one part matches.
	 * @param search the search criteria
	 * @return List&lt;{@link Song}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	public static final List<Song> searchSongs(String search) throws SQLException {
		String needle = search.trim().toUpperCase().replaceAll("'", "''");
		StringBuilder sb = new StringBuilder();

		// search song titles
		sb.append("SELECT id, title, title AS notes, added_date FROM songs WHERE searchable_title LIKE '%").append(needle).append("%' ");
		List<Song> songTitles = Songs.getSongsBySql(sb.toString());
		
		// search song parts
		sb.delete(0, sb.length());
		sb.append("SELECT songs.id, title, text AS notes, added_date FROM song_parts INNER JOIN songs ON song_parts.song_id = songs.id WHERE searchable_text LIKE '%").append(needle).append("%' ");
		List<Song> songTexts = Songs.getSongsBySql(sb.toString());
		
		// merge the lists
		songTitles.addAll(songTexts);
		Collections.sort(songTitles);
		
		// return the songs
		return songTitles;
	}
	
	/**
	 * Returns the list of matching songs for the given search criteria.
	 * <p>
	 * This will search song title and song part text and return a list of matching
	 * songs. This list will contain a distinct listing of any matching songs. No song
	 * parts are returned with the songs.
	 * @param search the search criteria
	 * @return List&lt;{@link Song}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	public static final List<Song> searchSongsDistinct(String search) throws SQLException {
		String needle = search.trim().toUpperCase().replaceAll("'", "''");
		StringBuilder sb = new StringBuilder();

		// search song titles
		sb.append("SELECT id, title, title AS notes, added_date FROM songs WHERE searchable_title LIKE '%").append(needle).append("%' ");
		List<Song> songTitles = Songs.getSongsBySql(sb.toString());
		
		// search song parts
		sb.delete(0, sb.length());
		sb.append("SELECT songs.id, title, text AS notes, added_date FROM song_parts INNER JOIN songs ON song_parts.song_id = songs.id WHERE searchable_text LIKE '%").append(needle).append("%' ");
		List<Song> songTexts = Songs.getSongsBySql(sb.toString());
		
		// merge the lists
		songTitles.addAll(songTexts);
		// sort by id
		Collections.sort(songTitles, new Comparator<Song>() {
			@Override
			public int compare(Song o1, Song o2) {
				return o1.id - o2.id;
			}
		});
		
		// perform the distinct operation
		Iterator<Song> it = songTitles.iterator();
		Song prev = null;
		while (it.hasNext()) {
			Song curr = it.next();
			// set the initial one
			if (prev == null) {
				prev = curr;
				continue;
			}
			if (prev.id == curr.id) {
				// remove this one
				it.remove();
			} else {
				// assign the next
				prev = curr;
			}
		}
		
		// sort again using the normal sort
		Collections.sort(songTitles);
		
		// return the songs
		return songTitles;
	}
			
	/**
	 * Saves the given song.
	 * @param song the song to save
	 * @throws SQLException if an exception occurs during execution
	 */
	public static final void saveSong(Song song) throws SQLException {
		try (Connection connection = Database.getInstance().getConnection()) {
			// start a transaction
			connection.setAutoCommit(false);
			try {
				// attempt to save the song
				saveSong(song, connection);
				// commit the transaction
				connection.commit();
			} catch (SQLException e) {
				// rollback any changes
				connection.rollback();
				// throw an exception
				throw new SQLException(e);
			}
		} catch (Exception e) {
			// this could happen if we couldnt get a connection or
			// the auto-commit flag could not be set
			throw new SQLException(e);
		}
	}
	
	/**
	 * Saves the given song part.
	 * @param songPart the song part to save
	 * @throws SQLException if an exception occurs during execution
	 */
	public static final void saveSongPart(SongPart songPart) throws SQLException {
		try (Connection connection = Database.getInstance().getConnection()) {
			// start a transaction
			connection.setAutoCommit(false);
			try {
				// attempt to save the song part
				saveSongPart(songPart, connection);
				// commit the transaction
				connection.commit();
			} catch (SQLException e) {
				// rollback any changes
				connection.rollback();
				// throw an exception
				throw new SQLException(e);
			}
		} catch (Exception e) {
			// this could happen if we couldnt get a connection or
			// the auto-commit flag could not be set
			throw new SQLException(e);
		}
	}
	
	/**
	 * Saves all the given songs.
	 * @param songs the songs to save
	 * @throws SQLException if an exception occurs during execution
	 */
	public static final void saveSongs(List<Song> songs) throws SQLException {
		try (Connection connection = Database.getInstance().getConnection()) {
			// start a transaction
			connection.setAutoCommit(false);
			// loop over the songs
			for (Song song : songs) {
				try {
					// attempt to save the song
					saveSong(song, connection);
				} catch (SQLException e) {
					// rollback any changes
					connection.rollback();
					// throw an exception
					throw new SQLException(e);
				}
			}
			try {
				// commit the transaction
				connection.commit();
			} catch (SQLException e) {
				// rollback any changes
				connection.rollback();
				// throw an exception
				throw new SQLException(e);
			}
		} catch (Exception e) {
			// this could happen if we couldnt get a connection or
			// the auto-commit flag could not be set
			throw new SQLException(e);
		}
	}

	/**
	 * Saves the given song using the given connection.
	 * @param song the song to save
	 * @param connection the connection
	 * @throws SQLException if an exception occurs during execution
	 */
	private static final void saveSong(Song song, Connection connection) throws SQLException {
		// check for a new song
		if (song.getId() == Song.NEW_SONG_ID) {
			// perform an insert
			PreparedStatement statement = connection.prepareStatement("INSERT INTO songs (title, notes, added_date) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, truncate(song.title, 100));
			statement.setClob(2, new StringReader(song.notes));
			statement.setTimestamp(3, new Timestamp(song.dateAdded.getTime()));
			// execute the insert
			int n = statement.executeUpdate();
			// make sure it worked
			if (n > 0) {
				// get the generated id
				ResultSet result = statement.getGeneratedKeys();
				if (result.next()) {
					// get the song id
					int id = result.getInt(1);
					song.id = id;
				} else {
					throw new SQLException("Failed to save the song, no auto-id generated.");
				}
			} else {
				// throw an exception
				throw new SQLException("Failed to save the song, no auto-id generated.");
			}
		} else {
			// perform an update
			PreparedStatement statement = connection.prepareStatement("UPDATE songs SET title = ?, notes = ? WHERE id = ?");
			statement.setString(1, truncate(song.title, 100));
			statement.setClob(2, new StringReader(song.notes));
			statement.setInt(3, song.id);
			int n = statement.executeUpdate();
			if (n <= 0) {
				// throw an exception
				throw new SQLException("Failed to save the song, the update was unsuccessful.");
			}
		}
		
		// then save the song parts
		if (song.id != Song.NEW_SONG_ID) {
			// delete any existing song parts
			PreparedStatement statement = connection.prepareStatement("DELETE FROM song_parts WHERE song_id = ?");
			statement.setInt(1, song.id);
			statement.executeUpdate();
			
			// loop over the song parts
			for (SongPart part : song.parts) {
				// assign the song id
				part.songId = song.id;
				// save the part
				Songs.saveSongPart(part, connection);
			}
		} else {
			// throw an exception
			throw new SQLException("Failed to save song, the song id is not valid.");
		}
	}
	
	/**
	 * Saves the given song part and returns true if successful.
	 * @param songPart the song part
	 * @param connection the connection
	 * @throws SQLException if an exception occurs during execution
	 */
	private static final void saveSongPart(SongPart songPart, Connection connection) throws SQLException {
		if (songPart.getSongId() == Song.NEW_SONG_ID) {
			// the song id is not set so we can't save this part
			throw new SQLException("Failed to save song part due to invalid song id: " + songPart.songId);
		} else {
			// perform an insert
			PreparedStatement statement = connection.prepareStatement("INSERT INTO song_parts (song_id, part_type, part_index, order_by, font_size, text) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			statement.setInt(1, songPart.songId);
			statement.setString(2, songPart.type.getValue());
			statement.setInt(3, songPart.index);
			statement.setInt(4, songPart.order);
			statement.setInt(5, songPart.fontSize);
			statement.setClob(6, new StringReader(songPart.text));
			
			int n = statement.executeUpdate();
			if (n > 0) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next()) {
					// get the song part id
					int id = result.getInt(1);
					songPart.id = id;
				} else {
					throw new SQLException("Failed to save song part due to no generated song part id.");
				}
			} else {
				throw new SQLException("Failed to save song part due to no generated song part id.");
			}
		}
	}
	
	/**
	 * Truncates the given string to the given length.
	 * @param string the string to truncate
	 * @param length the desired length
	 * @return String
	 */
	private static final String truncate(String string, int length) {
		if (string == null) return null;
		if (string.length() <= length) return string;
		return string.substring(0, length);
	}
	
	/**
	 * Deletes the song and returns true if successful.
	 * @param id the song id
	 * @return boolean
	 * @throws SQLException if an exception occurs during execution
	 */
	public static final boolean deleteSong(int id) throws SQLException {
		// check the id
		if (id != Song.NEW_SONG_ID) {
			try (Connection connection = Database.getInstance().getConnection())
			{
				connection.setAutoCommit(false);
				
				try {
					PreparedStatement statement = connection.prepareStatement("DELETE FROM song_parts WHERE song_id = ?");
					statement.setInt(1, id);
					statement.executeUpdate();
					
					statement = connection.prepareStatement("DELETE FROM songs WHERE id = ?");
					statement.setInt(1, id);
					int n = statement.executeUpdate();
					if (n <= 0) {
						// throw an exception
						throw new SQLException("Failed to delete song.");
					}
					
					// commit the changes
					connection.commit();
				} catch (SQLException e) {
					// rollback any changes
					connection.rollback();
					// throw an exception
					throw new SQLException(e);
				}
			} catch (Exception e) {
				throw new SQLException(e);
			}
		}
		return false;
	}

	// internal methods
	
	/**
	 * Interprets the given result set as one {@link Song}.
	 * @param result the result set
	 * @return {@link Song}
	 * @throws SQLException if an exception occurs while interpreting the result set
	 */
	private Song getSong(ResultSet result) throws SQLException {
		Song song = new Song();
		song.dateAdded = new Date(result.getTimestamp("added_date").getTime());
		song.id = result.getInt("id");
		song.properties.ccli = result.getInt("ccli");
		song.properties.copyright = result.getString("copyright");
		song.properties.key = result.getString("key");
		song.properties.keywords = result.getString("keywords");
		song.properties.publisher = result.getString("publisher");
		song.properties.released = result.getString("released");
		String tempo = result.getString("tempo");
		if (tempo != null && !tempo.isEmpty())
		song.properties.tempo = new Tempo();
		song.properties.tempo.text = tempo;
		song.properties.tempo.type = result.getString("tempo_type");
		song.properties.transposition = result.getInt("transposition");
		song.properties.variant = result.getString("variant");
		song.properties.verseOrder = result.getString("verse_order");
		return song;
	}
	
	/**
	 * Interprets the given result set as one {@link Author}.
	 * @param result the result set
	 * @return {@link Author}
	 * @throws SQLException if an exception occurs while interpreting the result set
	 */
	private Author getAuthor(ResultSet result) throws SQLException {
		Author author = new Author();
		author.songId = result.getInt("song_id");
		author.name = result.getString("name");
		author.type = result.getString("type");
		author.language = result.getString("language");
		return author;
	}
	
	/**
	 * Interprets the given result set as one {@link Songbook}.
	 * @param result the result set
	 * @return {@link Songbook}
	 * @throws SQLException if an exception occurs while interpreting the result set
	 */
	private Songbook getSongbook(ResultSet result) throws SQLException {
		Songbook book = new Songbook();
		book.songId = result.getInt("song_id");
		book.name = result.getString("name");
		book.entry = result.getString("entry");
		return book;
	}
	
	/**
	 * Interprets the given result set as one {@link Comment}.
	 * @param result the result set
	 * @return {@link Comment}
	 * @throws SQLException if an exception occurs while interpreting the result set
	 */
	private Comment getComment(ResultSet result) throws SQLException {
		Comment comment = new Comment();
		comment.songId = result.getInt("song_id");
		comment.text = result.getString("text");
		return comment;
	}

	/**
	 * Interprets the given result set as one {@link Theme}.
	 * @param result the result set
	 * @return {@link Theme}
	 * @throws SQLException if an exception occurs while interpreting the result set
	 */
	private Theme getTheme(ResultSet result) throws SQLException {
		Theme theme = new Theme();
		theme.songId = result.getInt("song_id");
		theme.text = result.getString("text");
		theme.language = result.getString("language");
		theme.transliteration = result.getString("translit");
		return theme;
	}

	/**
	 * Interprets the given result set as one {@link Title}.
	 * @param result the result set
	 * @return {@link Title}
	 * @throws SQLException if an exception occurs while interpreting the result set
	 */
	private Title getTitle(ResultSet result) throws SQLException {
		Title title = new Title();
		title.songId = result.getInt("song_id");
		title.original = result.getInt("original") == 1;
		title.text = result.getString("text");
		title.language = result.getString("language");
		title.transliteration = result.getString("translit");
		return title;
	}
	
	/**
	 * Interprets the given result set as one {@link Verse}.
	 * @param result the result set
	 * @return {@link Verse}
	 * @throws SQLException if an exception occurs while interpreting the result set
	 */
	private Verse getVerse(ResultSet result) throws SQLException {
		Verse verse = new Verse();
		verse.songId = result.getInt("song_id");
		verse.fontSize = result.getInt("font_size");
		verse.number = result.getInt("number");
		verse.part = result.getString("part");
		// use setType to generate the name field
		verse.setType(result.getString("type"));
		verse.text = result.getString("text");
		verse.language = result.getString("language");
		verse.transliteration = result.getString("translit");
		return verse;
	}
	
	/**
	 * Returns the {@link Song} (without the song parts) for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return {@link Song}
	 * @throws SQLException if an exception occurs during execution
	 */
	private Song getSongBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			Song song = null;
			if (result.next()) {
				// interpret the result
				song = getSong(result);
			} 
			
			return song;
		}
	}
	
	/**
	 * Returns the list of {@link Song}s (without the song parts) for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Song}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	private List<Song> getSongsBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Song> songs = new ArrayList<Song>();
			while (result.next()) {
				// interpret the result
				Song song = getSong(result);
				songs.add(song);
			} 
			
			return songs;
		}
	}

	/**
	 * Returns the list of {@link Authors}s for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Author}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	private List<Author> getAuthorsBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Author> authors = new ArrayList<Author>();
			while (result.next()) {
				// interpret the result
				Author author = getAuthor(result);
				authors.add(author);
			} 
			
			return authors;
		}
	}

	/**
	 * Returns the list of {@link Songbook}s for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Songbook}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	private List<Songbook> getSongbooksBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Songbook> songbooks = new ArrayList<Songbook>();
			while (result.next()) {
				// interpret the result
				Songbook songbook = getSongbook(result);
				songbooks.add(songbook);
			} 
			
			return songbooks;
		}
	}

	/**
	 * Returns the list of {@link Comment}s for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Comment}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	private List<Comment> getCommentsBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Comment> comments = new ArrayList<Comment>();
			while (result.next()) {
				// interpret the result
				Comment comment = getComment(result);
				comments.add(comment);
			} 
			
			return comments;
		}
	}

	/**
	 * Returns the list of {@link Theme}s for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Theme}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	private List<Theme> getThemesBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Theme> themes = new ArrayList<Theme>();
			while (result.next()) {
				// interpret the result
				Theme theme = getTheme(result);
				themes.add(theme);
			} 
			
			return themes;
		}
	}

	/**
	 * Returns the list of {@link Title}s for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Title}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	private List<Title> getTitlesBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Title> titles = new ArrayList<Title>();
			while (result.next()) {
				// interpret the result
				Title title = getTitle(result);
				titles.add(title);
			} 
			
			return titles;
		}
	}

	/**
	 * Returns the list of {@link Verse}s for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Verse}&gt;
	 * @throws SQLException if an exception occurs during execution
	 */
	private List<Verse> getVersesBySql(String sql) throws SQLException {
		// execute the query
		try (Connection connection = this.database.getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Verse> verses = new ArrayList<Verse>();
			while (result.next()) {
				// interpret the result
				Verse verse = getVerse(result);
				verses.add(verse);
			} 
			
			return verses;
		}
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
		}
	}
	
}
