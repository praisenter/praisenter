package org.praisenter.data.song;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.praisenter.data.ConnectionFactory;
import org.praisenter.data.DataException;
import org.praisenter.resources.Messages;

/**
 * Data access class for {@link Song}s.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Songs {
	
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
					result.getString("part_name"),
					result.getString("text"),
					result.getInt("part_index"),
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
		try (Connection connection = ConnectionFactory.getSongsConnection();
			 Statement statement = connection.createStatement();
			 ResultSet result = statement.executeQuery(sql);)
		{
			Song song = null;
			if (result.next()) {
				// interpret the result
				song = Songs.getSong(result);
			} 
			
			return song;
		} catch (SQLException e) {
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
		try (Connection connection = ConnectionFactory.getSongsConnection();
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
		} catch (SQLException e) {
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
		try (Connection connection = ConnectionFactory.getSongsConnection();
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
		} catch (SQLException e) {
			throw new DataException(e);
		}
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
		}
		
		// return the song
		return song;
	}
	
	/**
	 * Returns all the songs.
	 * @return &lt;{@link Song}&gt;
	 * @throws DataException if an exception occurs during execution
	 */
	public static final List<Song> getSongs() throws DataException {
		// get the songs
		List<Song> songs = Songs.getSongsBySql("SELECT * FROM songs ORDER BY id");
		
		// get the song parts
		List<SongPart> parts = Songs.getSongPartsBySql("SELECT * FROM song_parts ORDER BY song_id, part_type, part_index");
		
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
		}
		
		// sort by title
		Collections.sort(songs, new SongTitleComparator());
		
		// return the songs
		return songs;
	}
	
	/**
	 * Returns the list of matching songs for the given search criteria.
	 * <p>
	 * This will search song title and song part text.
	 * @param search the search criteria
	 * @return List&lt;{@link Song}&gt;
	 * @throws DataException if an exception occurs during execution
	 */
	public static final List<Song> searchSongs(String search) throws DataException {
		String needle = search.trim().toUpperCase().replaceAll("'", "''");
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT songs.id as id, title, added_date, notes FROM songs")
		.append(" LEFT OUTER JOIN song_parts ON songs.id = song_parts.song_id")
		.append(" WHERE searchable_title LIKE '%").append(needle).append("%'")
		.append(" OR searchable_text LIKE '%").append(needle).append("%'");
		
		// get the songs
		List<Song> songs = Songs.getSongsBySql(sb.toString());
		
		// loop over the songs
		for (Song song : songs) {
			song.parts = Songs.getSongPartsBySql("SELECT * FROM song_parts WHERE song_id = " + song.id + " ORDER BY part_type, part_index");
		}
		
		// return the song
		return songs;
	}
	
	/**
	 * Saves the given song.
	 * @param song the song to save
	 * @throws DataException if an exception occurs during execution
	 */
	public static final void saveSong(Song song) throws DataException {
		try (Connection connection = ConnectionFactory.getSongsConnection()) {
			// start a transaction
			connection.setAutoCommit(false);
			// check for a new song
			if (song.getId() == Song.NEW_SONG_ID) {
				// perform an insert
				try (PreparedStatement statement = connection.prepareStatement("INSERT INTO songs (title, notes, added_date) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
					statement.setString(1, song.title);
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
						}
					} else {
						// it didn't work, so rollback changed
						connection.rollback();
						// throw an exception
						throw new DataException(MessageFormat.format(Messages.getString("songs.save.failed"), song.title));
					}
				} catch (SQLException e) {
					// an error occurred so rollback
					connection.rollback();
					// throw an exception
					throw new DataException(MessageFormat.format(Messages.getString("songs.save.failed"), song.title), e);
				}
			} else {
				// perform an update
				try (PreparedStatement statement = connection.prepareStatement("UPDATE songs SET title = ?, notes = ? WHERE id = ?")) {
					statement.setString(1, song.title);
					statement.setClob(2, new StringReader(song.notes));
					statement.setInt(3, song.id);
					int n = statement.executeUpdate();
					if (n <= 0) {
						// it didn't work, so rollback changed
						connection.rollback();
						// throw an exception
						throw new DataException(MessageFormat.format(Messages.getString("songs.save.failed"), song.title));
					}
				} catch (SQLException e) {
					// an error occurred so rollback
					connection.rollback();
					// throw an exception
					throw new DataException(MessageFormat.format(Messages.getString("songs.save.failed"), song.title), e);
				}
			}
			
			// then save the song parts
			if (song.id != Song.NEW_SONG_ID) {
				// loop over the song parts
				for (SongPart part : song.parts) {
					// assign the song id
					part.songId = song.id;
					// save the part
					try {
						Songs.saveSongPart(connection, part);
					} catch (DataException e) {
						// an error occurred so rollback
						connection.rollback();
						// re-throw the exception
						throw e;
					}
				}
				
				// commit the changes
				connection.commit();
			} else {
				// the song failed to be saved
				connection.rollback();
				// throw an exception
				throw new DataException(MessageFormat.format(Messages.getString("songs.save.failed"), song.title));
			}
		} catch (SQLException e) {
			throw new DataException(MessageFormat.format(Messages.getString("songs.save.failed"), song.title), e);
		}
	}
	
	/**
	 * Saves the given song part and returns true if successful.
	 * @param connection the connection
	 * @param songPart the song part
	 * @return boolean
	 * @throws DataException if an exception occurs during execution
	 */
	private static final boolean saveSongPart(Connection connection, SongPart songPart) throws DataException {
		// check the song part id
		if (songPart.getId() == SongPart.NEW_SONG_PART_ID) {
			// then its a new song part
			if (songPart.getSongId() == Song.NEW_SONG_ID) {
				// the song id is not set so we can't save this part
				throw new DataException(MessageFormat.format(Messages.getString("songs.part.save.failed"), songPart.type, songPart.partIndex));
			} else {
				// perform an insert
				try (PreparedStatement statement = connection.prepareStatement("INSERT INTO song_parts (song_id, part_type, part_name, part_index, text, font_size) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
					statement.setInt(1, songPart.songId);
					statement.setString(2, Songs.getStringForPartType(songPart.type));
					statement.setString(3, songPart.partName);
					statement.setInt(4, songPart.partIndex);
					statement.setClob(5, new StringReader(songPart.text));
					statement.setInt(6, songPart.fontSize);
					
					int n = statement.executeUpdate();
					if (n > 0) {
						ResultSet result = statement.getGeneratedKeys();
						if (result.next()) {
							// get the song part id
							int id = result.getInt(1);
							songPart.id = id;
							return true;
						}
					} else {
						throw new DataException(MessageFormat.format(Messages.getString("songs.part.save.failed"), songPart.type, songPart.partIndex));
					}
				} catch (SQLException e) {
					throw new DataException(MessageFormat.format(Messages.getString("songs.part.save.failed"), songPart.type, songPart.partIndex), e);
				}
			}
		} else {
			// the song part already exists
			if (songPart.getSongId() == Song.NEW_SONG_ID) {
				// the song id is not set so we can't save this part
				throw new DataException(MessageFormat.format(Messages.getString("songs.part.save.failed"), songPart.type, songPart.partIndex));
			} else {
				try (PreparedStatement statement = connection.prepareStatement("UPDATE song_parts SET part_type = ?, part_name = ?, part_index = ?, text = ?, font_size = ? WHERE id = ?")) {
					statement.setString(1, Songs.getStringForPartType(songPart.type));
					statement.setString(2, songPart.partName);
					statement.setInt(3, songPart.partIndex);
					statement.setClob(4, new StringReader(songPart.text));
					statement.setInt(5, songPart.fontSize);
					statement.setInt(6, songPart.id);
					
					int n = statement.executeUpdate();
					if (n <= 0) {
						throw new DataException(MessageFormat.format(Messages.getString("songs.part.save.failed"), songPart.type, songPart.partIndex));
					}
				} catch (SQLException e) {
					throw new DataException(MessageFormat.format(Messages.getString("songs.part.save.failed"), songPart.type, songPart.partIndex), e);
				}
			}
		}
		return false;
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
			try (Connection connection = ConnectionFactory.getSongsConnection();
				 Statement statement = connection.createStatement();)
			{
				// its possible that this one will return 0 if there are no song parts
				statement.executeUpdate("DELETE FROM song_parts WHERE song_id = " + id);
				// so only check this one
				int n = statement.executeUpdate("DELETE FROM songs WHERE id = " + id);
				if (n > 0) {
					return true;
				}
			} catch (SQLException e) {
				throw new DataException(Messages.getString("songs.save.failed"), e);
			}
		}
		return false;
	}
}
