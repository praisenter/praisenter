import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;

import org.praisenter.javafx.easing.Easing;
import org.praisenter.javafx.slide.SlideConverter;
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
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.xml.XmlIO;

import com.sun.prism.paint.Stop;


public class TestSlideDisplay extends Application {
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		BasicSlide slide = new BasicSlide();
		
		SlideColor color = new SlideColor(0, 0, 200, 0.7);
		
		SlideLinearGradient gradient = new SlideLinearGradient(
				0, 0, 1, 1, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 100, 0, 1)),
				new SlideGradientStop(1, new SlideColor(0, 0, 100, 1)));
		
		SlideStroke stroke = new SlideStroke(
				gradient, 
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, 5.0, 10.0), 
				1, 
				0);
		
		SlideRadialGradient radial = new SlideRadialGradient(
				0.5, 0.5, 1.5, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 0, 0, 0.8)),
				new SlideGradientStop(1, new SlideColor(0, 200, 200, 0.8)));
		
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
		txt.setText("hello");
		
		Node text = SlideConverter.to(txt);
		
		Pane pane = new Pane();
		pane.getChildren().add(text);
		
		Scene scene = new Scene(pane, Color.TRANSPARENT);
		stage.setScene(scene);
		stage.show();
	}
}
