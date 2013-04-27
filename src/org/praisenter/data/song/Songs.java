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

import org.praisenter.data.ConnectionFactory;
import org.praisenter.data.DataException;

/**
 * Data access class for {@link Song}s.
 * @author William Bittle
 * @version 2.0.1
 * @since 1.0.0
 */
public final class Songs {
	
	/** Hidden default constructor */
	private Songs() {}
	
	// internal methods
	
	/**
	 * Returns the data equivalent of the given part type enum.
	 * @param type the part type enum
	 * @return String
	 */
	private static final String getStringForPartType(SongPartType type) {
		if (type == SongPartType.BRIDGE) {
			return "B";
		} else if (type == SongPartType.CHORUS) {
			return "C";
		} else if (type == SongPartType.END) {
			return "E";
		} else if (type == SongPartType.TAG) {
			return "T";
		} else if (type == SongPartType.VAMP) {
			return "A";
		} else if (type == SongPartType.VERSE) {
			return "V";
		} else {
			return "O";
		}
	}
	
	/**
	 * Returns the part type enum for the given data part type.
	 * @param type the data part type
	 * @return {@link SongPartType}
	 */
	private static final SongPartType getPartTypeForString(String type) {
		if ("B".equals(type)) {
			return SongPartType.BRIDGE;
		} else if ("C".equals(type)) {
			return SongPartType.CHORUS;
		} else if ("E".equals(type)) {
			return SongPartType.END;
		} else if ("T".equals(type)) {
			return SongPartType.TAG;
		} else if ("A".equals(type)) {
			return SongPartType.VAMP;
		} else if ("V".equals(type)) {
			return SongPartType.VERSE;
		} else {
			return SongPartType.OTHER;
		}
	}
	
	/**
	 * Interprets the given result set as one {@link Song}.
	 * @param result the result set
	 * @return {@link Song}
	 * @throws DataException if an exception occurs while interpreting the result set
	 */
	private static final Song getSong(ResultSet result) throws DataException {
		try {
			return new Song(
					result.getInt("id"),
					result.getString("title"),
					result.getString("notes"),
					new Date(result.getTimestamp("added_date").getTime()));
		} catch (SQLException e) {
			throw new DataException("An error occurred when interpreting the song result.", e);
		}
	}
	
	/**
	 * Interprets the given result set as one {@link SongPart}.
	 * @param result the result set
	 * @return {@link SongPart}
	 * @throws DataException if an exception occurs while interpreting the result set
	 */
	private static final SongPart getSongPart(ResultSet result) throws DataException {
		try {
			return new SongPart(
					result.getInt("id"),
					result.getInt("song_id"),
					Songs.getPartTypeForString(result.getString("part_type")),
					result.getInt("part_index"),
					result.getString("text"),
					result.getInt("order_by"),
					result.getInt("font_size"));
		} catch (SQLException e) {
			throw new DataException("An error occurred when interpreting the song part result.", e);
		}
	}
	
	/**
	 * Returns the {@link Song} (without the song parts) for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return {@link Song}
	 * @throws DataException if an exception occurs during execution
	 */
	private static final Song getSongBySql(String sql) throws DataException {
		// execute the query
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			Song song = null;
			if (result.next()) {
				// interpret the result
				song = Songs.getSong(result);
			} 
			
			return song;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
	
	/**
	 * Returns the list of {@link Song}s (without the song parts) for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link Song}&gt;
	 * @throws DataException if an exception occurs during execution
	 */
	private static final List<Song> getSongsBySql(String sql) throws DataException {
		// execute the query
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<Song> songs = new ArrayList<Song>();
			while (result.next()) {
				// interpret the result
				Song song = Songs.getSong(result);
				songs.add(song);
			} 
			
			return songs;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}

	/**
	 * Returns the list of {@link SongPart}s for the given sql statement.
	 * <p>
	 * No check is performed to verify the sql given is valid.
	 * @param sql the sql statement
	 * @return List&lt;{@link SongPart}&gt;
	 * @throws DataException if an exception occurs during execution
	 */
	private static final List<SongPart> getSongPartsBySql(String sql) throws DataException {
		// execute the query
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			List<SongPart> parts = new ArrayList<SongPart>();
			while (result.next()) {
				// interpret the result
				SongPart part = Songs.getSongPart(result);
				parts.add(part);
			} 
			
			return parts;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}

	/**
	 * Executes the given sql returning the count.
	 * @param sql the sql query
	 * @return int the count
	 * @throws DataException if any exception occurs during processing
	 */
	private static final int getCountBySql(String sql) throws DataException {
		// execute the query
		try (Connection connection = ConnectionFactory.getInstance().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			if (result.next()) {
				// interpret the result
				return result.getInt(1);
			} 
			
			return 0;
		} catch (Exception e) {
			throw new DataException(e);
		}
	}
	
	/**
	 * Returns the number of songs in the data store.
	 * @return int
	 * @throws DataException if an exception occurs during execution
	 */
	public static final int getSongCount() throws DataException {
		return Songs.getCountBySql("SELECT COUNT(*) FROM songs");
	}
	
	/**
	 * Returns the song for the given id.
	 * @param id the song id
	 * @return {@link Song}
	 * @throws DataException if an exception occurs during execution
	 */
	public static final Song getSong(int id) throws DataException {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM songs WHERE id = ").append(id);
		// get the song
		Song song = Songs.getSongBySql(sb.toString());
		
		if (song != null) {
			sb = new StringBuilder();
			sb.append("SELECT * FROM song_parts WHERE song_id = ").append(id);
			// get the song parts
			song.parts = Songs.getSongPartsBySql(sb.toString());
			Collections.sort(song.parts);
		}
		
		// return the song
		return song;
	}
	
	/**
	 * Returns all the songs with their parts.
	 * @return &lt;{@link Song}&gt;
	 * @throws DataException if an exception occurs during execution
	 */
	public static final List<Song> getSongs() throws DataException {
		return getSongs(true);
	}
	
	/**
	 * Returns all the songs.
	 * @param returnParts true if the song parts should be returned
	 * @return &lt;{@link Song}&gt;
	 * @throws DataException if an exception occurs during execution
	 */
	public static final List<Song> getSongs(boolean returnParts) throws DataException {
		// get the songs
		List<Song> songs = Songs.getSongsBySql("SELECT * FROM songs ORDER BY id");
		
		if (returnParts) {
			// get the song parts
			List<SongPart> parts = Songs.getSongPartsBySql("SELECT * FROM song_parts ORDER BY song_id");
			
			// loop over the songs
			for (Song song : songs) {
				Iterator<SongPart> it = parts.iterator();
				while (it.hasNext()) {
					SongPart part = it.next();
					if (song.id == part.songId) {
						// remove the element
						it.remove();
						song.parts.add(part);
					} else {
						// we can break here since we are ordering both results
						// by the song id, therefore we guarantee that there are
						// no more parts past the first non-equal part
						break;
					}
				}
				Collections.sort(song.parts);
			}
		}
		
		// sort by title
		Collections.sort(songs, new SongTitleComparator());
		
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
	 * @throws DataException if an exception occurs during execution
	 */
	public static final List<Song> searchSongs(String search) throws DataException {
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
	 * @throws DataException if an exception occurs during execution
	 */
	public static final List<Song> searchSongsDistinct(String search) throws DataException {
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
	 * @throws DataException if an exception occurs during execution
	 */
	public static final void saveSong(Song song) throws DataException {
		try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
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
				throw new DataException(e);
			}
		} catch (Exception e) {
			// this could happen if we couldnt get a connection or
			// the auto-commit flag could not be set
			throw new DataException(e);
		}
	}
	
	/**
	 * Saves the given song part.
	 * @param songPart the song part to save
	 * @throws DataException if an exception occurs during execution
	 */
	public static final void saveSongPart(SongPart songPart) throws DataException {
		try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
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
				throw new DataException(e);
			}
		} catch (Exception e) {
			// this could happen if we couldnt get a connection or
			// the auto-commit flag could not be set
			throw new DataException(e);
		}
	}
	
	/**
	 * Saves all the given songs.
	 * @param songs the songs to save
	 * @throws DataException if an exception occurs during execution
	 */
	public static final void saveSongs(List<Song> songs) throws DataException {
		try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
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
					throw new DataException(e);
				}
			}
			try {
				// commit the transaction
				connection.commit();
			} catch (SQLException e) {
				// rollback any changes
				connection.rollback();
				// throw an exception
				throw new DataException(e);
			}
		} catch (Exception e) {
			// this could happen if we couldnt get a connection or
			// the auto-commit flag could not be set
			throw new DataException(e);
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
			statement.setString(2, Songs.getStringForPartType(songPart.type));
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
	 * @throws DataException if an exception occurs during execution
	 */
	public static final boolean deleteSong(int id) throws DataException {
		// check the id
		if (id != Song.NEW_SONG_ID) {
			try (Connection connection = ConnectionFactory.getInstance().getConnection())
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
					throw new DataException(e);
				}
			} catch (Exception e) {
				throw new DataException(e);
			}
		}
		return false;
	}
}
