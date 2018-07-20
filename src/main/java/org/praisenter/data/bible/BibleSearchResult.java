package org.praisenter.data.bible;

import java.util.Collections;
import java.util.List;

import org.praisenter.data.search.SearchTextMatch;

public final class BibleSearchResult {
	/** The bible */
	private final ReadonlyBible bible;
	
	/** The book */
	private final ReadonlyBook book;
	
	/** The chapter */
	private final ReadonlyChapter chapter;
	
	/** The verse */
	private final ReadonlyVerse verse;
	
	/** The matched text */
	private final List<SearchTextMatch> matches;
	
	/** The matching score */
	private final float score;
	
	public BibleSearchResult(ReadonlyBible bible, ReadonlyBook book, ReadonlyChapter chapter, ReadonlyVerse verse, List<SearchTextMatch> matches, float score) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
		this.matches = matches;
		this.score = score;
	}
	
	public ReadonlyBible getBible() {
		return bible;
	}
	
	public ReadonlyBook getBook() {
		return book;
	}

	public ReadonlyChapter getChapter() {
		return chapter;
	}

	public ReadonlyVerse getVerse() {
		return verse;
	}

	public List<SearchTextMatch> getMatches() {
		return Collections.unmodifiableList(matches);
	}

	public float getScore() {
		return score;
	}

}
