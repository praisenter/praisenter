package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Transform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.text.TextMeasurer;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SongSlide;
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
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

// FIXME we may just end up editing the slide types and regenerating the JavaFX UI for editing 
public final class JavaFxSlideConverter {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private final PraisenterContext context;
	
	public JavaFxSlideConverter(PraisenterContext context) {
		this.context = context;
	}
	
	// graphics
	
	public Color to(SlideColor color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public SlideColor from(Color color) {
		return new SlideColor(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
	}
	
	public CycleMethod to(SlideGradientCycleType cycle) {
		switch (cycle) {
			case REPEAT:
				return CycleMethod.REPEAT;
			case REFLECT:
				return CycleMethod.REFLECT;
			default:
				return CycleMethod.NO_CYCLE;
		}
	}
	
	public SlideGradientCycleType from(CycleMethod cycle) {
		switch (cycle) {
			case REPEAT:
				return SlideGradientCycleType.REPEAT;
			case REFLECT:
				return SlideGradientCycleType.REFLECT;
			default:
				return SlideGradientCycleType.NONE;
		}
	}
	
	public StrokeLineCap to(SlideStrokeCap cap) {
		switch (cap) {
			case BUTT:
				return StrokeLineCap.BUTT;
			case ROUND:
				return StrokeLineCap.ROUND;
			default:
				return StrokeLineCap.SQUARE;
		}
	}
	
	public SlideStrokeCap from(StrokeLineCap cap) {
		switch (cap) {
			case BUTT:
				return SlideStrokeCap.BUTT;
			case ROUND:
				return SlideStrokeCap.ROUND;
			default:
				return SlideStrokeCap.SQUARE;
		}
	}
	
	public StrokeLineJoin to(SlideStrokeJoin join) {
		switch (join) {
			case BEVEL:
				return StrokeLineJoin.BEVEL;
			case ROUND:
				return StrokeLineJoin.ROUND;
			default:
				return StrokeLineJoin.MITER;
		}
	}
	
	public SlideStrokeJoin from(StrokeLineJoin join) {
		switch (join) {
			case BEVEL:
				return SlideStrokeJoin.BEVEL;
			case ROUND:
				return SlideStrokeJoin.ROUND;
			default:
				return SlideStrokeJoin.MITER;
		}
	}
	
	public StrokeType to(SlideStrokeType type) {
		switch (type) {
			case INSIDE:
				return StrokeType.INSIDE;
			case OUTSIDE:
				return StrokeType.OUTSIDE;
			default:
				return StrokeType.CENTERED;
		}
	}
	
	public SlideStrokeType from(StrokeType type) {
		switch (type) {
			case INSIDE:
				return SlideStrokeType.INSIDE;
			case OUTSIDE:
				return SlideStrokeType.OUTSIDE;
			default:
				return SlideStrokeType.CENTERED;
		}
	}
	
	public TextAlignment to(HorizontalTextAlignment alignment) {
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
	
	public HorizontalTextAlignment from(TextAlignment alignment) {
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
	
	public Pos to(VerticalTextAlignment alignment) {
		switch (alignment) {
			case CENTER:
				return Pos.CENTER_LEFT;
			case BOTTOM:
				return Pos.BOTTOM_LEFT;
			default:
				return Pos.TOP_LEFT;
		}
	}
	
	public VerticalTextAlignment from(Pos alignment) {
		switch (alignment) {
			case CENTER:
			case CENTER_LEFT:
			case CENTER_RIGHT:
				return VerticalTextAlignment.CENTER;
			case BOTTOM_CENTER:
			case BOTTOM_LEFT:
			case BOTTOM_RIGHT:
				return VerticalTextAlignment.BOTTOM;
			default:
				return VerticalTextAlignment.TOP;
		}
	}
	
	public Stop to(SlideGradientStop stop) {
		return new Stop(stop.getOffset(), to(stop.getColor()));
	}
	
	public SlideGradientStop from(Stop stop) {
		return new SlideGradientStop(stop.getOffset(), from(stop.getColor()));
	}
	
	public List<Stop> to(List<SlideGradientStop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<Stop> stps = new ArrayList<Stop>();
		for (SlideGradientStop s : stops) {
			stps.add(to(s));
		}
		return stps;
	}
	
	public List<SlideGradientStop> from(List<Stop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<SlideGradientStop> stps = new ArrayList<SlideGradientStop>();
		for (Stop s : stops) {
			stps.add(from(s));
		}
		return stps;
	}
	
	public BorderStrokeStyle to(SlideStrokeStyle style) {
		if (style == null) {
			return null;
		}
		
		return new BorderStrokeStyle(
				to(style.getType()), 
				to(style.getJoin()), 
				to(style.getCap()), 
				Double.MAX_VALUE, 
				0.0, 
				Arrays.asList(style.getDashes()));
	}
	
	public SlideStrokeStyle from(BorderStrokeStyle style) {
		if (style == null) {
			return null;
		}
		
		return new SlideStrokeStyle(
				from(style.getType()), 
				from(style.getLineJoin()), 
				from(style.getLineCap()), 
				style.getDashArray().toArray(new Double[0]));
	}
	
	public LinearGradient to(SlideLinearGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new LinearGradient(
				gradient.getStartX(), 
				gradient.getStartY(), 
				gradient.getEndX(), 
				gradient.getEndY(), 
				true, 
				to(gradient.getCycleType()), 
				to(gradient.getStops()));
	}
	
	public SlideLinearGradient from(LinearGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new SlideLinearGradient(
				gradient.getStartX(), 
				gradient.getStartY(), 
				gradient.getEndX(), 
				gradient.getEndY(), 
				from(gradient.getCycleMethod()), 
				from(gradient.getStops()));
	}
	
	public RadialGradient to(SlideRadialGradient gradient) {
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
				to(gradient.getCycleType()), 
				to(gradient.getStops()));
	}
	
	public SlideRadialGradient from(RadialGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new SlideRadialGradient(
				gradient.getCenterX(), 
				gradient.getCenterY(), 
				gradient.getRadius(),
				from(gradient.getCycleMethod()), 
				from(gradient.getStops()));
	}
	
	public Paint to(SlidePaint paint) {
		if (paint == null) {
			return null;
		}
		
		if (paint instanceof SlideColor) {
			SlideColor c = (SlideColor)paint;
			return to(c);
		} else if (paint instanceof SlideLinearGradient) {
			SlideLinearGradient g = (SlideLinearGradient)paint;
			return to(g);
		} else if (paint instanceof SlideRadialGradient) {
			SlideRadialGradient g = (SlideRadialGradient)paint;
			return to(g);
		} else if (paint instanceof MediaObject) {
			MediaObject mo = (MediaObject)paint;
			MediaLibrary ml = this.context.getMediaLibrary();
			Media media = ml.get(mo.getId());
			if (media != null) {
				if (media.getMetadata().getType() == MediaType.IMAGE) {
					try {
						Image image = this.context.getImageCache().get(media.getMetadata().getPath());
						ImagePattern ptrn = new ImagePattern(image);
						return ptrn;
					} catch (Exception ex) {
						// just log the error
						LOGGER.warn("Failed to load image " + media.getMetadata().getPath() + ".", ex);
					}
				}
			} else {
				// log warning about missing media
				LOGGER.warn("The referenced media {} was not found in the media library.", mo.getId());
			}
			// audio is ignored obviously
			// video is ignored because it should only be used for backgrounds
		}
		return null;
	}
	
	public SlidePaint from(Paint paint) {
		if (paint == null) {
			return null;
		}
		
		if (paint instanceof Color) {
			Color c = (Color)paint;
			return from(c);
		} else if (paint instanceof LinearGradient) {
			LinearGradient g = (LinearGradient)paint;
			return from(g);
		} else if (paint instanceof RadialGradient) {
			RadialGradient g = (RadialGradient)paint;
			return from(g);
		} else if (paint instanceof ImagePattern) {
			// TODO may need to have subclass of Image that contains the media UUID; the Image class is not final
		}
		return null;
	}
	
	public BorderStroke to(SlideStroke stroke) {
		return new BorderStroke(
				to(stroke.getPaint()),
				to(stroke.getStyle()),
				new CornerRadii(stroke.getRadius()),
				new BorderWidths(stroke.getWidth()));
	}
	
	public SlideStroke from(BorderStroke stroke) {
		return new SlideStroke(
				from(stroke.getTopStroke()),
				from(stroke.getTopStyle()),
				stroke.getWidths().getTop(),
				stroke.getRadii().getTopLeftHorizontalRadius());
	}
	
	public BackgroundSize to(ScaleType scaling) {
		BackgroundSize size = BackgroundSize.DEFAULT;
		if (scaling == ScaleType.NONUNIFORM) {
			size = new BackgroundSize(1.0, 1.0, true, true, false, false);
		} else if (scaling == ScaleType.UNIFORM) {
			size = new BackgroundSize(0.0, 0.0, false, false, true, false);
		}
		return size;
	}
	
	public ScaleType from(BackgroundSize size) {
		if (size == BackgroundSize.DEFAULT) {
			return ScaleType.NONE;
		} else if (size.isContain()) {
			return ScaleType.UNIFORM;
		} else {
			return ScaleType.NONUNIFORM;
		}
	}
	
	// nodes
	
	// TODO will need parameters for song/bible or we force the placeholders to be replaced already
	
	public JavaFxSlide to(Slide slide, SlideMode mode, int placeHolders) {
		// FIXME implement
		
		if (slide instanceof SongSlide) {
			// look up the song
			// if not found then what?
			if (mode == SlideMode.PRESENT || mode == SlideMode.EDIT) {
				// Note: EDIT in this context is edit of the slide, not the song
				// then replace the placeholders specified in the placeHolders int
				
				// its possible the song is not found, so use the stored text in the placeholder
				
				// check for xml formatting and strip it
			} else if (mode == SlideMode.MUSICIAN) {
				// we need to format the "xml" properly with a TextPane or group of Text objects
				
				// in fact we probably want to have the song prompt to be statically configured some where with
				// just a background color & text color (for high contrast)
				
				// FEATURE show a scan of the music or something side by side
				
				// we may want to show the musician slide before we show the main one or maybe show the whole song or
				// the current verse + the next
			}
		}
		
		// then do normal slide conversion
		return null;
	}
	
	public Node to(TextComponent component) {
		// compute the bounding text width and height so 
		// we can compute an accurate font size
		double padding = component.getPadding();
		double pw = component.getWidth() - 2 * padding;
		double ph = component.getHeight() - 2 * padding;
		
		// set the wrapping width and the bounds type
		Text text = new Text();
		text.setWrappingWidth(pw);
		text.setBoundsType(TextBoundsType.VISUAL);
		
		// component.getOrder()
		// handled by order of nodes in scene graph

		// component.getText()
		String str = component.getText();
		
		// compute a fitting font, if necessary
		Font base = Font.font(component.getFontName(), component.getFontSize());
		Font font = base;
		if (component.getFontScaleType() == FontScaleType.REDUCE_SIZE_ONLY) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, component.getFontSize(), pw, ph, component.getLineSpacing(), TextBoundsType.VISUAL);
		} else if (component.getFontScaleType() == FontScaleType.BEST_FIT) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, Double.MAX_VALUE, pw, ph, component.getLineSpacing(), TextBoundsType.VISUAL);
		}
		
		// the text, font, text fill, line spacing and horizontal alignment
		text.setText(str);
		text.setFont(font);
		text.setFill(to(component.getTextPaint()));
		text.setLineSpacing(component.getLineSpacing());
		text.setTextAlignment(to(component.getHorizontalTextAlignment()));
		
		// text border
		SlideStroke ss = component.getTextBorder();
		if (ss != null) {
			text.setStroke(to(ss.getPaint()));
			text.setStrokeLineCap(to(ss.getStyle().getCap()));
			text.setStrokeLineJoin(to(ss.getStyle().getJoin()));
			text.setStrokeType(to(ss.getStyle().getType()));
			text.setStrokeWidth(ss.getWidth());
			text.getStrokeDashArray().addAll(ss.getStyle().getDashes());
		}
		
		// positioning, sizing, and padding
		VBox box = new VBox();
		box.setPrefSize(component.getWidth(), component.getHeight());
		box.setLayoutX(component.getX());
		box.setLayoutY(component.getY());
		box.setPadding(new Insets(padding));
		
		// vertical alignment
		box.setAlignment(to(component.getVerticalTextAlignment()));
		
		// background
		SlidePaint bg = component.getBackground();
		if (bg != null) {
			if (bg instanceof MediaObject) {
				MediaObject m = (MediaObject)bg;
				Media media = this.context.getMediaLibrary().get(m.getId());
				if (media != null) {
					if (media.getMetadata().getType() == MediaType.VIDEO) {
						// TODO video - we need to know if we are editing or displaying
					} else if (media.getMetadata().getType() == MediaType.IMAGE) {
						// image
						try  {
							Image image = this.context.getImageCache().get(media.getMetadata().getPath());
							box.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, to(m.getScaling()))));
						} catch (Exception ex) {
							// just log the error
							LOGGER.warn("Failed to load image " + media.getMetadata().getPath() + ".", ex);
						}
					} else {
						// log warning about type (audio)
						LOGGER.warn("The media type {} for media {} is not valid for a background.", media.getMetadata().getType(), media.getMetadata().getPath());
					}
				} else {
					// log warning about missing media
					LOGGER.warn("The referenced media {} was not found in the media library.", m.getId());
				}
			} else {
				// color or gradient
				box.setBackground(new Background(new BackgroundFill(to(bg), ss != null ? new CornerRadii(ss.getRadius()) : null, null)));
			}
		}
		
		// border
		SlideStroke bdr = component.getBorder();
		if (bdr != null) {
			box.setBorder(new Border(to(bdr)));
		}
		
		box.getChildren().add(text);
		
		return box;
	}
	
	public Image thumbnail(Node node, double width, double height) {
		SnapshotParameters params = new SnapshotParameters();
		// TODO fill with the transparent background image
		//params.setFill(fill);
		// TODO scale to fit desired thumbnail size
		//params.setTransform(Transform.scale(x, y));
		return node.snapshot(params, null);
	}
}
