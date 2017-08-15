import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.praisenter.Constants;
import org.praisenter.MediaType;
import org.praisenter.Tag;
import org.praisenter.TextType;
import org.praisenter.TextVariant;
import org.praisenter.bible.Bible;
import org.praisenter.bible.BibleReferenceSet;
import org.praisenter.bible.BibleReferenceTextStore;
import org.praisenter.bible.BibleReferenceVerse;
import org.praisenter.bible.Book;
import org.praisenter.bible.Chapter;
import org.praisenter.bible.Verse;
import org.praisenter.configuration.Configuration;
import org.praisenter.json.JsonIO;
import org.praisenter.media.Media;
import org.praisenter.slide.BasicSlide;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Blinds;
import org.praisenter.slide.animation.Direction;
import org.praisenter.slide.animation.Fade;
import org.praisenter.slide.animation.Operation;
import org.praisenter.slide.animation.Orientation;
import org.praisenter.slide.animation.Push;
import org.praisenter.slide.animation.ShapeType;
import org.praisenter.slide.animation.Shaped;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Split;
import org.praisenter.slide.animation.Swap;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.animation.Zoom;
import org.praisenter.slide.easing.Cubic;
import org.praisenter.slide.easing.EasingType;
import org.praisenter.slide.easing.Elastic;
import org.praisenter.slide.easing.Exponential;
import org.praisenter.slide.easing.Linear;
import org.praisenter.slide.easing.Quadratic;
import org.praisenter.slide.easing.Quartic;
import org.praisenter.slide.easing.Quintic;
import org.praisenter.slide.easing.Sinusoidal;
import org.praisenter.slide.effects.SlideColorAdjust;
import org.praisenter.slide.effects.SlideShadow;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.ShadowType;
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
import org.praisenter.slide.media.MediaComponent;
import org.praisenter.slide.media.MediaObject;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.TextPlaceholderComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.xml.XmlIO;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonTest {

	public static void main(String[] args) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		
		Bible bible = new Bible();
		bible.setCopyright("test");
		bible.setLanguage("en");
		bible.setModifiedDate(Instant.now());
		bible.setName("Testing");
		bible.setNotes("some notes");
		bible.setSource("me");
		
		Book book = new Book();
		book.setName("Book 1");
		book.setNumber((short)1);
		
		Chapter chapter = new Chapter();
		chapter.setNumber((short)1);
		
		Verse verse = new Verse();
		verse.setNumber((short)1);
		verse.setText("The first verse");
		
		chapter.getVerses().add(verse);
		book.getChapters().add(chapter);
		bible.getBooks().add(book);
		
		String value = mapper.writeValueAsString(bible);
		System.out.println(value);
		
		// attempt to read it in
		Bible out = mapper.readValue(value, Bible.class);
		System.out.println(out.getId().equals(bible.getId()));
		
		String json = "{  \"format\" : \"praisenter2\",  \"version\" : 2,  \"id\" : \"31301a9d-69e8-4d33-93f2-cbc9e7018b2b\",  \"createdDate\" : \"2017-08-10T12:22:17.345Z\",  \"modifiedDate\" : \"2017-08-10T12:22:17.345Z\",  \"hadImportWarning\" : false,  \"name\" : \"Testing\",  \"language\" : \"en\",  \"source\" : \"me\",  \"copyright\" : \"test\",  \"books\" : [ {    \"name\" : \"Book 1\",    \"number\" : 1,    \"chapters\" : [ {      \"number\" : 1,      \"verses\" : [ {        \"number\" : 1,        \"text\" : \"The first verse\"      } ],      \"lastVerse\" : {        \"number\" : 1,        \"text\" : \"The first verse\"      },      \"maxVerseNumber\" : 1    } ],    \"maxChapterNumber\" : 1,    \"lastChapter\" : {      \"number\" : 1,      \"verses\" : [ {        \"number\" : 1,        \"text\" : \"The first verse\"      } ],      \"lastVerse\" : {        \"number\" : 1,        \"text\" : \"The first verse\"      },      \"maxVerseNumber\" : 1    }  } ],  \"notes\" : \"some notes\"}";
		Bible out2 = mapper.readValue(json, Bible.class);
		System.out.println(out2.getVersion());
		System.out.println(out2.getFormat());
		
//		Path path = Paths.get("C:\\Users\\wbittle\\Praisenter3\\slides\\");
//		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
//			for (Path file : stream) {
//				try {
//					BasicSlide s = XmlIO.read(file, BasicSlide.class);
//					mapper.writeValue(file.resolveSibling(file.getFileName().toString().replaceAll(".xml", ".json")).toFile(), s);
//				} catch (JAXBException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
		
//		Path path = Paths.get("C:\\Users\\wbittle\\Praisenter3\\media\\_metadata\\");
//		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
//			for (Path file : stream) {
//				try {
//					Media m = XmlIO.read(file, Media.class);
//					mapper.writeValue(file.resolveSibling(file.getFileName().toString().replaceAll(".xml", ".json")).toFile(), m);
//				} catch (JAXBException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
		
//		Configuration conf = Configuration.load();
//		JsonIO.write(Paths.get(Constants.CONFIG_ABSOLUTE_FILE_PATH).resolveSibling("configuration.json"), conf);
		
		BasicSlide slide = new BasicSlide();
		slide.setBackground(new SlideColor(0.5, 0.5, 0.5, 0.5));
		slide.setBorder(new SlideStroke(new SlideColor(0.2, 0.5, 0.6, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, null), 5, 0));
		slide.setHeight(600);
		slide.setLastModifiedDate(Instant.now());
		slide.setName("Test");
		slide.setOpacity(0.9);
		slide.setTime(10000l);
		slide.setWidth(800);
		slide.setX(0);
		slide.setY(0);
		
		SlideAnimation sa1 = new SlideAnimation(UUID.randomUUID(), new Swap(AnimationType.IN, 1, 1000, 1, false, new Linear(EasingType.IN)));
		SlideAnimation sa2 = new SlideAnimation(UUID.randomUUID(), new Fade(AnimationType.OUT, 1000, 0, 3, true, new Quadratic(EasingType.OUT)));
		SlideAnimation sa3 = new SlideAnimation(UUID.randomUUID(), new Blinds(AnimationType.IN, 500, 1000, 1, false, new Cubic(EasingType.BOTH), Orientation.HORIZONTAL, 10));
		SlideAnimation sa4 = new SlideAnimation(UUID.randomUUID(), new Swipe(AnimationType.IN, 200, 1000, 1, false, new Quintic(EasingType.IN), Direction.CLOCKWISE));
		SlideAnimation sa5 = new SlideAnimation(UUID.randomUUID(), new Push(AnimationType.IN, 300, 1000, 1, false, new Quartic(EasingType.IN), Direction.UP));
		SlideAnimation sa6 = new SlideAnimation(UUID.randomUUID(), new Shaped(AnimationType.IN, 400, 1000, 1, false, new Sinusoidal(EasingType.IN), ShapeType.CIRCLE, Operation.COLLAPSE));
		SlideAnimation sa7 = new SlideAnimation(UUID.randomUUID(), new Split(AnimationType.IN, 500, 1000, 1, false, new Exponential(EasingType.IN), Orientation.VERTICAL, Operation.EXPAND));
		SlideAnimation sa8 = new SlideAnimation(UUID.randomUUID(), new Zoom(AnimationType.IN, 600, 1000, 1, false, new Elastic(EasingType.IN)));
		slide.getAnimations().add(sa1);
		slide.getAnimations().add(sa2);
		slide.getAnimations().add(sa3);
		slide.getAnimations().add(sa4);
		slide.getAnimations().add(sa5);
		slide.getAnimations().add(sa6);
		slide.getAnimations().add(sa7);
		slide.getAnimations().add(sa8);
		
		slide.getTags().add(new Tag("testing"));
		slide.getTags().add(new Tag("praisenter"));
		
		BasicTextComponent c1 = new BasicTextComponent();
		c1.setBackground(new SlideLinearGradient(0, 0, 1, 1, SlideGradientCycleType.REFLECT, new SlideGradientStop[] { new SlideGradientStop(0, new SlideColor()), new SlideGradientStop(1, new SlideColor(1, 1, 1, 1)) }));
		c1.setBorder(new SlideStroke(new SlideColor(0.2, 0.5, 0.6, 1), new SlideStrokeStyle(SlideStrokeType.CENTERED, SlideStrokeJoin.MITER, SlideStrokeCap.SQUARE, DashPattern.DASH_DOT.getScaledDashPattern(1)), 5, 0));
		c1.setHeight(200);
		c1.setOpacity(0.5);
		c1.setWidth(400);
		c1.setX(100);
		c1.setY(50);
		c1.setGlow(new SlideShadow(ShadowType.INNER, new SlideColor(), 2, 2, 5, 0));
		c1.setShadow(new SlideShadow(ShadowType.OUTER, new SlideColor(), 2, 2, 5, 0));
		c1.setFont(new SlideFont("arial", SlideFontWeight.BLACK, SlideFontPosture.ITALIC, 10));
		c1.setFontScaleType(FontScaleType.BEST_FIT);
		c1.setHorizontalTextAlignment(HorizontalTextAlignment.JUSTIFY);
		c1.setLineSpacing(5);
		c1.setPadding(new SlidePadding(4));
		c1.setText("Hello!");
		c1.setTextBorder(new SlideStroke(new SlideColor(0.2, 0.5, 0.6, 1), new SlideStrokeStyle(SlideStrokeType.INSIDE, SlideStrokeJoin.BEVEL, SlideStrokeCap.ROUND, null), 5, 10));
		c1.setTextGlow(new SlideShadow(ShadowType.INNER, new SlideColor(), 2, 2, 5, 0));
		c1.setTextPaint(new SlideRadialGradient(0.5, 0.5, 1, SlideGradientCycleType.REPEAT, new SlideGradientStop[] { new SlideGradientStop(0, new SlideColor()), new SlideGradientStop(1, new SlideColor(1, 1, 1, 1)) }));
		c1.setTextShadow(new SlideShadow(ShadowType.OUTER, new SlideColor(), 2, 2, 5, 0));
		c1.setTextWrapping(true);
		c1.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
		
		DateTimeComponent c2 = new DateTimeComponent();
		c2.setDateTimeFormat(new SimpleDateFormat());
		
		CountdownComponent c3 = new CountdownComponent();
		c3.setCountdownFormat(CountdownComponent.DEFAULT_FORMAT);
		c3.setCountdownTarget(LocalDateTime.now());
		c3.setCountdownTimeOnly(false);
		
		TextPlaceholderComponent c4 = new TextPlaceholderComponent();
		c4.setPlaceholderType(TextType.TEXT);
		c4.setPlaceholderVariant(TextVariant.PRIMARY);
		
		MediaComponent c5 = new MediaComponent();
		c5.setMedia(new MediaObject(UUID.randomUUID(), "test.png", MediaType.IMAGE, ScaleType.UNIFORM, false, true, new SlideColorAdjust(0.2, 0.3, -0.2, 0)));
		
		slide.addComponent(c1);
		slide.addComponent(c2);
		slide.addComponent(c3);
		slide.addComponent(c4);
		slide.addComponent(c5);
		
		BibleReferenceTextStore data = new BibleReferenceTextStore();
		BibleReferenceSet pset = new BibleReferenceSet();
		pset.getReferenceVerses().add(new BibleReferenceVerse(UUID.randomUUID(), "test", "Genesis", (short)1, (short)1, (short)1, "This is the text"));
		data.setVariant(TextVariant.PRIMARY, pset);
		slide.setPlaceholderData(data);
		
		value = mapper.writeValueAsString(slide);
		System.out.println(value);
		Slide out3 = mapper.readValue(value, Slide.class);
		
		System.out.println(out3.getName());
	}
}
