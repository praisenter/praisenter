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
package org.praisenter.common.utilities;

/**
 * Class containing utilities for string manipulation.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public final class StringUtilities {
	/** Hidden default constructor */
	private StringUtilities() {}
	
	/**
	 * Wraps the given text in HTML and inserts &lt;br&gt; tags in whitespace areas
	 * every interval characters. 
	 * @param text the text to add line breaks to
	 * @param interval the number of characters before inserting a line break
	 * @return String
	 */
	public static final String addLineBreaksAtInterval(String text, int interval) {
		return addLineBreaksAtInterval(text, interval, false);
	}
	
	/**
	 * Wraps the given text in HTML and inserts &lt;br&gt; tags in whitespace areas
	 * every interval characters. 
	 * @param text the text to add line breaks to
	 * @param interval the number of characters before inserting a line break
	 * @param breakAtNewLines true if the text should be broken up at new line characters
	 * @return String
	 */
	public static final String addLineBreaksAtInterval(String text, int interval, boolean breakAtNewLines) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		// make sure we replace any os type line break with a standard one
		text = text.replaceAll("(\\r\\n)|(\\r)", "\n");
		int n = 0;
		for (int i = 0; i < text.length(); i++) {
			// get the character
			char c = text.charAt(i);
			// add the char
			sb.append(c);
			// see if the current char is whitespace
			if (Character.isWhitespace(c)) {
				if (breakAtNewLines && c == '\n') {
					sb.append("<br />");
					n = 0;
				} else {
					// if so, then check the interval count
					if (n >= interval) {
						// add a line break
						sb.append("<br />");
						n = 0;
					}
				}
			}
		    n++;
		}
		sb.append("</html>");
		return sb.toString();
	}
}
