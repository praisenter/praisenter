import java.io.IOException;
import java.nio.file.Paths;

import org.apache.tika.Tika;

public class TikaTest {
	public static void main(String[] args) throws IOException {
		Tika tika = new Tika();
		System.out.println(tika.detect(Paths.get("D:\\Personal\\Praisenter\\trailer_1080p.ogg")));
	}
}
