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

import java.util.Collections;
import java.util.List;

/**
 * A lucene bible search result.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleSearchResult implements Comparable<BibleSearchResult> {
	/** The bible */
	final Bible bible;
	
	/** The book */
	final Book book;
	
	/** The chapter */
	final Chapter chapter;
	
	/** The verse */
	final Verse verse;
	
	/** The matched text */
	final List<BibleSearchMatch> matches;
	
	/** The matching score */
	final float score;
	
	/**
	 * Full constructor.
	 * @param score the score
	 * @param bible the bible
	 * @param book the book
	 * @param chapter the chapter
	 * @param verse the verse
	 * @param matches the matched text
	 */
	public BibleSearchResult(float score, Bible bible, Book book, Chapter chapter, Verse verse, List<BibleSearchMatch> matches) {
		this.score = score;
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
		this.matches = Collections.unmodifiableList(matches);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BibleSearchResult o) {
		int diff = bible.id.compareTo(o.bible.id);
		if (diff == 0) {
			diff = book.number - o.book.number;
			if (diff == 0) {
				diff = chapter.number - o.chapter.number;
				if (diff == 0) {
					return verse.number - o.verse.number;
				}
			}
		}
		return diff;
	}
	
	/**
	 * Returns the score for this match.
	 * @return float
	 */
	public float getScore() {
		return this.score;
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
	
	/**
	 * Returns an unmodifiable list of the matches.
	 * @return List&lt;{@link BibleSearchMatch}&gt;
	 */
	public List<BibleSearchMatch> getMatches() {
		return this.matches;
	}
}
