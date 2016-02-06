package org.praisenter.song.openlyrics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.praisenter.Constants;
import org.praisenter.song.Author;
import org.praisenter.song.Br;
import org.praisenter.song.Chord;
import org.praisenter.song.Comment;
import org.praisenter.song.Lyrics;
import org.praisenter.song.Song;
import org.praisenter.song.SongImportException;
import org.praisenter.song.SongImporter;
import org.praisenter.song.Songbook;
import org.praisenter.song.TextFragment;
import org.praisenter.song.Theme;
import org.praisenter.song.Title;
import org.praisenter.song.Verse;
import org.praisenter.xml.XmlIO;

public final class OpenLyricsSongImporter implements SongImporter {
	private static final Pattern PATTERN_VERSE_NAME = Pattern.compile("^([a-zA-Z]+)(\\d+)?([a-zA-Z]+)?$");
	
	@Override
	public List<Song> read(Path path) throws IOException, SongImportException {
		try {
			OpenLyricsSong olsong = XmlIO.read(path, OpenLyricsSong.class);
			
			// clean it up
			olsong.prepare();
			
			// convert
			Song song = new Song();
			song.setCcli(olsong.properties.ccli);
			song.setCopyright(olsong.properties.copyright);
			song.setCreatedDate(new Date());
			song.setCreatedIn(olsong.createdIn);
			song.setKey(olsong.properties.key);
			song.setKeywords(olsong.properties.keywords);
			song.setLastModifiedDate(olsong.modifiedDate);
			song.setLastModifiedIn(olsong.modifiedIn);
			song.setPublisher(olsong.properties.publisher);
			song.setReleased(olsong.properties.released);
			if (olsong.properties.tempo != null) {
				song.setTempo(olsong.properties.tempo.text);
			}
			song.setTransposition(olsong.properties.transposition);
			song.setVariant(olsong.properties.variant);
			song.setSequence(olsong.properties.verseOrder);
			song.setVersion(olsong.properties.version);
			
			// copy over titles
			for (OpenLyricsTitle title : olsong.properties.titles) {
				Title t = new Title();
				t.setOriginal(title.original);
				t.setLanguage(title.language);
				t.setTransliteration(title.transliteration);
				t.setText(title.text);
				song.getTitles().add(t);
			}
			
			// copy over authors
			for (OpenLyricsAuthor author : olsong.properties.authors) {
				Author a = new Author();
				a.setLanguage(author.language);
				a.setType(author.type);
				a.setName(author.name);
				song.getAuthors().add(a);
			}
			
			// copy over themes
			for (OpenLyricsTheme theme : olsong.properties.themes) {
				Theme t = new Theme();
				t.setLanguage(theme.language);
				t.setTransliteration(theme.transliteration);
				t.setText(theme.text);
				song.getThemes().add(t);
			}
			
			// copy songbooks over
			for (OpenLyricsSongbook songbook : olsong.properties.songbooks) {
				Songbook s = new Songbook();
				s.setEntry(songbook.entry);
				s.setName(songbook.name);
				song.getSongbooks().add(s);
			}
			
			// condense comments into one
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < olsong.properties.comments.size(); i++) {
				OpenLyricsComment comment = olsong.properties.comments.get(i);
				if (i != 0) {
					sb.append(Constants.NEW_LINE);
				}
				sb.append(comment.text);
			}
			song.setComments(sb.toString());
			
			// map lyrics over
			Map<String, Lyrics> lmap = new HashMap<String, Lyrics>(); 
			for (OpenLyricsVerse verse : olsong.verses) {
				// get the lyrics for this language+transliteration
				String key = verse.language + "|" + verse.transliteration;
				Lyrics lyrics = lmap.get(key);
				if (lyrics == null) {
					lyrics = new Lyrics();
					lyrics.setLanguage(verse.language);
					lyrics.setTransliteration(verse.transliteration);
					lmap.put(key, lyrics);
				}
				
				String type = "c";
				int number = 0;
				String part = null;
				
				// get the parts of the name
				Matcher matcher = PATTERN_VERSE_NAME.matcher(verse.name);
				if (matcher.matches()) {
					if (matcher.groupCount() > 0) {
						type = matcher.group(1);
					} 
					if (matcher.groupCount() > 1) {
						try {
							number = Integer.parseInt(matcher.group(2));
						} catch (Exception e) {
							
						}
					}
					if (matcher.groupCount() > 2) {
						part = matcher.group(3);
					}
				}
				
				// handle the case where there's more than one of the same verse name
				// the format says this isn't allowed but it's not guaranteed that this
				// will always be the case
				Verse v = lyrics.getVerse(verse.name);
				if (v == null) {
					v = new Verse();
					// set the name
					v.setName(type, number, part);
					lyrics.getVerses().add(v);
				}
				
				// each verse can have many 'lines' elements which indicate parts or optional
				// break slide break points, both of these we ignore
				for (OpenLyricsLine line : verse.lines) {
					// each line has a list of elements, text, br, etc.
					for (Object fragment : line.elements) {
						// we ignore tag, but they should have been removed already anyway
						if (fragment instanceof OpenLyricsBr) {
							v.getFragments().add(new Br());
						} else if (fragment instanceof OpenLyricsChord) {
							Chord chord = new Chord();
							chord.setName(((OpenLyricsChord)fragment).name);
							v.getFragments().add(chord);
						} else if (fragment instanceof String) {
							TextFragment text = new TextFragment();
							text.setText((String)fragment);
							v.getFragments().add(text);
						} else if (fragment instanceof OpenLyricsLineComment) {
							Comment comment = new Comment();
							comment.setText(((OpenLyricsLineComment)fragment).text);
							v.getFragments().add(comment);
						}
					}
				}
			}
			song.getLyrics().addAll(lmap.values());
			
			List<Song> songs = new ArrayList<>();
			songs.add(song);
			return songs;
		} catch (JAXBException e) {
			throw new SongImportException(e);
		}
	}
}
