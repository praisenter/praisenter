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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a book in the Bible.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "book")
@XmlAccessorType(XmlAccessType.NONE)
public final class Book implements Comparable<Book>, Serializable {
	/** The serialization id */
	private static final long serialVersionUID = 8128626695273164169L;

	/** The book name */
	@XmlElement(name = "name", required = false)
	String name;

	/** The book number */
	@XmlAttribute(name = "number", required = false)
	short number;
	
	/** The chapters in this book */
	@XmlElement(name = "chapter", required = false)
	@XmlElementWrapper(name = "chapters", required = false)
	final List<Chapter> chapters;
	
	/**
	 * Default constructor.
	 */
	public Book() {
		this.name = null;
		this.number = 0;
		this.chapters = new ArrayList<Chapter>();
	}
	
	/**
	 * Optional constructor.
	 * @param name the book name
	 * @param number the book number
	 */
	public Book(String name, short number) {
		this(name, number, null);
	}
	
	/**
	 * Full constructor.
	 * @param name the book name
	 * @param number the book number
	 * @param chapters the list of chapters; or null
	 */
	public Book(String name, short number, List<Chapter> chapters) {
		this.name = name;
		this.number = number;
		this.chapters = chapters != null ? chapters : new ArrayList<Chapter>();
	}
	
	/**
	 * Copy constructor.
	 * @param book the book to copy
	 */
	public Book(Book book) {
		this.chapters = new ArrayList<Chapter>();
		this.name = book.name;
		this.number = book.number;

		for (Chapter chapter : book.chapters) {
			this.chapters.add(chapter.copy());
		}
	}
	
	/**
	 * Returns the maximum chapter number for this book.
	 * @return short
	 */
	public short getMaxChapterNumber() {
		short max = 0;
		for (Chapter chapter : this.chapters) {
			max = max < chapter.number ? chapter.number : max;
		}
		return max;
	}

	/**
	 * Returns the last chapter of this book.
	 * @return {@link Chapter}
	 */
	public Chapter getLastChapter() {
		if (this.chapters.isEmpty()) {
			return null;
		}
		return this.chapters.get(this.chapters.size() - 1);
	}
	
	/**
	 * Performs a deep copy of this book.
	 * @return {@link Book}
	 */
	public Book copy() {
		return new Book(this);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Book o) {
		if (o == null) return 1;
		return this.number - o.number;
	}

	/**
	 * Returns the specified verse or null if it doesn't exist.
	 * @param chapterNumber the chapter number
	 * @param verseNumber the verse number
	 * @return {@link LocatedVerse}
	 */
	public LocatedVerse getVerse(short chapterNumber, short verseNumber) {
		for (Chapter chapter : this.chapters) {
			if (chapter.number == chapterNumber) {
				for (Verse verse : chapter.verses) {
					if (verse.number == verseNumber) {
						return new LocatedVerse(null, this, chapter, verse);
					}
				}
			}
		}
		return null;
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
	 * Returns the number.
	 * @return short
	 */
	public short getNumber() {
		return this.number;
	}
	
	/**
	 * Sets the number.
	 * @param number the position
	 */
	public void setNumber(short number) {
		this.number = number;
	}
	
	/**
	 * Returns the list of chapters for this book.
	 * @return List&lt;{@link Chapter}&gt;
	 */
	public List<Chapter> getChapters() {
		return this.chapters;
	}
}
