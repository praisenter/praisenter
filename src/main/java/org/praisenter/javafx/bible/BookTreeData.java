package org.praisenter.javafx.bible;

import org.praisenter.bible.Bible;
import org.praisenter.bible.Book;

final class BookTreeData extends TreeData {
	final Bible bible;
	final Book book;
	
	public BookTreeData(Bible bible, Book book) {
		this.bible = bible;
		this.book = book;
		
		this.label.set(book.getName());
	}
	
	@Override
	public void update() {
		this.label.set(book.getName());
	}
}
