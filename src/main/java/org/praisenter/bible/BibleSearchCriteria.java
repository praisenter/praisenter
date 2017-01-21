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

import java.util.UUID;

import org.praisenter.SearchType;

/**
 * Represents the search criteria for a bible search.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleSearchCriteria {
	/** The default maximum results */
	public static final int MAXIMUM_RESULTS = 100;
	
	/** The bible id to search in; can be null */
	private final UUID bibleId;
	
	/** The book number to search in; can be null */
	private final Short bookNumber;
	
	/** The text to search for; cannot be null or empty */
	private final String text; 
	
	/** The search type; cannot be null */
	private final SearchType type;
	
	/** The maximum results */
	private final int maximumResults = MAXIMUM_RESULTS;
	
	/**
	 * Sets up a new set of search criteria.
	 * @param text the search text
	 * @param type the search type
	 */
	public BibleSearchCriteria(String text, SearchType type) {
		this(null, null, text, type);
	}
	
	/**
	 * Sets up a new set of search criteria.
	 * @param bibleId the id of the bible to search; or null to search all
	 * @param text the search text
	 * @param type the search type
	 */
	public BibleSearchCriteria(UUID bibleId, String text, SearchType type) {
		this(bibleId, null, text, type);
	}
	
	/**
	 * Sets up a new set of search criteria.
	 * @param bibleId the id of the bible to search; or null to search all
	 * @param bookNumber the book number of the book to search; or null to search all
	 * @param text the search text
	 * @param type the search type
	 */
	public BibleSearchCriteria(UUID bibleId, Short bookNumber, String text, SearchType type) {
		this.bibleId = bibleId;
		this.bookNumber = bookNumber;
		this.text = text;
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Text: '").append(text).append("'");
		sb.append(" SearchType: ").append(this.type);
		sb.append(" MaximumResults: ").append(this.maximumResults);
		if (this.bibleId != null) {
			sb.append(" BibleId: ").append(this.bibleId);
		}
		if (this.bookNumber != null) {
			sb.append(" Book: ").append(this.bookNumber);
		}
		return sb.toString();
	}
	
	/**
	 * Returns the bible id.
	 * @return UUID
	 */
	public UUID getBibleId() {
		return this.bibleId;
	}
	
	/**
	 * Returns the book number.
	 * @return Short
	 */
	public Short getBookNumber() {
		return this.bookNumber;
	}
	
	/**
	 * Returns the search text.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Returns the search type.
	 * @return {@link SearchType}
	 */
	public SearchType getType() {
		return this.type;
	}

	/**
	 * Returns the maximum results.
	 * @return int
	 */
	public int getMaximumResults() {
		return this.maximumResults;
	}
}
