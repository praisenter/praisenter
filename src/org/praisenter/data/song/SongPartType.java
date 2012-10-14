package org.praisenter.data.song;

import org.praisenter.resources.Messages;

/**
 * Enumeration of the song part types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum SongPartType {
	/** Verse part */
	VERSE(Messages.getString("song.part.type.verse")),
	
	/** Chorus part */
	CHORUS(Messages.getString("song.part.type.chorus")),
	
	/** Bridge part */
	BRIDGE(Messages.getString("song.part.type.bridge")),
	
	/** End part */
	END(Messages.getString("song.part.type.end")),
	
	/** Vamp part */
	VAMP(Messages.getString("song.part.type.vamp")),
	
	/** Tag part */
	TAG(Messages.getString("song.part.type.tag")),
	
	/** Other part */
	OTHER(Messages.getString("song.part.type.other"));
	
	/** The user friendly part name */
	private String name;
	
	/**
	 * Minimal constructor.
	 * @param name the user friendly part name
	 */
	private SongPartType(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the user friendly part name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
}
