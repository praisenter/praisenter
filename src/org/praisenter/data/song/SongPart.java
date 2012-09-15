package org.praisenter.data.song;

/**
 * Represents a part of a {@link Song}; a verse for example.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongPart {
	/** The new song id */
	protected static final int NEW_SONG_PART_ID = -1;
	
	/** The default song font size */
	public static final int DEFAULT_FONT_SIZE = 40;
	
	/** The beginning index for a song part */
	public static final int BEGINNING_INDEX = 1;
	
	/** The song part id */
	protected int id;
	
	/** The song id */
	protected int songId;
	
	/** The song part type */
	protected SongPartType type;
	
	/** The song part name; typically something like 'Chorus 1' */
	protected String partName;
	
	/** The song part index; for sorting of like part types */
	protected int partIndex;
	
	/** The song part text */
	protected String text;
	
	/** The song part font size */
	protected int fontSize;
	
	/**
	 * Default constructor.
	 */
	public SongPart() {
		this(SongPart.NEW_SONG_PART_ID, Song.NEW_SONG_ID, SongPartType.OTHER, "", "", SongPart.BEGINNING_INDEX, SongPart.DEFAULT_FONT_SIZE);
	}
	
	/**
	 * Optional constructor.
	 * @param type the song part type
	 * @param partName the part name; typically something like 'Chorus 1'
	 * @param text the part text
	 */
	public SongPart(SongPartType type, String partName, String text) {
		this(SongPart.NEW_SONG_PART_ID, Song.NEW_SONG_ID, type, partName, text, SongPart.BEGINNING_INDEX, SongPart.DEFAULT_FONT_SIZE);
	}
	
	/**
	 * Optional constructor.
	 * @param type the song part type
	 * @param partName the part name; typically something like 'Chorus 1'
	 * @param text the part text
	 * @param index the part index
	 */
	public SongPart(SongPartType type, String partName, String text, int index) {
		this(SongPart.NEW_SONG_PART_ID, Song.NEW_SONG_ID, type, partName, text, index, SongPart.DEFAULT_FONT_SIZE);
	}
	
	/**
	 * Full constructor.
	 * @param id the part id
	 * @param songId the song id
	 * @param type the song part type
	 * @param partName the part name; typically something like 'Chorus 1'
	 * @param partIndex the part index
	 * @param text the part text
	 * @param fontSize the the part font size
	 */
	protected SongPart(int id, int songId, SongPartType type, String partName, String text, int partIndex, int fontSize) {
		this.id = id;
		this.songId = songId;
		this.type = type;
		this.partName = partName;
		this.partIndex = partIndex;
		this.text = text;
		this.fontSize = fontSize;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This method does not copy the song part id.
	 * @param part the song part to copy
	 */
	public SongPart(SongPart part) {
		this.id = SongPart.NEW_SONG_PART_ID;
		this.songId = part.songId;
		this.type = part.type;
		this.partName = part.partName;
		this.partIndex = part.partIndex;
		this.text = part.text;
		this.fontSize = part.fontSize;
	}
	
	/**
	 * Returns the song part id.
	 * @return int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the song part id.
	 * @param id the song part id
	 */
	protected void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the song id.
	 * @return int
	 */
	public int getSongId() {
		return this.songId;
	}
	
	/**
	 * Sets the song id.
	 * @param songId the song id
	 */
	protected void setSongId(int songId) {
		this.songId = songId;
	}
	
	/**
	 * Returns the song part type.
	 * @return {@link SongPartType}
	 */
	public SongPartType getType() {
		return this.type;
	}
	
	/**
	 * Sets the song part type.
	 * @param type the song part type
	 */
	public void setType(SongPartType type) {
		this.type = type;
	}
	
	/**
	 * Returns the song part name.
	 * @return String
	 */
	public String getPartName() {
		return this.partName;
	}
	
	/**
	 * Set song part name.
	 * @param partName the song part name
	 */
	public void setPartName(String partName) {
		this.partName = partName;
	}
	
	/**
	 * Returns the song part index.
	 * @return int
	 */
	public int getPartIndex() {
		return this.partIndex;
	}
	
	/**
	 * Sets the song part index.
	 * @param partIndex the song part index
	 */
	public void setPartIndex(int partIndex) {
		this.partIndex = partIndex;
	}
	
	/**
	 * Returns the song part text.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Sets the song part text.
	 * @param text the song part text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * Returns the song part font size.
	 * @return int
	 */
	public int getFontSize() {
		return this.fontSize;
	}
	
	/**
	 * Sets the song part font size.
	 * @param fontSize the song part font size
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
}
