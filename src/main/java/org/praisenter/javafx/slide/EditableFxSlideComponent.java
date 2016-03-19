package org.praisenter.javafx.slide;

import java.text.SimpleDateFormat;
import java.util.Collections;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.JavaFxNodeHelper;
import org.praisenter.slide.Slide;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.SlideAnimation;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.object.MediaObject;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.VerticalTextAlignment;

public final class EditableFxSlideComponent {

	final FxSlideComponent<?> editable;
	
	final IntegerProperty x;
	final IntegerProperty y;
	final IntegerProperty width;
	final IntegerProperty height;
	final ObjectProperty<SlidePaint> background;
	final ObjectProperty<SlideStroke> border;
	
	// flatten all component properties
	
	// media
//	final ObjectProperty<MediaObject> media;
//	
//	// text
//	final ObjectProperty<SlidePaint> textBackground;
//	final ObjectProperty<SlideStroke> textBorder;
//	final StringProperty fontName;
//	final IntegerProperty fontSize;
//	final ObjectProperty<FontScaleType> fontScaling;
//	final ObjectProperty<VerticalTextAlignment> verticalTextAlignment;
//	final ObjectProperty<HorizontalTextAlignment> horizontalTextAlignment;
//	final DoubleProperty padding;
//	final DoubleProperty lineSpacing;
//	final StringProperty text;
//	
//	// text placeholder
//	final IntegerProperty placeholders;
//	
//	// date/time
//	final ObjectProperty<SimpleDateFormat> dateFormat;
	
	public EditableFxSlideComponent(FxSlideComponent<?> component) {
		this.editable = component;
		
		this.x = new SimpleIntegerProperty();
		this.y = new SimpleIntegerProperty();
		this.width = new SimpleIntegerProperty();
		this.height = new SimpleIntegerProperty();
		this.background = new SimpleObjectProperty<SlidePaint>();
		this.border = new SimpleObjectProperty<SlideStroke>();
		
		final SlideComponent sc = component.component;
		
		// x/y
		// wire up
		this.x.set(sc.getX());
		this.y.set(sc.getY());
		this.x.addListener((obs, o, n) -> {
			int v = n.intValue();
			sc.setX(v);
		});
		this.y.addListener((obs, o, n) -> {
			int v = n.intValue();
			sc.setY(v);
		});

		// width/height
		// wire up
		this.width.set(sc.getWidth());
		this.height.set(sc.getHeight());
		this.width.addListener((obs, o, n) -> {
			int v = n.intValue();
			int ch = height.get();
			sc.setWidth(v);
//			JavaFxNodeHelper.setSize(this.node, v, ch);
		});
		this.height.addListener((obs, o, n) -> {
			int v = n.intValue();
			int cw = width.get();
			sc.setHeight(v);
//			JavaFxNodeHelper.setSize(this.foregroundNode, cw, v);
		});

		// border
		// wire up
		this.border.set(sc.getBorder());
		this.border.addListener((obs, o, n) -> {
			sc.setBorder(n);
			Border border = new Border(this.editable.getBorderStroke(n, component.component instanceof Slide));
			this.editable.borderNode.setBorder(border);
		});

		// background
		// wire up
		this.background.set(sc.getBackground());
		this.background.addListener((obs, o, n) -> {
			sc.setBackground(n);
			Background background = this.editable.getBackground(n);
//			this.editable.backgroundNode.setBackground(background);
		});
	}
	
	public Class<?> getComponentType() {
		return this.editable.component.getClass();
	}
}
