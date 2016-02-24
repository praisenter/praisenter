package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.PraisenterContext;
import org.praisenter.media.Media;
import org.praisenter.media.MediaLibrary;
import org.praisenter.media.MediaType;
import org.praisenter.slide.SlideRegion;
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
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
import javafx.scene.text.TextAlignment;

public abstract class SlideRegionWrapper<T extends SlideRegion> {
	private static final Logger LOGGER = LogManager.getLogger();
	
	final PraisenterContext context;
	final T component;
	final SlideMode mode;
	
	public SlideRegionWrapper(PraisenterContext context, T component, SlideMode mode) {
		this.context = context;
		this.component = component;
		this.mode = mode;
	}
	
	// helpers
	
	Color getColor(SlideColor color) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	CycleMethod getCycleMethod(SlideGradientCycleType cycle) {
		switch (cycle) {
			case REPEAT:
				return CycleMethod.REPEAT;
			case REFLECT:
				return CycleMethod.REFLECT;
			default:
				return CycleMethod.NO_CYCLE;
		}
	}
	
	StrokeLineCap getStrokeLineCap(SlideStrokeCap cap) {
		switch (cap) {
			case BUTT:
				return StrokeLineCap.BUTT;
			case ROUND:
				return StrokeLineCap.ROUND;
			default:
				return StrokeLineCap.SQUARE;
		}
	}
	
	StrokeLineJoin getStrokeLineJoin(SlideStrokeJoin join) {
		switch (join) {
			case BEVEL:
				return StrokeLineJoin.BEVEL;
			case ROUND:
				return StrokeLineJoin.ROUND;
			default:
				return StrokeLineJoin.MITER;
		}
	}
	
	StrokeType getStrokeType(SlideStrokeType type) {
		switch (type) {
			case INSIDE:
				return StrokeType.INSIDE;
			case OUTSIDE:
				return StrokeType.OUTSIDE;
			default:
				return StrokeType.CENTERED;
		}
	}
	
	TextAlignment getTextAlignment(HorizontalTextAlignment alignment) {
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
	
	Pos getPos(VerticalTextAlignment alignment) {
		switch (alignment) {
			case CENTER:
				return Pos.CENTER_LEFT;
			case BOTTOM:
				return Pos.BOTTOM_LEFT;
			default:
				return Pos.TOP_LEFT;
		}
	}
	
	Stop getStop(SlideGradientStop stop) {
		return new Stop(stop.getOffset(), getColor(stop.getColor()));
	}
	
	List<Stop> getGradientStops(List<SlideGradientStop> stops) {
		if (stops == null) {
			return null;
		}
		
		List<Stop> stps = new ArrayList<Stop>();
		for (SlideGradientStop s : stops) {
			stps.add(getStop(s));
		}
		return stps;
	}
	
	BorderStrokeStyle getBorderStrokeStyle(SlideStrokeStyle style) {
		if (style == null) {
			return null;
		}
		
		return new BorderStrokeStyle(
				getStrokeType(style.getType()), 
				getStrokeLineJoin(style.getJoin()), 
				getStrokeLineCap(style.getCap()), 
				Double.MAX_VALUE, 
				0.0, 
				Arrays.asList(style.getDashes()));
	}
	
	LinearGradient getLinearGradient(SlideLinearGradient gradient) {
		if (gradient == null) {
			return null;
		}
		
		return new LinearGradient(
				gradient.getStartX(), 
				gradient.getStartY(), 
				gradient.getEndX(), 
				gradient.getEndY(), 
				true, 
				getCycleMethod(gradient.getCycleType()), 
				getGradientStops(gradient.getStops()));
	}
	
	RadialGradient getRadialGradient(SlideRadialGradient gradient) {
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
				getCycleMethod(gradient.getCycleType()), 
				getGradientStops(gradient.getStops()));
	}
	
	Paint getPaint(SlidePaint paint) {
		if (paint == null) {
			return null;
		}
		
		if (paint instanceof SlideColor) {
			SlideColor c = (SlideColor)paint;
			return getColor(c);
		} else if (paint instanceof SlideLinearGradient) {
			SlideLinearGradient g = (SlideLinearGradient)paint;
			return getLinearGradient(g);
		} else if (paint instanceof SlideRadialGradient) {
			SlideRadialGradient g = (SlideRadialGradient)paint;
			return getRadialGradient(g);
		} else if (paint instanceof MediaObject) {
			// NOTE: this is for PATTERN painting specifically
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
			// audio and video is ignored here, it must be checked separately
		}
		return null;
	}
	
	// NOTE: this method will return null for Video or Audio media paints
	BorderStroke getBorderStroke(SlideStroke stroke) {
		return new BorderStroke(
				getPaint(stroke.getPaint()),
				getBorderStrokeStyle(stroke.getStyle()),
				new CornerRadii(stroke.getRadius()),
				new BorderWidths(stroke.getWidth()));
	}
	
	BackgroundSize getBackgroundSize(ScaleType scaling) {
		BackgroundSize size = BackgroundSize.DEFAULT;
		if (scaling == ScaleType.NONUNIFORM) {
			size = new BackgroundSize(1.0, 1.0, true, true, false, false);
		} else if (scaling == ScaleType.UNIFORM) {
			size = new BackgroundSize(0.0, 0.0, false, false, true, false);
		}
		return size;
	}

	
	// NOTE: this method is only valid for EDIT mode
	Background getBackground(SlidePaint paint) {
		if (paint instanceof MediaObject) {
			// get the media id
			MediaObject mo = (MediaObject)paint;
			UUID id = mo.getId();
			// make sure the id is present
			if (id != null) {
				// get the media
				Media m = this.context.getMediaLibrary().get(id);
				// check for missing media
				if (m != null) {
					// check the media type
					if (m.getMetadata().getType() == MediaType.VIDEO) {
						// if not in present mode, then just show the single frame
						try  {
							Image image = this.context.getImageCache().get(this.context.getMediaLibrary().getFramePath(m));
							return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, this.getBackgroundSize(mo.getScaling())));
						} catch (Exception ex) {
							// just log the error
							LOGGER.warn("Failed to load video frame " + m.getMetadata().getPath() + ".", ex);
						}
					} else if (m.getMetadata().getType() == MediaType.IMAGE) {
						// image
						try  {
							Image image = this.context.getImageCache().get(m.getMetadata().getPath());
							return new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, this.getBackgroundSize(mo.getScaling())));
						} catch (Exception ex) {
							// just log the error
							LOGGER.warn("Failed to load image " + m.getMetadata().getPath() + ".", ex);
						}
					} else {
						// log warning about type (audio)
						LOGGER.warn("Invalid media type for background {} with path {}.", m.getMetadata().getType(), m.getMetadata().getPath());
					}
				} else {
					// log warning about missing media
					LOGGER.warn("The referenced media {} was not found in the media library.", id);
				}
			} else {
				LOGGER.warn("The media id is null.");
			}
			return null;
		} else {
			Paint pnt = getPaint(paint);
			return new Background(new BackgroundFill(pnt, new CornerRadii(this.component.getBorder().getRadius()), null));
		}
	}
	
	// getters/setters
	
	public T getComponent() {
		return this.component;
	}
}
