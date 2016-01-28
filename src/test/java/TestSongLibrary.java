import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.SearchType;
import org.praisenter.song.Author;
import org.praisenter.song.Song;
import org.praisenter.song.SongLibrary;
import org.praisenter.song.Title;
import org.praisenter.song.churchview.ChurchViewSongImporter;
import org.praisenter.song.openlyrics.OpenLyricsAuthor;
import org.praisenter.song.openlyrics.OpenLyricsSong;
import org.praisenter.song.openlyrics.OpenLyricsSongImporter;
import org.praisenter.song.openlyrics.OpenLyricsTitle;
import org.praisenter.utility.StringManipulator;
import org.praisenter.xml.XmlIO;

/**
 * Workflow:
 * Import -> XML File -> Jaxb Unmarshal -> Add data to lucene index -> Marshal to song dir
 * Add New -> User Input -> Add data to lucene index -> Marshal to song dir
 * 
 * @author William
 *
 */
public class TestSongLibrary {
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		try {
			Path path = Paths.get("D:\\Personal\\Praisenter\\songs");
			//Path path = Paths.get("C:\\Users\\William\\Desktop\\test\\songs");
			
			Map<String, Integer> nameMap = new HashMap<String, Integer>();
			
			// churchview testing
//			ChurchViewSongImporter importer = new ChurchViewSongImporter();
//			List<Song> songs = importer.read(Paths.get("D:\\Personal\\Eclipse\\GitRepository\\praisenter\\data\\ChurchViewSongs.cvDat"));
			
			// openlyrics testing
			OpenLyricsSongImporter importer = new OpenLyricsSongImporter();
			List<Song> songs = new ArrayList<Song>();
			Path olPath = Paths.get("C:\\Users\\wbittle\\Desktop\\openlyrics-0.8\\openlyrics-0.8\\examples");
			try (DirectoryStream<Path> dir = Files.newDirectoryStream(olPath)) {
				for (Path file : dir) {
					if (Files.isRegularFile(file)) {
						songs.addAll(importer.read(file));
					}
				}
			}
			
			for (Song song : songs) {
				Title title = song.getDefaultTitle();
				String variant = song.getVariant();
				Author author = song.getDefaultAuthor();
				
				StringBuilder sb = new StringBuilder();
				if (title != null) {
					String ttl = StringManipulator.toFileName(title.getText());
					if (ttl.length() == 0) {
						ttl = "Untitled";
					}
					sb.append(ttl);
				}
				if (variant != null) {
					String var = StringManipulator.toFileName(variant);
					if (var.length() != 0) {
						sb.append("_").append(var);
					}
				}
				if (author != null) {
					String auth = StringManipulator.toFileName(author.getName());
					if (auth.length() != 0) {
						sb.append("_").append(auth);
					}
				}
				
				String name = sb.toString();
				
				// truncate the name to certain length
				final int max = 46;
				if (name.length() > max) {
					LOGGER.warn("File name too long '{}', truncating.", name);
					name = name.substring(0, Math.min(name.length() - 1, max));
				}
				
				// avoid naming collisions
				Path dest = path.resolve(name + ".xml");
				if (nameMap.containsKey(name)) {
					//LOGGER.warn("File name already exists '{}'.", name);
					Integer n = nameMap.get(name);
					n++;
					dest = path.resolve(name + String.valueOf(n) + ".xml");
					nameMap.put(name, n);
				} else {
					nameMap.put(name, 1);
				}
				
				XmlIO.save(dest, song);
			}
			
			SongLibrary sl = SongLibrary.open(path);
			
			System.out.println("-------------------------------------------------------------");
			
			long t0 = System.nanoTime();
			sl.search("glory hallelu", SearchType.PHRASE);
			long t1 = System.nanoTime();
			System.out.println();
			System.out.println(((t1 - t0) / 1e6) + " ms");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
