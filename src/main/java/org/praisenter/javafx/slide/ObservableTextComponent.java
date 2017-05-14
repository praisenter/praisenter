package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.utility.Fx;
import org.praisenter.javafx.utility.TextMeasurer;
import org.praisenter.slide.graphics.DashPattern;
import org.praisenter.slide.graphics.SlidePadding;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideShadow;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.text.FontScaleType;
import org.praisenter.slide.text.HorizontalTextAlignment;
import org.praisenter.slide.text.SlideFont;
import org.praisenter.slide.text.TextComponent;
import org.praisenter.slide.text.VerticalTextAlignment;
import org.praisenter.utility.Scaling;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

// JAVABUG 06/30/16 HIGH Right/Center/Justify alignment bugs https://bugs.openjdk.java.net/browse/JDK-8145496 -- http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8145496
// FIXME Try using TextFlow with a Text inside to see if it has different behavior

public abstract class ObservableTextComponent<T extends TextComponent> extends ObservableSlideComponent<T> {

	// editable
	
	private final StringProperty text = new SimpleStringProperty("");  // NOTE: this should be static text for the most part
	private final ObjectProperty<SlidePaint> textPaint = new SimpleObjectProperty<SlidePaint>();
	private final ObjectProperty<SlideStroke> textBorder = new SimpleObjectProperty<SlideStroke>();
	private final ObjectProperty<SlideFont> font = new SimpleObjectProperty<SlideFont>();
	private final ObjectProperty<HorizontalTextAlignment> horizontalTextAlignment = new SimpleObjectProperty<HorizontalTextAlignment>();
	private final ObjectProperty<VerticalTextAlignment> verticalTextAlignment = new SimpleObjectProperty<VerticalTextAlignment>();
	private final ObjectProperty<FontScaleType> fontScaleType = new SimpleObjectProperty<FontScaleType>();
	private final ObjectProperty<SlidePadding> padding = new SimpleObjectProperty<SlidePadding>();
	private final DoubleProperty lineSpacing = new SimpleDoubleProperty();
	private final BooleanProperty textWrapping = new SimpleBooleanProperty();
	private final ObjectProperty<SlideShadow> textShadow = new SimpleObjectProperty<SlideShadow>();
	private final ObjectProperty<SlideShadow> textGlow = new SimpleObjectProperty<SlideShadow>();
	
	// nodes
	
	private final VBox textWrapper;
	private final Text textNode;
	
	public ObservableTextComponent(T component, PraisenterContext context, SlideMode mode) {
		super(component, context, mode);
		
		// set initial values
		this.text.set(component.getText());
		this.textPaint.set(component.getTextPaint());
		this.textBorder.set(component.getTextBorder());
		this.font.set(component.getFont());
		this.horizontalTextAlignment.set(component.getHorizontalTextAlignment());
		this.verticalTextAlignment.set(component.getVerticalTextAlignment());
		this.fontScaleType.set(component.getFontScaleType());
		this.padding.set(component.getPadding());
		this.lineSpacing.set(component.getLineSpacing());
		this.textWrapping.set(component.isTextWrapping());
		this.textShadow.set(component.getTextShadow());
		this.textGlow.set(component.getTextGlow());
		
		this.textWrapper = new VBox();
		this.textNode = new Text();
		this.textNode.setBoundsType(TextBoundsType.LOGICAL);
		this.textWrapper.getChildren().add(this.textNode);
		
		// listen for changes
		this.text.addListener((obs, ov, nv) -> { 
			this.region.setText(nv); 
			updateText();
		});
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
			updatePadding();
			updateFont();
		});
		this.lineSpacing.addListener((obs, ov, nv) -> { 
			this.region.setLineSpacing(nv.doubleValue());
			this.updateFont();
		});
		this.textWrapping.addListener((obs, ov, nv) -> {
			this.region.setTextWrapping(nv);
			updatePadding();
			updateFont();
		});
		this.textShadow.addListener((obs, ov, nv) -> {
			this.region.setTextShadow(nv);
			updateTextEffects();
		});
		this.textGlow.addListener((obs, ov, nv) -> {
			this.region.setTextGlow(nv);
			updateTextEffects();
		});
	}

	void build() {
		updateTextPaint();
		updateTextBorder();
		updateAlignment();
		updateTextEffects();
		
		super.build(this.textWrapper);
		
		updateText();
	}
	
	protected final void updateTextBorder() {
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
		
		this.onTextBorderUpdate(nv);
	}

	protected final void updateTextPaint() {
		SlidePaint paint = this.textPaint.get();
		this.textNode.setFill(JavaFXTypeConverter.toJavaFX(paint));
		
		this.onTextPaintUpdate(paint);
	}
	
	protected final void updateAlignment() {
		HorizontalTextAlignment hAlignment = this.horizontalTextAlignment.get();
		VerticalTextAlignment vAlignment = this.verticalTextAlignment.get();
		
		this.textNode.setTextAlignment(JavaFXTypeConverter.toJavaFX(hAlignment));
		// NOTE: this is required since single line text nodes horizontal alignment does nothing
		this.textWrapper.setAlignment(JavaFXTypeConverter.toJavaFX(vAlignment, hAlignment));
		
		this.onTextAlignmentUpdate(vAlignment, hAlignment);
	}
	
	protected final void updatePadding() {
		double w = this.getWidth();
		double h = this.getHeight();
		
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
	}
	
	protected final void updateFont() {
		double w = this.getWidth();
		double h = this.getHeight();
		
		// compute the bounding text width and height so 
		// we can compute an accurate font size
		SlidePadding padding = this.padding.get();
		double pw = Math.max(1.0, w - padding.getLeft() - padding.getRight());
		double ph = Math.max(1.0, h - padding.getTop() - padding.getBottom());
		
		FontScaleType scaleType = this.fontScaleType.get();
		double lineSpacing = this.lineSpacing.get();
		boolean isWrapping = this.textWrapping.get();
		
		String str = this.text.get();
		
		// compute a fitting font, if necessary
		SlideFont sf = this.font.get();
		Font base = JavaFXTypeConverter.toJavaFX(sf);
		Font font = base;
		if (scaleType == FontScaleType.REDUCE_SIZE_ONLY) {
			if (isWrapping) {
				font = TextMeasurer.getFittingFontForParagraph(str, base, base.getSize(), pw, ph, lineSpacing, TextBoundsType.LOGICAL);
			} else {
				font = TextMeasurer.getFittingFontForLine(str, base, base.getSize(), pw, TextBoundsType.LOGICAL);
			}
		} else if (scaleType == FontScaleType.BEST_FIT) {
			if (isWrapping) {
				font = TextMeasurer.getFittingFontForParagraph(str, base, Double.MAX_VALUE, pw, ph, lineSpacing, TextBoundsType.LOGICAL);
			} else {
				font = TextMeasurer.getFittingFontForLine(str, base, Double.MAX_VALUE, pw, TextBoundsType.LOGICAL);
			}
		}
		
		this.textNode.setFont(font);
		this.textNode.setLineSpacing(lineSpacing);
		
		this.onFontUpdate(sf, scaleType, lineSpacing, isWrapping);
	}
	
	protected final void updateText() {
		String text = this.text.get();
		// set a default text so that there's something visible when the component is created
		if (this.mode == SlideMode.EDIT && (text == null || text.trim().length() <= 0)) {
			text = "Set the text using the ribbon";
		}
		this.textNode.setText(text);
		// only update the font size 
		if (this.fontScaleType.get() != FontScaleType.NONE) {
			this.updateFont();
		}
		
		this.onTextUpdate(text);
	}
	
	protected final void updateTextEffects() {
		SlideShadow ss = this.textShadow.get();
		SlideShadow sg = this.textGlow.get();
		
		EffectBuilder builder = EffectBuilder.create();
		Effect shadow = JavaFXTypeConverter.toJavaFX(ss);
		Effect glow = JavaFXTypeConverter.toJavaFX(sg);
		builder.add(shadow, shadow != null && shadow instanceof InnerShadow ? 10 : 30);
		builder.add(glow, glow != null && glow instanceof InnerShadow ? 20 : 40);
		Effect effect = builder.build();
		this.textNode.setEffect(effect);
		
		this.onTextEffectsUpdate(ss, sg);
	}
	
	// events

	protected void onTextBorderUpdate(SlideStroke border) {}
	protected void onTextPaintUpdate(SlidePaint paint) {}
	protected void onTextAlignmentUpdate(VerticalTextAlignment vAlignment, HorizontalTextAlignment hAlignment) {}
	protected void onFontUpdate(SlideFont font, FontScaleType scaleType, double lineSpacing, boolean textWrapping) {}
	protected void onTextUpdate(String text) {}
	protected void onTextEffectsUpdate(SlideShadow shadow, SlideShadow glow) {}

	@Override
	protected void onSizeUpdate(double w, double h, Scaling scaling) {
		Fx.setSize(this.textWrapper, w, h);
		
		// when the size changes we need to adjust the wrapping width
		this.updatePadding();
		
		// when the size changes we may need to change the font size
		this.updateFont();
	}
	
	// text
	
	public String getText() {
		return this.text.get();
	}
	
	public void setText(String text) {
		this.text.set(text);
	}
	
	public StringProperty textProperty() {
		return this.text;
	}
	
	// text paint
	
	public SlidePaint getTextPaint() {
		return this.textPaint.get();
	}
	
	public void setTextPaint(SlidePaint paint) {
		this.textPaint.set(paint);
	}
	
	public ObjectProperty<SlidePaint> textPaintProperty() {
		return this.textPaint;
	}
	
	// text border
	
	public SlideStroke getTextBorder() {
		return this.textBorder.get();
	}
	
	public void setTextBorder(SlideStroke border) {
		this.textBorder.set(border);
	}
	
	public ObjectProperty<SlideStroke> textBorderProperty() {
		return this.textBorder;
	}
	
	// font
	
	public SlideFont getFont() {
		return this.font.get();
	}
	
	public void setFont(SlideFont font) {
		this.font.set(font);
	}
	
	public ObjectProperty<SlideFont> fontProperty() {
		return this.font;
	}
	
	// horizontal alignment
	
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment.get();
	}
	
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		this.horizontalTextAlignment.set(alignment);
	}
	
	public ObjectProperty<HorizontalTextAlignment> horizontalTextAlignmentProperty() {
		return this.horizontalTextAlignment;
	}

	// vertical alignment
	
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment.get();
	}
	
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		this.verticalTextAlignment.set(alignment);
	}
	
	public ObjectProperty<VerticalTextAlignment> verticalTextAlignmentProperty() {
		return this.verticalTextAlignment;
	}

	// font scale type
	
	public FontScaleType getFontScaleType() {
		return this.fontScaleType.get();
	}
	
	public void setFontScaleType(FontScaleType scaleType) {
		this.fontScaleType.set(scaleType);
	}
	
	public ObjectProperty<FontScaleType> fontScaleTypeProperty() {
		return this.fontScaleType;
	}

	// padding
	
	public SlidePadding getPadding() {
		return this.padding.get();
	}
	
	public void setPadding(SlidePadding padding) {
		this.padding.set(padding);
	}
	
	public ObjectProperty<SlidePadding> paddingProperty() {
		return this.padding;
	}

	// line spacing
	
	public double getLineSpacing() {
		return this.lineSpacing.get();
	}
	
	public void setLineSpacing(double lineSpacing) {
		this.lineSpacing.set(lineSpacing);
	}
	
	public DoubleProperty lineSpacingProperty() {
		return this.lineSpacing;
	}
	
	// text wrapping
	
	public boolean isTextWrapping() {
		return this.textWrapping.get();
	}
	
	public void setTextWrapping(boolean flag) {
		this.textWrapping.set(flag);
	}
	
	public BooleanProperty textWrappingProperty() {
		return this.textWrapping;
	}

	// text shadow
	
	public SlideShadow getTextShadow() {
		return this.textShadow.get();
	}
	
	public void setTextShadow(SlideShadow shadow) {
		this.textShadow.set(shadow);
	}
	
	public ObjectProperty<SlideShadow> textShadowProperty() {
		return this.textShadow;
	}

	// text glow
	
	public SlideShadow getTextGlow() {
		return this.textGlow.get();
	}
	
	public void setTextGlow(SlideShadow glow) {
		this.textGlow.set(glow);
	}
	
	public ObjectProperty<SlideShadow> textGlowProperty() {
		return this.textGlow;
	}

}
