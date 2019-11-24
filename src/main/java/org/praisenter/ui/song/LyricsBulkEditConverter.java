package org.praisenter.ui.song;

import org.praisenter.Constants;
import org.praisenter.data.song.Lyrics;
import org.praisenter.data.song.Section;
import org.praisenter.ui.BulkEditConverter;
import org.praisenter.ui.BulkEditParseException;
import org.praisenter.utility.StringManipulator;

final class LyricsBulkEditConverter implements BulkEditConverter<Lyrics> {
	private static final String SAMPLE = "It's All In Him\n\nChorus\nIt's all in Him, It's all in Him,\nthe fullness of the godhead is all in Him\n\nVerse 1\nVerse one text";
	
	@Override
	public String getSample() {
		return SAMPLE;
	}
	
	@Override
	public String toString(Lyrics obj) {
		StringBuilder sb = new StringBuilder();
		sb.append(obj.getTitle()).append(Constants.NEW_LINE).append(Constants.NEW_LINE);
		for (Section section : obj.getSections()) {
			sb.append(section.getName()).append(Constants.NEW_LINE);
			sb.append(section.getText()).append(Constants.NEW_LINE).append(Constants.NEW_LINE);
		}
		return sb.toString();
	}
	
	@Override
	public Lyrics fromString(String data) throws BulkEditParseException {
		Lyrics lyrics = new Lyrics();
		
		if (StringManipulator.isNullOrEmpty(data)) return lyrics;
		
		String[] groups = data.split("\\r?\\n{2,}");
		
		if (groups.length == 0) return lyrics;
		
		lyrics.setTitle(groups[0].trim());
		
		for (int i = 1; i < groups.length; i++) {
			String[] lines = groups[i].split("\\r?\\n");
			if (lines.length > 0) {
				Section section = new Section();
				section.setName(lines[0].trim());
				StringBuilder sb = new StringBuilder();
				for (int j = 1; j < lines.length; j++) {
					if (j != 1) sb.append(Constants.NEW_LINE);
					sb.append(lines[j].trim());
				}
				section.setText(sb.toString());
				
				if (!StringManipulator.isNullOrEmpty(section.getName()) || !StringManipulator.isNullOrEmpty(section.getText())) {
					lyrics.getSections().add(section);
				}
			}
		}
		
		return lyrics;
	}
}
