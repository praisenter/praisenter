import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipException;

import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleLibrary;
import org.praisenter.data.Database;


public class TestBibleLibrary {
	public static void main(String[] args) throws ZipException, IOException, SQLException {
		//Database database = Database.open(Paths.get("C:\\Users\\William\\Desktop\\test\\data\\_db"));
		Database database = Database.open(Paths.get("D:\\Personal\\Praisenter\\data\\_db"));
		BibleLibrary bl = new BibleLibrary(database);
		
		List<Bible> bibles = bl.getBibles();
		System.out.println(bibles.size());
		
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
