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
	
	final Label num;
	
	public VerseTreeData(Bible bible, Book book, Chapter chapter, Verse verse) {
		this.bible = bible;
		this.book = book;
		this.chapter = chapter;
		this.verse = verse;
		
		this.num = new Label(String.valueOf(verse.getNumber()));
		this.num.setStyle("-fx-font-weight: bold; -fx-font-size: 0.8em;");
		
		this.label.set(verse.getText());
		this.graphic.set(num);
	}
	
	@Override
	public void update() {
		this.label.set(verse.getText());
		this.num.setText(String.valueOf(verse.getNumber()));
	}
}
