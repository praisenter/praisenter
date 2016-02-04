import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.praisenter.javafx.easing.Easing;
import org.praisenter.javafx.easing.Easings;
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
import org.praisenter.slide.text.TextPlaceholderComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.xml.XmlIO;

import com.sun.prism.paint.Stop;


public class TestSlideSaving {
	public static void main(String[] args) throws JAXBException, IOException {
		BasicSlide slide = new BasicSlide();
		
		SlideColor color = new SlideColor(0.5, 0.5, 1, 0.5);
		
		SlideLinearGradient gradient = new SlideLinearGradient(
				0, 0, 1, 1, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 1, 0, 0.5)),
				new SlideGradientStop(1, new SlideColor(0, 0, 1, 0.5)));
		
		SlideStroke stroke = new SlideStroke(
				gradient, 
				new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, 20.0, 10.0), 
				5, 
				0);
		
		SlideRadialGradient radial = new SlideRadialGradient(
				0.5, 0.5, 1, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 1, 0, 0.5)),
				new SlideGradientStop(1, new SlideColor(1, 0, 0, 0.5)));
		
		slide.setBackground(color);
		slide.setBorder(stroke);
		slide.setHeight(400);
		slide.setWidth(400);
		slide.setX(0);
		slide.setY(0);
		slide.setTransition(Easings.getLinear().getId());
		slide.setPath(Paths.get("/test/man"));
		
		MediaComponent med = new MediaComponent();
		med.setBackground(radial);
		med.setHeight(300);
		med.setWidth(300);
		MediaObject mp = new MediaObject();
		mp.setId(UUID.randomUUID());
		mp.setLoop(true);
		mp.setMute(true);
		mp.setScaling(ScaleType.UNIFORM);
		med.setMedia(mp);
		med.setOrder(10);
		med.setX(200);
		med.setY(100);
		slide.addComponent(med);
		
		BasicTextComponent txt = new BasicTextComponent();
		txt.setFontName("Segoe UI Light");
		txt.setFontScaleType(FontScaleType.BEST_FIT);
		txt.setFontSize(50);
		txt.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		txt.setOrder(5);
		txt.setPadding(5);
		txt.setText("hello");
		slide.addComponent(txt);
		
		DateTimeComponent dtc = new DateTimeComponent();
		dtc.setFormat(new SimpleDateFormat("YYYY"));
		dtc.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		dtc.setTextPaint(radial);
		dtc.setTextBorder(stroke);
		slide.addComponent(dtc);
		
		TextPlaceholderComponent tpc = new TextPlaceholderComponent();
		tpc.addType(TextPlaceholderComponent.TYPE_PRIMARY);
		tpc.addType(TextPlaceholderComponent.TYPE_TERTIARY);
		slide.addComponent(tpc);
		
//		XmlIO.save(Paths.get("C:\\Users\\William\\Desktop\\test\\slides\\test.xml"), slide);
//		Slide s = XmlIO.read(Paths.get("C:\\Users\\William\\Desktop\\test\\slides\\test.xml"), BasicSlide.class);
		
		XmlIO.save(Paths.get("D:\\Personal\\Praisenter\\slides\\test.xml"), slide);
		Slide s = XmlIO.read(Paths.get("D:\\Personal\\Praisenter\\slides\\test.xml"), BasicSlide.class);
		
		System.out.println(s.getId());
	}
}
