package org.praisenter.data.song;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a song.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Song {
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
	
	/**
	 * Creates a new song part for this song, adds it to the list of song
	 * parts and return it.
	 * @param type the song part type
	 * @param partName the song part name
	 * @param text the song part text
	 * @return {@link SongPart}
	 */
	public SongPart addSongPart(SongPartType type, String partName, String text) {
		SongPart last = this.getLastSongPart(type);
		int index = SongPart.BEGINNING_INDEX;
		if (last != null) {
			index = last.partIndex + 1;
		}
		SongPart part = new SongPart(type, partName, text, index);
		this.parts.add(part);
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
			part.partIndex = last.partIndex + 1;
		}
		// add the song part
		this.parts.add(part);
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
				if (part.getPartIndex() == index) {
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
				if (part.getPartIndex() > index) {
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
