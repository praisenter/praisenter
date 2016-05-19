package org.praisenter.javafx.slide;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class ObservableTextComponent<T extends TextComponent> extends ObservableSlideComponent<T> implements SlideRegion, SlideComponent, TextComponent {

	final ObjectProperty<SlidePaint> textPaint = new SimpleObjectProperty<SlidePaint>();
	final ObjectProperty<SlideStroke> textBorder = new SimpleObjectProperty<SlideStroke>();
	final ObjectProperty<SlideFont> font = new SimpleObjectProperty<SlideFont>();
	final ObjectProperty<HorizontalTextAlignment> horizontalTextAlignment = new SimpleObjectProperty<HorizontalTextAlignment>();
	final ObjectProperty<VerticalTextAlignment> verticalTextAlignment = new SimpleObjectProperty<VerticalTextAlignment>();
	final ObjectProperty<FontScaleType> fontScaleType = new SimpleObjectProperty<FontScaleType>();
	final DoubleProperty padding = new SimpleDoubleProperty();
	final DoubleProperty lineSpacing = new SimpleDoubleProperty();
	
	public ObservableTextComponent(T component) {
		super(component);
		
		// set initial values
		this.textPaint.set(component.getTextPaint());
		this.textBorder.set(component.getTextBorder());
		this.font.set(component.getFont());
		this.horizontalTextAlignment.set(component.getHorizontalTextAlignment());
		this.verticalTextAlignment.set(component.getVerticalTextAlignment());
		this.fontScaleType.set(component.getFontScaleType());
		this.padding.set(component.getPadding());
		this.lineSpacing.set(component.getLineSpacing());
		
		// listen for changes
		this.textPaint.addListener((obs, ov, nv) -> { this.region.setTextPaint(nv); });
		this.textBorder.addListener((obs, ov, nv) -> { this.region.setTextBorder(nv); });
		this.font.addListener((obs, ov, nv) -> { this.region.setFont(nv); });
		this.horizontalTextAlignment.addListener((obs, ov, nv) -> { this.region.setHorizontalTextAlignment(nv); });
		this.verticalTextAlignment.addListener((obs, ov, nv) -> { this.region.setVerticalTextAlignment(nv); });
		this.fontScaleType.addListener((obs, ov, nv) -> { this.region.setFontScaleType(nv); });
		this.padding.addListener((obs, ov, nv) -> { this.region.setPadding(nv.doubleValue()); });
		this.lineSpacing.addListener((obs, ov, nv) -> { this.region.setLineSpacing(nv.doubleValue()); });
	}
	
	// text paint
	
	@Override
	public SlidePaint getTextPaint() {
		return this.textPaint.get();
	}
	
	@Override
	public void setTextPaint(SlidePaint paint) {
		this.textPaint.set(paint);
	}
	
	public ObjectProperty<SlidePaint> textPaintProperty() {
		return this.textPaint;
	}
	
	// text border
	
	@Override
	public SlideStroke getTextBorder() {
		return this.textBorder.get();
	}
	
	@Override
	public void setTextBorder(SlideStroke border) {
		this.textBorder.set(border);
	}
	
	public ObjectProperty<SlideStroke> textBorderProperty() {
		return this.textBorder;
	}
	
	// font
	
	@Override
	public SlideFont getFont() {
		return this.font.get();
	}
	
	@Override
	public void setFont(SlideFont font) {
		this.font.set(font);
	}
	
	public ObjectProperty<SlideFont> fontProperty() {
		return this.font;
	}
	
	// horizontal alignment
	
	@Override
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment.get();
	}
	
	@Override
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		this.horizontalTextAlignment.set(alignment);
	}
	
	public ObjectProperty<HorizontalTextAlignment> horizontalTextAlignmentProperty() {
		return this.horizontalTextAlignment;
	}

	// vertical alignment
	
	@Override
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment.get();
	}
	
	@Override
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		this.verticalTextAlignment.set(alignment);
	}
	
	public ObjectProperty<VerticalTextAlignment> verticalTextAlignmentProperty() {
		return this.verticalTextAlignment;
	}

	// font scale type
	
	@Override
	public FontScaleType getFontScaleType() {
		return this.fontScaleType.get();
	}
	
	@Override
	public void setFontScaleType(FontScaleType scaleType) {
		this.fontScaleType.set(scaleType);
	}
	
	public ObjectProperty<FontScaleType> fontScaleTypeProperty() {
		return this.fontScaleType;
	}

	// padding
	
	@Override
	public double getPadding() {
		return this.padding.get();
	}
	
	@Override
	public void setPadding(double padding) {
		this.padding.set(padding);
	}
	
	public DoubleProperty paddingProperty() {
		return this.padding;
	}

	// line spacing
	
	@Override
	public double getLineSpacing() {
		return this.lineSpacing.get();
	}
	
	@Override
	public void setLineSpacing(double lineSpacing) {
		this.lineSpacing.set(lineSpacing);
	}
	
	public DoubleProperty lineSpacingProperty() {
		return this.lineSpacing;
	}
}
