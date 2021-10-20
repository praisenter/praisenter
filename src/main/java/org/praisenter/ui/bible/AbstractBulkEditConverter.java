package org.praisenter.ui.bible;

import org.praisenter.Constants;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.BulkEditConverter;
import org.praisenter.ui.BulkEditParseException;
import org.praisenter.ui.translations.Translations;

abstract class AbstractBulkEditConverter<T> implements BulkEditConverter<T> {
	protected final void append(StringBuilder sb, Book book) {
		sb.append(book.getNumber()).append(" ").append(book.getName()).append(Constants.NEW_LINE);
	}
	
	protected final void append(StringBuilder sb, Chapter chapter) {
		sb.append(chapter.getNumber()).append(Constants.NEW_LINE);
	}
	
	protected final void append(StringBuilder sb, Verse verse) {
		sb.append(verse.getNumber()).append(" ").append(verse.getText()).append(Constants.NEW_LINE);
	}
	
	protected final Book parseBook(String line) throws BulkEditParseException {
		if (!line.matches("^(\\d+)\\s+.+$")) {
			throw new BulkEditParseException(Translations.get("bible.bulk.book.pattern.invalid"));
		}
		
		int index = 0;
		char c = ' ';
		do {
			c = line.charAt(index++);			
		} while (c >= '0' && c <= '9');
		
		String num = line.substring(0, index).trim();
		String text = line.substring(index).trim();
		
		return new Book(Integer.parseInt(num), text);
	}
	
	protected final Chapter parseChapter(String line) throws BulkEditParseException {
		if (!line.matches("^\\d+$")) {
			throw new BulkEditParseException(Translations.get("bible.bulk.chapter.pattern.invalid"));
		}
		
		return new Chapter(Integer.parseInt(line));
	}

	protected final Verse parseVerse(String line) throws BulkEditParseException {
		if (!line.matches("^(\\d+)\\s+.+$")) {
			throw new BulkEditParseException(Translations.get("bible.bulk.verse.pattern.invalid"));
		}
		
		int index = 0;
		char c = ' ';
		do {
			c = line.charAt(index++);			
		} while (c >= '0' && c <= '9');
		
		String num = line.substring(0, index).trim();
		String text = line.substring(index).trim();
		
		return new Verse(Integer.parseInt(num), text);
	}
}
