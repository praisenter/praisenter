package org.praisenter.data.bible;

import java.util.Collections;
import java.util.List;

import org.praisenter.data.search.SearchTextMatch;

public final class BibleSearchResult {
	/** The bible */
	private final ReadOnlyBible bible;
	
	/** The book */
	private final ReadOnlyBook book;
	
	/** The chapter */
	private final ReadOnlyChapter chapter;
	
	/** The verse */
	private final ReadOnlyVerse verse;
	
	/** The matched text */
	private final List<SearchTextMatch> matches;
	
	/** The matching score */
	private final float score;
	
	public BibleSearchResult(ReadOnlyBible bible, ReadOnlyBook book, ReadOnlyChapter chapter, ReadOnlyVerse verse, List<SearchTextMatch> matches, float score) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
		this.matches = matches;
		this.score = score;
	}
	
	public ReadOnlyBible getBible() {
		return bible;
	}
	
	public ReadOnlyBook getBook() {
		return book;
	}

	public ReadOnlyChapter getChapter() {
		return chapter;
	}

	public ReadOnlyVerse getVerse() {
		return verse;
	}

	public List<SearchTextMatch> getMatches() {
		return Collections.unmodifiableList(matches);
	}

	public float getScore() {
		return score;
	}

}
