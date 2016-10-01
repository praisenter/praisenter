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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a Bible translation.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "bible")
@XmlAccessorType(XmlAccessType.NONE)
public final class Bible implements Comparable<Bible> {
	/** The bible id */
	@XmlAttribute(name = "id", required = false)
	final UUID id;
	
	/** The path to the XML document */
	Path path;
	
	/** The name of the bible */
	@XmlElement(name = "name", required = false)
	String name;
	
	/** The language the bible is in (using ISO 639-2 and ISO 639-3 codes along with micro variants (but all three code it seems)) */
	@XmlElement(name = "language", required = false)
	String language;

	/** The source for the bible's contents */
	@XmlElement(name = "source", required = false)
	String source;
	
	/** The date the bible was imported */
	@XmlAttribute(name = "importDate", required = false)
	Date importDate;
	
	/** The copyright */
	@XmlElement(name = "copyright", required = false)
	String copyright;
	
	/** The number of verses */
	int verseCount;
	
	/** True if a warning was found during import */
	@XmlAttribute(name = "hadImportWarning", required = false)
	boolean hadImportWarning;
	
	@XmlElement(name = "book", required = false)
	@XmlElementWrapper(name = "books", required = false)
	List<Book> books;
	
	Bible() {
		// for JAXB
		this.id = UUID.randomUUID();
		this.name = null;
		this.language = null;
		this.source = null;
		this.importDate = null;
		this.copyright = null;
		this.verseCount = 0;
		this.hadImportWarning = false;
		this.books = new ArrayList<Book>();
	}
	
	/**
	 * Full constructor.
	 * @param id the bible id
	 * @param name the bible name
	 * @param language the bible language
	 * @param source the bible source
	 * @param importDate the import date
	 * @param copyright the copyright (if any)
	 * @param verseCount the total number of verses
	 * @param hadImportWarning true if a warning occurred during import
	 * @param books the books for this bible
	 */
	public Bible(UUID id, 
		  String name, 
		  String language, 
		  String source, 
		  Date importDate,
		  String copyright,
		  int verseCount,
		  boolean hadImportWarning,
		  List<Book> books) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.source = source;
		this.importDate = importDate;
		this.copyright = copyright;
		this.verseCount = verseCount;
		this.hadImportWarning = hadImportWarning;
		this.books = books != null ? books : new ArrayList<Book>();
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
		return this.id.hashCode();
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
		return this.name.compareTo(o.name);
	}
	
	/**
	 * Returns the id for this {@link Bible}.
	 * @return int
	 */
	public UUID getId() {
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
	 * Sets the name of this {@link Bible}.
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Sets the language for this bible.
	 * @param language the language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * Returns the source for this {@link Bible}'s contents.
	 * @return String
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Sets the source of this bible.
	 * @param source the source
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns the import date.
	 * @return Date
	 */
	public Date getImportDate() {
		return this.importDate;
	}

	/**
	 * Sets the import date of this bible.
	 * @param importDate the import date
	 */
	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}

	/**
	 * Returns the copyright information (if any).
	 * @return String
	 */
	public String getCopyright() {
		return this.copyright;
	}

	/**
	 * Sets the copyright for this bible.
	 * @param copyright the copyright
	 */
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	/**
	 * Returns the total verse count.
	 * @return int
	 */
	public int getVerseCount() {
		return this.verseCount;
	}

	/**
	 * Sets the verse count for this bible.
	 * @param verseCount the verse count
	 */
	public void setVerseCount(int verseCount) {
		this.verseCount = verseCount;
	}
	
	/**
	 * Returns true if a warning occurred during import of
	 * this bible.
	 * @return boolean
	 */
	public boolean hadImportWarning() {
		return this.hadImportWarning;
	}

	/**
	 * Sets the import warning flag.
	 * @param hadImportWarning true if there was an error during import
	 */
	public void setHadImportWarning(boolean hadImportWarning) {
		this.hadImportWarning = hadImportWarning;
	}
}
