package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;
import org.praisenter.bible.Verse;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ObservableBibleItem {
	final Bible bible;
	final Verse verse;
	final Book book;
	
	// book detail
	final StringProperty bookName = new SimpleStringProperty();
	final StringProperty bookCode = new SimpleStringProperty();
	final IntegerProperty bookOrder = new SimpleIntegerProperty();
	
	// verse detail
	final IntegerProperty chapter = new SimpleIntegerProperty();
	final IntegerProperty number = new SimpleIntegerProperty();
	final IntegerProperty subVerse = new SimpleIntegerProperty();
	final IntegerProperty order = new SimpleIntegerProperty();
	final StringProperty text = new SimpleStringProperty();
	
	final StringProperty label = new SimpleStringProperty();
	
	public ObservableBibleItem(Bible bible, Book book, int chap, Verse verse) {
		this.bible = bible;
		this.verse = verse;
		this.book = book;
		
		this.chapter.set(chap);
		
		if (verse != null) {
			this.number.set(verse.getVerse());
			this.subVerse.set(verse.getSubVerse());
			this.order.set(verse.getOrder());
			this.text.set(verse.getText());
		}
		
		this.label.bind(new StringBinding() {
			{
				bind(bookName, chapter, number, subVerse, text);
			}
			
			@Override
			protected String computeValue() {
				if (verse != null) {
					return verse.getVerse() + "=" + verse.getText();
				} else if (book != null) {
					return book.getName();
				} else if (bible != null) {
					return bible.getName();
				} else if (chapter.get() > 0) {
					return "Chapter " + chapter.get();
				} else {
					return null;
				}
			}
		});
	}
	
	
}
