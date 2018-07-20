package org.praisenter.data.search;

public class SearchTextMatch {
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
	public SearchTextMatch(String field, String value, String matchedText) {
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
