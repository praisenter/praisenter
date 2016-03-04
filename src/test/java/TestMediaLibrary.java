import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.media.MediaLibraryPane;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.utility.ClasspathLoader;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestMediaLibrary extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		Path path = Paths.get("D:\\Personal\\Praisenter\\testmedialibrary");
//    	Path path = Paths.get("C:\\Users\\William\\Desktop\\test\\media");
		MediaThumbnailSettings settings = new MediaThumbnailSettings(
				100, 100,
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/image-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/music-default-thumbnail.png"),
				ClasspathLoader.getBufferedImage("/org/praisenter/resources/video-default-thumbnail.png"));
    	MediaLibrary library = null;
		try {
			library = MediaLibrary.open(path, new JavaFXMediaImportFilter(path), settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		MediaLibraryPane root = new MediaLibraryPane(library, Orientation.HORIZONTAL);
		
		Scene scene = new Scene(root);
		primaryStage.setTitle("Media Library");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
