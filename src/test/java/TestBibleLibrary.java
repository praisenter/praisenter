import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.zip.ZipException;

import org.praisenter.data.Database;
import org.praisenter.data.bible.Bible;
import org.praisenter.data.bible.BibleLibrary;


public class TestBibleLibrary {
	public static void main(String[] args) throws ZipException, IOException, SQLException {
		Database database = Database.open(Paths.get("C:\\Users\\William\\Desktop\\test\\data\\_db"));
		BibleLibrary bl = new BibleLibrary(database);
		
		List<Bible> bibles = bl.getBibles();
		System.out.println(bibles.size());
		
//		UnboundBibleImporter importer = new UnboundBibleImporter(database);
//		try {
//			importer.execute(Paths.get("C:\\Users\\William\\Desktop\\test\\bibles\\kjv_apocrypha.zip"));
//		} catch (BibleAlreadyExistsException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (BibleFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (BibleImportException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
