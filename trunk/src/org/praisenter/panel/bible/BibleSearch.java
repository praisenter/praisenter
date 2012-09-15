package org.praisenter.panel.bible;

import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleSearchType;

/**
 * Represents a text search in a {@link Bible}.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class BibleSearch implements Cloneable {
	/** The {@link Bible} to search */
	private Bible bible;
	
	/** The text to search */
	private String text;
	
	/** The search type */
	private BibleSearchType type;
	
	/** The search callback */
	private BibleSearchThread.Callback callback;
	
	/**
	 * Minimal constructor.
	 * @param bible the {@link Bible} to search
	 * @param text the text to search for
	 * @param type the search type
	 * @param callback the code to run after the search has completed
	 */
	public BibleSearch(Bible bible, String text, BibleSearchType type, BibleSearchThread.Callback callback) {
		this.bible = bible;
		this.text = text;
		this.type = type;
		this.callback = callback;
	}
	
	/**
	 * Returns the {@link Bible} to search.
	 * @return {@link Bible}
	 */
	public Bible getBible() {
		return this.bible;
	}
	
	/**
	 * Returns the text to search for.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Returns the bible search type.
	 * @return {@link BibleSearchType}
	 */
	public BibleSearchType getType() {
		return this.type;
	}
	
	/**
	 * Returns the code to run after the search is completed.
	 * @return {@link BibleSearchThread.Callback}
	 */
	public BibleSearchThread.Callback getCallback() {
		return this.callback;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public BibleSearch clone() {
		return new BibleSearch(this.bible, this.text, this.type, this.callback);
	}
}
