package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;

final class ChapterTreeData extends TreeData {
	final Bible bible;
	final Book book;
	final Chapter chapter;
	
	public ChapterTreeData(Bible bible, Book book, Chapter chapter) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		
		// TODO translate
		this.label.set("Chapter " + chapter.getNumber());
	}
	
	@Override
	public void update() {
		this.label.set("Chapter " + chapter.getNumber());
	}
}
