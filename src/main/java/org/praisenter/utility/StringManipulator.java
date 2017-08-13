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

import java.util.UUID;

import org.praisenter.Constants;

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
	 * Invalid codepoints are replaced by the given replacement character or empty string if
	 * replacement is null.
	 * @param string the string
	 * @param replacement the string to replace invalid characters
	 * @return String
	 */
	private static String toFileName(String string, String replacement) {
		if (string == null) return null;
		
		StringBuilder filename = new StringBuilder();

		int[] codes = string.codePoints().toArray();
		for (int c : codes) {
			if (Character.isLetterOrDigit(c)) {
				filename.appendCodePoint(c);
			} else {
				filename.append(replacement);
			}
		}
		
		String name = filename.toString();
		// condense consecutive replacement characters with one
		if (replacement != null && replacement.length() > 0) {
			name = name.replaceAll("[" + replacement + "]+", replacement);
		}
		
		return name;
	}
	
	/**
	 * Strips invalid characters from the given string based on
	 * a cross platform white-list of characters (basically
	 * any letter or digit, including unicode codepoints).
	 * <p>
	 * Invalid codepoints are replaced by the given replacement character or empty string if
	 * replacement is null.
	 * <p>
	 * If the given string or the stripped string is null or empty, the given
	 * default string is used instead. The default string is assumed to be
	 * a valid file name already.
	 * <p>
	 * This method will also truncate the file name to the given maxLength.
	 * @param string the string
	 * @param defaultString the default string if string is null or empty or contains all invalid characters
	 * @param maxLength the maximum length of the string
	 * @param replacement the string to replace invalid characters
	 * @return String
	 */
	public static String toFileName(String string, String defaultString, int maxLength, String replacement) {
		// strip invalid characters
		string = toFileName(string, replacement == null ? "" : replacement);
		
		// is it null or empty?
		if (string == null || string.length() == 0) {
			string = defaultString;
		}
		
		// is it just the invalid character replacement string?
		if (replacement != null && replacement.equals(string)) {
			string = defaultString;
		}
		
		// truncate the name to certain length
		if (string.length() > maxLength) {
			string = string.substring(0, Math.min(string.length() - 1, maxLength));
		}
		
		return string;
	}
	
	/**
	 * Strips invalid characters from the given string based on
	 * a cross platform white-list of characters (basically
	 * any letter or digit, including unicode codepoints).
	 * <p>
	 * Invalid codepoints are replaced by the given replacement character or empty string if
	 * replacement is null.
	 * <p>
	 * If the given string or the stripped string is null or empty, the given
	 * id will be used.
	 * <p>
	 * This method will also truncate the file name to the given maxLength.
	 * @param string the string
	 * @param id a unique identifier
	 * @param maxLength the maximum length of the string
	 * @param replacement the string to replace invalid characters
	 * @return String
	 */
	public static String toFileName(String string, UUID id, int maxLength, String replacement) {
		return StringManipulator.toFileName(
				string,
				StringManipulator.toFileName(id),
				maxLength,
				replacement);
	}
	
	/**
	 * Strips invalid characters from the given string based on
	 * a cross platform white-list of characters (basically
	 * any letter or digit, including unicode codepoints).
	 * <p>
	 * If the given string or the stripped string is null or empty, the given
	 * id will be used.
	 * <p>
	 * This method will also truncate the file name to the given maxLength.
	 * @param string the string
	 * @param id a unique identifier
	 * @param maxLength the maximum length of the string
	 * @return String
	 */
	public static String toFileName(String string, UUID id, int maxLength) {
		return StringManipulator.toFileName(
				string,
				StringManipulator.toFileName(id),
				maxLength,
				null);
	}
	
	/**
	 * Strips invalid characters from the given string based on
	 * a cross platform white-list of characters (basically
	 * any letter or digit, including unicode codepoints).
	 * <p>
	 * If the given string or the stripped string is null or empty, the given
	 * id will be used.
	 * @param string the string
	 * @param id a unique identifier
	 * @return String
	 */
	public static String toFileName(String string, UUID id) {
		return StringManipulator.toFileName(
				string,
				StringManipulator.toFileName(id),
				Constants.MAX_FILE_NAME_CODEPOINTS,
				null);
	}
	
	/**
	 * Converts the given UUID to a string and strips "-" characters.
	 * @param uuid the UUID
	 * @return String
	 */
	public static String toFileName(UUID uuid) {
		return uuid.toString().toLowerCase().replaceAll("-", "");
	}
}
