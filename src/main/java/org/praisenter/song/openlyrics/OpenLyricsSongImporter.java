package org.praisenter.song.openlyrics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.praisenter.Constants;
import org.praisenter.Tag;
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
import org.praisenter.song.Verse;
import org.praisenter.utility.StringManipulator;
import org.praisenter.xml.XmlIO;
import org.xml.sax.SAXException;

// FIXME convert to a SAX parser

public final class OpenLyricsSongImporter implements SongImporter {
	private static final Pattern PATTERN_VERSE_NAME = Pattern.compile("^([a-zA-Z]+)(\\d+)?([a-zA-Z]+)?$");
	
	@Override
	public List<Song> read(Path path) throws IOException, SongImportException {
		try {
			OpenLyricsSong olsong = null;
			try {
				olsong = XmlIO.read(path, OpenLyricsSong.class);
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// clean it up
			olsong.prepare();
			
			// convert
			Song song = new Song();
			song.setCcli(olsong.properties.ccli);
			song.setCopyright(olsong.properties.copyright);
			song.setKey(olsong.properties.key);
			song.setKeywords(olsong.properties.keywords);
			song.setModifiedDate(olsong.modifiedDate.toInstant());
			song.setPublisher(olsong.properties.publisher);
			song.setReleased(olsong.properties.released);
			song.setSource(olsong.createdIn);
			if (olsong.properties.tempo != null) {
				song.setTempo(olsong.properties.tempo.text);
			}
			song.setTransposition(olsong.properties.transposition);
			song.setVariant(olsong.properties.variant);
			
			// best effort here
			String[] names = olsong.properties.verseOrder.split("\\s+");
			song.getSequence().addAll(Arrays.asList(names));
			song.getSequence().removeIf(n -> StringManipulator.isNullOrEmpty(n));
			
			// copy over themes
			for (OpenLyricsTheme theme : olsong.properties.themes) {
				Tag t = new Tag(theme.text);
				song.getTags().add(t);
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
			
			// the praisenter format is different from the OpenLyrics format so
			// we need to transform it into what we think matches our format.
			// what this equates to is that we consider each language+transliteration
			// combination as a SET of lyrics so we need to group the sets of
			// lyrics by the language and transliteration.  For titles we try to
			// find the best match. For authors and songbooks, we just assume
			// all the sets have all the authors and are in all the songbooks
			
			// get the default title
			OpenLyricsTitle defaultTitle = this.getDefault(olsong.properties.titles);
			
			// create a map of all the language-transliteration combinations
			// (based on titles and verses)
			Map<String, String> tmap = new HashMap<String, String>(); 
			Map<String, Lyrics> lmap = new HashMap<String, Lyrics>(); 
			for (OpenLyricsTitle title : olsong.properties.titles) {
				String text = null;
				if (!StringManipulator.isNullOrEmpty(title.text)) {
					text = title.text;
				} else {
					text = defaultTitle.text;
				}
				String key = this.getKey(title.language, title.transliteration);
				Lyrics lyrics = lmap.get(key);
				if (lyrics == null) {
					lyrics = createLyrics(
							title.language, 
							title.transliteration, 
							text, 
							olsong.properties.songbooks, 
							olsong.properties.authors);
					lmap.put(key, lyrics);
				}
				tmap.put(key, text);
			}
			
			// create any more lyric sets and set the titles on the lyrics
			for (OpenLyricsVerse verse : olsong.verses) {
				String key = this.getKey(verse.language, verse.transliteration);
				Lyrics lyrics = lmap.get(key);
				if (lyrics == null) {
					lyrics = createLyrics(
							verse.language, 
							verse.transliteration, 
							tmap.get(key), 
							olsong.properties.songbooks, 
							olsong.properties.authors);
					lmap.put(key, lyrics);
				}
			}
			
			for (OpenLyricsVerse verse : olsong.verses) {
				// get the lyrics for this language+transliteration
				String key = this.getKey(verse.language, verse.transliteration);
				Lyrics lyrics = lmap.get(key);

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
	
	private String getKey(String language, String transliteration) {
		String key = "";
		if (!StringManipulator.isNullOrEmpty(language)) {
			key = language;
		}
		if (!StringManipulator.isNullOrEmpty(transliteration)) {
			key += "|" + transliteration;
		}
		return key;
	}
	
	private OpenLyricsTitle getDefault(Collection<OpenLyricsTitle> titles) {
		OpenLyricsTitle title = null;
		for (OpenLyricsTitle ttl : titles) {
			if (!StringManipulator.isNullOrEmpty(ttl.text)) {
				continue;
			}
			if (ttl.original) {
				return ttl;
			}
			if (StringManipulator.isNullOrEmpty(ttl.language)) {
				title = ttl;
			}
		}
		if (title == null) {
			// this means there were no non-empty titles with no language set
			// just choose the first one
			Optional<OpenLyricsTitle> first = titles.stream().findFirst();
			if (first.isPresent()) {
				title = first.get();
			}
		}
		return title;
	}
	
	private Lyrics createLyrics(String language, String transliteration, String title, Collection<OpenLyricsSongbook> songbooks, Collection<OpenLyricsAuthor> authors) {
		Lyrics lyrics = new Lyrics();
		lyrics.setLanguage(language);
		lyrics.setTransliteration(transliteration);
		
		lyrics.setTitle(title);

		// copy songbooks over
		for (OpenLyricsSongbook songbook : songbooks) {
			Songbook s = new Songbook();
			s.setEntry(songbook.entry);
			s.setName(songbook.name);
			lyrics.getSongbooks().add(s);
		}
		
		// copy over authors
		for (OpenLyricsAuthor author : authors) {
			Author a = new Author();
			a.setType(author.type);
			a.setName(author.name);
			lyrics.getAuthors().add(a);
		}
		
		return lyrics;
	}
}
