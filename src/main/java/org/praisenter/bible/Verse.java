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

/**
 * Represents a {@link Verse} of the {@link Bible}.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Verse implements Comparable<Verse> {
	/** The {@link Bible} the verse is in */
	final Bible bible;

	/** The {@link Book} the verse is in */
	final Book book;
	
	/** The verse id */
	final int id;
	
	/** The chapter number */
	final int chapter;
	
	/** The verse number */
	final int verse;
	
	/** The sub verse number */
	final int subVerse;
	
	/** The verse order */
	final int order;
	
	/** The verse text */
	final String text;

	/**
	 * Full constructor.
	 * @param bible the bible containing the verse
	 * @param book the book containing the verse
	 * @param id the verse id
	 * @param chapter the chapter number
	 * @param verse the verse number
	 * @param subVerse the sub verse number
	 * @param order the verse order
	 * @param text the verse text
	 */
	Verse(Bible bible, Book book, int id, int chapter, int verse, int subVerse, int order, String text) {
		this.bible = bible;
		this.book = book;
		this.id = id;
		this.chapter = chapter;
		this.verse = verse;
		this.subVerse = subVerse;
		this.order = order;
		this.text = text;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Verse) {
			Verse other = (Verse)obj;
			if (other.id == this.id) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the given verse is the same
	 * verse as this verse.
	 * <p>
	 * This does a reference comparison so that a verse
	 * from one bible to another will return true.
	 * @param verse the verse
	 * @return boolean
	 */
	public boolean isSameVerse(Verse verse) {
		if (verse == null) return false;
		if (verse == this) return true;
		if (verse.book.code.equals(this.book.code) &&
			verse.chapter == this.chapter &&
			verse.verse == this.verse) {
			return true;
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
		sb.append("Verse[Bible=").append(this.bible.getName())
		  .append("|Book=").append(this.book.getName())
		  .append("|Id=").append(this.id)
		  .append("|Chapter=").append(this.chapter)
		  .append("|Verse=").append(this.verse)
		  .append("|SubVerse=").append(this.subVerse)
		  .append("|Order=").append(this.order)
		  .append("|Text=").append(this.text)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Verse o) {
		if (o == null) return 1;
		return this.id - o.id;
	}
	
	/**
	 * Returns the {@link Bible} that contains this {@link Verse}.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible;
	}
	
	/**
	 * Returns the verse id for this {@link Verse}.
	 * @return int
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Returns the {@link Book} this {@link Verse} is contained in.
	 * @return {@link Book}
	 */
	public Book getBook() {
		return this.book;
	}
	
	/**
	 * Returns the chapter number this {@link Verse} is contained in.
	 * @return int
	 */
	public int getChapter() {
		return this.chapter;
	}
	
	/**
	 * Returns the verse number of this {@link Verse}.
	 * @return int
	 */
	public int getVerse() {
		return this.verse;
	}
	
	/**
	 * Returns the sub verse number of this {@link Verse}.
	 * @return int
	 */
	public int getSubVerse() {
		return this.subVerse;
	}
	
	/**
	 * Returns the verse order.
	 * @return int
	 */
	public int getOrder() {
		return this.order;
	}
	
	/**
	 * Returns the text of this verse.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
}
