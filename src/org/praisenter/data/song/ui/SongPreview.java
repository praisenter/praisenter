package org.praisenter.data.song.ui;

import org.praisenter.data.song.Song;
import org.praisenter.slide.SongSlideTemplate;

/**
 * Stores the necessary information for a song preview.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
public class SongPreview {
	/** The song */
	protected Song song;
	
	/** The {@link SongSlideTemplate} to use */
	protected SongSlideTemplate template;
	
	/**
	 * Default constructor.
	 */
	public SongPreview() {}
	
	/**
	 * Full constructor.
	 * @param song the song
	 * @param template the {@link SongSlideTemplate} to use
	 */
	public SongPreview(Song song, SongSlideTemplate template) {
		this.song = song;
		this.template = template;
	}
	
	/**
	 * Returns the song.
	 * @return {@link Song}
	 */
	public Song getSong() {
		return this.song;
	}
	
	/**
	 * Returns the {@link SongSlideTemplate} to use.
	 * @return {@link SongSlideTemplate}
	 */
	public SongSlideTemplate getTemplate() {
		return this.template;
	}
}
