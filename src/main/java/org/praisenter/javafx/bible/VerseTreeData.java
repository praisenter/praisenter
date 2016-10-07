package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;

import javafx.scene.control.Label;

final class VerseTreeData extends TreeData {
	final Bible bible;
	Book book;
	Chapter chapter;
	final Verse verse;
	
	public VerseTreeData(Bible bible, Book book, Chapter chapter, Verse verse) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
		
		this.label.set(verse.getText());
		this.list.set(String.valueOf(verse.getNumber()));
	}
	
	@Override
	public void update() {
		this.label.set(verse.getText());
		this.list.set(String.valueOf(verse.getNumber()));
	}
}
