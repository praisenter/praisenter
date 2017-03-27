import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.praisenter.media.Media;
import org.praisenter.slide.BasicSlide;
import org.praisenter.xml.XmlIO;

public class TextJaxb {
	public static void main(String[] args) throws JAXBException, IOException {
		Media media = XmlIO.read(new FileInputStream(new File("C:\\Users\\William\\Desktop\\media-images\\_metadata\\btn-close.png_metadata.xml")), Media.class);
		
		System.out.println(media.getId());
		
		BasicSlide slide = XmlIO.read(new FileInputStream(new File("C:\\Users\\William\\Desktop\\media-images\\_metadata\\btn-close.png_metadata.xml")), BasicSlide.class);
		
		System.out.println(slide.getId());
	}
}
