package org.praisenter.data.song;

import java.util.Collections;
import java.util.List;

import org.praisenter.data.search.SearchTextMatch;

public final class SongSearchResult implements Comparable<SongSearchResult> {
	/** The song */
	private final ReadOnlySong song;
	
	/** The matched text */
	private final List<SearchTextMatch> matches;
	
	/** The matching score */
	private final float score;
	
	public SongSearchResult(ReadOnlySong song, List<SearchTextMatch> matches, float score) {
		this.song = song;
		this.matches = matches;
		this.score = score;
	}
	
	@Override
	public int compareTo(SongSearchResult o) {
		if (o == null) return -1;
		if (o == this) return 0;
		
		return this.song.getId().compareTo(o.song.getId());
	}
	
	public ReadOnlySong getSong() {
		return this.song;
	}
	
	public List<SearchTextMatch> getMatches() {
		return Collections.unmodifiableList(matches);
	}

	public float getScore() {
		return score;
	}

}
