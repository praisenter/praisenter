package org.praisenter.data.song.ui;

/**
 * Represents a text search in a song list.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongSearch implements Cloneable {
	/** The text to search */
	private String text;
	
	/** The search callback */
	private SongSearchThread.Callback callback;
	
	/**
	 * Minimal constructor.
	 * @param text the text to search for
	 * @param callback the code to run after the search has completed
	 */
	public SongSearch(String text, SongSearchThread.Callback callback) {
		this.text = text;
		this.callback = callback;
	}
	
	/**
	 * Returns the text to search for.
	 * @return String
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Returns the code to run after the search is completed.
	 * @return {@link SongSearchThread.Callback}
	 */
	public SongSearchThread.Callback getCallback() {
		return this.callback;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SongSearch clone() {
		return new SongSearch(this.text, this.callback);
	}
}
