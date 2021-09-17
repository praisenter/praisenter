package org.praisenter.utility;

import java.util.UUID;

public final class StringManipulator {
	private StringManipulator() {}
	
	/**
	 * Returns true if the given string is null or if it only
	 * contains whitespace.
	 * @param str the string
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isBlank();
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
	 * @see <a href="http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars">Cross Platform FileNames</a>
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
	 * @see <a href="http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars">Cross Platform FileNames</a>
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
	 * @see <a href="http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars">Cross Platform FileNames</a>
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
	 * @see <a href="http://stackoverflow.com/questions/1155107/is-there-a-cross-platform-java-method-to-remove-filename-special-chars">Cross Platform FileNames</a>
	 */
	public static String toFileName(String string, UUID id, int maxLength) {
		return StringManipulator.toFileName(
				string,
				StringManipulator.toFileName(id),
				maxLength,
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
