package org.praisenter.data.song;

import java.util.Collections;
import java.util.List;

import org.praisenter.data.search.SearchTextMatch;

public final class SongSearchResult implements Comparable<SongSearchResult> {
	/** The song */
	private final ReadOnlySong song;
	
	/** The lyrics */
	private final ReadOnlyLyrics lyrics;
	
	/** The section */
	private final ReadOnlySection section;
	
	/** The matched text */
	private final List<SearchTextMatch> matches;
	
	/** The matching score */
	private final float score;
	
	public SongSearchResult(ReadOnlySong song, ReadOnlyLyrics lyrics, ReadOnlySection section, List<SearchTextMatch> matches, float score) {
		this.song = song;
		this.lyrics = lyrics;
		this.section = section;
		this.matches = matches;
		this.score = score;
	}
	
	@Override
	public int compareTo(SongSearchResult o) {
		if (o == null) return -1;
		if (o == this) return 0;
		
		int diff = this.song.getId().compareTo(o.song.getId());
		if (diff == 0) {
			diff = this.lyrics.getId().compareTo(o.lyrics.getId());
			if (diff == 0) {
				String n1 = this.section.getName();
				String n2 = o.section.getName();
				diff = (n1 != null ? n1 : "").compareTo(n2);
			}
		}
		
		return diff;
	}
	
	public ReadOnlySong getSong() {
		return this.song;
	}
	
	public ReadOnlyLyrics getLyrics() {
		return this.lyrics;
	}

	public ReadOnlySection getSection() {
		return this.section;
	}

	public List<SearchTextMatch> getMatches() {
		return Collections.unmodifiableList(matches);
	}

	public float getScore() {
		return score;
	}

}
