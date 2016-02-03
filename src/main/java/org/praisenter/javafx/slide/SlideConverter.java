package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

import org.praisenter.javafx.text.TextMeasurer;
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
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

public final class SlideConverter {
	public static final Color to(SlideColor color) {
		return Color.rgb(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public static final Border to(SlideStroke stroke) {
		return new Border(new BorderStroke(
				to(stroke.getPaint()),
				to(stroke.getStyle()),
				new CornerRadii(stroke.getRadius()),
				new BorderWidths(stroke.getWidth())));
	}
	
	public static final BorderStrokeStyle to(SlideStrokeStyle style) {
		return new BorderStrokeStyle(
				to(style.getType()), 
				to(style.getJoin()), 
				to(style.getCap()), 
				Double.MAX_VALUE, 
				0.0, 
				to(style.getDashes()));
	}
	
	public static final List<Double> to(double[] values) {
		List<Double> ds = new ArrayList<Double>();
		for(double d : values) {
			ds.add(d);
		}
		return ds;
	}
	
	public static final Paint to(SlidePaint paint) {
		if (paint instanceof SlideColor) {
			SlideColor c = (SlideColor)paint;
			return Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		} else if (paint instanceof SlideLinearGradient) {
			SlideLinearGradient g = (SlideLinearGradient)paint;
			LinearGradient gr = new LinearGradient(
					g.getStartX(), g.getStartY(), g.getEndX(), g.getEndY(), 
					true, to(g.getCycleType()), to(g.getStops()));
			return gr;
		} else if (paint instanceof SlideRadialGradient) {
			SlideRadialGradient g = (SlideRadialGradient)paint;
			RadialGradient gr = new RadialGradient(
					0.0, 0.0, g.getCenterX(), g.getCenterY(), g.getRadius(),
					true, to(g.getCycleType()), to(g.getStops()));
			return gr;
		}
		return null;
	}
	
	public static final List<Stop> to(List<SlideGradientStop> stops) {
		List<Stop> stps = new ArrayList<Stop>();
		for (SlideGradientStop s : stops) {
			stps.add(to(s));
		}
		return stps;
	}
	
	public static final Stop to(SlideGradientStop stop) {
		return new Stop(stop.getOffset(), to(stop.getColor()));
	}
	
	public static final CycleMethod to(SlideGradientCycleType cycle) {
		switch (cycle) {
			case REPEAT:
				return CycleMethod.REPEAT;
			case REFLECT:
				return CycleMethod.REFLECT;
		}
		return CycleMethod.NO_CYCLE;
	}
	
	public static final StrokeLineCap to(SlideStrokeCap cap) {
		switch (cap) {
			case BUTT:
				return StrokeLineCap.BUTT;
			case ROUND:
				return StrokeLineCap.ROUND;
		}
		return StrokeLineCap.SQUARE;
	}
	
	public static final StrokeLineJoin to(SlideStrokeJoin join) {
		switch (join) {
			case BEVEL:
				return StrokeLineJoin.BEVEL;
			case ROUND:
				return StrokeLineJoin.ROUND;
		}
		return StrokeLineJoin.MITER;
	}
	
	public static final StrokeType to(SlideStrokeType type) {
		switch (type) {
			case INSIDE:
				return StrokeType.INSIDE;
			case OUTSIDE:
				return StrokeType.OUTSIDE;
		}
		return StrokeType.CENTERED;
	}
	
	public static final TextAlignment to(HorizontalTextAlignment alignment) {
		switch (alignment) {
			case RIGHT:
				return TextAlignment.RIGHT;
			case CENTER:
				return TextAlignment.CENTER;
			case JUSTIFY:
				return TextAlignment.JUSTIFY;
		}
		return TextAlignment.LEFT;
	}
	
	public static final Pos to(VerticalTextAlignment alignment) {
		switch (alignment) {
			case CENTER:
				return Pos.CENTER_LEFT;
			case BOTTOM:
				return Pos.BOTTOM_LEFT;
		}
		return Pos.TOP_LEFT;
	}
	
	public static final Node to(TextComponent component) {
//		component.getPadding()
		double padding = component.getPadding();
		double pw = component.getWidth() - 2 * padding;
		double ph = component.getHeight() - 2 * padding;
		
		Text text = new Text();
		text.setWrappingWidth(pw);
		text.setBoundsType(TextBoundsType.VISUAL);
		
//		component.getOrder() --- handled by order of nodes in graph
//		component.getVerticalTextAlignment()

		// TODO get based on TextComponent type
		String str = "Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.";
		
//		component.getFontName()
//		component.getFontScaleType()
//		component.getFontSize()
//		component.getLineSpacing()
		// compute a fitting font, if necessary
		Font base = Font.font(component.getFontName(), component.getFontSize());
		Font font = base;
		if (component.getFontScaleType() == FontScaleType.REDUCE_SIZE_ONLY) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, component.getFontSize(), pw, ph, component.getLineSpacing(), TextBoundsType.VISUAL);
		} else if (component.getFontScaleType() == FontScaleType.BEST_FIT) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, Double.MAX_VALUE, pw, ph, component.getLineSpacing(), TextBoundsType.VISUAL);
		}
		
		text.setText(str);
		text.setFont(font);
//		component.getTextPaint()
		text.setFill(to(component.getTextPaint()));
		text.setLineSpacing(component.getLineSpacing());
//		component.getHorizontalTextAlignment()
		text.setTextAlignment(to(component.getHorizontalTextAlignment()));
		
//		component.getTextBorder()
		SlideStroke ss = component.getTextBorder();
		if (ss != null) {
			text.setStroke(to(ss.getPaint()));
			text.setStrokeLineCap(to(ss.getStyle().getCap()));
			text.setStrokeLineJoin(to(ss.getStyle().getJoin()));
			text.setStrokeType(to(ss.getStyle().getType()));
			text.setStrokeWidth(ss.getWidth());
			text.getStrokeDashArray().addAll(to(ss.getStyle().getDashes()));
		}
		
//		component.getHeight()
//		component.getWidth()
//		component.getX()
//		component.getY()
//		component.getPadding()
		VBox box = new VBox();
		box.setPrefSize(component.getWidth(), component.getHeight());
		box.setLayoutX(component.getX());
		box.setLayoutY(component.getY());
		box.setPadding(new Insets(padding));
		box.setAlignment(to(component.getVerticalTextAlignment()));
		
//		component.getBackground()
		SlidePaint bg = component.getBackground();
		if (bg != null) {
			box.setBackground(new Background(new BackgroundFill(to(bg), ss != null ? new CornerRadii(ss.getRadius()) : null, null)));
		}
		
//		component.getBorder()
		SlideStroke bdr = component.getBorder();
		if (bdr != null) {
			box.setBorder(to(bdr));
		}
		
		box.getChildren().add(text);
		
		return box;
	}
}
