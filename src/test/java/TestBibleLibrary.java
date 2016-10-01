import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipException;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.praisenter.Constants;
import org.praisenter.InvalidFormatException;
import org.praisenter.SearchType;
import org.praisenter.UnknownFormatException;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleImporter;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.BibleLibraryV1;
import org.praisenter.bible.BibleSearchMatch;
import org.praisenter.bible.BibleSearchResult;
import org.praisenter.bible.UnboundBibleImporter;
import org.praisenter.data.Database;


public class TestBibleLibrary {

	static {
		// set the log file path (used in the log4j2.xml file)
		System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
		
		// set the log4j configuration file path
		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
	}
	
	public static void main(String[] args) throws ZipException, IOException, SQLException {
		//Database database = Database.open(Paths.get("C:\\Users\\William\\Desktop\\test\\data\\_db"));
//		Database database = Database.open(Paths.get("D:\\Personal\\Praisenter\\data\\_db"));
//		BibleLibraryV1 bl = new BibleLibraryV1(database);
//		
//		List<Bible> bibles = bl.getBibles();
//		System.out.println(bibles.size());
		
		BibleLibrary bl = BibleLibrary.open(Paths.get("D:\\Personal\\Praisenter\\bibles"));
//		BibleImporter bi = new UnboundBibleImporter(bl);
//		try {
//			bi.execute(Paths.get("D:\\Personal\\Praisenter\\data\\kjv_apocrypha.zip"));
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnknownFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		List<BibleSearchResult> results = bl.search(null, "void", SearchType.ALL_WORDS);
		Collections.sort(results);
		for (BibleSearchResult result : results) {
			System.out.print(result.getBible().getName() + " " + result.getBook().getName() + " " + result.getVerse().getChapter() + ":" + result.getVerse().getVerse());
			for (BibleSearchMatch match : result.getMatches()) {
				System.out.println("\t" + match.getMatchedText());
			}
		}
		
//		UnboundBibleImporter importer = new UnboundBibleImporter(database);
//		try {
//			importer.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\kjv_apocrypha.zip"));
//		} catch (BibleAlreadyExistsException e) {
//			e.printStackTrace();
//		} catch (BibleFormatException e) {
//			e.printStackTrace();
//		} catch (BibleImportException e) {
//			e.printStackTrace();
//		}
	}
}
