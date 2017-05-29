/*
 * Copyright (c) 2015-2016 William Bittle  http://www.praisenter.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of Praisenter nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.praisenter.javafx.slide;

import org.praisenter.javafx.PraisenterContext;
import org.praisenter.javafx.slide.converters.BorderConverter;
import org.praisenter.javafx.slide.converters.EffectConverter;
import org.praisenter.javafx.slide.converters.FontConverter;
import org.praisenter.javafx.slide.converters.PaintConverter;
import org.praisenter.javafx.slide.converters.TextAlignmentConverter;
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
import javafx.scene.text.TextBoundsType;

// JAVABUG 06/30/16 HIGH Right/Center/Justify alignment bugs https://bugs.openjdk.java.net/browse/JDK-8145496 -- http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8145496
// FIXME Try using TextFlow with a Text inside to see if it has different behavior

/**
 * Represents an observable {@link TextComponent}.
 * @author William Bittle
 * @version 3.0.0
 * @param <T> {@link TextComponent}
 */
public abstract class ObservableTextComponent<T extends TextComponent> extends ObservableSlideComponent<T> implements Playable {

	// editable
	
	/** The text */
	private final StringProperty text = new SimpleStringProperty("");
	
	/** The text paint */
	private final ObjectProperty<SlidePaint> textPaint = new SimpleObjectProperty<SlidePaint>();
	
	/** The text border */
	private final ObjectProperty<SlideStroke> textBorder = new SimpleObjectProperty<SlideStroke>();
	
	/** The font */
	private final ObjectProperty<SlideFont> font = new SimpleObjectProperty<SlideFont>();
	
	/** The horizontal alignment */
	private final ObjectProperty<HorizontalTextAlignment> horizontalTextAlignment = new SimpleObjectProperty<HorizontalTextAlignment>();
	
	/** The vertical alignment */
	private final ObjectProperty<VerticalTextAlignment> verticalTextAlignment = new SimpleObjectProperty<VerticalTextAlignment>();
	
	/** The font scale type */
	private final ObjectProperty<FontScaleType> fontScaleType = new SimpleObjectProperty<FontScaleType>();
	
	/** The padding */
	private final ObjectProperty<SlidePadding> padding = new SimpleObjectProperty<SlidePadding>();
	
	/** The line spacing */
	private final DoubleProperty lineSpacing = new SimpleDoubleProperty();
	
	/** True if text wrapping is enabled */
	private final BooleanProperty textWrapping = new SimpleBooleanProperty();
	
	/** The text shadow */
	private final ObjectProperty<SlideShadow> textShadow = new SimpleObjectProperty<SlideShadow>();
	
	/** The text glow */
	private final ObjectProperty<SlideShadow> textGlow = new SimpleObjectProperty<SlideShadow>();
	
	// nodes
	
	/** A wrapper node for the text node (used to apply padding and vertical alignment) */
	private final VBox textWrapper;
	
	/** The text node */
	private final Text textNode;
	
	/**
	 * Minimal constructor.
	 * @param component the text component
	 * @param context the context
	 * @param mode the slide mode
	 */
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
			updateName();
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

	/**
	 * Builds the component.
	 */
	protected void build() {
		updateTextPaint();
		updateTextBorder();
		updateAlignment();
		updateTextEffects();
		
		super.build(this.textWrapper);
		
		updateText();
	}
	
	/**
	 * Updates the Java FX component with the new text border.
	 */
	protected final void updateTextBorder() {
		SlideStroke nv = this.textBorder.get();
		if (nv != null) {
			this.textNode.setStroke(PaintConverter.toJavaFX(nv.getPaint()));
			this.textNode.setStrokeLineCap(BorderConverter.toJavaFX(nv.getStyle().getCap()));
			this.textNode.setStrokeLineJoin(BorderConverter.toJavaFX(nv.getStyle().getJoin()));
			this.textNode.setStrokeType(BorderConverter.toJavaFX(nv.getStyle().getType()));
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

	/**
	 * Updates the Java FX component with the new text paint.
	 */
	protected final void updateTextPaint() {
		SlidePaint paint = this.textPaint.get();
		this.textNode.setFill(PaintConverter.toJavaFX(paint));
		
		this.onTextPaintUpdate(paint);
	}

	/**
	 * Updates the Java FX component with the new text horizontal and vertical alignments.
	 */
	protected final void updateAlignment() {
		HorizontalTextAlignment hAlignment = this.horizontalTextAlignment.get();
		VerticalTextAlignment vAlignment = this.verticalTextAlignment.get();
		
		this.textNode.setTextAlignment(TextAlignmentConverter.toJavaFX(hAlignment));
		// NOTE: this is required since single line text nodes horizontal alignment does nothing
		this.textWrapper.setAlignment(TextAlignmentConverter.toJavaFX(vAlignment, hAlignment));
		
		this.onTextAlignmentUpdate(vAlignment, hAlignment);
	}
	
	/**
	 * Updates the Java FX component with the new text padding.
	 */
	protected final void updatePadding() {
		double w = this.getWidth();
		
		this.textWrapper.setPadding(TextAlignmentConverter.toJavaFX(this.padding.get()));
		
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
	
	/**
	 * Updates the Java FX component with the new font.
	 */
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
		Font base = FontConverter.toJavaFX(sf);
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
	
	/**
	 * Updates the Java FX component with the new text.
	 */
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
	
	/**
	 * Updates the Java FX component with the new text effects.
	 */
	protected final void updateTextEffects() {
		SlideShadow ss = this.textShadow.get();
		SlideShadow sg = this.textGlow.get();
		
		EffectBuilder builder = EffectBuilder.create();
		Effect shadow = EffectConverter.toJavaFX(ss);
		Effect glow = EffectConverter.toJavaFX(sg);
		builder.add(shadow, shadow != null && shadow instanceof InnerShadow ? 10 : 30);
		builder.add(glow, glow != null && glow instanceof InnerShadow ? 20 : 40);
		Effect effect = builder.build();
		this.textNode.setEffect(effect);
		
		this.onTextEffectsUpdate(ss, sg);
	}
	
	// events

	/**
	 * Called after the text border has been updated.
	 * @param border the new border
	 */
	protected void onTextBorderUpdate(SlideStroke border) {}
	
	/**
	 * Called after the text paint has been updated.
	 * @param paint the new text paint
	 */
	protected void onTextPaintUpdate(SlidePaint paint) {}
	
	/**
	 * Called after the text alignments have been changed.
	 * @param vAlignment the vertical alignment
	 * @param hAlignment the horizontal alignment
	 */
	protected void onTextAlignmentUpdate(VerticalTextAlignment vAlignment, HorizontalTextAlignment hAlignment) {}
	
	/**
	 * Called after the font has been updated.
	 * @param font the new font
	 * @param scaleType the font scaling type
	 * @param lineSpacing the line spacing
	 * @param textWrapping true whether line wrapping is enabled
	 */
	protected void onFontUpdate(SlideFont font, FontScaleType scaleType, double lineSpacing, boolean textWrapping) {}
	
	/**
	 * Called after the text has been updated.
	 * @param text the new text
	 */
	protected void onTextUpdate(String text) {}
	
	/**
	 * Called after the text effects have been updated.
	 * @param shadow the shadow
	 * @param glow the glow
	 */
	protected void onTextEffectsUpdate(SlideShadow shadow, SlideShadow glow) {}

	/* (non-Javadoc)
	 * @see org.praisenter.javafx.slide.ObservableSlideRegion#onSizeUpdate(double, double, org.praisenter.utility.Scaling)
	 */
	@Override
	protected void onSizeUpdate(double w, double h, Scaling scaling) {
		Fx.setSize(this.textWrapper, w, h);
		
		// when the size changes we need to adjust the wrapping width
		this.updatePadding();
		
		// when the size changes we may need to change the font size
		this.updateFont();
	}
	
	// text
	
	/**
	 * Returns the text.
	 * @return String
	 */
	public String getText() {
		return this.text.get();
	}
	
	/**
	 * Sets the text.
	 * <p>
	 * NOTE: Sub classes may disregard any value given here.
	 * @param text the text
	 */
	public void setText(String text) {
		this.text.set(text);
	}
	
	/**
	 * Returns the text property.
	 * @return StringProperty
	 */
	public StringProperty textProperty() {
		return this.text;
	}
	
	// text paint
	
	/**
	 * Returns the text paint.
	 * @return {@link SlidePaint}
	 */
	public SlidePaint getTextPaint() {
		return this.textPaint.get();
	}
	
	/**
	 * Sets the text paint.
	 * @param paint the text paint
	 */
	public void setTextPaint(SlidePaint paint) {
		this.textPaint.set(paint);
	}
	
	/**
	 * Returns the text paint property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlidePaint> textPaintProperty() {
		return this.textPaint;
	}
	
	// text border
	
	/**
	 * Returns the text border.
	 * @return {@link SlideStroke}
	 */
	public SlideStroke getTextBorder() {
		return this.textBorder.get();
	}
	
	/**
	 * Sets the text border.
	 * @param border the text border
	 */
	public void setTextBorder(SlideStroke border) {
		this.textBorder.set(border);
	}
	
	/**
	 * Returns the text border property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlideStroke> textBorderProperty() {
		return this.textBorder;
	}
	
	// font
	
	/**
	 * Returns the font.
	 * @return {@link SlideFont}
	 */
	public SlideFont getFont() {
		return this.font.get();
	}
	
	/**
	 * Sets the font.
	 * @param font the font
	 */
	public void setFont(SlideFont font) {
		this.font.set(font);
	}
	
	/**
	 * Returns the font property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlideFont> fontProperty() {
		return this.font;
	}
	
	// horizontal alignment
	
	/**
	 * Returns the horizontal text alignment.
	 * @return {@link HorizontalTextAlignment}
	 */
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment.get();
	}
	
	/**
	 * Sets the horizontal text alignment.
	 * @param alignment the horizontal text alignment
	 */
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		this.horizontalTextAlignment.set(alignment);
	}
	
	/**
	 * Returns the horizontal text alignment property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<HorizontalTextAlignment> horizontalTextAlignmentProperty() {
		return this.horizontalTextAlignment;
	}

	// vertical alignment
	
	/**
	 * Returns the vertical text alignment.
	 * @return {@link VerticalTextAlignment}
	 */
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment.get();
	}
	
	/**
	 * Sets the vertical text alignment.
	 * @param alignment the vertical text alignment
	 */
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		this.verticalTextAlignment.set(alignment);
	}
	
	/**
	 * Returns the vertical text alignment property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<VerticalTextAlignment> verticalTextAlignmentProperty() {
		return this.verticalTextAlignment;
	}

	// font scale type
	
	/**
	 * Returns the font scale type.
	 * @return {@link FontScaleType}
	 */
	public FontScaleType getFontScaleType() {
		return this.fontScaleType.get();
	}
	
	/**
	 * Sets the font scale type.
	 * @param scaleType the font scale type
	 */
	public void setFontScaleType(FontScaleType scaleType) {
		this.fontScaleType.set(scaleType);
	}
	
	/**
	 * Returns the font scale type property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<FontScaleType> fontScaleTypeProperty() {
		return this.fontScaleType;
	}

	// padding
	
	/**
	 * Returns the padding.
	 * @return {@link SlidePadding}
	 */
	public SlidePadding getPadding() {
		return this.padding.get();
	}
	
	/**
	 * Sets the padding.
	 * @param padding the padding
	 */
	public void setPadding(SlidePadding padding) {
		this.padding.set(padding);
	}
	
	/**
	 * Returns the padding property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlidePadding> paddingProperty() {
		return this.padding;
	}

	// line spacing
	
	/**
	 * Returns the line spacing.
	 * @return double
	 */
	public double getLineSpacing() {
		return this.lineSpacing.get();
	}
	
	/**
	 * Sets the line spacing.
	 * @param lineSpacing the line spacing
	 */
	public void setLineSpacing(double lineSpacing) {
		this.lineSpacing.set(lineSpacing);
	}
	
	/**
	 * Returns the line spacing property.
	 * @return DoubleProperty
	 */
	public DoubleProperty lineSpacingProperty() {
		return this.lineSpacing;
	}
	
	// text wrapping
	
	/**
	 * Returns the true if the text should wrap.
	 * @return boolean
	 */
	public boolean isTextWrapping() {
		return this.textWrapping.get();
	}
	
	/**
	 * Sets the text wrapping.
	 * @param flag true if the text should wrap
	 */
	public void setTextWrapping(boolean flag) {
		this.textWrapping.set(flag);
	}
	
	/**
	 * Returns the text wrapping property.
	 * @return BooleanProperty
	 */
	public BooleanProperty textWrappingProperty() {
		return this.textWrapping;
	}

	// text shadow
	
	/**
	 * Returns the text shadow.
	 * @return {@link SlideShadow}
	 */
	public SlideShadow getTextShadow() {
		return this.textShadow.get();
	}
	
	/**
	 * Sets the text shadow.
	 * @param shadow the text shadow
	 */
	public void setTextShadow(SlideShadow shadow) {
		this.textShadow.set(shadow);
	}
	
	/**
	 * Returns the text shadow property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlideShadow> textShadowProperty() {
		return this.textShadow;
	}

	// text glow
	
	/**
	 * Returns the text glow.
	 * @return {@link SlideShadow}
	 */
	public SlideShadow getTextGlow() {
		return this.textGlow.get();
	}
	
	/**
	 * Sets the text glow.
	 * @param glow the text glow
	 */
	public void setTextGlow(SlideShadow glow) {
		this.textGlow.set(glow);
	}
	
	/**
	 * Returns the text glow property.
	 * @return ObjectProperty
	 */
	public ObjectProperty<SlideShadow> textGlowProperty() {
		return this.textGlow;
	}
}
