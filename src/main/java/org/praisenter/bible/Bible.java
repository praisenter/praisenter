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

import java.util.Date;

/**
 * Represents a Bible translation.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Bible implements Comparable<Bible> {
	/** The bible id */
	final int id;
	
	/** The name of the bible */
	final String name;
	
	/** The language the bible is in (using ISO 639-2 and ISO 639-3 codes along with micro variants (but all three code it seems)) */
	final String language;

	/** The source for the bible's contents */
	final String source;
	
	/** The date the bible was imported */
	final Date importDate;
	
	/**
	 * Full constructor.
	 * @param id the bible id
	 * @param name the bible name
	 * @param language the bible language
	 * @param source the bible source
	 * @param importDate the import date
	 */
	Bible(int id, String name, String language, String source, Date importDate) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.source = source;
		this.importDate = importDate;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Bible) {
			Bible other = (Bible)obj;
			if (this.id == other.id) {
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
		sb.append("Bible[Id=").append(this.id)
		  .append("|Name=").append(this.name)
		  .append("|Language=").append(this.language)
		  .append("|Source=").append(this.source)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Bible o) {
		if (o == null) return 1;
		// sort by id
		return o.id - this.id;
	}
	
	/**
	 * Returns the id for this {@link Bible}.
	 * @return int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns the name of this {@link Bible}.
	 * @return String
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the language of this {@link Bible}.
	 * <p>
	 * The language code is not the ISO language code.
	 * @return String
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Returns the source for this {@link Bible}'s contents.
	 * @return String
	 */
	public String getSource() {
		return this.source;
	}
}
