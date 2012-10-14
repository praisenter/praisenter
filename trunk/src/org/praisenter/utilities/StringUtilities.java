package org.praisenter.utilities;

/**
 * Class containing utilities for string manipulation.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class StringUtilities {
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
