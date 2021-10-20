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
 * Represents a song book entry.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Songbook {
	/** The book name */
	@JsonProperty
	String name;
	
	/** The entry */
	@JsonProperty
	String entry;
	
	/**
	 * Full constructor.
	 * @param name the name of the song book
	 * @param entry the entry in the song book
	 */
	@JsonCreator
	public Songbook(
			@JsonProperty("name") String name, 
			@JsonProperty("entry") String entry) {
		this.name = name;
		this.entry = entry;
	}
	
	/**
	 * Copy constructor.
	 * @param songbook the song book to copy
	 */
	public Songbook(Songbook songbook) {
		this.name = songbook.name;
		this.entry = songbook.entry;
	}
	
	/**
	 * Returns a deep copy of this song book.
	 * @return {@link Songbook}
	 */
	public Songbook copy() {
		return new Songbook(this);
	}
	
	/**
	 * Returns the book name.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the book name.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the entry (page number for example).
	 * @return String
	 */
	public String getEntry() {
		return this.entry;
	}

	/**
	 * Sets the entry.
	 * @param entry the entry
	 */
	public void setEntry(String entry) {
		this.entry = entry;
	}
}
