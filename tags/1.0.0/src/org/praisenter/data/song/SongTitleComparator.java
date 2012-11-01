package org.praisenter.data.song;

import java.util.Comparator;

/**
 * Comparator used to sort a collection of {@link Song}s by their title.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class SongTitleComparator implements Comparator<Song> {
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Song song1, Song song2) {
		if (song1.title == null) return -1;
		if (song2.title == null) return 1;
		if (song1 == song2) return 0;
		return song1.title.compareTo(song2.title);
	}
}
