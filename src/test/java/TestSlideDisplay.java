import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.praisenter.ThumbnailSettings;
import org.praisenter.javafx.ImageCache;
import org.praisenter.javafx.JavaFXContext;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.media.JavaFXMediaImportFilter;
import org.praisenter.javafx.media.ObservableMediaLibrary;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.SlideMode;
import org.praisenter.media.MediaLibrary;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class TestSlideDisplay extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BasicSlide slide = new BasicSlide();
		slide.setWidth(800);
		slide.setHeight(600);
		
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
		
		SlideStroke thick = new SlideStroke(
				new SlideColor(0.5, 0, 0, 1), 
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, 5.0, 10.0), 
				5, 
				5);
		
		SlideRadialGradient radial = new SlideRadialGradient(
				0.5, 0.5, 1.5, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 0, 0, 0.8)),
				new SlideGradientStop(1, new SlideColor(0, 1, 1, 0.8)));
		
		BasicTextComponent txt = new BasicTextComponent();
		txt.setFont(new SlideFont("Impact", SlideFontWeight.LIGHT, SlideFontPosture.ITALIC, 20));
		txt.setFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		txt.setWidth(400);
		txt.setHeight(400);
		txt.setX(20);
		txt.setY(100);
		txt.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		txt.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
//		txt.setOrder(0);
		txt.setPadding(new SlidePadding(10));
		txt.setBackground(new SlideColor(0.5, 0, 0, 0.5));
		txt.setBorder(thick);
		txt.setTextPaint(radial);
		txt.setTextBorder(stroke);
		txt.setText("Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.");
		
		slide.addComponent(txt);
		
		Path path = Paths.get("D:\\Personal\\Praisenter\\testmedialibrary");
//    	Path path = Paths.get("C:\\Users\\William\\Desktop\\test\\media");
		ThumbnailSettings settings = new ThumbnailSettings(
				100, 100);
    	MediaLibrary library = null;
		try {
			library = MediaLibrary.open(path, new JavaFXMediaImportFilter(path, null), settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ObservableMediaLibrary oml = new ObservableMediaLibrary(library, null);
		
		MediaObject img = new MediaObject(
				UUID.fromString("f6668fb0-3a40-4590-99a4-1ba474315dca"),
//				UUID.fromString("245d1e2a-9b82-431d-8dd9-bac0ed0a7aca"),
				ScaleType.UNIFORM,
				false,
				true);
		txt.setBackground(img);
		
		MediaObject vid = new MediaObject(
				UUID.fromString("a5d7dab1-8c59-4103-87cf-a13db23152f3"),
//				UUID.fromString("abe57410-81b9-4226-a15f-95f0bedcea89"),
				ScaleType.NONUNIFORM,
				false,
				true);
		
		MediaComponent mc = new MediaComponent();
		mc.setBackground(img);
		mc.setBorder(thick);
		mc.setWidth(100);
		mc.setHeight(100);
		mc.setX(100);
		mc.setY(200);
		mc.setMedia(vid);
		
		slide.addComponent(mc);
		

		slide.setBackground(vid);
		slide.setBorder(thick);
		
		
		PraisenterContext context = new PraisenterContext(new JavaFXContext(null, stage), null, null, new ImageCache(), library, null, null, null);
//		
//		FxSlide wrapper = new FxSlide(context, slide, SlideMode.PRESENT);
//		List<MediaPlayer> players = wrapper.getMediaPlayers();
//		
//		for (MediaPlayer mp : players) {
//			mp.play();
//		}
//		
		ObservableSlide<BasicSlide> wrapper2 = new ObservableSlide<BasicSlide>(slide, context, SlideMode.PRESENT);
		
		wrapper2.play();
		
		// test speed of conversion and snapshots
		
//		long t0, t1;
//		Node text;
//		Image image;
//		
//		t0 = System.nanoTime();
//		text = converter.getBackgroundSize(txt);
//		t1 = System.nanoTime();
//		System.out.println((t1 - t0) / 1e9 );
//
//		t0 = System.nanoTime();
//		text = converter.getBackgroundSize(txt);
//		t1 = System.nanoTime();
//		System.out.println((t1 - t0) / 1e9 );
//		
//		t0 = System.nanoTime();
//		text = converter.getBackgroundSize(txt);
//		t1 = System.nanoTime();
//		System.out.println((t1 - t0) / 1e9 );
//		
//		t0 = System.nanoTime(); 
//		image = converter.thumbnail(text, 200, 200);
//		t1 = System.nanoTime();
//		System.out.println((t1 - t0) / 1e9 );
//		
//		t0 = System.nanoTime(); 
//		image = converter.thumbnail(text, 200, 200);
//		t1 = System.nanoTime();
//		System.out.println((t1 - t0) / 1e9 );
//		
//		t0 = System.nanoTime(); 
//		image = converter.thumbnail(text, 200, 200);
//		t1 = System.nanoTime();
//		System.out.println((t1 - t0) / 1e9 );
		
		Pane pane = new Pane();
//		pane.getChildren().addAll(wrapper.getBackgroundNode(), wrapper.getContentNode(), wrapper.getBorderNode());
		pane.getChildren().add(wrapper2.getDisplayPane());
		Scene scene = new Scene(pane, Color.TRANSPARENT);
		stage.setScene(scene);
		stage.show();
	}
}
