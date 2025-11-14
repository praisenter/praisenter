package org.praisenter.data.song;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.praisenter.data.search.SearchTextMatch;

public final class SongSearchResult implements Comparable<SongSearchResult> {
	/** The song */
	private final ReadOnlySong song;

	/** The matched lyrics (may be null) */
	private final ReadOnlyLyrics lyrics;
	
	/** The matched section (may be null) */
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
			UUID tl = this.lyrics != null ? this.lyrics.getId() : null;
			UUID ol = o.lyrics != null ? o.lyrics.getId() : null;
			
			if (tl == null && ol != null) {
				return -1;
			} else if (tl != null && ol == null) {
				return 1;
			} else if (tl != null && ol != null) {
				diff = tl.compareTo(ol);
			} else {
				diff = 0;
			}
			
			if (diff == 0) {
				UUID ts = this.section != null ? this.section.getId() : null;
				UUID os = o.section != null ? o.section.getId() : null;
				
				if (ts == null && os != null) {
					return -1;
				} else if (ts != null && os == null) {
					return 1;
				} else if (ts != null && os != null) {
					diff = ts.compareTo(os);
				} else {
					diff = 0;
				}
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
