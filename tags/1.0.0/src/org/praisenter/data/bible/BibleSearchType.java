package org.praisenter.data.bible;

/**
 * Enumerations of bible search types.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public enum BibleSearchType {
	/** Searches for any of the words in the search criteria */
	ANY_WORD,
	
	/** Searches for all the words in the search criteria */
	ALL_WORDS,
	
	/** Searches for the exact phrase of the search criteria */
	PHRASE,
	
	/** Searches for the location of the search criteria (book, chapter, verse) */
	LOCATION
}
