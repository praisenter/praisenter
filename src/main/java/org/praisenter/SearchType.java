package org.praisenter;

public enum SearchType {
	/** The results must contain all the words */
	ALL_WORDS,
	
	/** The results must contain at least one of the words */
	ANY_WORD,
	
	/** The results must contain the phrase */
	PHRASE,
	
	/** The results must be at a specified location (for example Luke 1:1) */
	LOCATION
}
