package org.praisenter.bible;

public final class LocatedVerse {
	private final Bible bible;
	private final Book book;
	private final Chapter chapter;
	private final Verse verse;
	
	public LocatedVerse(Bible bible, Book book, Chapter chapter, Verse verse) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
	}

	public Bible getBible() {
		return bible;
	}
	
	public Book getBook() {
		return book;
	}
	
	public Chapter getChapter() {
		return chapter;
	}
	
	public Verse getVerse() {
		return verse;
	}
}
