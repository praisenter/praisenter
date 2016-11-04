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
 * Class representing verse in a given bible, book, and chapter.
 * @author William Bittle
 * @version 3.0.0
 */
public final class LocatedVerse {
	/** The bible */
	private final Bible bible;
	
	/** The book */
	private final Book book;
	
	/** The chapter */
	private final Chapter chapter;
	
	/** The verse */
	private final Verse verse;
	
	/**
	 * Full constructor.
	 * @param bible the bible
	 * @param book the book
	 * @param chapter the chapter
	 * @param verse the verse
	 */
	public LocatedVerse(Bible bible, Book book, Chapter chapter, Verse verse) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
	}

	/**
	 * Returns the bible.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible;
	}
	
	/**
	 * Returns the book.
	 * @return {@link Book}
	 */
	public Book getBook() {
		return this.book;
	}
	
	/**
	 * Returns the chapter.
	 * @return {@link Chapter}
	 */
	public Chapter getChapter() {
		return this.chapter;
	}
	
	/**
	 * Returns the verse.
	 * @return {@link Verse}
	 */
	public Verse getVerse() {
		return this.verse;
	}
}
