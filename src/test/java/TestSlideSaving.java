import java.io.IOException;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.praisenter.javafx.easing.Easing;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaintStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.xml.XmlIO;

import com.sun.prism.paint.Stop;


public class TestSlideSaving {
	public static void main(String[] args) throws JAXBException, IOException {
		BasicSlide slide = new BasicSlide();
		
		SlideColor color = new SlideColor(20, 10, 50, 40);
		
		SlideLinearGradient gradient = new SlideLinearGradient(
				0, 0, 1, 1, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 100, 0, 150)),
				new SlideGradientStop(1, new SlideColor(0, 0, 100, 150)));
		
		SlidePaintStroke stroke = new SlidePaintStroke(
				gradient, 
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, new double[] { 20, 10 }), 
				5, 
				0);
		
		slide.setBackground(color);
		slide.setBorder(stroke);
		slide.setHeight(400);
		slide.setWidth(400);
		slide.setX(0);
		slide.setY(0);
		slide.setTransition(Easing.LINEAR.getId());
		slide.setPath(Paths.get("/test/man"));
		
		BasicTextComponent txt = new BasicTextComponent();
		txt.setFontName("Segoe UI Light");
		txt.setFontScaleType(FontScaleType.BEST_FIT);
		txt.setFontSize(50);
		txt.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		txt.setOrder(5);
		txt.setPadding(5);
		txt.setText("hello");
		slide.addComponent(txt);
		
		XmlIO.save(Paths.get("C:\\Users\\William\\Desktop\\test\\slides\\test.xml"), slide);
	}
}
