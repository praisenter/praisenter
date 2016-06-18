package org.praisenter.javafx.slide;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.media.MediaType;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideGradientCycleType;
import org.praisenter.slide.graphics.SlideGradientStop;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.graphics.SlideStrokeCap;
import org.praisenter.slide.graphics.SlideStrokeJoin;
import org.praisenter.slide.graphics.SlideStrokeStyle;
import org.praisenter.slide.graphics.SlideStrokeType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public final class JavaFXTypeConverter {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private JavaFXTypeConverter() {}
	
	// paint
	
	public static Color toJavaFX(SlideColor color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public static SlideColor fromJavaFX(Color color) {
		return new SlideColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
	}
	
	public static Paint toJavaFX(SlidePaint paint) {
		Paint bgPaint = null;
		if (paint instanceof SlideColor) {
			bgPaint = JavaFXTypeConverter.toJavaFX((SlideColor)paint);
		} else if (paint instanceof SlideLinearGradient) {
			bgPaint = JavaFXTypeConverter.toJavaFX((SlideLinearGradient)paint);
		} else if (paint instanceof SlideRadialGradient) {
			bgPaint = JavaFXTypeConverter.toJavaFX((SlideRadialGradient)paint);
		}
		return bgPaint;
	}
	
	// text alignment

	public static TextAlignment toJavaFX(HorizontalTextAlignment alignment) {
		switch (alignment) {
			case RIGHT:
				return TextAlignment.RIGHT;
			case CENTER:
				return TextAlignment.CENTER;
			case JUSTIFY:
				return TextAlignment.JUSTIFY;
			default:
				return TextAlignment.LEFT;
		}
	}
	
	public static HorizontalTextAlignment fromJavaFX(TextAlignment alignment) {
		switch (alignment) {
			case RIGHT:
				return HorizontalTextAlignment.RIGHT;
			case CENTER:
				return HorizontalTextAlignment.CENTER;
			case JUSTIFY:
				return HorizontalTextAlignment.JUSTIFY;
			default:
				return HorizontalTextAlignment.LEFT;
		}
	}
	
	public static Pos toJavaFX(VerticalTextAlignment alignment) {
		switch (alignment) {
			case CENTER:
				return Pos.CENTER_LEFT;
			case BOTTOM:
				return Pos.BOTTOM_LEFT;
			default:
				return Pos.TOP_LEFT;
		}
	}
	
	public static VerticalTextAlignment fromJavaFX(Pos alignment) {
		switch (alignment) {
			case CENTER:
			case CENTER_RIGHT:
			case CENTER_LEFT:
				return VerticalTextAlignment.CENTER;
			case BOTTOM_CENTER:
			case BOTTOM_RIGHT:
			case BOTTOM_LEFT:
				return VerticalTextAlignment.BOTTOM;
			default:
				return VerticalTextAlignment.TOP;
		}
	}
	
	public static Pos toJavaFX(VerticalTextAlignment valign, HorizontalTextAlignment halign) {
		if (valign == VerticalTextAlignment.TOP) {
			if (halign == HorizontalTextAlignment.RIGHT) {
				return Pos.TOP_RIGHT;
			} else if (halign == HorizontalTextAlignment.CENTER) {
				return Pos.TOP_CENTER;
			} else if (halign == HorizontalTextAlignment.JUSTIFY) {
				return Pos.TOP_CENTER;
			} else {
				return Pos.TOP_LEFT;
			}
		} else if (valign == VerticalTextAlignment.CENTER) {
			if (halign == HorizontalTextAlignment.RIGHT) {
				return Pos.CENTER_RIGHT;
			} else if (halign == HorizontalTextAlignment.CENTER) {
				return Pos.CENTER;
			} else if (halign == HorizontalTextAlignment.JUSTIFY) {
				return Pos.CENTER;
			} else {
				return Pos.CENTER_LEFT;
			}
		} else if (valign == VerticalTextAlignment.BOTTOM) {
			if (halign == HorizontalTextAlignment.RIGHT) {
				return Pos.BOTTOM_RIGHT;
			} else if (halign == HorizontalTextAlignment.CENTER) {
				return Pos.BOTTOM_CENTER;
			} else if (halign == HorizontalTextAlignment.JUSTIFY) {
				return Pos.BOTTOM_CENTER;
			} else {
				return Pos.BOTTOM_LEFT;
			}
		} else {
			return Pos.CENTER;
		}
	}
	
	// gradient
	
	public static CycleMethod toJavaFX(SlideGradientCycleType cycle) {
		switch (cycle) {
			case REPEAT:
				return CycleMethod.REPEAT;
			case REFLECT:
				return CycleMethod.REFLECT;
			default:
				return CycleMethod.NO_CYCLE;
		}
	}
	
	public static SlideGradientCycleType fromJavaFX(CycleMethod cycle) {
		switch (cycle) {
			case REPEAT:
				return SlideGradientCycleType.REPEAT;
			case REFLECT:
				return SlideGradientCycleType.REFLECT;
			default:
				return SlideGradientCycleType.NONE;
		}
	}
	
	public static Stop toJavaFX(SlideGradientStop stop) {
		return new Stop(stop.getOffset(), toJavaFX(stop.getColor()));
	}
	
	public static SlideGradientStop fromJavaFX(Stop stop) {
		return new SlideGradientStop(stop.getOffset(), fromJavaFX(stop.getColor()));
	}
	
	public static List<Stop> toJavaFX(List<SlideGradientStop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<Stop> stps = new ArrayList<Stop>();
		for (SlideGradientStop s : stops) {
			stps.add(toJavaFX(s));
		}
		return stps;
	}
	
	public static List<SlideGradientStop> fromJavaFX(List<Stop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<SlideGradientStop> stps = new ArrayList<SlideGradientStop>();
		for (Stop s : stops) {
			stps.add(fromJavaFX(s));
		}
		return stps;
	}

	public static LinearGradient toJavaFX(SlideLinearGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new LinearGradient(
				gradient.getStartX(), 
				gradient.getStartY(), 
				gradient.getEndX(), 
				gradient.getEndY(), 
				true, 
				toJavaFX(gradient.getCycleType()), 
				toJavaFX(gradient.getStops()));
	}
	
	public static SlideLinearGradient fromJavaFX(LinearGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new SlideLinearGradient(
				gradient.getStartX(), 
				gradient.getStartY(), 
				gradient.getEndX(), 
				gradient.getEndY(),  
				fromJavaFX(gradient.getCycleMethod()), 
				fromJavaFX(gradient.getStops()));
	}

	public static RadialGradient toJavaFX(SlideRadialGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new RadialGradient(
				0.0, 
				0.0, 
				gradient.getCenterX(), 
				gradient.getCenterY(), 
				gradient.getRadius(),
				true, 
				toJavaFX(gradient.getCycleType()), 
				toJavaFX(gradient.getStops()));
	}
	
	public static SlideRadialGradient fromJavaFX(RadialGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new SlideRadialGradient(
				gradient.getCenterX(), 
				gradient.getCenterY(), 
				gradient.getRadius(),
				fromJavaFX(gradient.getCycleMethod()), 
				fromJavaFX(gradient.getStops()));
	}
	
	// border

	public static StrokeLineCap toJavaFX(SlideStrokeCap cap) {
		switch (cap) {
			case BUTT:
				return StrokeLineCap.BUTT;
			case ROUND:
				return StrokeLineCap.ROUND;
			default:
				return StrokeLineCap.SQUARE;
		}
	}
	
	public static SlideStrokeCap fromJavaFX(StrokeLineCap cap) {
		switch (cap) {
			case BUTT:
				return SlideStrokeCap.BUTT;
			case ROUND:
				return SlideStrokeCap.ROUND;
			default:
				return SlideStrokeCap.SQUARE;
		}
	}
	
	public static StrokeLineJoin toJavaFX(SlideStrokeJoin join) {
		switch (join) {
			case BEVEL:
				return StrokeLineJoin.BEVEL;
			case ROUND:
				return StrokeLineJoin.ROUND;
			default:
				return StrokeLineJoin.MITER;
		}
	}
	
	public static SlideStrokeJoin fromJavaFX(StrokeLineJoin join) {
		switch (join) {
			case BEVEL:
				return SlideStrokeJoin.BEVEL;
			case ROUND:
				return SlideStrokeJoin.ROUND;
			default:
				return SlideStrokeJoin.MITER;
		}
	}
	
	public static StrokeType toJavaFX(SlideStrokeType type) {
		switch (type) {
			case INSIDE:
				return StrokeType.INSIDE;
			case OUTSIDE:
				return StrokeType.OUTSIDE;
			default:
				return StrokeType.CENTERED;
		}
	}
	
	public static SlideStrokeType fromJavaFX(StrokeType type) {
		switch (type) {
			case INSIDE:
				return SlideStrokeType.INSIDE;
			case OUTSIDE:
				return SlideStrokeType.OUTSIDE;
			default:
				return SlideStrokeType.CENTERED;
		}
	}
	
	public static BorderStrokeStyle toJavaFX(SlideStrokeStyle style) {
		if (style == null) {
			return null;
		}
		
		return new BorderStrokeStyle(
				toJavaFX(style.getType()), 
				toJavaFX(style.getJoin()), 
				toJavaFX(style.getCap()), 
				Double.MAX_VALUE, 
				0.0, 
				Arrays.asList(style.getDashes()));
	}
	
	public static SlideStrokeStyle fromJavaFX(BorderStrokeStyle style) {
		if (style == null) {
			return null;
		}
		
		return new SlideStrokeStyle(
				fromJavaFX(style.getType()), 
				fromJavaFX(style.getLineJoin()), 
				fromJavaFX(style.getLineCap()), 
				style.getDashArray().toArray(new Double[0]));
	}
	
	public static BorderStroke toJavaFX(SlideStroke stroke) {
		if (stroke == null) {
			return null;
		}
		// convert to JavaFX paint type
		SlidePaint sp = stroke.getPaint();
		Paint paint = null;
		if (sp instanceof SlideColor) {
			paint = toJavaFX((SlideColor)sp);
		} else if (sp instanceof SlideLinearGradient) {
			paint = toJavaFX((SlideLinearGradient)sp);
		} else if (sp instanceof SlideRadialGradient) {
			paint = toJavaFX((SlideRadialGradient)sp);
		} else {
			LOGGER.warn("Media paints are not supported with borders.");
		}
		if (paint == null) {
			return null;
		}
		return new BorderStroke(
				paint,
				toJavaFX(stroke.getStyle()),
				new CornerRadii(stroke.getRadius()),
				new BorderWidths(stroke.getWidth()));
	}
	
	// background/scaling
	
	public static BackgroundSize toJavaFX(ScaleType scaling) {
		BackgroundSize size = BackgroundSize.DEFAULT;
		if (scaling == ScaleType.NONUNIFORM) {
			size = new BackgroundSize(1.0, 1.0, true, true, false, false);
		} else if (scaling == ScaleType.UNIFORM) {
			size = new BackgroundSize(0.0, 0.0, false, false, true, false);
		}
		return size;
	}
	
	public static ScaleType fromJavaFX(BackgroundSize size) {
		if (size == null || size == BackgroundSize.DEFAULT) {
			return ScaleType.NONE;
		} else if (!size.isContain() && !size.isCover() && size.isWidthAsPercentage() && size.isHeightAsPercentage()) {
			return ScaleType.NONUNIFORM;
		} else if (size.isContain() && !size.isCover() && !size.isWidthAsPercentage() && !size.isHeightAsPercentage()) {
			return ScaleType.UNIFORM;
		}
		return ScaleType.NONE;
	}

	// fonts
	
	public static FontWeight toJavaFX(SlideFontWeight weight) {
		switch (weight) {
			case BLACK:
				return FontWeight.BLACK;
			case BOLD:
				return FontWeight.BOLD;
			case EXTRA_BOLD:
				return FontWeight.EXTRA_BOLD;
			case EXTRA_LIGHT:
				return FontWeight.EXTRA_LIGHT;
			case LIGHT:
				return FontWeight.LIGHT;
			case MEDIUM:
				return FontWeight.MEDIUM;
			case SEMI_BOLD:
				return FontWeight.SEMI_BOLD;
			case THIN:
				return FontWeight.THIN;
			case NORMAL:
			default:
				return FontWeight.NORMAL;
		}
	}
	
	public static SlideFontWeight fromJavaFX(FontWeight weight) {
		switch (weight) {
			case BLACK:
				return SlideFontWeight.BLACK;
			case BOLD:
				return SlideFontWeight.BOLD;
			case EXTRA_BOLD:
				return SlideFontWeight.EXTRA_BOLD;
			case EXTRA_LIGHT:
				return SlideFontWeight.EXTRA_LIGHT;
			case LIGHT:
				return SlideFontWeight.LIGHT;
			case MEDIUM:
				return SlideFontWeight.MEDIUM;
			case SEMI_BOLD:
				return SlideFontWeight.SEMI_BOLD;
			case THIN:
				return SlideFontWeight.THIN;
			case NORMAL:
			default:
				return SlideFontWeight.NORMAL;
		}
	}
	
	public static FontPosture toJavaFX(SlideFontPosture posture) {
		switch (posture) {
			case ITALIC:
				return FontPosture.ITALIC;
			case REGULAR:
			default:
				return FontPosture.REGULAR;
		}
	}
	
	public static SlideFontPosture fromJavaFX(FontPosture posture) {
		switch (posture) {
			case ITALIC:
				return SlideFontPosture.ITALIC;
			case REGULAR:
			default:
				return SlideFontPosture.REGULAR;
		}
	}
	
	public static Font toJavaFX(SlideFont font) {
		if (font == null) {
			return Font.getDefault();
		}
		return Font.font(
				font.getFamily(), 
				toJavaFX(font.getWeight()),
				toJavaFX(font.getPosture()),
				font.getSize());
	}
	
	public static SlideFont fromJavaFX(Font font) {
		if (font == null) {
			return null;
		}
		String style = font.getStyle();
		String[] styles = (style == null ? "" : style.trim().toUpperCase()).split(" ");
		return new SlideFont(
				font.getFamily(), 
				fromJavaFX(getWeight(styles)), 
				fromJavaFX(getPosture(styles)), 
				font.getSize());
	}
	
	private static final FontWeight getWeight(String[] styles) {
		for (String s : styles) {
			FontWeight weight = FontWeight.findByName(s);
			if (weight != null) {
				return weight;
			}
		}
		return FontWeight.NORMAL;
	}
	
	private static final FontPosture getPosture(String[] styles) {
		for (String s : styles) {
			FontPosture posture = FontPosture.findByName(s);
			if (posture != null) {
				return posture;
			}
		}
		return FontPosture.REGULAR;
	}
	
	// media
	
	// only for video/audio
	public static MediaPlayer toJavaFXMediaPlayer(PraisenterContext context, Media media, boolean loop, boolean mute) {
		// check for missing media
		if (media == null) {
			return null;
		}
		// check the type
		if (media.getMetadata().getType() != MediaType.AUDIO && media.getMetadata().getType() != MediaType.VIDEO) {
			return null;
		}
		try {
			// attempt to open the media
			javafx.scene.media.Media m = new javafx.scene.media.Media(media.getMetadata().getPath().toUri().toString());
			m.setOnError(() -> { LOGGER.error(m.getError()); });
			
			// create a player
			MediaPlayer player = new MediaPlayer(m);
			// set the player attributes
			player.setMute(mute);
			player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 0);
			player.setOnError(() -> { LOGGER.error(player.getError()); });
			
			return player;
		} catch (Exception ex) {
			// if it blows up, then just log the error
			LOGGER.error("Failed to create media or media player.", ex);
		}
		
		return null;
	}
	
	// will work for all media types
	public static Image toJavaFXImage(PraisenterContext context, Media media) {
		// check for missing media
		if (media == null) {
			return null;
		}
		// check the media type
		Image image = null;
		Path path = null;
		if (media.getMetadata().getType() == MediaType.VIDEO) {
			// for video's we just need to show a single frame
			path = context.getMediaLibrary().getFramePath(media);
		} else if (media.getMetadata().getType() == MediaType.IMAGE) {
			// image
			path = media.getMetadata().getPath();
		} else if (media.getMetadata().getType() == MediaType.AUDIO) {
			path = Paths.get("/org/praisenter/resources/music-default-thumbnail.png");
		} else {
			LOGGER.error("Unknown media type " + media.getMetadata().getType());
		}
		
		if (path == null) {
			return null;
		}
		
		try  {
			image = context.getImageCache().get(path);
		} catch (Exception ex) {
			// just log the error
			LOGGER.warn("Failed to load image " + media.getMetadata().getPath() + ".", ex);
		}
		
		if (image == null) {
			return null;
		}
		
		return image;
	}
}
