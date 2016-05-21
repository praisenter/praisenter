package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.text.TextMeasurer;
import org.praisenter.javafx.utility.Fx;
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
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public abstract class ObservableTextComponent<T extends TextComponent> extends ObservableSlideComponent<T> implements SlideRegion, SlideComponent, TextComponent {

	// editable
	
	final ObjectProperty<SlidePaint> textPaint = new SimpleObjectProperty<SlidePaint>();
	final ObjectProperty<SlideStroke> textBorder = new SimpleObjectProperty<SlideStroke>();
	final ObjectProperty<SlideFont> font = new SimpleObjectProperty<SlideFont>();
	final ObjectProperty<HorizontalTextAlignment> horizontalTextAlignment = new SimpleObjectProperty<HorizontalTextAlignment>();
	final ObjectProperty<VerticalTextAlignment> verticalTextAlignment = new SimpleObjectProperty<VerticalTextAlignment>();
	final ObjectProperty<FontScaleType> fontScaleType = new SimpleObjectProperty<FontScaleType>();
	final DoubleProperty padding = new SimpleDoubleProperty();
	final DoubleProperty lineSpacing = new SimpleDoubleProperty();

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
		
		this.textWrapper = new VBox();
		this.textNode = new Text();
		this.textNode.setBoundsType(TextBoundsType.VISUAL);
		this.textWrapper.getChildren().add(this.textNode);
		
		// listen for changes
		this.textPaint.addListener((obs, ov, nv) -> { 
			this.region.setTextPaint(nv);
			this.textNode.setFill(JavaFXTypeConverter.toJavaFX(nv));
		});
		this.textBorder.addListener((obs, ov, nv) -> { 
			this.region.setTextBorder(nv);
			if (nv != null) {
				this.textNode.setStroke(JavaFXTypeConverter.toJavaFX(nv.getPaint()));
				this.textNode.setStrokeLineCap(JavaFXTypeConverter.toJavaFX(nv.getStyle().getCap()));
				this.textNode.setStrokeLineJoin(JavaFXTypeConverter.toJavaFX(nv.getStyle().getJoin()));
				this.textNode.setStrokeType(JavaFXTypeConverter.toJavaFX(nv.getStyle().getType()));
				this.textNode.setStrokeWidth(nv.getWidth());
				this.textNode.getStrokeDashArray().removeAll();
				this.textNode.getStrokeDashArray().addAll(nv.getStyle().getDashes());
			} else {
				this.textNode.setStroke(null);
				this.textNode.setStrokeDashOffset(0);
				this.textNode.setStrokeWidth(0);
			}
		});
		this.font.addListener((obs, ov, nv) -> { 
			this.region.setFont(nv);
			this.textNode.setFont(JavaFXTypeConverter.toJavaFX(nv));
			this.updateFont();
		});
		this.horizontalTextAlignment.addListener((obs, ov, nv) -> { 
			this.region.setHorizontalTextAlignment(nv);
			this.textNode.setTextAlignment(JavaFXTypeConverter.toJavaFX(nv));
		});
		this.verticalTextAlignment.addListener((obs, ov, nv) -> { 
			this.region.setVerticalTextAlignment(nv);
			this.textWrapper.setAlignment(JavaFXTypeConverter.toJavaFX(nv));
		});
		this.fontScaleType.addListener((obs, ov, nv) -> { 
			this.region.setFontScaleType(nv);
			this.updateFont();
		});
		this.padding.addListener((obs, ov, nv) -> { 
			this.region.setPadding(nv.doubleValue());
			this.textWrapper.setPadding(new Insets(nv.doubleValue()));
			updateSize();
		});
		this.lineSpacing.addListener((obs, ov, nv) -> { 
			this.region.setLineSpacing(nv.doubleValue());
			this.textNode.setLineSpacing(nv.doubleValue());
			this.updateFont();
		});
	}
	
	@Override
	protected void updateSize() {
		super.updateSize();
		
		int w = this.width.get();
		int h = this.height.get();
		
		Fx.setSize(this.textWrapper, w, h);
		
		// compute the bounding text width and height so 
		// we can compute an accurate font size
		double padding = this.padding.get();
		double pw = w - padding * 2;
		
		// set the wrapping width and the bounds type
		this.textNode.setWrappingWidth(pw);
		
		this.updateFont();
	}
	
	protected void updateFont() {
		int w = this.width.get();
		int h = this.height.get();
		
		// compute the bounding text width and height so 
		// we can compute an accurate font size
		double padding = this.padding.get();
		double pw = w - padding * 2;
		double ph = h - padding * 2;
		FontScaleType scaleType = this.fontScaleType.get();
		double lineSpacing = this.lineSpacing.get();
		
		// component.getText()
		String str = getText();
		
		// compute a fitting font, if necessary
		Font base = JavaFXTypeConverter.toJavaFX(this.font.get());
		Font font = base;
		if (scaleType == FontScaleType.REDUCE_SIZE_ONLY) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, base.getSize(), pw, ph, lineSpacing, TextBoundsType.VISUAL);
		} else if (scaleType == FontScaleType.BEST_FIT) {
			font = TextMeasurer.getFittingFontForParagraph(str, base, Double.MAX_VALUE, pw, ph, lineSpacing, TextBoundsType.VISUAL);
		}
		
		this.textNode.setFont(font);
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
