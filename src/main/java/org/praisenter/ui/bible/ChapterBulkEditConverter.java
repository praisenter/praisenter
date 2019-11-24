package org.praisenter.ui.bible;

import org.praisenter.data.bible.Chapter;
import org.praisenter.data.bible.Verse;
import org.praisenter.ui.BulkEditConverter;
import org.praisenter.ui.BulkEditParseException;
import org.praisenter.utility.StringManipulator;

final class ChapterBulkEditConverter extends AbstractBulkEditConverter<Chapter> implements BulkEditConverter<Chapter> {
	private static final String EXAMPLE_CHAPTER_FORMAT = "1\n1 In the beginning God created the heaven and the earth.\n2 And the earth was without form, and void; and darkness was upon the face of the deep. And the Spirit of God moved upon the face of the waters.\n3 And God said, Let there be light: and there was light.";

	@Override
	public String getSample() {
		return EXAMPLE_CHAPTER_FORMAT;
	}

	@Override
	public String toString(Chapter chapter) {
		StringBuilder sb = new StringBuilder();
		this.append(sb, chapter);
		for (Verse verse : chapter.getVerses()) {
			this.append(sb, verse);				
		}
		return sb.toString();
	}
	
	@Override
	public Chapter fromString(String data) throws BulkEditParseException {
		Chapter chapter = new Chapter();
		
		if (data == null) return chapter;
		
		String[] lines = data.split("\\r?\\n");
		
		if (lines.length == 0) return chapter;
		
		chapter = this.parseChapter(lines[0].trim());
		
		for (int i = 1; i < lines.length; i++) {
			String line = lines[i].trim();
			// ignore empty lines
			if (StringManipulator.isNullOrEmpty(line)) continue;
			Verse verse = this.parseVerse(line);
			chapter.getVerses().add(verse);
		}
		
		return chapter;
	}
}
