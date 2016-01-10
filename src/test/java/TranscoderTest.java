import java.io.IOException;
import java.nio.file.Paths;

public class TranscoderTest {
	public static void main(String[] args) {
		try {
			Transcoder.transcode(Paths.get("D:\\Personal\\Praisenter\\testmedialibrary\\trailer_1080p.ogg"));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
