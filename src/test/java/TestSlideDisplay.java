import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.easing.Easing;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.slide.JavaFxSlideConverter;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaThumbnailSettings;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.utility.ClasspathLoader;
import org.praisenter.xml.XmlIO;

import com.sun.prism.paint.Stop;


public class TestSlideDisplay extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BasicSlide slide = new BasicSlide();
		
		SlideColor color = new SlideColor(0, 0, 0.8, 0.7);
		
		SlideLinearGradient gradient = new SlideLinearGradient(
				0, 0, 1, 1, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 1, 0, 1)),
				new SlideGradientStop(1, new SlideColor(0, 0, 1, 1)));
		
		SlideStroke stroke = new SlideStroke(
				gradient, 
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, 5.0, 10.0), 
				1, 
				0);
		
		SlideRadialGradient radial = new SlideRadialGradient(
				0.5, 0.5, 1.5, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 0, 0, 0.8)),
				new SlideGradientStop(1, new SlideColor(0, 1, 1, 0.8)));
		
		BasicTextComponent txt = new BasicTextComponent();
		txt.setFontName("Impact");
		txt.setFontScaleType(FontScaleType.BEST_FIT);
		txt.setFontSize(10);
		txt.setWidth(400);
		txt.setHeight(400);
		txt.setX(20);
		txt.setY(100);
		txt.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		txt.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		txt.setOrder(5);
		txt.setPadding(5);
		txt.setBackground(color);
		txt.setBorder(stroke);
		txt.setTextPaint(radial);
		txt.setTextBorder(stroke);
		txt.setText("Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.");
		
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
		
		MediaObject mo = new MediaObject(
				UUID.fromString("3a455fd7-c8f0-4c81-955b-0bcb3e4c47ef"),
				ScaleType.NONE,
				false,
				true);
		txt.setBackground(mo);
		
		PraisenterContext context = new PraisenterContext(library, null, null, null);
		JavaFxSlideConverter converter = new JavaFxSlideConverter(context);
		
		// test speed of conversion and snapshots
		
		long t0, t1;
		Node text;
		Image image;
		
		t0 = System.nanoTime();
		text = converter.to(txt);
		t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1e9 );

		t0 = System.nanoTime();
		text = converter.to(txt);
		t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1e9 );
		
		t0 = System.nanoTime();
		text = converter.to(txt);
		t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1e9 );
		
		t0 = System.nanoTime(); 
		image = converter.thumbnail(text, 200, 200);
		t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1e9 );
		
		t0 = System.nanoTime(); 
		image = converter.thumbnail(text, 200, 200);
		t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1e9 );
		
		t0 = System.nanoTime(); 
		image = converter.thumbnail(text, 200, 200);
		t1 = System.nanoTime();
		System.out.println((t1 - t0) / 1e9 );
		
		Pane pane = new Pane();
		pane.getChildren().add(text);
		
		Scene scene = new Scene(pane, Color.TRANSPARENT);
		stage.setScene(scene);
		stage.show();
	}
}
