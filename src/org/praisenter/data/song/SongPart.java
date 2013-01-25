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

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.resources.Messages;

/**
 * Represents a part of a {@link Song}; a verse for example.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
@XmlRootElement(name = "Part")
@XmlAccessorType(XmlAccessType.NONE)
public class SongPart implements Comparable<SongPart> {
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
	@XmlAttribute(name = "Type", required = false)
	protected SongPartType type;

	/** The song part index; for sorting of like part types */
	@XmlAttribute(name = "Index", required = false)
	protected int index;
	
	/** The song part text */
	@XmlElement(name = "Text", required = false, nillable = true)
	protected String text;
	
	/** The song part order */
	@XmlAttribute(name = "Order", required = false)
	protected int order;
	
	/** The song part font size */
	@XmlAttribute(name = "FontSize", required = false)
	protected int fontSize;
	
	/**
	 * Default constructor.
	 */
	public SongPart() {
		this(SongPart.NEW_SONG_PART_ID, Song.NEW_SONG_ID, SongPartType.CHORUS, SongPart.BEGINNING_INDEX, "", 1, SongPart.DEFAULT_FONT_SIZE);
	}
	
	/**
	 * Optional constructor.
	 * @param type the song part type
	 * @param text the part text
	 */
	public SongPart(SongPartType type, String text) {
		this(SongPart.NEW_SONG_PART_ID, Song.NEW_SONG_ID, type, SongPart.BEGINNING_INDEX, text, 1, SongPart.DEFAULT_FONT_SIZE);
	}
	
	/**
	 * Optional constructor.
	 * @param type the song part type
	 * @param index the part index
	 * @param text the part text
	 */
	public SongPart(SongPartType type, int index, String text) {
		this(SongPart.NEW_SONG_PART_ID, Song.NEW_SONG_ID, type, index, text, 1, SongPart.DEFAULT_FONT_SIZE);
	}
	
	/**
	 * Full constructor.
	 * @param id the part id
	 * @param songId the song id
	 * @param type the song part type
	 * @param index the part index
	 * @param text the part text
	 * @param order the part order
	 * @param fontSize the the part font size
	 */
	protected SongPart(int id, int songId, SongPartType type, int index, String text, int order, int fontSize) {
		this.id = id;
		this.songId = songId;
		this.type = type;
		this.index = index;
		this.text = text;
		this.order = order;
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
		this.index = part.index;
		this.text = part.text;
		this.order = part.order;
		this.fontSize = part.fontSize;
	}

	/**
	 * Returns true if this song part is a new song part (that has not been saved).
	 * @return boolean
	 */
	public boolean isNew() {
		return this.id == SongPart.NEW_SONG_PART_ID;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SongPart o) {
		// sort by order first
		int diff = this.order - o.order;
		if (diff == 0) {
			// sort by type next
			diff = this.type.compareTo(o.type);
			if (diff == 0) {
				// sort by index next
				diff = this.index - o.index;
				if (diff == 0) {
					// sort by text next
					diff = this.text.compareTo(o.text);
				}
			}
		}
		return diff;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SongPart) {
			SongPart part = (SongPart)obj;
			if (part.id == this.id) {
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
		sb.append("SongPart[Id=").append(this.id)
		  .append("|SongId=").append(this.songId)
		  .append("|Type=").append(this.type)
		  .append("|Index=").append(this.index)
		  .append("|Text=").append(this.text)
		  .append("|Order=").append(this.order)
		  .append("|FontSize=").append(this.fontSize)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the name for this part.
	 * @return String
	 */
	public String getName() {
		SongPartType type = this.type;
		if (type == null) {
			type = SongPartType.OTHER;
		}
		return MessageFormat.format(Messages.getString("song.part.name.pattern"), type.getName(), this.index);
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
	 * Returns the song part index.
	 * @return int
	 */
	public int getIndex() {
		return this.index;
	}
	
	/**
	 * Sets the song part index.
	 * @param partIndex the song part index
	 */
	public void setIndex(int partIndex) {
		this.index = partIndex;
	}
	
	/**
	 * Returns the part order.
	 * @return int
	 */
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * Sets the part order
	 * @param order the order
	 */
	public void setOrder(int order) {
		this.order = order;
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
