package org.praisenter.data.song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Represents a song.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Song implements Comparable<Song> {
	/** The id assigned to new songs */
	protected static final int NEW_SONG_ID = -1;
	
	/** The song id */
	protected int id;
	
	/** The song title */
	protected String title;
	
	/** The song notes */
	protected String notes;
	
	/** The date the song was added */
	protected Date dateAdded;
	
	/** The song parts */
	protected List<SongPart> parts;
	
	/**
	 * Default constructor.
	 */
	public Song() {
		this("", "");
	}
	
	/**
	 * Optional constructor.
	 * @param title the song title
	 * @param notes the song notes
	 */
	public Song(String title, String notes) {
		this(Song.NEW_SONG_ID, title, notes, new Date());
	}
	
	/**
	 * Full constructor.
	 * @param id the song id
	 * @param title the song title
	 * @param notes the song notes
	 * @param dateAdded the date the song was added
	 */
	protected Song(int id, String title, String notes, Date dateAdded) {
		this.id = id;
		this.title = title;
		this.notes = notes;
		this.dateAdded = dateAdded;
		this.parts = new ArrayList<SongPart>();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Song) {
			Song song = (Song)obj;
			if (song.id == this.id) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Song[Id=").append(this.id)
		  .append("|Title=").append(this.title)
		  .append("|Notes=").append(this.notes)
		  .append("|DateAdded=").append(this.dateAdded)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Song o) {
		// sort by title first
		int diff = this.title.compareToIgnoreCase(o.title);
		if (diff == 0) {
			// then sort by date added
			diff = this.dateAdded.compareTo(o.dateAdded);
			if (diff == 0) {
				// finally sort by id
				diff = this.id - o.id;
			}
		}
		return diff;
	}
	
	/**
	 * Returns true if this song is a new song (that has not been saved).
	 * @return boolean
	 */
	public boolean isNew() {
		return this.id == Song.NEW_SONG_ID;
	}
	
	/**
	 * Creates a new song part for this song, adds it to the list of song
	 * parts and return it.
	 * @param type the song part type
	 * @param text the song part text
	 * @return {@link SongPart}
	 */
	public SongPart addSongPart(SongPartType type, String text) {
		SongPart last = this.getLastSongPart(type);
		int index = SongPart.BEGINNING_INDEX;
		if (last != null) {
			index = last.index + 1;
		}
		SongPart part = new SongPart(type, index, text);
		// set the song id
		part.songId = this.id;
		this.parts.add(part);
		Collections.sort(this.parts);
		return part;
	}
	
	/**
	 * Adds the given song part to this song.
	 * <p>
	 * This method will copy the given song part.
	 * @param songPart the song part
	 */
	public void addSongPart(SongPart songPart) {
		// create a new one just in case this one came from another song
		SongPart part = new SongPart(songPart);
		// set the song id
		part.songId = this.id;
		// get the last part of this part's type
		SongPart last = this.getLastSongPart(part.type);
		if (last != null) {
			part.index = last.index + 1;
		}
		// add the song part
		this.parts.add(part);
		Collections.sort(this.parts);
	}
	
	/**
	 * Returns the song part of the given type with the given index.
	 * <p>
	 * Returns null if no song part exists.
	 * @param type the song part type
	 * @param index the index
	 * @return {@link SongPart}
	 */
	public SongPart getSongPart(SongPartType type, int index) {
		for (SongPart part : this.parts) {
			if (part.getType() == type) {
				if (part.getIndex() == index) {
					return part;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the last song part of the given type.
	 * <p>
	 * Returns null if no song part of the given type exists.
	 * @param type the song part type
	 * @return {@link SongPart}
	 */
	protected SongPart getLastSongPart(SongPartType type) {
		int index = -1;
		SongPart last = null;
		for (SongPart part : this.parts) {
			if (part.getType() == type) {
				if (part.getIndex() > index) {
					last = part;
				}
			}
		}
		return last;
	}
	
	/**
	 * Returns the song id.
	 * @return int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the song id.
	 * @param id the song id
	 */
	protected void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the song title.
	 * @return String
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Sets the song title.
	 * @param title the song title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the song notes.
	 * @return String
	 */
	public String getNotes() {
		return this.notes;
	}
	
	/**
	 * Sets the song notes.
	 * @param notes the song notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Returns the date the song was added.
	 * @return Date
	 */
	public Date getDateAdded() {
		return this.dateAdded;
	}
	
	/**
	 * Sets the date the song was added.
	 * @param dateAdded the date added
	 */
	protected void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	/**
	 * Returns the list of song parts.
	 * @return List&lt;{@link SongPart}&gt;
	 */
	public List<SongPart> getParts() {
		return this.parts;
	}
}
