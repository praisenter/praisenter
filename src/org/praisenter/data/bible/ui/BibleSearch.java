/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.data.bible.ui;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleSearchType;

/**
 * Represents a text search in a {@link Bible}.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class BibleSearch {
	/** The {@link Bible} to search */
	private Bible bible;
	
	/** The text to search */
	private String text;
	
	/** True if the apocrypha should be included in the search (if available) */
	private boolean apocryphaIncluded;
	
	/** The search type */
	private BibleSearchType type;
	
	/** The search callback */
	private BibleSearchCallback callback;
	
	/**
	 * Minimal constructor.
	 * @param bible the {@link Bible} to search
	 * @param text the text to search for
	 * @param apocryphaIncluded true if the apocrypha should be included in the search (if available)
	 * @param type the search type
	 * @param callback the code to run after the search has completed
	 */
	public BibleSearch(Bible bible, String text, boolean apocryphaIncluded, 
			BibleSearchType type, BibleSearchCallback callback) {
		this.bible = bible;
		this.text = text;
		this.apocryphaIncluded = apocryphaIncluded;
		this.type = type;
		this.callback = callback;
	}
	
	/**
	 * Returns the {@link Bible} to search.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible;
	}
	
	/**
	 * Returns the text to search for.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Returns true if the apocrypha should be included in the
	 * search (if available in the current bible).
	 * @return boolean
	 */
	public boolean isApocryphaIncluded() {
		return this.apocryphaIncluded;
	}
	
	/**
	 * Returns the bible search type.
	 * @return {@link BibleSearchType}
	 */
	public BibleSearchType getType() {
		return this.type;
	}
	
	/**
	 * Returns the code to run after the search is completed.
	 * @return {@link BibleSearchCallback}
	 */
	public BibleSearchCallback getCallback() {
		return this.callback;
	}
}
