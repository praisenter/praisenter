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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an author of a song.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Author {
	/** Author type = lyrics */
	public static final String TYPE_LYRICS = "lyrics";
	
	/** Author type = music */
	public static final String TYPE_MUSIC = "music";
	
	/** Author type = translation */
	public static final String TYPE_TRANSLATION = "translation";

	/** The author type */
	@JsonProperty
	String type;
	
	/** The author's name */
	@JsonProperty
	String name;

	/**
	 * Creates a new author
	 * @param type the author type
	 * @param name the author name
	 */
	@JsonCreator
	public Author(
			@JsonProperty("type") String type, 
			@JsonProperty("name") String name) {
		this.type = type;
		this.name = name;
	}

	/**
	 * Copy constructor.
	 * @param author the author to copy
	 */
	public Author(Author author) {
		this.name = author.name;
		this.type = author.type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/**
	 * Returns a deep copy of this author.
	 * @return {@link Author}
	 */
	public Author copy() {
		return new Author(this);
	}
	
	/**
	 * Returns the author type.
	 * @return String
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type.
	 * @param type the author type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the name of the author.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of the author.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
