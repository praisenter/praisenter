package org.praisenter.javafx.slide;

import java.util.ArrayList;
import java.util.List;

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
import org.praisenter.slide.text.TextComponent;

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
	
	public static final Node to(TextComponent component) {
		Text text = new Text();
		text.setWrappingWidth(component.getWidth());
		text.setBoundsType(TextBoundsType.VISUAL);
		
		String str = "Lorem ipsum dolor \n\nsit amet, consectetur adipiscing elit. Nam viverra tristique mauris. Suspendisse potenti. Etiam justo erat, mollis eget mi nec, euismod interdum magna. Aenean ac nulla fermentum, ullamcorper arcu sed, fermentum orci. Donec varius neque eget sapien cursus maximus. Fusce mauris lectus, pellentesque vel sem cursus, dapibus vehicula est. In tincidunt ultrices est nec finibus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Curabitur eu nisi augue. Integer commodo enim sed rutrum rutrum. Quisque tristique id ipsum sed malesuada. Maecenas non diam eget felis pulvinar sodales.";
		
		Font bf = Font.font(component.getFontName(), component.getFontSize());
		Font font = TextMeasurer.getFittingFontForParagraph(str, bf, component.getFontSize(), component.getWidth(), component.getHeight(), component.getLineSpacing(), TextBoundsType.VISUAL);
		
		text.setText(str);
		text.setFont(font);
		text.setFill(to(component.getTextPaint()));
		text.setLineSpacing(component.getLineSpacing());
		text.setTextAlignment(TextAlignment.CENTER);
		
		SlideStroke ss = component.getTextBorder();
		if (ss != null) {
			text.setStroke(to(ss.getPaint()));
			text.setStrokeLineCap(to(ss.getStyle().getCap()));
			text.setStrokeLineJoin(to(ss.getStyle().getJoin()));
			text.setStrokeType(to(ss.getStyle().getType()));
			text.setStrokeWidth(ss.getWidth());
		}
		
		VBox box = new VBox();
		box.setPrefSize(component.getWidth(), component.getHeight());
		box.setLayoutX(component.getX());
		box.setLayoutY(component.getY());
		box.setAlignment(Pos.BOTTOM_LEFT);
		
		SlidePaint bg = component.getBackground();
		if (bg != null) {
			box.setBackground(new Background(new BackgroundFill(to(bg), ss != null ? new CornerRadii(ss.getRadius()) : null, null)));
		}
		
		SlideStroke bdr = component.getBorder();
		if (bdr != null) {
			box.setBorder(to(bdr));
		}
		
		box.getChildren().add(text);
		
		return box;
	}
}
