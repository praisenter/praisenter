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
 * A matched section of text for a given lucene field for a lucene search.
 * @author William Bittle
 * @version 3.0.0
 */
public final class BibleSearchMatch {
	/** The field */
	final String field;
	
	/** The field value */
	final String value;
	
	/** The matched part of the value with highlight indicators */
	final String matchedText;
	
	/**
	 * Full constructor.
	 * @param field the lucene field
	 * @param value the value of the field
	 * @param matchedText the matched text and surrounding text
	 */
	public BibleSearchMatch(String field, String value, String matchedText) {
		this.field = field;
		this.value = value;
		this.matchedText = matchedText;
	}
	
	/**
	 * The field that matched.
	 * @return String
	 */
	public String getField() {
		return field;
	}

	/**
	 * The full value of the field that was matched.
	 * @return String
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns the text around a match with the matched text
	 * highlighted using &lt;b&gt; tags.
	 * @return String
	 */
	public String getMatchedText() {
		return matchedText;
	}
}
