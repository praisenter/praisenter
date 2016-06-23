package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.javafx.utility.TextMeasurer;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

// FIXME right/center/justify alignment bugs  https://bugs.openjdk.java.net/browse/JDK-8145496 -- http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8145496

public abstract class ObservableTextComponent<T extends TextComponent> extends ObservableSlideComponent<T> implements SlideRegion, SlideComponent, TextComponent {

	// editable
	
	final ObjectProperty<SlidePaint> textPaint = new SimpleObjectProperty<SlidePaint>();
	final ObjectProperty<SlideStroke> textBorder = new SimpleObjectProperty<SlideStroke>();
	final ObjectProperty<SlideFont> font = new SimpleObjectProperty<SlideFont>();
	final ObjectProperty<HorizontalTextAlignment> horizontalTextAlignment = new SimpleObjectProperty<HorizontalTextAlignment>();
	final ObjectProperty<VerticalTextAlignment> verticalTextAlignment = new SimpleObjectProperty<VerticalTextAlignment>();
	final ObjectProperty<FontScaleType> fontScaleType = new SimpleObjectProperty<FontScaleType>();
	final ObjectProperty<SlidePadding> padding = new SimpleObjectProperty<SlidePadding>();
	final DoubleProperty lineSpacing = new SimpleDoubleProperty();
	final BooleanProperty textWrapping = new SimpleBooleanProperty();

	// nodes
	
	final VBox textWrapper;
	final Text textNode;
	
	public ObservableTextComponent(T component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.textPaint.set(component.getTextPaint());
		this.textBorder.set(component.getTextBorder());
		this.font.set(component.getFont());
		this.horizontalTextAlignment.set(component.getHorizontalTextAlignment());
		this.verticalTextAlignment.set(component.getVerticalTextAlignment());
		this.fontScaleType.set(component.getFontScaleType());
		this.padding.set(component.getPadding());
		this.lineSpacing.set(component.getLineSpacing());
		this.textWrapping.set(component.isTextWrapping());
		
		this.textWrapper = new VBox();
		this.textNode = new Text();
		this.textNode.setBoundsType(TextBoundsType.LOGICAL);
		this.textWrapper.getChildren().add(this.textNode);
		
		// listen for changes
		this.textPaint.addListener((obs, ov, nv) -> { 
			this.region.setTextPaint(nv);
			updateTextPaint();
		});
		this.textBorder.addListener((obs, ov, nv) -> { 
			this.region.setTextBorder(nv);
			updateTextBorder();
		});
		this.font.addListener((obs, ov, nv) -> { 
			this.region.setFont(nv);
			updateFont();
		});
		this.horizontalTextAlignment.addListener((obs, ov, nv) -> { 
			this.region.setHorizontalTextAlignment(nv);
			updateAlignment();
		});
		this.verticalTextAlignment.addListener((obs, ov, nv) -> { 
			this.region.setVerticalTextAlignment(nv);
			updateAlignment();
		});
		this.fontScaleType.addListener((obs, ov, nv) -> { 
			this.region.setFontScaleType(nv);
			this.updateFont();
		});
		this.padding.addListener((obs, ov, nv) -> { 
			this.region.setPadding(nv);
			updateSize();
		});
		this.lineSpacing.addListener((obs, ov, nv) -> { 
			this.region.setLineSpacing(nv.doubleValue());
			this.updateFont();
		});
		this.textWrapping.addListener((obs, ov, nv) -> {
			this.region.setTextWrapping(nv);
			this.updateFont();
		});
	}

	void build() {
		updateTextPaint();
		updateTextBorder();
		updateAlignment();
		
		super.build(this.textWrapper);
	}
	
	void updateTextBorder() {
		SlideStroke nv = this.textBorder.get();
		if (nv != null) {
			this.textNode.setStroke(JavaFXTypeConverter.toJavaFX(nv.getPaint()));
			this.textNode.setStrokeLineCap(JavaFXTypeConverter.toJavaFX(nv.getStyle().getCap()));
			this.textNode.setStrokeLineJoin(JavaFXTypeConverter.toJavaFX(nv.getStyle().getJoin()));
			this.textNode.setStrokeType(JavaFXTypeConverter.toJavaFX(nv.getStyle().getType()));
			this.textNode.setStrokeWidth(nv.getWidth());
			this.textNode.getStrokeDashArray().clear();

			Double[] dashes = nv.getStyle().getDashes();
			DashPattern pattern = DashPattern.getDashPattern(dashes);
			// does the style match a dash pattern?
			// we don't need to scale in the case of SOLID and if
			// it doesn't match a dash pattern, then SOLID is returned
			// so this should work for both cases
			if (pattern != DashPattern.SOLID) {
				// scale the dashes based on the line width
				dashes = pattern.getScaledDashPattern(nv.getWidth());
			}
			
			this.textNode.getStrokeDashArray().addAll(dashes);
		} else {
			this.textNode.setStroke(null);
			this.textNode.setStrokeDashOffset(0);
			this.textNode.setStrokeWidth(0);
		}
	}

	void updateTextPaint() {
		this.textNode.setFill(JavaFXTypeConverter.toJavaFX(this.textPaint.get()));
	}
	
	void updateAlignment() {
		this.textNode.setTextAlignment(JavaFXTypeConverter.toJavaFX(this.horizontalTextAlignment.get()));
		
		// NOTE: this is required since single line text nodes horizontal alignment does nothing
		this.textWrapper.setAlignment(JavaFXTypeConverter.toJavaFX(this.verticalTextAlignment.get(), this.horizontalTextAlignment.get()));
	}
	
	@Override
	void updateSize() {
		super.updateSize();
		
		int w = this.width.get();
		int h = this.height.get();
		
		Fx.setSize(this.textWrapper, w, h);
		
		this.textWrapper.setPadding(JavaFXTypeConverter.toJavaFX(this.padding.get()));
		
		// compute the bounding text width and height so 
		// we can compute an accurate font size
		double pw = w - this.padding.get().getLeft() - this.padding.get().getRight();
		
		// set the wrapping width and the bounds type
		if (this.textWrapping.get()) {
			this.textNode.setWrappingWidth(pw);
		} else {
			this.textNode.setWrappingWidth(0);
		}
		
		this.updateFont();
	}
	
	void updateFont() {
		int w = this.width.get();
		int h = this.height.get();
		
		// compute the bounding text width and height so 
		// we can compute an accurate font size
		SlidePadding padding = this.padding.get();
		double pw = Math.max(1.0, Math.floor(w - padding.getLeft() - padding.getRight()));
		double ph = Math.max(1.0, Math.floor(h - padding.getTop() - padding.getBottom()));
		FontScaleType scaleType = this.fontScaleType.get();
		double lineSpacing = this.lineSpacing.get();
		
		String str = getText();
		
		// compute a fitting font, if necessary
		Font base = JavaFXTypeConverter.toJavaFX(this.font.get());
		Font font = base;
		if (scaleType == FontScaleType.REDUCE_SIZE_ONLY) {
			if (this.textWrapping.get()) {
				font = TextMeasurer.getFittingFontForParagraph(str, base, base.getSize(), pw, ph, lineSpacing, TextBoundsType.LOGICAL);
			} else {
				font = TextMeasurer.getFittingFontForLine(str, base, base.getSize(), pw, TextBoundsType.LOGICAL);
			}
		} else if (scaleType == FontScaleType.BEST_FIT) {
			if (this.textWrapping.get()) {
				font = TextMeasurer.getFittingFontForParagraph(str, base, Double.MAX_VALUE, pw, ph, lineSpacing, TextBoundsType.LOGICAL);
			} else {
				font = TextMeasurer.getFittingFontForLine(str, base, Double.MAX_VALUE, pw, TextBoundsType.LOGICAL);
			}
		}
		
		this.textNode.setFont(font);
		this.textNode.setLineSpacing(lineSpacing);
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
	public SlidePadding getPadding() {
		return this.padding.get();
	}
	
	@Override
	public void setPadding(SlidePadding padding) {
		this.padding.set(padding);
	}
	
	public ObjectProperty<SlidePadding> paddingProperty() {
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
	
	// text wrapping
	
	@Override
	public boolean isTextWrapping() {
		return this.textWrapping.get();
	}
	
	@Override
	public void setTextWrapping(boolean flag) {
		this.textWrapping.set(flag);
	}
	
	public BooleanProperty textWrappingProperty() {
		return this.textWrapping;
	}
}
