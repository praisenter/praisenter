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
package org.praisenter.utility;

/**
 * Class containing string manipulation methods.
 * @author William Bittle
 * @see <a href="http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars">Cross Platform FileNames</a>
 */
public final class StringManipulator {
	/** Hidden default constructor */
	private StringManipulator() {}
	
	/**
	 * Returns true if the given string is null or if it only
	 * contains whitespace.
	 * @param str the string
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().length() <= 0;
	}
	
	/**
	 * Strips invalid characters from the given string based on
	 * a cross platform list of white-listed characters (basically
	 * any letter or digit, including unicode codepoints).
	 * <p>
	 * This method will replace those characters with '-'.
	 * @param str the string
	 * @return String
	 */
	public static String toFileName(String str) {
		return toFileName(str, '-');
	}
	
	/**
	 * Strips invalid characters from the given string based on
	 * a cross platform list of white-listed characters (basically
	 * any letter or digit, including unicode codepoints).
	 * @param str the string
	 * @param replacement the string to replace invalid characters
	 * @return String
	 */
	public static String toFileName(String str, char replacement) {
		StringBuilder filename = new StringBuilder();

		int[] codes = str.codePoints().toArray();
		for (int c : codes) {
			if (Character.isLetterOrDigit(c)) {
				filename.appendCodePoint(c);
			} else {
				filename.append(replacement);
			}
		}
		
		// condense consecutive dashes
		String name = filename.toString().replaceAll("[" + replacement + "]+", Character.toString(replacement));
		
		return name;
	}
}
