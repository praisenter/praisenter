package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.logging.slf4j.SLF4JLoggingException;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
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
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.SlideFontPosture;
import org.praisenter.slide.text.SlideFontWeight;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

final class JavaFXTypeConverter {
	private JavaFXTypeConverter() {}
	
	// paint
	
	static Color toJavaFX(SlideColor color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	static SlideColor fromJavaFX(Color color) {
		return new SlideColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
	}
	
	static Paint toJavaFX(SlidePaint paint) {
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

	static TextAlignment toJavaFX(HorizontalTextAlignment alignment) {
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
	
	static HorizontalTextAlignment fromJavaFX(TextAlignment alignment) {
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
	
	static Pos toJavaFX(VerticalTextAlignment alignment) {
		switch (alignment) {
			case CENTER:
				return Pos.CENTER_LEFT;
			case BOTTOM:
				return Pos.BOTTOM_LEFT;
			default:
				return Pos.TOP_LEFT;
		}
	}
	
	static VerticalTextAlignment fromJavaFX(Pos alignment) {
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
	
	// gradient
	
	static CycleMethod toJavaFX(SlideGradientCycleType cycle) {
		switch (cycle) {
			case REPEAT:
				return CycleMethod.REPEAT;
			case REFLECT:
				return CycleMethod.REFLECT;
			default:
				return CycleMethod.NO_CYCLE;
		}
	}
	
	static SlideGradientCycleType fromJavaFX(CycleMethod cycle) {
		switch (cycle) {
			case REPEAT:
				return SlideGradientCycleType.REPEAT;
			case REFLECT:
				return SlideGradientCycleType.REFLECT;
			default:
				return SlideGradientCycleType.NONE;
		}
	}
	
	static Stop toJavaFX(SlideGradientStop stop) {
		return new Stop(stop.getOffset(), toJavaFX(stop.getColor()));
	}
	
	static SlideGradientStop fromJavaFX(Stop stop) {
		return new SlideGradientStop(stop.getOffset(), fromJavaFX(stop.getColor()));
	}
	
	static List<Stop> toJavaFX(List<SlideGradientStop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<Stop> stps = new ArrayList<Stop>();
		for (SlideGradientStop s : stops) {
			stps.add(toJavaFX(s));
		}
		return stps;
	}
	
	static List<SlideGradientStop> fromJavaFX(List<Stop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<SlideGradientStop> stps = new ArrayList<SlideGradientStop>();
		for (Stop s : stops) {
			stps.add(fromJavaFX(s));
		}
		return stps;
	}

	static LinearGradient toJavaFX(SlideLinearGradient gradient) {
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
	
	static SlideLinearGradient fromJavaFX(LinearGradient gradient) {
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

	static RadialGradient toJavaFX(SlideRadialGradient gradient) {
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
	
	static SlideRadialGradient fromJavaFX(RadialGradient gradient) {
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

	static StrokeLineCap toJavaFX(SlideStrokeCap cap) {
		switch (cap) {
			case BUTT:
				return StrokeLineCap.BUTT;
			case ROUND:
				return StrokeLineCap.ROUND;
			default:
				return StrokeLineCap.SQUARE;
		}
	}
	
	static SlideStrokeCap fromJavaFX(StrokeLineCap cap) {
		switch (cap) {
			case BUTT:
				return SlideStrokeCap.BUTT;
			case ROUND:
				return SlideStrokeCap.ROUND;
			default:
				return SlideStrokeCap.SQUARE;
		}
	}
	
	static StrokeLineJoin toJavaFX(SlideStrokeJoin join) {
		switch (join) {
			case BEVEL:
				return StrokeLineJoin.BEVEL;
			case ROUND:
				return StrokeLineJoin.ROUND;
			default:
				return StrokeLineJoin.MITER;
		}
	}
	
	static SlideStrokeJoin fromJavaFX(StrokeLineJoin join) {
		switch (join) {
			case BEVEL:
				return SlideStrokeJoin.BEVEL;
			case ROUND:
				return SlideStrokeJoin.ROUND;
			default:
				return SlideStrokeJoin.MITER;
		}
	}
	
	static StrokeType toJavaFX(SlideStrokeType type) {
		switch (type) {
			case INSIDE:
				return StrokeType.INSIDE;
			case OUTSIDE:
				return StrokeType.OUTSIDE;
			default:
				return StrokeType.CENTERED;
		}
	}
	
	static SlideStrokeType fromJavaFX(StrokeType type) {
		switch (type) {
			case INSIDE:
				return SlideStrokeType.INSIDE;
			case OUTSIDE:
				return SlideStrokeType.OUTSIDE;
			default:
				return SlideStrokeType.CENTERED;
		}
	}
	
	static BorderStrokeStyle toJavaFX(SlideStrokeStyle style) {
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
	
	static SlideStrokeStyle fromJavaFX(BorderStrokeStyle style) {
		if (style == null) {
			return null;
		}
		
		return new SlideStrokeStyle(
				fromJavaFX(style.getType()), 
				fromJavaFX(style.getLineJoin()), 
				fromJavaFX(style.getLineCap()), 
				style.getDashArray().toArray(new Double[0]));
	}
	
	static BorderStroke toJavaFX(SlideStroke stroke) {
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
			// FIXME show warning for media paint
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
	
	static BackgroundSize toJavaFX(ScaleType scaling) {
		BackgroundSize size = BackgroundSize.DEFAULT;
		if (scaling == ScaleType.NONUNIFORM) {
			size = new BackgroundSize(1.0, 1.0, true, true, false, false);
		} else if (scaling == ScaleType.UNIFORM) {
			size = new BackgroundSize(0.0, 0.0, false, false, true, false);
		}
		return size;
	}
	
	static ScaleType fromJavaFX(BackgroundSize size) {
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
	
	static FontWeight toJavaFX(SlideFontWeight weight) {
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
	
	static SlideFontWeight fromJavaFX(FontWeight weight) {
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
	
	static FontPosture toJavaFX(SlideFontPosture posture) {
		switch (posture) {
			case ITALIC:
				return FontPosture.ITALIC;
			case REGULAR:
			default:
				return FontPosture.REGULAR;
		}
	}
	
	static SlideFontPosture fromJavaFX(FontPosture posture) {
		switch (posture) {
			case ITALIC:
				return SlideFontPosture.ITALIC;
			case REGULAR:
			default:
				return SlideFontPosture.REGULAR;
		}
	}
	
	static Font toJavaFX(SlideFont font) {
		if (font == null) {
			return Font.getDefault();
		}
		return Font.font(
				font.getFamily(), 
				toJavaFX(font.getWeight()),
				toJavaFX(font.getPosture()),
				font.getSize());
	}
	
	// media
	
	// only for video/audio
	static MediaPlayer toJavaFXMediaPlayer(PraisenterContext context, Media media, boolean loop, boolean mute) {
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
			// create a player
			MediaPlayer player = new MediaPlayer(m);
			// set the player attributes
			player.setMute(mute);
			player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 0);
			
			return player;
		} catch (Exception ex) {
			// if it blows up, then just log the error
			// FIXME handle errors
//			LOGGER.error("Failed to create media or media player.", ex);
		}
		
		return null;
	}
	
	// will work for all media types
	static Image toJavaFXImage(PraisenterContext context, Media media) {
		// check for missing media
		if (media == null) {
			return null;
		}
		// check the media type
		Image image = null;
		if (media.getMetadata().getType() == MediaType.VIDEO) {
			// for video's we just need to show a single frame
			try  {
				image = context.getImageCache().get(context.getMediaLibrary().getFramePath(media));
			} catch (Exception ex) {
				// just log the error
				// FIXME handle errors
//				LOGGER.warn("Failed to load image " + m.getMetadata().getPath() + ".", ex);
			}
		} else if (media.getMetadata().getType() == MediaType.IMAGE) {
			// image
			try  {
				image = context.getImageCache().get(media.getMetadata().getPath());
			} catch (Exception ex) {
				// just log the error
				// FIXME handle errors
//				LOGGER.warn("Failed to load image " + m.getMetadata().getPath() + ".", ex);
			}
		} else if (media.getMetadata().getType() == MediaType.AUDIO) {
			// TODO show a default icon for audio
		}
		
		if (image == null) {
			return null;
		}
		
		return image;
	}
}
