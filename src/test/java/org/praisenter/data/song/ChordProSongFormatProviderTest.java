package org.praisenter.data.song;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.data.DataImportResult;
import org.praisenter.utility.MimeType;

public class ChordProSongFormatProviderTest {
	@TempDir
	private Path path;

	@ParameterizedTest
	@ValueSource(strings = { 
			"chordpro-example01.cho", "chordpro-example02.cho", "chordpro-example03.cho", "chordpro-example04.cho", "chordpro-example05.cho",
			"chordpro-example06.cho", "chordpro-example07.cho", "chordpro-example08.cho", "chordpro-example09.cho", "chordpro-example10.cho",
			"chordpro-example11.cho", "chordpro-example12.cho", "chordpro-example13.cho", "chordpro-example14.cho", "chordpro-example15.cho",
			"chordpro-example16.cho", "chordpro-example17.cho", "chordpro-example18.cho"})
	public void testParseAll(String fileName) throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/" + fileName).toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		ChordProSongFormatProvider provider = new ChordProSongFormatProvider();
		provider.imp(adpt, songPath);
	}
	
	@Test
	public void testMimeType() throws URISyntaxException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/chordpro-example01.cho").toURI());
		ChordProSongFormatProvider provider = new ChordProSongFormatProvider();
		Assertions.assertTrue(provider.isSupported(songPath));
		
		String mimeType = MimeType.get(songPath);
		
		Assertions.assertEquals("text/plain", mimeType);
	}
	
	@Test
	public void testAllVariations() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/chordpro-example19.cho").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		ChordProSongFormatProvider provider = new ChordProSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(2, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("12345", first.getCCLINumber());
		Assertions.assertEquals("CC-BY License", first.getCopyright());
		Assertions.assertEquals("ख्रीष्टकहाँ आऊ साथी", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals("C Major", first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("My Song", first.getName());
		Assertions.assertEquals("This song is transposed two steps up for easier guitar playing.\nChorus\nINTRO:\nKey is %{key}%{key_actual|, actual %{}}%{key_from|, from %{}}", first.getNotes());
		Assertions.assertEquals("\"Hope & Harmony\"", first.getPublisher());
		Assertions.assertEquals("2023", first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals("120", first.getTempo());
		Assertions.assertEquals("+2", first.getTransposition());
		Assertions.assertEquals("Testing meta simple", first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("ख्रीष्टकहाँ आऊ साथी", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(3, lyrics.getAuthors().size());
		Author author1 = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Mickey Mouse", author1.getName());
		Assertions.assertEquals(Author.TYPE_MUSIC, author1.getType());
		Author author2 = lyrics.getAuthors().get(1);
		Assertions.assertEquals("Bugs Bunny", author2.getName());
		Assertions.assertEquals(Author.TYPE_MUSIC, author2.getType());
		Author author3 = lyrics.getAuthors().get(2);
		Assertions.assertEquals("The Man", author3.getName());
		Assertions.assertEquals(Author.TYPE_LYRICS, author3.getType());
		
		// books
		
		Assertions.assertEquals(1, lyrics.getSongBooks().size());
		SongBook book1 = lyrics.getSongBooks().get(0);
		Assertions.assertEquals("Hymnals Unlimited", book1.getName());
		Assertions.assertEquals("", book1.getEntry());
		
		// sections
		
		Assertions.assertEquals(9, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Verse 1", section1.getName());
		Assertions.assertEquals("This is some text\nAnother line without tags", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("verse 2", section2.getName());
		Assertions.assertEquals("जीवनको रोटी उनै हुन - भोको मनलाई तृप्ति दिने\nउनलाई तिमीले खायौ भने\nतिमी कहिल्यै भोको बन्दैनौ\nयुगानु-युगसम्मलाई", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("verse 3", section3.getName());
		Assertions.assertEquals("She was a fish monger\nbut sure, 'twas no wonder\nFor so were her father and mother before\nThey both wheeled their barrows\nthrough streets broad and narrow\nCrying “Cockles and Mussels, alive alive-o”", section3.getText());
		Section section4 = lyrics.getSections().get(3);
		Assertions.assertEquals("textblock 1", section4.getName());
		Assertions.assertEquals("She died of the fever,\n"
				+ "and nothing could save her\n"
				+ "And that was the end of sweet Molly Malone\n"
				+ "But her ghost wheels a barrow\n"
				+ "through streets broad and narrow\n"
				+ "Crying “Cockles and Mussels, alive alive-o”", section4.getText());
		Section section5 = lyrics.getSections().get(4);
		Assertions.assertEquals("chorus 1", section5.getName());
		Assertions.assertEquals("Աշխարհը եկել է,   2x \n"
				+ "կուզե քեզ սեյր անի.   2x\n"
				+ "Յար ջան, սարի եղնիկ ես,\n"
				+ "Ինչպես հըպարտ ջեյրանի։", section5.getText());
		Section section6 = lyrics.getSections().get(5);
		Assertions.assertEquals("whatever 1", section6.getName());
		Assertions.assertEquals("E-----------------------------------------------------\n"
				+ "B--5--5-5-5--5-8--8-5--8-5---0--0-0-0--0-1--1-0--1-0--\n"
				+ "G--5--5-5-5--5-9--9-5--9-5---0--0-0-0--0-1--1-0--1-0--\n"
				+ "D--0-------------------------0------------------------\n"
				+ "A-----------------------------------------------------\n"
				+ "E----------------------------3------------------------", section6.getText());
		Section section7 = lyrics.getSections().get(6);
		Assertions.assertEquals("Verse 4", section7.getName());
		Assertions.assertEquals("VERSE 1:\n"
				+ "--------", section7.getText());
		Section section8 = lyrics.getSections().get(7);
		Assertions.assertEquals("Verse 5", section8.getName());
		Assertions.assertEquals("  Lyrics are here   yes they           are", section8.getText());
		Section section9 = lyrics.getSections().get(8);
		Assertions.assertEquals("Verse 6", section9.getName());
		Assertions.assertEquals("  test  test testtest  test test test test  test ", section9.getText());

		// song 2
		Song second = songs.getCreated().get(1);
		Assertions.assertEquals(null, second.getCCLINumber());
		Assertions.assertEquals(null, second.getCopyright());
		Assertions.assertEquals("Roman D", second.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, second.getFormat());
		Assertions.assertEquals("D", second.getKey());
		Assertions.assertEquals("test this song", second.getKeywords());
		Assertions.assertEquals("Roman D", second.getName());
		Assertions.assertEquals("And a final chorus\nVerse 1", second.getNotes());
		Assertions.assertEquals(null, second.getPublisher());
		Assertions.assertEquals(null, second.getReleased());
		Assertions.assertEquals(null, second.getSource());
		Assertions.assertEquals(null, second.getTempo());
		Assertions.assertEquals(null, second.getTransposition());
		Assertions.assertEquals(null, second.getVariant());
		Assertions.assertEquals(Version.STRING, second.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, second.getLyrics().size());
		lyrics = second.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Roman D", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		
		// books
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(5, lyrics.getSections().size());
		section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Verse 1", section1.getName());
		Assertions.assertEquals("Hello, World!", section1.getText());
		
		section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("whatever 1", section2.getName());
		Assertions.assertEquals("This is for Guitar", section2.getText());
		
		section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("Verse 2", section3.getName());
		Assertions.assertEquals("I looked over Jordan, and what did I see,\n"
				+ "     “Comin’ for to carry me home.”\n"
				+ "A band of angels comin’ after me,\n"
				+ "     Comin’ for to carry me home.               ", section3.getText());
		
		section4 = lyrics.getSections().get(3);
		Assertions.assertEquals("Verse 3", section4.getName());
		Assertions.assertEquals("Rise again, rise again!\n"
				+ "Don’t you fade away, my friend.", section4.getText());
		
		section5 = lyrics.getSections().get(4);
		Assertions.assertEquals("Verse 4", section5.getName());
		Assertions.assertEquals("I walked that road so many times,  \n"
				+ "never found where it leads.", section5.getText());
		
		// test export
		
		Path outputPath = path.resolve("outputSong.cho");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\r\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("{ccli: 12345}\r\n"
				+ "{copyright: CC-BY License}\r\n"
				+ "{key: C Major}\r\n"
				+ "{title: My Song}\r\n"
				+ "{comment: This song is transposed two steps up for easier guitar playing.}\r\n"
				+ "{comment: Chorus}\r\n"
				+ "{comment: INTRO:}\r\n"
				+ "{comment: Key is %{key}%{key_actual|, actual %{}}%{key_from|, from %{}}}\r\n"
				+ "{author: \"Hope & Harmony\"}\r\n"
				+ "{year: 2023}\r\n"
				+ "{tempo: 120}\r\n"
				+ "{transpose: +2}\r\n"
				+ "{subtitle: Testing meta simple}\r\n"
				+ "\r\n"
				+ "{subtitle: ख्रीष्टकहाँ आऊ साथी}\r\n"
				+ "{composer: Mickey Mouse}\r\n"
				+ "{composer: Bugs Bunny}\r\n"
				+ "{lyricist: The Man}\r\n"
				+ "{book: Hymnals Unlimited}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"Verse 1\"}\r\n"
				+ "This is some text\r\n"
				+ "Another line without tags\r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"verse 2\"}\r\n"
				+ "जीवनको रोटी उनै हुन - भोको मनलाई तृप्ति दिने\r\n"
				+ "उनलाई तिमीले खायौ भने\r\n"
				+ "तिमी कहिल्यै भोको बन्दैनौ\r\n"
				+ "युगानु-युगसम्मलाई\r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"verse 3\"}\r\n"
				+ "She was a fish monger\r\n"
				+ "but sure, 'twas no wonder\r\n"
				+ "For so were her father and mother before\r\n"
				+ "They both wheeled their barrows\r\n"
				+ "through streets broad and narrow\r\n"
				+ "Crying “Cockles and Mussels, alive alive-o”\r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"textblock 1\"}\r\n"
				+ "She died of the fever,\r\n"
				+ "and nothing could save her\r\n"
				+ "And that was the end of sweet Molly Malone\r\n"
				+ "But her ghost wheels a barrow\r\n"
				+ "through streets broad and narrow\r\n"
				+ "Crying “Cockles and Mussels, alive alive-o”\r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n"
				+ "{start_of_chorus: label=\"chorus 1\"}\r\n"
				+ "Աշխարհը եկել է,   2x \r\n"
				+ "կուզե քեզ սեյր անի.   2x\r\n"
				+ "Յար ջան, սարի եղնիկ ես,\r\n"
				+ "Ինչպես հըպարտ ջեյրանի։\r\n"
				+ "{end_of_chorus}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"whatever 1\"}\r\n"
				+ "E-----------------------------------------------------\r\n"
				+ "B--5--5-5-5--5-8--8-5--8-5---0--0-0-0--0-1--1-0--1-0--\r\n"
				+ "G--5--5-5-5--5-9--9-5--9-5---0--0-0-0--0-1--1-0--1-0--\r\n"
				+ "D--0-------------------------0------------------------\r\n"
				+ "A-----------------------------------------------------\r\n"
				+ "E----------------------------3------------------------\r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"Verse 4\"}\r\n"
				+ "VERSE 1:\r\n"
				+ "--------\r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"Verse 5\"}\r\n"
				+ "  Lyrics are here   yes they           are\r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n"
				+ "{start_of_verse: label=\"Verse 6\"}\r\n"
				+ "  test  test testtest  test test test test  test \r\n"
				+ "{end_of_verse}\r\n"
				+ "\r\n", content);
	}
}
