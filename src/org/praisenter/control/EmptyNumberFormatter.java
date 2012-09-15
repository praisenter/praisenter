package org.praisenter.control;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

/**
 * Represents a NumberFormatter class that will accept null or blank input.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EmptyNumberFormatter extends NumberFormatter {
	/** The version id */
	private static final long serialVersionUID = 6190545681135671005L;
	
	/**
	 * Minimal constructor.
	 * @param format the format
	 */
	public EmptyNumberFormatter(NumberFormat format) {
		super(format);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.text.InternationalFormatter#stringToValue(java.lang.String)
	 */
	@Override
	public Object stringToValue(String text) throws ParseException {
		if (text == null || text.length() == 0) {
			return null;
		}
		return super.stringToValue(text);
	}
}
