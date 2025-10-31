package org.praisenter.data.song;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.praisenter.Constants;
import org.praisenter.Version;
import org.praisenter.data.DataImportResult;
import org.praisenter.data.Tag;
import org.praisenter.utility.MimeType;

public class OpenLyricsSongFormatProviderTest {
	@TempDir
	private Path path;

	@Test
	public void testMimeType() throws URISyntaxException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example01.xml").toURI());
		
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		Assertions.assertTrue(provider.isSupported(songPath));
		
		String mimeType = MimeType.get(songPath);
		Assertions.assertEquals("application/xml", mimeType);
	}
	
	@Test
	public void testExample01() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example01.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals("Amazing Grace", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Amazing Grace", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals("OpenLP 1.9.0", first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		Assertions.assertEquals(Instant.parse("2012-04-10T22:00:00+10:00"), first.getModifiedDate());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Amazing Grace", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(1, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section1.getName());
		Assertions.assertEquals("Amazing grace how sweet the sound\nthat saved a wretch like me;", section1.getText());
		
		// test export
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"OpenLP 1.9.0\" modifiedDate=\"2012-04-10T12:00:00Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title>Amazing Grace</title>\n"
				+ "        </titles>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>\n"
				+ "                Amazing grace how sweet the sound\n"
				+ "                <br/>\n"
				+ "                that saved a wretch like me;\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n", content);
	}
	
	@Test
	public void testExample02() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example02.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals("Amazing Grace", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Amazing Grace", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals("OpenLP 1.9.0", first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		Assertions.assertEquals(Instant.parse("2012-04-10T22:00:00+10:00"), first.getModifiedDate());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Amazing Grace", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		// sections
		
		Assertions.assertEquals(1, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section1.getName());
		Assertions.assertEquals("Amazing grace! How sweet the sound\nThat saved a wretch likeme.\nI once was lost, but now am found,\nWas blind but now I see.", section1.getText());
		
		// test export
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"OpenLP 1.9.0\" modifiedDate=\"2012-04-10T12:00:00Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title>Amazing Grace</title>\n"
				+ "        </titles>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>\n"
				+ "                Amazing grace! How sweet the sound\n"
				+ "                <br/>\n"
				+ "                That saved a wretch likeme.\n"
				+ "                <br/>\n"
				+ "                I once was lost, but now am found,\n"
				+ "                <br/>\n"
				+ "                Was blind but now I see.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n", content);
	}
	
	@Test
	public void testExample03() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example03.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals("Jezu Kriste, štědrý kněže", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Jezu Kriste, štědrý kněže", first.getName());
		Assertions.assertEquals(null, first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals("OpenLP 1.9.7", first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		Assertions.assertEquals(Instant.parse("2012-04-10T22:00:00+10:00"), first.getModifiedDate());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Jezu Kriste, štědrý kněže", lyrics.getTitle());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		Author author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("M. Jan Hus", author.getName());
		Assertions.assertEquals(null, author.getType());
		
		Assertions.assertEquals(1, lyrics.getSongBooks().size());
		SongBook songbook = lyrics.getSongBooks().get(0);
		Assertions.assertEquals("Jistebnický kancionál", songbook.getName());
		Assertions.assertEquals(null, songbook.getEntry());
		
		// sections
		
		Assertions.assertEquals(6, lyrics.getSections().size());
		Section section1 = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section1.getName());
		Assertions.assertEquals("Jezu Kriste, štědrý kněže,\ns Otcem, Duchem jeden Bože,\nštědrost Tvá je naše zboží,\nz Tvé milosti.", section1.getText());
		Section section2 = lyrics.getSections().get(1);
		Assertions.assertEquals("v2", section2.getName());
		Assertions.assertEquals("Ty jsi v světě, bydlil s námi,\nTvé tělo trpělo rány\nza nás za hříšné křesťany,\nz Tvé milosti.", section2.getText());
		Section section3 = lyrics.getSections().get(2);
		Assertions.assertEquals("v3", section3.getName());
		Assertions.assertEquals("Ó, Tvá dobroto důstojná\na k nám milosti přehojná!\nDáváš nám bohatství mnohá\nz Tvé milosti.", section3.getText());
		Section section4 = lyrics.getSections().get(3);
		Assertions.assertEquals("v4", section4.getName());
		Assertions.assertEquals("Ráčils nás sám zastoupiti,\nživot za nás položiti,\ntak smrt věčnou zahladiti,\nz Tvé milosti.", section4.getText());
		Section section5 = lyrics.getSections().get(4);
		Assertions.assertEquals("v5", section5.getName());
		Assertions.assertEquals("Ó, křesťané, z bludů vstaňme,\ndané dobro nám poznejme,\nk Synu Božímu chvátejme,\nk té milosti!", section5.getText());
		Section section6 = lyrics.getSections().get(5);
		Assertions.assertEquals("v6", section6.getName());
		Assertions.assertEquals("ChválabudižBohuOtci,\nSynu jeho téže moci,\nDuchu jeho rovné moci,\nz též milosti!", section6.getText());
		
		// test export
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"OpenLP 1.9.7\" modifiedDate=\"2012-04-10T12:00:00Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title>Jezu Kriste, štědrý kněže</title>\n"
				+ "        </titles>\n"
				+ "        <authors>\n"
				+ "            <author>M. Jan Hus</author>\n"
				+ "        </authors>\n"
				+ "        <songbooks>\n"
				+ "            <songbook name=\"Jistebnický kancionál\"/>\n"
				+ "        </songbooks>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>\n"
				+ "                Jezu Kriste, štědrý kněže,\n"
				+ "                <br/>\n"
				+ "                s Otcem, Duchem jeden Bože,\n"
				+ "                <br/>\n"
				+ "                štědrost Tvá je naše zboží,\n"
				+ "                <br/>\n"
				+ "                z Tvé milosti.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v2\">\n"
				+ "            <lines>\n"
				+ "                Ty jsi v světě, bydlil s námi,\n"
				+ "                <br/>\n"
				+ "                Tvé tělo trpělo rány\n"
				+ "                <br/>\n"
				+ "                za nás za hříšné křesťany,\n"
				+ "                <br/>\n"
				+ "                z Tvé milosti.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v3\">\n"
				+ "            <lines>\n"
				+ "                Ó, Tvá dobroto důstojná\n"
				+ "                <br/>\n"
				+ "                a k nám milosti přehojná!\n"
				+ "                <br/>\n"
				+ "                Dáváš nám bohatství mnohá\n"
				+ "                <br/>\n"
				+ "                z Tvé milosti.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v4\">\n"
				+ "            <lines>\n"
				+ "                Ráčils nás sám zastoupiti,\n"
				+ "                <br/>\n"
				+ "                život za nás položiti,\n"
				+ "                <br/>\n"
				+ "                tak smrt věčnou zahladiti,\n"
				+ "                <br/>\n"
				+ "                z Tvé milosti.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v5\">\n"
				+ "            <lines>\n"
				+ "                Ó, křesťané, z bludů vstaňme,\n"
				+ "                <br/>\n"
				+ "                dané dobro nám poznejme,\n"
				+ "                <br/>\n"
				+ "                k Synu Božímu chvátejme,\n"
				+ "                <br/>\n"
				+ "                k té milosti!\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v6\">\n"
				+ "            <lines>\n"
				+ "                ChválabudižBohuOtci,\n"
				+ "                <br/>\n"
				+ "                Synu jeho téže moci,\n"
				+ "                <br/>\n"
				+ "                Duchu jeho rovné moci,\n"
				+ "                <br/>\n"
				+ "                z též milosti!\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n", content);
	}
	
	@Test
	public void testExample04() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example04.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals("4639462", first.getCCLINumber());
		Assertions.assertEquals("public domain", first.getCopyright());
		Assertions.assertEquals("Amazing Grace", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals("C#", first.getKey());
		Assertions.assertEquals("something to help with more accurate results", first.getKeywords());
		Assertions.assertEquals("Amazing Grace", first.getName());
		Assertions.assertEquals("v1 v2  v3 c v4 c1 c2 b b1 b2\nThis is one of the most popular songs in our congregation.\nany comment\nany text\nany text", first.getNotes());
		Assertions.assertEquals("Sparrow Records", first.getPublisher());
		Assertions.assertEquals("1779", first.getReleased());
		Assertions.assertEquals("OpenLP 1.9.0", first.getSource());
		Assertions.assertEquals("90", first.getTempo());
		Assertions.assertEquals("2", first.getTransposition());
		Assertions.assertEquals("Newsboys", first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		Assertions.assertEquals(Instant.parse("2012-04-10T22:00:00+10:00"), first.getModifiedDate());
		
		// lyrics
		
		Assertions.assertEquals(6, first.getLyrics().size());
		
		// en-US
		
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals("en-US", lyrics.getLanguage());
		Assertions.assertEquals("Amazing Grace", lyrics.getTitle());
		Assertions.assertEquals(true, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		
		Assertions.assertEquals(3, lyrics.getSongBooks().size());
		SongBook songbook = lyrics.getSongBooks().get(0);
		Assertions.assertEquals("Songbook without Number", songbook.getName());
		Assertions.assertEquals(null, songbook.getEntry());
		songbook = lyrics.getSongBooks().get(1);
		Assertions.assertEquals("Songbook with Number", songbook.getName());
		Assertions.assertEquals("48", songbook.getEntry());
		songbook = lyrics.getSongBooks().get(2);
		Assertions.assertEquals("Songbook with Letters in Entry Name", songbook.getName());
		Assertions.assertEquals("153c", songbook.getEntry());
		
		Assertions.assertEquals(1, lyrics.getSections().size());
		Section section = lyrics.getSections().get(0);
		Assertions.assertEquals("v2", section.getName());
		Assertions.assertEquals("Amazing grace how sweet the sound that saved a wretch like me;\nAmazing grace how sweet the sound that saved a wretch like me;\nAmazing grace how sweet the sound that saved a wretch like me;\nA b c\nD e f", section.getText());

		// en
		
		lyrics = first.getLyrics().get(1);
		Assertions.assertEquals("en", lyrics.getLanguage());
		Assertions.assertEquals("Amazing Grace", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		Assertions.assertEquals(1, lyrics.getSections().size());
		section = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section.getName());
		Assertions.assertEquals("Amazing grace how sweet the sound that saved a wretch like me;\nA b c\nD e f", section.getText());
		
		// null
		
		lyrics = first.getLyrics().get(2);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Amazing", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(3, lyrics.getAuthors().size());
		Author author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("John Newton", author.getName());
		Assertions.assertEquals(null, author.getType());
		author = lyrics.getAuthors().get(1);
		Assertions.assertEquals("Chris Rice", author.getName());
		Assertions.assertEquals(Author.TYPE_LYRICS, author.getType());
		author = lyrics.getAuthors().get(2);
		Assertions.assertEquals("Richard Wagner", author.getName());
		Assertions.assertEquals(Author.TYPE_MUSIC, author.getType());
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		Assertions.assertEquals(1, lyrics.getSections().size());
		section = lyrics.getSections().get(0);
		Assertions.assertEquals("c", section.getName());
		Assertions.assertEquals("Line content.", section.getText());
		
		// de-DE
		
		lyrics = first.getLyrics().get(3);
		Assertions.assertEquals("de-DE", lyrics.getLanguage());
		Assertions.assertEquals("Erstaunliche Anmut", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		Assertions.assertEquals(0, lyrics.getSections().size());
		
		// cs
		
		lyrics = first.getLyrics().get(4);
		Assertions.assertEquals("cs", lyrics.getLanguage());
		Assertions.assertEquals(null, lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("František Foo", author.getName());
		Assertions.assertEquals(Author.TYPE_TRANSLATION, author.getType());
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		Assertions.assertEquals(0, lyrics.getSections().size());
		
		// de
		
		lyrics = first.getLyrics().get(5);
		Assertions.assertEquals("de", lyrics.getLanguage());
		Assertions.assertEquals(null, lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		Assertions.assertEquals(3, lyrics.getSections().size());
		section = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section.getName());
		Assertions.assertEquals("Erstaunliche Ahmut, wie", section.getText());
		section = lyrics.getSections().get(1);
		Assertions.assertEquals("emptyline", section.getName());
		Assertions.assertEquals("", section.getText());
		section = lyrics.getSections().get(2);
		Assertions.assertEquals("e", section.getName());
		Assertions.assertEquals("This is text of ending.", section.getText());
		
		// tags
		Assertions.assertEquals(7, first.getTags().size());
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Adoration")));
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Grace")));
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Praise")));
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Salvation")));
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Graça")));
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Adoração")));
		Assertions.assertEquals(true, first.getTags().contains(new Tag("Salvação")));

		
		// sections
		
		// test export
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"OpenLP 1.9.0\" modifiedDate=\"2012-04-10T12:00:00Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title lang=\"en-US\" original=\"true\">Amazing Grace</title>\n"
				+ "            <title lang=\"en\">Amazing Grace</title>\n"
				+ "            <title>Amazing</title>\n"
				+ "            <title lang=\"de-DE\">Erstaunliche Anmut</title>\n"
				+ "        </titles>\n"
				+ "        <authors>\n"
				+ "            <author>John Newton</author>\n"
				+ "            <author type=\"lyrics\">Chris Rice</author>\n"
				+ "            <author type=\"music\">Richard Wagner</author>\n"
				+ "            <author lang=\"cs\" type=\"translation\">František Foo</author>\n"
				+ "        </authors>\n"
				+ "        <copyright>public domain</copyright>\n"
				+ "        <ccliNo>4639462</ccliNo>\n"
				+ "        <released>1779</released>\n"
				+ "        <transposition>2</transposition>\n"
				+ "        <key>C#</key>\n"
				+ "        <variant>Newsboys</variant>\n"
				+ "        <publisher>Sparrow Records</publisher>\n"
				+ "        <keywords>something to help with more accurate results</keywords>\n"
				+ "        <tempo type=\"bpm\">90</tempo>\n"
				+ "        <songbooks>\n"
				+ "            <songbook name=\"Songbook without Number\"/>\n"
				+ "            <songbook entry=\"48\" name=\"Songbook with Number\"/>\n"
				+ "            <songbook entry=\"153c\" name=\"Songbook with Letters in Entry Name\"/>\n"
				+ "        </songbooks>\n"
				+ "        <themes>\n"
				+ "            <theme>Adoration</theme>\n"
				+ "            <theme>Salvation</theme>\n"
				+ "            <theme>Adoração</theme>\n"
				+ "            <theme>Grace</theme>\n"
				+ "            <theme>Praise</theme>\n"
				+ "            <theme>Graça</theme>\n"
				+ "            <theme>Salvação</theme>\n"
				+ "        </themes>\n"
				+ "        <comments>\n"
				+ "            <comment>v1 v2  v3 c v4 c1 c2 b b1 b2</comment>\n"
				+ "            <comment>This is one of the most popular songs in our congregation.</comment>\n"
				+ "            <comment>any comment</comment>\n"
				+ "            <comment>any text</comment>\n"
				+ "            <comment>any text</comment>\n"
				+ "        </comments>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse lang=\"en-US\" name=\"v2\">\n"
				+ "            <lines>\n"
				+ "                Amazing grace how sweet the sound that saved a wretch like me;\n"
				+ "                <br/>\n"
				+ "                Amazing grace how sweet the sound that saved a wretch like me;\n"
				+ "                <br/>\n"
				+ "                Amazing grace how sweet the sound that saved a wretch like me;\n"
				+ "                <br/>\n"
				+ "                A b c\n"
				+ "                <br/>\n"
				+ "                D e f\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse lang=\"en\" name=\"v1\">\n"
				+ "            <lines>\n"
				+ "                Amazing grace how sweet the sound that saved a wretch like me;\n"
				+ "                <br/>\n"
				+ "                A b c\n"
				+ "                <br/>\n"
				+ "                D e f\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"c\">\n"
				+ "            <lines>Line content.</lines>\n"
				+ "        </verse>\n"
				+ "        <verse lang=\"de\" name=\"v1\">\n"
				+ "            <lines>Erstaunliche Ahmut, wie</lines>\n"
				+ "        </verse>\n"
				+ "        <verse lang=\"de\" name=\"emptyline\">\n"
				+ "            <lines/>\n"
				+ "        </verse>\n"
				+ "        <verse lang=\"de\" name=\"e\">\n"
				+ "            <lines>This is text of ending.</lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n"
				+ "", content);
	}
	
	@Test
	public void testExample05() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example05.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals("Laboratory rat", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Laboratory rat", first.getName());
		Assertions.assertEquals("i v1 v2\nnormal 0.9-style chords mixed: <chord>text</chord> <chord/>text\nonly empty chords in 0.9: <chord/>text", first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		
		// en-US
		
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Laboratory rat", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		Author author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Gellért Gyuris", author.getName());
		Assertions.assertEquals(null, author.getType());
		
		Assertions.assertEquals(1, lyrics.getSongBooks().size());
		SongBook songbook = lyrics.getSongBooks().get(0);
		Assertions.assertEquals("Test book", songbook.getName());
		Assertions.assertEquals("1", songbook.getEntry());
		
		Assertions.assertEquals(2, lyrics.getSections().size());
		Section section = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section.getName());
		Assertions.assertEquals("Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit. Maecenas turpis tortor,\ntempor eget lacinia quis.", section.getText());
		section = lyrics.getSections().get(1);
		Assertions.assertEquals("v2", section.getName());
		Assertions.assertEquals("Accumsan eget neque.\nVestibulum facilisis lacus non feugiat pulvinar.\nCras nulla leo, placerat a bibendum ac, efficitur vel dolor.\nPraesent rhoncus turpis at libero faucibus euismod.\nNulla congue fringilla nisi in auctor.\nPellentesque laoreet arcu eu justo aliquam,\nnec suscipit eros imperdiet. Nunc vel iaculis elit.", section.getText());
		
		// test export
		first.setModifiedDate(Instant.parse("2025-10-31T00:56:28.137345Z"));
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"\" modifiedDate=\"2025-10-31T00:56:28.137345Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title>Laboratory rat</title>\n"
				+ "        </titles>\n"
				+ "        <authors>\n"
				+ "            <author>Gellért Gyuris</author>\n"
				+ "        </authors>\n"
				+ "        <songbooks>\n"
				+ "            <songbook entry=\"1\" name=\"Test book\"/>\n"
				+ "        </songbooks>\n"
				+ "        <comments>\n"
				+ "            <comment>i v1 v2</comment>\n"
				+ "            <comment>normal 0.9-style chords mixed: &lt;chord&gt;text&lt;/chord&gt; &lt;chord/&gt;text</comment>\n"
				+ "            <comment>only empty chords in 0.9: &lt;chord/&gt;text</comment>\n"
				+ "        </comments>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>\n"
				+ "                Lorem ipsum dolor sit amet,\n"
				+ "                <br/>\n"
				+ "                consectetur adipiscing elit. Maecenas turpis tortor,\n"
				+ "                <br/>\n"
				+ "                tempor eget lacinia quis.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v2\">\n"
				+ "            <lines>\n"
				+ "                Accumsan eget neque.\n"
				+ "                <br/>\n"
				+ "                Vestibulum facilisis lacus non feugiat pulvinar.\n"
				+ "                <br/>\n"
				+ "                Cras nulla leo, placerat a bibendum ac, efficitur vel dolor.\n"
				+ "                <br/>\n"
				+ "                Praesent rhoncus turpis at libero faucibus euismod.\n"
				+ "                <br/>\n"
				+ "                Nulla congue fringilla nisi in auctor.\n"
				+ "                <br/>\n"
				+ "                Pellentesque laoreet arcu eu justo aliquam,\n"
				+ "                <br/>\n"
				+ "                nec suscipit eros imperdiet. Nunc vel iaculis elit.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n", content);
	}
	
	@Test
	public void testExample06() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example06.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals("Testing 0.8 to 0.9 converting", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals("Esm", first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Testing 0.8 to 0.9 converting", first.getName());
		Assertions.assertEquals("v1 v2 v3 v4 v5", first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals("Geany", first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals("-6", first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		Assertions.assertEquals(Instant.parse("2021-04-03T10:00:00+00:00"), first.getModifiedDate());
		
		// lyrics
		
		Assertions.assertEquals(2, first.getLyrics().size());
		
		// en-US
		
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Testing 0.8 to 0.9 converting", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		Assertions.assertEquals(5, lyrics.getSections().size());
		Section section = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section.getName());
		Assertions.assertEquals("Testing 0.8.", section.getText());
		section = lyrics.getSections().get(1);
		Assertions.assertEquals("v2", section.getName());
		Assertions.assertEquals("A Te nevedben mi együtt vagyunk.\nJézus, nevedben mi együtt vagyunk.\nHallgasd meg imánk, mikor hozzád járulunk,\nélő igéddel táplálj hű Urunk!", section.getText());
		section = lyrics.getSections().get(2);
		Assertions.assertEquals("v3", section.getName());
		Assertions.assertEquals("A Te nevedben mi együtt vagyunk.\nJézus, nevedben mi együtt vagyunk.\n‖: Tégy eggyé minket szentségedben, ez óhajunk!\nHasználj munkádban drága, hű Urunk! :‖", section.getText());
		section = lyrics.getSections().get(3);
		Assertions.assertEquals("v4", section.getName());
		Assertions.assertEquals("‖: A-a-alleluja, dicsőség a mennyben Istenünknek,\nés békesség a földön népednek! :‖\nAtyánk, jóságos Atya,\nminden dicsőség a Tiéd,\nszerető Atya Isten!", section.getText());
		section = lyrics.getSections().get(4);
		Assertions.assertEquals("v5", section.getName());
		Assertions.assertEquals("", section.getText());
		
		lyrics = first.getLyrics().get(1);
		Assertions.assertEquals("en", lyrics.getLanguage());
		Assertions.assertEquals(null, lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		Author author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Gellért Gyuris", author.getName());
		Assertions.assertEquals(Author.TYPE_TRANSLATION, author.getType());
		
		// test export
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"Geany\" modifiedDate=\"2021-04-03T10:00:00Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title>Testing 0.8 to 0.9 converting</title>\n"
				+ "        </titles>\n"
				+ "        <authors>\n"
				+ "            <author lang=\"en\" type=\"translation\">Gellért Gyuris</author>\n"
				+ "        </authors>\n"
				+ "        <transposition>-6</transposition>\n"
				+ "        <key>Esm</key>\n"
				+ "        <comments>\n"
				+ "            <comment>v1 v2 v3 v4 v5</comment>\n"
				+ "        </comments>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>Testing 0.8.</lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v2\">\n"
				+ "            <lines>\n"
				+ "                A Te nevedben mi együtt vagyunk.\n"
				+ "                <br/>\n"
				+ "                Jézus, nevedben mi együtt vagyunk.\n"
				+ "                <br/>\n"
				+ "                Hallgasd meg imánk, mikor hozzád járulunk,\n"
				+ "                <br/>\n"
				+ "                élő igéddel táplálj hű Urunk!\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v3\">\n"
				+ "            <lines>\n"
				+ "                A Te nevedben mi együtt vagyunk.\n"
				+ "                <br/>\n"
				+ "                Jézus, nevedben mi együtt vagyunk.\n"
				+ "                <br/>\n"
				+ "                ‖: Tégy eggyé minket szentségedben, ez óhajunk!\n"
				+ "                <br/>\n"
				+ "                Használj munkádban drága, hű Urunk! :‖\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v4\">\n"
				+ "            <lines>\n"
				+ "                ‖: A-a-alleluja, dicsőség a mennyben Istenünknek,\n"
				+ "                <br/>\n"
				+ "                és békesség a földön népednek! :‖\n"
				+ "                <br/>\n"
				+ "                Atyánk, jóságos Atya,\n"
				+ "                <br/>\n"
				+ "                minden dicsőség a Tiéd,\n"
				+ "                <br/>\n"
				+ "                szerető Atya Isten!\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v5\">\n"
				+ "            <lines/>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n"
				+ "", content);
	}
	
	@Test
	public void testExample07() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example07.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals("Testing 0.9", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Testing 0.9", first.getName());
		Assertions.assertEquals("i v1", first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());
		
		// null
		
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Testing 0.9", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(5, lyrics.getAuthors().size());
		Author author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Csiszér László", author.getName());
		Assertions.assertEquals(null, author.getType());
		author = lyrics.getAuthors().get(1);
		Assertions.assertEquals("Flach Ferenc", author.getName());
		Assertions.assertEquals(Author.TYPE_LYRICS, author.getType());
		author = lyrics.getAuthors().get(2);
		Assertions.assertEquals("Flach Ferenc", author.getName());
		Assertions.assertEquals(Author.TYPE_MUSIC, author.getType());
		author = lyrics.getAuthors().get(3);
		Assertions.assertEquals("Majoros Ildikó", author.getName());
		Assertions.assertEquals(Author.TYPE_TRANSLATION, author.getType());
		author = lyrics.getAuthors().get(4);
		Assertions.assertEquals("Gellért Gyuris", author.getName());
		Assertions.assertEquals("arrangement", author.getType());
		
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		
		Assertions.assertEquals(1, lyrics.getSections().size());
		Section section = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section.getName());
		Assertions.assertEquals("Testing 0.9.", section.getText());
		
		// test export
		first.setModifiedDate(Instant.parse("2025-10-31T00:56:28.137345Z"));
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"\" modifiedDate=\"2025-10-31T00:56:28.137345Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title>Testing 0.9</title>\n"
				+ "        </titles>\n"
				+ "        <authors>\n"
				+ "            <author>Csiszér László</author>\n"
				+ "            <author type=\"lyrics\">Flach Ferenc</author>\n"
				+ "            <author type=\"music\">Flach Ferenc</author>\n"
				+ "            <author type=\"translation\">Majoros Ildikó</author>\n"
				+ "            <author type=\"arrangement\">Gellért Gyuris</author>\n"
				+ "        </authors>\n"
				+ "        <comments>\n"
				+ "            <comment>i v1</comment>\n"
				+ "        </comments>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>Testing 0.9.</lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n"
				+ "", content);
	}
	
	@Test
	public void testExample08() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example08.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals(null, first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("A kapudat nyisd meg", first.getName());
		Assertions.assertEquals("i v1", first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(2, first.getLyrics().size());
		
		// hu
		
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals("hu", lyrics.getLanguage());
		Assertions.assertEquals("A kapudat nyisd meg", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(0, lyrics.getAuthors().size());
		Assertions.assertEquals(0, lyrics.getSongBooks().size());
		Assertions.assertEquals(0, lyrics.getSections().size());
		
		// null
		
		lyrics = first.getLyrics().get(1);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals(null, lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		Author author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("ismeretlen", author.getName());
		Assertions.assertEquals(null, author.getType());
		
		Assertions.assertEquals(1, lyrics.getSongBooks().size());
		SongBook songbook = lyrics.getSongBooks().get(0);
		Assertions.assertEquals("Teszt könyv", songbook.getName());
		Assertions.assertEquals("166", songbook.getEntry());
		
		Assertions.assertEquals(1, lyrics.getSections().size());
		Section section = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section.getName());
		Assertions.assertEquals("A kapudat nyisd meg, Jézusnak nyisd meg, jön, közeledik.\nA kapudat nyisd meg, Jézusnak nyisd meg, jön, közeledik.\nNyisd meg, íme, jön, közeledik.", section.getText());
		
		// test export
		first.setModifiedDate(Instant.parse("2025-10-31T00:56:28.137345Z"));
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"\" modifiedDate=\"2025-10-31T00:56:28.137345Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title lang=\"hu\">A kapudat nyisd meg</title>\n"
				+ "        </titles>\n"
				+ "        <authors>\n"
				+ "            <author>ismeretlen</author>\n"
				+ "        </authors>\n"
				+ "        <songbooks>\n"
				+ "            <songbook entry=\"166\" name=\"Teszt könyv\"/>\n"
				+ "        </songbooks>\n"
				+ "        <comments>\n"
				+ "            <comment>i v1</comment>\n"
				+ "        </comments>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>\n"
				+ "                A kapudat nyisd meg, Jézusnak nyisd meg, jön, közeledik.\n"
				+ "                <br/>\n"
				+ "                A kapudat nyisd meg, Jézusnak nyisd meg, jön, közeledik.\n"
				+ "                <br/>\n"
				+ "                Nyisd meg, íme, jön, közeledik.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n", content);
	}
	
	@Test
	public void testExample09() throws URISyntaxException, IOException {
		Path songPath = Path.of(ClassLoader.getSystemResource("org/praisenter/data/song/openlyrics-example09.xml").toURI());
		SongPersistAdapter adpt = new SongPersistAdapter(this.path);
		OpenLyricsSongFormatProvider provider = new OpenLyricsSongFormatProvider();
		DataImportResult<Song> songs = provider.imp(adpt, songPath);
		
		Assertions.assertEquals(1, songs.getCreated().size());
		
		// test the first song
		Song first = songs.getCreated().get(0);
		Assertions.assertEquals(null, first.getCCLINumber());
		Assertions.assertEquals(null, first.getCopyright());
		Assertions.assertEquals("Laboratory rat", first.getDefaultTitle());
		Assertions.assertEquals(Constants.FORMAT_NAME, first.getFormat());
		Assertions.assertEquals(null, first.getKey());
		Assertions.assertEquals(null, first.getKeywords());
		Assertions.assertEquals("Laboratory rat", first.getName());
		Assertions.assertEquals("i v1 v2\nnormal 0.9-style chords mixed: <chord>text</chord> <chord/>text\nonly empty chords in 0.9: <chord/>text", first.getNotes());
		Assertions.assertEquals(null, first.getPublisher());
		Assertions.assertEquals(null, first.getReleased());
		Assertions.assertEquals(null, first.getSource());
		Assertions.assertEquals(null, first.getTempo());
		Assertions.assertEquals(null, first.getTransposition());
		Assertions.assertEquals(null, first.getVariant());
		Assertions.assertEquals(Version.STRING, first.getVersion());
		
		// lyrics
		
		Assertions.assertEquals(1, first.getLyrics().size());

		// null
		
		Lyrics lyrics = first.getLyrics().get(0);
		Assertions.assertEquals(null, lyrics.getLanguage());
		Assertions.assertEquals("Laboratory rat", lyrics.getTitle());
		Assertions.assertEquals(false, lyrics.isOriginal());
		Assertions.assertEquals(null, lyrics.getTransliteration());
		
		Assertions.assertEquals(1, lyrics.getAuthors().size());
		Author author = lyrics.getAuthors().get(0);
		Assertions.assertEquals("Gellért Gyuris", author.getName());
		Assertions.assertEquals(null, author.getType());
		
		Assertions.assertEquals(1, lyrics.getSongBooks().size());
		SongBook songbook = lyrics.getSongBooks().get(0);
		Assertions.assertEquals("Test book", songbook.getName());
		Assertions.assertEquals("1", songbook.getEntry());
		
		Assertions.assertEquals(2, lyrics.getSections().size());
		Section section = lyrics.getSections().get(0);
		Assertions.assertEquals("v1", section.getName());
		Assertions.assertEquals("Lorem ipsum dolor sit amet,\nconsectetur adipiscing elit. Maecenas turpis tortor,\ntempor eget lacinia quis.", section.getText());
		section = lyrics.getSections().get(1);
		Assertions.assertEquals("v2", section.getName());
		Assertions.assertEquals("Accumsan eget neque.\nVestibulum facilisis lacus non feugiat pulvinar.\nCras nulla leo, placerat a bibendum ac, efficitur vel dolor.\nPraesent rhoncus turpis at libero faucibus euismod.\nNulla congue fringilla nisi in auctor.\nPellentesque laoreet arcu eu justo aliquam,\nnec suscipit eros imperdiet. Nunc vel iaculis elit.", section.getText());
		
		// test export
		first.setModifiedDate(Instant.parse("2025-10-31T00:56:28.137345Z"));
		
		Path outputPath = path.resolve("outputSong.xml");
		provider.exp(adpt, outputPath, first);
		
		List<String> lines = Files.readAllLines(outputPath);
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line).append("\n");
		}
		String content = sb.toString();
		
		Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
				+ "<song xmlns=\"http://openlyrics.info/namespace/2009/song\" createdIn=\"\" modifiedDate=\"2025-10-31T00:56:28.137345Z\" version=\"0.9\">\n"
				+ "    <properties>\n"
				+ "        <titles>\n"
				+ "            <title>Laboratory rat</title>\n"
				+ "        </titles>\n"
				+ "        <authors>\n"
				+ "            <author>Gellért Gyuris</author>\n"
				+ "        </authors>\n"
				+ "        <songbooks>\n"
				+ "            <songbook entry=\"1\" name=\"Test book\"/>\n"
				+ "        </songbooks>\n"
				+ "        <comments>\n"
				+ "            <comment>i v1 v2</comment>\n"
				+ "            <comment>normal 0.9-style chords mixed: &lt;chord&gt;text&lt;/chord&gt; &lt;chord/&gt;text</comment>\n"
				+ "            <comment>only empty chords in 0.9: &lt;chord/&gt;text</comment>\n"
				+ "        </comments>\n"
				+ "    </properties>\n"
				+ "    <lyrics>\n"
				+ "        <verse name=\"v1\">\n"
				+ "            <lines>\n"
				+ "                Lorem ipsum dolor sit amet,\n"
				+ "                <br/>\n"
				+ "                consectetur adipiscing elit. Maecenas turpis tortor,\n"
				+ "                <br/>\n"
				+ "                tempor eget lacinia quis.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "        <verse name=\"v2\">\n"
				+ "            <lines>\n"
				+ "                Accumsan eget neque.\n"
				+ "                <br/>\n"
				+ "                Vestibulum facilisis lacus non feugiat pulvinar.\n"
				+ "                <br/>\n"
				+ "                Cras nulla leo, placerat a bibendum ac, efficitur vel dolor.\n"
				+ "                <br/>\n"
				+ "                Praesent rhoncus turpis at libero faucibus euismod.\n"
				+ "                <br/>\n"
				+ "                Nulla congue fringilla nisi in auctor.\n"
				+ "                <br/>\n"
				+ "                Pellentesque laoreet arcu eu justo aliquam,\n"
				+ "                <br/>\n"
				+ "                nec suscipit eros imperdiet. Nunc vel iaculis elit.\n"
				+ "            </lines>\n"
				+ "        </verse>\n"
				+ "    </lyrics>\n"
				+ "</song>\n"
				+ "", content);
	}
}
