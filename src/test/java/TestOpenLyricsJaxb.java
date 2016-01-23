import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.praisenter.DisplayType;
import org.praisenter.data.song.Song;
import org.praisenter.data.song.Songbook;
import org.praisenter.data.song.Verse;
import org.praisenter.xml.XmlIO;


public class TestOpenLyricsJaxb {
	public static void main(String[] args) {
		try {
			Song song = XmlIO.read(Paths.get("C:\\Users\\wbittle\\Desktop\\openlyrics-0.8\\openlyrics-0.8\\examples\\format2.xml"), Song.class);
			
//			Verse v = new Verse();
//			v.setFontSize(40);
//			v.setLanguage("en");
//			v.setType("c");
//			v.setNumber(1);
//			v.setText("hello");
//			song.getVerses().add(v);
//			
//			Songbook sb = new Songbook();
//			sb.setEntry("121");
//			sb.setName("Hymns");
//			song.getProperties().getSongbooks().add(sb);
			
//			System.out.println(song.getVerses().get(1).getText());
//			song.getVerses().get(1).setText("<chord name=\"Eb\"/>Testing setting the text<comment>hello</comment><tag name=\"blue\">this should be formatted</tag>");
			
			//XmlIO.save(Paths.get("C:\\Users\\William\\Desktop\\test\\openlyrics-0.8\\openlyrics-0.8\\examples\\complex2.xml"), song);
			
			System.out.println(song.getDisplayText(DisplayType.MAIN));
			System.out.println("MUSICIAN -------------------------------------------------");
			System.out.println(song.getDisplayText(DisplayType.MUSICIAN));
			System.out.println("EDIT -------------------------------------------------");
			System.out.println(song.getDisplayText(DisplayType.EDIT));
			
			song.prepare();
			
			System.out.println(song.getDisplayText(DisplayType.MAIN));
			System.out.println("MUSICIAN -------------------------------------------------");
			System.out.println(song.getDisplayText(DisplayType.MUSICIAN));
			System.out.println("EDIT -------------------------------------------------");
			System.out.println(song.getDisplayText(DisplayType.EDIT));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
