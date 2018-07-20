import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipException;

import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.BibleSearchCriteria;
import org.praisenter.bible.BibleSearchMatch;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.data.InvalidFormatException;
import org.praisenter.data.search.SearchType;
import org.praisenter.song.OpenLyricsSongImporter;
import org.praisenter.song.Song;


public class TestFormatImporters {
	
	public static void main(String[] args) throws IOException, InvalidFormatException {
		Path path = Paths.get("D:\\Personal\\Praisenter\\openlyrics-0.8\\openlyrics-0.8\\examples");
		OpenLyricsSongImporter importer = new OpenLyricsSongImporter();
		
		List<Song> songs = new ArrayList<Song>();
		
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path file : stream) {
				// only open files
				if (Files.isRegularFile(file)) {
					songs.addAll(importer.execute(file.getFileName().toString(), Files.newInputStream(file)));
				}
			}
		}
		
		System.out.println(songs.size());
	}
}
