import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.Slide;
import org.praisenter.slide.graphics.DashPattern;
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
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.PlaceholderType;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.TextPlaceholderComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.xml.XmlIO;


public class TestSlideSaving {
	public static void main(String[] args) throws JAXBException, IOException {
		BasicSlide slide = new BasicSlide();
//		SongSlide slide = new SongSlide();
//		BibleSlide slide = new BibleSlide();
		
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
				new SlideStrokeStyle(SlideStrokeType.INSIDE, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, DashPattern.DASH.getDashes()), 
				5, 
				5);
		
		SlideRadialGradient radial = new SlideRadialGradient(
				0.5, 0.5, 1.5, 
				SlideGradientCycleType.NONE, 
				new SlideGradientStop(0, new SlideColor(0, 0, 0, 0.8)),
				new SlideGradientStop(1, new SlideColor(0, 1, 1, 0.8)));
		
		BasicTextComponent txt = new BasicTextComponent();
		txt.setFont(new SlideFont("Impact", SlideFontWeight.BOLD, SlideFontPosture.REGULAR, 20));
		txt.setFontScaleType(FontScaleType.REDUCE_SIZE_ONLY);
		txt.setWidth(400);
		txt.setHeight(400);
		txt.setX(20);
		txt.setY(100);
		txt.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		txt.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		txt.setPadding(new SlidePadding(10));
		txt.setBackground(new SlideColor(0.5, 0, 0, 0.5));
		txt.setBorder(thick);
		txt.setTextPaint(radial);
		txt.setTextBorder(stroke);
		txt.setLineSpacing(10);
		txt.setText("Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.");
		
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
		
		DateTimeComponent dt = new DateTimeComponent();
		dt.setFont(new SlideFont("Arial", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 20));
		dt.setFontScaleType(FontScaleType.NONE);
		dt.setWidth(600);
		dt.setHeight(200);
		dt.setX(0);
		dt.setY(0);
		dt.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
		dt.setVerticalTextAlignment(VerticalTextAlignment.TOP);
		dt.setPadding(new SlidePadding(20));
		dt.setBackground(new SlideColor(0.5, 0, 0, 0.5));
		dt.setTextPaint(new SlideColor(1.0, 0, 0, 1));
		dt.setDateTimeFormat(new SimpleDateFormat("M/d/yyyy h:mm a z"));
		
		TextPlaceholderComponent tp = new TextPlaceholderComponent();
		tp.setFont(new SlideFont("Verdana", SlideFontWeight.NORMAL, SlideFontPosture.ITALIC, 5));
		tp.setFontScaleType(FontScaleType.BEST_FIT);
		tp.setWidth(200);
		tp.setHeight(400);
		tp.setX(200);
		tp.setY(0);
		tp.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
		tp.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
		tp.setPadding(new SlidePadding(10));
		//tp.setBackground(new SlideColor(0.5, 0, 0.5, 0.5));
		tp.setTextPaint(gradient);
		tp.setTextBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE), 1, 0));
		tp.setLineSpacing(2);
		tp.setPlaceholderType(PlaceholderType.TITLE);
		//tp.setVariants(variants);
		
		CountdownComponent cd = new CountdownComponent();
		cd.setFont(new SlideFont("Segoe UI Light", SlideFontWeight.NORMAL, SlideFontPosture.REGULAR, 100));
		cd.setFontScaleType(FontScaleType.BEST_FIT);
		cd.setWidth(400);
		cd.setHeight(100);
		cd.setX(200);
		cd.setY(0);
		cd.setTextWrapping(false);
		cd.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
		cd.setVerticalTextAlignment(VerticalTextAlignment.CENTER);
		cd.setPadding(new SlidePadding(10));
		//tp.setBackground(new SlideColor(0.5, 0, 0.5, 0.5));
		cd.setTextPaint(gradient);
		cd.setTextBorder(new SlideStroke(new SlideColor(0, 1, 0, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, new Double[] { 10.0, 10.0, 5.0 }), 1, 0));
//		cd.setLineSpacing(2);
		cd.setCountdownTarget(LocalDateTime.now().plusYears(1).plusMonths(2).plusDays(3).plusHours(4).plusMinutes(5).plusSeconds(6));
		//tp.setVariants(variants);
		
		slide.addComponent(txt);
		slide.addComponent(mc);
		slide.addComponent(dt);
		slide.addComponent(tp);
		slide.addComponent(cd);

//		slide.setBackground(vid);
		slide.setBackground(new SlideColor(0, 0, 1.0, 0.5));
		//slide.setBorder(thick);
		
//		slide.setSongId(UUID.randomUUID());
//		slide.setVerse("v1");
//		slide.getLyrics().add(new SongSlideLyrics("en"));
//		slide.getLyrics().add(new SongSlideLyrics("es-MX"));
		
//		slide.setBookCode("01O");
//		slide.setChapter(1);
//		slide.setVerse(1);
//		slide.getBibles().add("KJV");
		
//		XmlIO.save(Paths.get("C:\\Users\\William\\Desktop\\test\\slides\\test.xml"), slide);
//		Slide s = XmlIO.read(Paths.get("C:\\Users\\William\\Desktop\\test\\slides\\test.xml"), BasicSlide.class);
		
		XmlIO.save(Paths.get("D:\\Personal\\Praisenter\\slides\\test.xml"), slide);
		Slide s = XmlIO.read(Paths.get("D:\\Personal\\Praisenter\\slides\\test.xml"), BasicSlide.class);
		
		System.out.println(s.getId() + " " + s.getClass().getName());
		
//		SlideShow show = new SlideShow();
//		show.getSlides().add(s);
		
//		XmlIO.save(Paths.get("C:\\Users\\William\\Desktop\\test\\slides\\show.xml"), show);
//		SlideShow ss = XmlIO.read(Paths.get("C:\\Users\\William\\Desktop\\test\\slides\\show.xml"), SlideShow.class);
		
//		XmlIO.save(Paths.get("D:\\Personal\\Praisenter\\slides\\show.xml"), show);
//		SlideShow ss = XmlIO.read(Paths.get("D:\\Personal\\Praisenter\\slides\\show.xml"), SlideShow.class);
		
//		System.out.println(ss.getSlides().size());
	}
}
