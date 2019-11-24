package org.praisenter.ui.bible;

import org.praisenter.Constants;
import org.praisenter.data.bible.Book;
import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.BulkEditConverter;
import org.praisenter.ui.BulkEditParseException;
import org.praisenter.ui.translations.Translations;
import org.praisenter.utility.StringManipulator;

final class BookBulkEditConverter extends AbstractBulkEditConverter<Book> implements BulkEditConverter<Book> {
	private static final String EXAMPLE_BOOK_FORMAT = "10 Genesis\n\n1\n1 In the beginning God created the heaven and the earth.\n2 And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters.\n3 And God said, Let there be light: and there was light.\n2\n1 Thus the heavens and the earth were finished, and all the host of them.";

	@Override
	public String getSample() {
		return EXAMPLE_BOOK_FORMAT;
	}

	@Override
	public String toString(Book book) {
		StringBuilder sb = new StringBuilder();
		this.append(sb, book);
		for (Chapter chapter : book.getChapters()) {
			this.append(sb, chapter);
			for (Verse verse : chapter.getVerses()) {
				this.append(sb, verse);				
			}
			sb.append(Constants.NEW_LINE);
		}
		return sb.toString();
	}
	
	@Override
	public Book fromString(String data) throws BulkEditParseException {
		Book book = new Book();
		
		if (data == null) return book;
		
		String[] lines = data.split("\\r?\\n");
		
		if (lines.length == 0) return book;
		
		book = this.parseBook(lines[0].trim());
		Chapter chapter = null;
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i].trim();
			// ignore empty lines
			if (StringManipulator.isNullOrEmpty(line)) continue;
			// does it match a chapter?
			if (line.matches("^\\d+$")) {
				chapter = new Chapter();
				chapter.setNumber(Integer.parseInt(line));
				book.getChapters().add(chapter);
			} else {
				if (chapter == null) {
					throw new BulkEditParseException(Translations.get("bible.bulk.chapter.missing"));
				}
				Verse verse = this.parseVerse(line);
				chapter.getVerses().add(verse);
			}
		}
		
		return book;
	}
}
