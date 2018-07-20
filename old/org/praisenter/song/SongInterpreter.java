package org.praisenter.song;

import java.util.Iterator;

import org.praisenter.Constants;

public final class SongInterpreter {
	public static final Lyrics getLyrics(String text) {
		if (text == null) return null;
		if (text.isEmpty()) return null;
		
		String[] lines = text.trim().replaceAll("(\\r?\\n)(\\r?\\n)+", "\\n").split("\\r?\\n");
		
		Lyrics lyrics = new Lyrics();
		
		Verse verse = new Verse();
		lyrics.verses.add(verse);
		for (String line : lines) {
			if (line.isEmpty()) {
				verse = new Verse();
				verse.name = null;
				lyrics.verses.add(verse);
			}
			if (verse.name == null) {
				verse.name = line;
			} else if (verse.text == null) {
				verse.text = line;
			} else {
				verse.text += Constants.NEW_LINE + line;
			}
		}
		
		// then do some cleansing
		Iterator<Verse> it = lyrics.verses.iterator();
		while (it.hasNext()) {
			Verse v = it.next();
			if (v.name == null || v.name.isEmpty()) {
				if (v.text == null || v.text.isEmpty()) {
					it.remove();
					continue;
				}
				v.setName("v");
			}
		}
		
		if (lyrics.verses.size() == 0) {
			return null;
		}
		
		return lyrics;
	}
}
