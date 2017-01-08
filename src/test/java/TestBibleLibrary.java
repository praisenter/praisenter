import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipException;

import org.praisenter.SearchType;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.bible.BibleSearchMatch;
import org.praisenter.bible.BibleSearchResult;


public class TestBibleLibrary {

//	static {
//		// set the log file path (used in the log4j2.xml file)
//		System.setProperty("praisenter.logs.dir", Constants.LOGS_ABSOLUTE_PATH);
//		
//		// set the log4j configuration file path
//		System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
//	}
	
	public static void main(String[] args) throws ZipException, IOException, SQLException {
		//Database database = Database.open(Paths.get("C:\\Users\\William\\Desktop\\test\\data\\_db"));
//		Database database = Database.open(Paths.get("D:\\Personal\\Praisenter\\data\\_db"));
//		BibleLibraryV1 bl = new BibleLibraryV1(database);
//		
//		List<Bible> bibles = bl.getBibles();
//		System.out.println(bibles.size());
		
//		BibleLibrary bl = BibleLibrary.open(Paths.get("D:\\Personal\\Praisenter\\bibles"));
		BibleLibrary bl = BibleLibrary.open(Paths.get("C:\\Users\\William\\Desktop\\test\\biblelibrary"));
//		BibleImporter bi = new FormatIdentifingBibleImporter(bl);
//		try {
//			// Unbound Bible
//			bi.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\kjv_apocrypha.zip"));
//			bi.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\asv.zip"));
//			bi.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\thai_kjv.zip"));
//			// OpenSong
//			bi.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\CEV.zip"));
//			bi.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\TB.zip"));
//			// Zefania
//			bi.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\SF_2009-01-20_ENG_YLT_(YLT).zip"));
//			bi.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\SF_2009-01-20_SPA_RVA_(REINA VALERA 1989).zip"));
//		} catch (JAXBException e) {
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			e.printStackTrace();
//		} catch (UnknownFormatException e) {
//			e.printStackTrace();
//		}
		
//		List<BibleSearchResult> results = bl.search(null, "void", SearchType.ALL_WORDS);
		List<BibleSearchResult> results = bl.search(UUID.fromString("0d4c1aba-1bef-4cc9-9992-f21ee68ff774"), null, "void", SearchType.ALL_WORDS);
		Collections.sort(results);
		for (BibleSearchResult result : results) {
			System.out.print(result.getBible().getName() + " " + result.getBook().getName() + " " + result.getChapter().getNumber() + ":" + result.getVerse().getNumber());
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
