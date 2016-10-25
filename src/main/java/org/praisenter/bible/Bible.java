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

import org.praisenter.Constants;

/**
 * Represents a Bible translation.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "bible")
@XmlAccessorType(XmlAccessType.NONE)
public final class Bible implements Comparable<Bible> {
	/** The current version number */
	public static final int CURRENT_VERSION = 1;
	
	/** The bible id */
	@XmlAttribute(name = "id", required = false)
	final UUID id;

	/** The format (for format identification only) */
	@XmlAttribute(name = "format", required = false)
	final String format = Constants.FORMAT_NAME;
	
	/** The version number */
	@XmlAttribute(name = "version", required = false)
	final int version = CURRENT_VERSION;
	
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

	/** The date the bible was imported */
	@XmlAttribute(name = "lastModifiedDate", required = false)
	Date lastModifiedDate;
	
	/** The copyright */
	@XmlElement(name = "copyright", required = false)
	String copyright;
	
	/** True if a warning was found during import */
	@XmlAttribute(name = "hadImportWarning", required = false)
	boolean hadImportWarning;
	
	/** The books in this bible */
	@XmlElement(name = "book", required = false)
	@XmlElementWrapper(name = "books", required = false)
	final List<Book> books;
	
	/** Any notes */
	@XmlElement(name = "notes", required = false)
	String notes;
	
	/**
	 * Default constructor.
	 */
	public Bible() {
		this(null,
			 null,
			 null,
			 new Date(),
			 null,
			 null,
			 false,
			 null);
	}
	
	/**
	 * Full constructor.
	 * @param name the bible name
	 * @param language the bible language
	 * @param source the bible source
	 * @param importDate the import date
	 * @param copyright the copyright (if any)
	 * @param notes bible notes
	 * @param hadImportWarning true if a warning occurred during import
	 * @param books the books for this bible
	 */
	public Bible(
		  String name, 
		  String language, 
		  String source, 
		  Date importDate,
		  String copyright,
		  String notes,
		  boolean hadImportWarning,
		  List<Book> books) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.language = language;
		this.source = source;
		this.importDate = importDate;
		this.lastModifiedDate = importDate;
		this.copyright = copyright;
		this.notes = notes;
		this.hadImportWarning = hadImportWarning;
		this.books = books != null ? books : new ArrayList<Book>();
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Bible) {
			Bible bbl = (Bible)obj;
			if (bbl.id.equals(this.id)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the maximum book number in this bible.
	 * @return short
	 */
	public short getMaxBookNumber() {
		short max = 0;
		for (Book book : this.books) {
			max = max < book.number ? book.number : max;
		}
		return max;
	}
	
	/**
	 * Makes a deep copy of the this bible.
	 * <p>
	 * The new bible will have it's own id so the returned bible
	 * and this bible will not be equal according to the {@link #equals(Object)} method.
	 * <p>
	 * The new bible will have a null path as well.
	 * @return {@link Bible}
	 */
	public Bible copy() {
		Bible bible = new Bible();
		bible.copyright = this.copyright;
		bible.hadImportWarning = false;
		bible.importDate = null;
		bible.lastModifiedDate = new Date();
		bible.language = this.language;
		bible.name = this.name;
		bible.notes = this.notes;
		bible.path = null;
		bible.source = this.source;
		
		for (Book book : this.books) {
			bible.books.add(book.copy());
		}
		
		return bible;
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
	 * Returns the last modified date.
	 * @return Date
	 */
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
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
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return this.notes;
	}

	/**
	 * Sets the notes.
	 * @param notes the notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Returns the total verse count.
	 * @return int
	 */
	public int getVerseCount() {
		int n = 0;
		if (this.books != null) {
			for (Book book : this.books) {
				if (book.chapters != null) {
					for (Chapter chapter : book.chapters) {
						n += chapter.verses.size();
					}
				}
			}
		}
		return n;
	}
	
	/**
	 * Returns the number of books.
	 * @return int
	 */
	public int getBookCount() {
		if (this.books != null) {
			return this.books.size();
		}
		return 0;
	}

	/**
	 * Returns the books of this bible.
	 * @return List&lt;{@link Book}&gt;
	 */
	public List<Book> getBooks() {
		return this.books;
	}
	
	/**
	 * Returns true if a warning occurred during import of
	 * this bible.
	 * @return boolean
	 */
	public boolean hadImportWarning() {
		return this.hadImportWarning;
	}
}
