package org.praisenter.data.bible;

/**
 * The bible has two testaments and some bibles include an apocrypha.
 * This enumeration represents that divisional structure.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Division {
	/** The old testament */
	OLD("O"), 
	
	/** The new testament */
	NEW("N"),
	
	/** The deuteroncanonical/apocryphal books */
	APOCRYPHA("A");
	
	/** The division code */
	private String code;
	
	/**
	 * Constructor.
	 * @param code the code
	 */
	private Division(String code) {
		this.code = code;
	}
	
	/**
	 * Returns the division code.
	 * @return String
	 */
	public String getCode() {
		return this.code;
	}
}
