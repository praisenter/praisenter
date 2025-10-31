package org.praisenter.data.song;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.Tag;

public class SongSelectSongFormatProviderTest {
	@TempDir
	private Path path;

	@Test
	public void testTxtExample01() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/songselect-example01.txt").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		SongSelectSongFormatProvider provider = new SongSelectSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("22025", first.getCCLINumber());
		Assertions.assertEquals("© Public Domain", first.getCopyright());
		Assertions.assertEquals("Amazing Grace", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Amazing Grace", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Amazing Grace", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(3, lyrics.getAuthors().size());
		Author author1 = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Edwin Othello Excell", author1.getName());
		Assertions.assertEquals(null, author1.getType());
		Author author2 = lyrics.getAuthors().get(1);
		Assertions.assertEquals("John Newton", author2.getName());
		Assertions.assertEquals(null, author2.getType());
		Author author3 = lyrics.getAuthors().get(2);
		Assertions.assertEquals("John P. Rees", author3.getName());
		Assertions.assertEquals(null, author3.getType());
		
		// books
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(6, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Verse 1", section1.getName());
		Assertions.assertEquals("Amazing grace how sweet the sound\n"
				+ "That saved a wretch like me\n"
				+ "I once was lost but now am found\n"
				+ "Was blind but now I see", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("Verse 2", section2.getName());
		Assertions.assertEquals("'Twas grace that taught my heart to fear\n"
				+ "And grace my fears relieved\n"
				+ "How precious did that grace appear\n"
				+ "The hour I first believed", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("Verse 3", section3.getName());
		Assertions.assertEquals("The Lord has promised good to me\n"
				+ "His Word my hope secures\n"
				+ "He will my shield and portion be\n"
				+ "As long as life endures", section3.getText());
		Section section4 = lyrics.getSections().get(3);
		Assertions.assertEquals("Verse 4", section4.getName());
		Assertions.assertEquals("Through many dangers toils and snares\n"
				+ "I have already come\n"
				+ "'Tis grace hath brought me safe thus far\n"
				+ "And grace will lead me home", section4.getText());
		Section section5 = lyrics.getSections().get(4);
		Assertions.assertEquals("Verse 5", section5.getName());
		Assertions.assertEquals("Yea when this flesh and heart shall fail\n"
				+ "And mortal life shall cease\n"
				+ "I shall possess within the veil\n"
				+ "A life of joy and peace", section5.getText());
		Section section6 = lyrics.getSections().get(5);
		Assertions.assertEquals("Verse 6", section6.getName());
		Assertions.assertEquals("When we've been there ten thousand years\n"
				+ "Bright shining as the sun\n"
				+ "We've no less days to sing God's praise\n"
				+ "Than when we first begun", section6.getText());
	}
	
	@Test
	public void testTxtExample02() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/songselect-example02.txt").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		SongSelectSongFormatProvider provider = new SongSelectSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("3798438", first.getCCLINumber());
		Assertions.assertEquals("© 2002 Thankyou Music (Admin. by Crossroad Distributors Pty. Ltd.)", first.getCopyright());
		Assertions.assertEquals("Blessed Be Your Name", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Blessed Be Your Name", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Blessed Be Your Name", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(2, lyrics.getAuthors().size());
		Author author1 = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Beth Redman", author1.getName());
		Assertions.assertEquals(null, author1.getType());
		Author author2 = lyrics.getAuthors().get(1);
		Assertions.assertEquals("Matt Redman", author2.getName());
		Assertions.assertEquals(null, author2.getType());
		
		// books
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(6, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Chorus 1", section1.getName());
		Assertions.assertEquals("Every blessing You pour out\n"
				+ "I'll turn back to praise\n"
				+ "When the darkness closes in\n"
				+ "Lord still I will say\n"
				+ "Blessed be the name of the Lord\n"
				+ "Blessed be Your name\n"
				+ "Blessed be the name of the Lord\n"
				+ "Blessed be Your glorious name", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("Verse 1", section2.getName());
		Assertions.assertEquals("Blessed be Your name\n"
				+ "In the land that is plentiful\n"
				+ "Where Your streams of abundance flow\n"
				+ "Blessed be Your name", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("Verse 2", section3.getName());
		Assertions.assertEquals("Blessed be Your name\n"
				+ "When I'm found in the desert place\n"
				+ "Though I walk through the wilderness\n"
				+ "Blessed be Your name", section3.getText());
		Section section4 = lyrics.getSections().get(3);
		Assertions.assertEquals("Verse 3", section4.getName());
		Assertions.assertEquals("Blessed be Your name\n"
				+ "When the sun's shining down on me\n"
				+ "When the world's all as it should be\n"
				+ "Blessed be Your name", section4.getText());
		Section section5 = lyrics.getSections().get(4);
		Assertions.assertEquals("Verse 4", section5.getName());
		Assertions.assertEquals("Blessed be Your name\n"
				+ "On the road marked with suffering\n"
				+ "Though there's pain in the offering\n"
				+ "Blessed be Your name", section5.getText());
		Section section6 = lyrics.getSections().get(5);
		Assertions.assertEquals("Misc 1 (BRIDGE)", section6.getName());
		Assertions.assertEquals("You give and take away\n"
				+ "You give and take away\n"
				+ "My heart will choose to say\n"
				+ "Lord blessed be Your name", section6.getText());
	}
	
	@Test
	public void testTxtExample03() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/songselect-example03.txt").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		SongSelectSongFormatProvider provider = new SongSelectSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("1874117", first.getCCLINumber());
		Assertions.assertEquals("© 1995 Mercy / Vineyard Publishing (Admin. by Vineyard Music USA)", first.getCopyright());
		Assertions.assertEquals("Breathe", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Breathe", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Breathe", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		Author author1 = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Marie Barnett", author1.getName());
		Assertions.assertEquals(null, author1.getType());
		
		// books
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(3, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Chorus 1", section1.getName());
		Assertions.assertEquals("And I I'm desperate for You\n"
				+ "And I I'm lost without You", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("Verse 1", section2.getName());
		Assertions.assertEquals("This is the air I breathe\n"
				+ "This is the air I breathe\n"
				+ "Your holy presence living in me", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("Verse 2", section3.getName());
		Assertions.assertEquals("This is my daily bread\n"
				+ "This is my daily bread\n"
				+ "Your very word spoken to me", section3.getText());
	}
	
	@Test
	public void testTxtExample04() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/songselect-example04.txt").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		SongSelectSongFormatProvider provider = new SongSelectSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("2456623", first.getCCLINumber());
		Assertions.assertEquals("© 1996 worshiptogether.com songs (Admin. by Crossroad Distributors Pty. Ltd.)", first.getCopyright());
		Assertions.assertEquals("You Are My King", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("You Are My King", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("You Are My King", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		Author author1 = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Billy Foote", author1.getName());
		Assertions.assertEquals(null, author1.getType());
		
		// books
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(3, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Chorus 1", section1.getName());
		Assertions.assertEquals("Amazing love\n"
				+ "How can it be\n"
				+ "That You my King\n"
				+ "Would die for me\n"
				+ "Amazing love\n"
				+ "I know it's true\n"
				+ "It's my joy to honor You\n"
				+ "In all I do I honor You", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("Verse 1", section2.getName());
		Assertions.assertEquals("I'm forgiven\n"
				+ "Because You were forsaken\n"
				+ "I'm accepted\n"
				+ "You were condemned\n"
				+ "I'm alive and well\n"
				+ "Your Spirit is within me\n"
				+ "Because You died\n"
				+ "And rose again", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("Misc 1 (ENDING)", section3.getName());
		Assertions.assertEquals("You are my King\n"
				+ "You are my King\n"
				+ "Jesus You are my King\n"
				+ "Jesus You are my King", section3.getText());
	}
	
	@Test
	public void testUsrExample01() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/songselect-example01.usr").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		SongSelectSongFormatProvider provider = new SongSelectSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("22025", first.getCCLINumber());
		Assertions.assertEquals("Public Domain", first.getCopyright());
		Assertions.assertEquals("Amazing Grace", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals("A", first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Amazing Grace", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals("SongSelect Import File", first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		Assertions.assertEquals(1, first.getTags().size());
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Test")));
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Amazing Grace", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(3, lyrics.getAuthors().size());
		Author author1 = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Edwin Othello Excell", author1.getName());
		Assertions.assertEquals(null, author1.getType());
		Author author2 = lyrics.getAuthors().get(1);
		Assertions.assertEquals("John Newton", author2.getName());
		Assertions.assertEquals(null, author2.getType());
		Author author3 = lyrics.getAuthors().get(2);
		Assertions.assertEquals("John P. Rees", author3.getName());
		Assertions.assertEquals(null, author3.getType());
		
		// books
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(6, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Verse 1", section1.getName());
		Assertions.assertEquals("Amazing grace how sweet the sound\n"
				+ "That saved a wretch like me\n"
				+ "I once was lost but now am found\n"
				+ "Was blind but now I see", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("Verse 2", section2.getName());
		Assertions.assertEquals("'Twas grace that taught my heart to fear\n"
				+ "And grace my fears relieved\n"
				+ "How precious did that grace appear\n"
				+ "The hour I first believed", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("Verse 3", section3.getName());
		Assertions.assertEquals("The Lord has promised good to me\n"
				+ "His Word my hope secures\n"
				+ "He will my shield and portion be\n"
				+ "As long as life endures", section3.getText());
		Section section4 = lyrics.getSections().get(3);
		Assertions.assertEquals("Verse 4", section4.getName());
		Assertions.assertEquals("Through many dangers toils and snares\n"
				+ "I have already come\n"
				+ "'Tis grace hath brought me safe thus far\n"
				+ "And grace will lead me home", section4.getText());
		Section section5 = lyrics.getSections().get(4);
		Assertions.assertEquals("Verse 5", section5.getName());
		Assertions.assertEquals("Yea when this flesh and heart shall fail\n"
				+ "And mortal life shall cease\n"
				+ "I shall possess within the veil\n"
				+ "A life of joy and peace", section5.getText());
		Section section6 = lyrics.getSections().get(5);
		Assertions.assertEquals("Verse 6", section6.getName());
		Assertions.assertEquals("When we've been there ten thousand years\n"
				+ "Bright shining as the sun\n"
				+ "We've no less days to sing God's praise\n"
				+ "Than when we first begun", section6.getText());
	}
	
	@Test
	public void testUsrExample02() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/songselect-example02.usr").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		SongSelectSongFormatProvider provider = new SongSelectSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("3798438", first.getCCLINumber());
		Assertions.assertEquals("© 2002 Thankyou Music (Admin. by Crossroad Distributors Pty. Ltd.)", first.getCopyright());
		Assertions.assertEquals("Blessed Be Your Name", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Blessed Be Your Name", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals("SongSelect Import File", first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		Assertions.assertEquals(0, first.getTags().size());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Blessed Be Your Name", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		// authors
		
		Assertions.assertEquals(2, lyrics.getAuthors().size());
		Author author1 = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Beth Redman", author1.getName());
		Assertions.assertEquals(null, author1.getType());
		Author author2 = lyrics.getAuthors().get(1);
		Assertions.assertEquals("Matt Redman", author2.getName());
		Assertions.assertEquals(null, author2.getType());
		
		// books
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(6, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("Chorus 1", section1.getName());
		Assertions.assertEquals("Every blessing You pour out\nI'll turn back to praise\nWhen the darkness closes in\nLord still I will say\nBlessed be the name of the Lord\nBlessed be Your name\nBlessed be the name of the Lord\nBlessed be Your glorious name", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("Verse 1", section2.getName());
		Assertions.assertEquals("Blessed be Your name\nIn the land that is plentiful\nWhere Your streams of abundance flow\nBlessed be Your name", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("Verse 2", section3.getName());
		Assertions.assertEquals("Blessed be Your name\nWhen I'm found in the desert place\nThough I walk through the wilderness\nBlessed be Your name", section3.getText());
		Section section4 = lyrics.getSections().get(3);
		Assertions.assertEquals("Verse 3", section4.getName());
		Assertions.assertEquals("Blessed be Your name\nWhen the sun's shining down on me\nWhen the world's all as it should be\nBlessed be Your name", section4.getText());
		Section section5 = lyrics.getSections().get(4);
		Assertions.assertEquals("Verse 4", section5.getName());
		Assertions.assertEquals("Blessed be Your name\nOn the road marked with suffering\nThough there's pain in the offering\nBlessed be Your name", section5.getText());
		Section section6 = lyrics.getSections().get(5);
		Assertions.assertEquals("Misc 1 (BRIDGE)", section6.getName());
		Assertions.assertEquals("You give and take away\nYou give and take away\nMy heart will choose to say\nLord blessed be Your name", section6.getText());
	}
}
