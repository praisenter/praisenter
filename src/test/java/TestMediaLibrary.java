import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.praisenter.javafx.ImageCache;
import org.praisenter.javafx.JavaFXContext;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.configuration.Configuration;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.media.MediaLibraryPane;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
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
				100, 100);
    	MediaLibrary library = null;
		try {
			library = MediaLibrary.open(path, new JavaFXMediaImportFilter(path, null), settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		PraisenterContext context = new PraisenterContext(new JavaFXContext(null, primaryStage), null, null, new ImageCache(), library, null, null, null);
		
		BorderPane root = new BorderPane();
//		MediaPicker pkrMedia = new MediaPicker(context, MediaType.IMAGE, MediaType.AUDIO);
//		pkrMedia.setValue(library.all().get(0));
//		root.setTop(pkrMedia);
		
//		pkrMedia.valueProperty().addListener((obs, ov, nv) -> {
//			System.out.println(nv != null ? nv.getMetadata().getName() : "null");
//		});
		
//		ColorPicker pkrColor = new ColorPicker();
//		root.setBottom(pkrColor);
		
		MediaLibraryPane mlp = new MediaLibraryPane(
				context, 
				Orientation.HORIZONTAL);
		
		root.setCenter(mlp);
		
		primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon16x16.png"));
		primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon32x32.png"));
		primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon48x48.png"));
		primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon64x64.png"));
		primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon96x96.png"));
		primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon128x128.png"));
    	primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon256x256.png"));
    	primaryStage.getIcons().add(new Image("org/praisenter/resources/logo/icon512x512.png"));
    	
		Scene scene = new Scene(root);
//		scene.getStylesheets().add(Praisenter.THEME_CSS);
		primaryStage.setTitle("Media Library");
		primaryStage.setScene(scene);
		primaryStage.setWidth(650);
		primaryStage.show();
	}
}
