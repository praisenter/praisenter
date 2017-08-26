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
package org.praisenter.song;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a verse of a song.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Verse {
	/** Notifies that the font size is not overridden */
	public static final int USE_TEMPLATE_FONT_SIZE = -1;
	
	/** The verse name */
	@JsonProperty
	String name;
	
	/** The verse font size */
	@JsonProperty
	int fontSize;

	/** The verse text */
	@JsonProperty
	String text;
	
	/**
	 * Default constructor.
	 */
	public Verse() {
		this.name = "c1";
		this.text = null;
		this.fontSize = USE_TEMPLATE_FONT_SIZE;
	}
	
	/**
	 * Copy constructor.
	 * @param verse the verse to copy
	 */
	public Verse(Verse verse) {
		this.fontSize = verse.fontSize;
		this.name = verse.name;
		this.text = verse.text;
	}
	
	/**
	 * Returns a deep copy of this verse.
	 * @return {@link Verse}
	 */
	public Verse copy() {
		return new Verse(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + "=" + this.text;
	}
	
	/**
	 * Sets the name given the type, number and part.
	 * @param type the type (c, v, e, etc.)
	 * @param number the number 1-n
	 * @param part the part (a, b, c, etc.)
	 */
	public void setName(String type, int number, String part) {
		this.name = 
				(type == null || type.length() == 0 ? "c" : type) + 
				(number > 0 ? number : "") + 
				(part == null || part.length() == 0 ? "" : part);
	}
	
	/**
	 * Returns the verse name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the verse name.
	 * @param name the verse name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the font size.
	 * @return int
	 */
	public int getFontSize() {
		return this.fontSize;
	}

	/**
	 * Sets the font size.
	 * @param fontSize the font size
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	/**
	 * Returns the verse text.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Sets the verse text.
	 * @param text the text
	 */
	public void setText(String text) {
		this.text = text;
	}
}
