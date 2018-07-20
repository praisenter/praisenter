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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a chapter of the bible.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class Chapter implements Comparable<Chapter>, Serializable {
	/** The serialization id */
	private static final long serialVersionUID = 9220452361686192242L;

	/** The chapter number */
	@JsonProperty
	short number;
	
	/** The verses in this chapter */
	@JsonProperty
	final List<Verse> verses;
	
	/**
	 * Default constructor.
	 */
	public Chapter() {
		this((short)0);
	}
	
	/**
	 * Optional constructor.
	 * @param number the chapter number
	 */
	public Chapter(short number) {
		this(number, null);
	}
	
	/**
	 * Full constructor.
	 * @param number the chapter number
	 * @param verses the verses
	 */
	public Chapter(short number, List<Verse> verses) {
		this.number = number;
		this.verses = verses != null ? verses : new ArrayList<Verse>();
	}
	
	/**
	 * Copy constructor.
	 * @param chapter the chapter to copy
	 */
	public Chapter(Chapter chapter) {
		this.number = chapter.number;
		this.verses = new ArrayList<Verse>();
		
		for (Verse verse : chapter.verses) {
			this.verses.add(verse.copy());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Chapter c) {
		if (c == null) return 1;
		return this.number - c.number;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.valueOf(this.number);
	}
	
	/**
	 * Returns the maximum verse number in this chapter.
	 * @return short
	 */
	public short getMaxVerseNumber() {
		short max = 0;
		for (Verse verse : this.verses) {
			max = max < verse.number ? verse.number : max;
		}
		return max;
	}
	
	/**
	 * Returns the last verse of this chapter.
	 * @return {@link Verse}
	 */
	public Verse getLastVerse() {
		if (this.verses.isEmpty()) {
			return null;
		}
		return this.verses.get(this.verses.size() - 1);
	}

	/**
	 * Returns the specified verse of this chapter or null if not present.
	 * @param verse the verse number
	 * @return {@link Verse}
	 */
	public Verse getVerse(short verse) {
		if (this.verses.isEmpty()) {
			return null;
		}
		for (short i = 0; i < this.verses.size(); i++) {
			Verse v = this.verses.get(i);
			if (v.getNumber() == verse) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Returns a deep copy of this chapter
	 * @return {@link Chapter}
	 */
	public Chapter copy() {
		return new Chapter(this);
	}
	
	/**
	 * Returns the chapter number.
	 * @return short
	 */
	public short getNumber() {
		return this.number;
	}

	/**
	 * Sets the chapter number.
	 * @param number the chapter number
	 */
	public void setNumber(short number) {
		this.number = number;
	}

	/**
	 * Returns the verses.
	 * @return List&lt;{@link Verse}&gt;
	 */
	public List<Verse> getVerses() {
		return this.verses;
	}
}
