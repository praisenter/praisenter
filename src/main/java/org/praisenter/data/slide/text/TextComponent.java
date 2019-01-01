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
package org.praisenter.data.slide.text;

import org.praisenter.Watchable;
import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;
import org.praisenter.data.slide.SlideComponent;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.SlideColor;
import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract implementation of the {@link TextComponent} interface.
 * @author William Bittle
 * @version 3.0.0
 */
public class TextComponent extends SlideComponent implements ReadOnlyTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	protected final ObjectProperty<SlidePaint> textPaint;
	protected final ObjectProperty<SlideStroke> textBorder;
	protected final ObjectProperty<SlideFont> font;
	protected final ObjectProperty<FontScaleType> fontScaleType;
	protected final ObjectProperty<VerticalTextAlignment> verticalTextAlignment;
	protected final ObjectProperty<HorizontalTextAlignment> horizontalTextAlignment;
	protected final ObjectProperty<SlidePadding> padding;
	protected final DoubleProperty lineSpacing;
	protected final BooleanProperty textWrapping;
	protected final ObjectProperty<SlideShadow> textShadow;
	protected final ObjectProperty<SlideShadow> textGlow;
	protected final StringProperty text;
	
	public TextComponent() {
		super();
		this.textPaint = new SimpleObjectProperty<>(new SlideColor());
		this.textBorder = new SimpleObjectProperty<>();
		this.font = new SimpleObjectProperty<>();
		this.fontScaleType = new SimpleObjectProperty<>(FontScaleType.NONE);
		this.verticalTextAlignment = new SimpleObjectProperty<>(VerticalTextAlignment.TOP);
		this.horizontalTextAlignment = new SimpleObjectProperty<>(HorizontalTextAlignment.LEFT);
		this.padding = new SimpleObjectProperty<>(new SlidePadding());
		this.lineSpacing = new SimpleDoubleProperty();
		this.textWrapping = new SimpleBooleanProperty(true);
		this.textShadow = new SimpleObjectProperty<>();
		this.textGlow = new SimpleObjectProperty<>();
		this.text = new SimpleStringProperty();
		
		this.name.bind(this.text);
	}
	
	protected void copyTo(TextComponent component) {
		super.copyTo(component);
		
		SlidePaint tp = this.textPaint.get();
		if (tp != null) {
			component.textPaint.set(tp.copy());
		}
		
		SlideStroke ts = this.textBorder.get();
		if (ts != null) {
			component.textBorder.set(ts.copy());
		} else {
			component.textBorder.set(null);
		}
		
		SlideFont sf = this.font.get();
		if (sf != null) {
			component.font.set(sf.copy());
		}
		
		SlidePadding sp = this.padding.get();
		if (sp != null) {
			component.padding.set(sp.copy());
		}
		
		SlideShadow sg = this.textGlow.get();
		if (sg != null) {
			component.textGlow.set(sg.copy());
		} else {
			component.textGlow.set(null);
		}
		
		SlideShadow ss= this.textShadow.get();
		if (ss != null) {
			component.textShadow.set(ss.copy());
		} else {
			component.textShadow.set(null);
		}
		
		component.fontScaleType.set(this.fontScaleType.get());
		component.verticalTextAlignment.set(this.verticalTextAlignment.get());
		component.horizontalTextAlignment.set(this.horizontalTextAlignment.get());
		component.lineSpacing.set(this.lineSpacing.get());
		component.textWrapping.set(this.textWrapping.get());
		component.text.set(this.text.get());
	}
	
	@Override
	public TextComponent copy() {
		TextComponent tc = new TextComponent();
		this.copyTo(tc);
		return tc;
	}
	
	@Override
	@JsonProperty
	public SlidePaint getTextPaint() {
		return this.textPaint.get();
	}

	@JsonProperty
	public void setTextPaint(SlidePaint paint) {
		this.textPaint.set(paint);
	}
	
	@Override
	@Watchable(name = "textPaint")
	public ObjectProperty<SlidePaint> textPaintProperty() {
		return this.textPaint;
	}

	@Override
	@JsonProperty
	public SlideStroke getTextBorder() {
		return this.textBorder.get();
	}

	@JsonProperty
	public void setTextBorder(SlideStroke border) {
		this.textBorder.set(border);
	}

	@Override
	@Watchable(name = "textBorder")
	public ObjectProperty<SlideStroke> textBorderProperty() {
		return this.textBorder;
	}

	@Override
	@JsonProperty
	public SlideFont getFont() {
		return this.font.get();
	}
	
	@JsonProperty
	public void setFont(SlideFont font) {
		this.font.set(font);
	}
	
	@Override
	@Watchable(name = "font")
	public ObjectProperty<SlideFont> fontProperty() {
		return this.font;
	}

	@Override
	@JsonProperty
	public FontScaleType getFontScaleType() {
		return this.fontScaleType.get();
	}
	
	@JsonProperty
	public void setFontScaleType(FontScaleType fontScaleType) {
		this.fontScaleType.set(fontScaleType);
	}
	
	@Override
	@Watchable(name = "fontScaleType")
	public ObjectProperty<FontScaleType> fontScaleTypeProperty() {
		return this.fontScaleType;
	}

	@Override
	@JsonProperty
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment.get();
	}
	
	@JsonProperty
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		this.verticalTextAlignment.set(alignment);
	}
	
	@Override
	@Watchable(name = "verticalTextAlignment")
	public ObjectProperty<VerticalTextAlignment> verticalTextAlignmentProperty() {
		return this.verticalTextAlignment;
	}
	
	@Override
	@JsonProperty
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment.get();
	}
	
	@JsonProperty
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		this.horizontalTextAlignment.set(alignment);
	}
	
	@Override
	@Watchable(name = "horizontalTextAlignment")
	public ObjectProperty<HorizontalTextAlignment> horizontalTextAlignmentProperty() {
		return this.horizontalTextAlignment;
	}
	
	@Override
	@JsonProperty
	public SlidePadding getPadding() {
		return this.padding.get();
	}
	
	@JsonProperty
	public void setPadding(SlidePadding padding) {
		this.padding.set(padding);
	}
	
	@Override
	@Watchable(name = "padding")
	public ObjectProperty<SlidePadding> paddingProperty() {
		return this.padding;
	}
	
	@Override
	@JsonProperty
	public double getLineSpacing() {
		return this.lineSpacing.get();
	}
	
	@JsonProperty
	public void setLineSpacing(double spacing) {
		this.lineSpacing.set(spacing);
	}
	
	@Override
	@Watchable(name = "lineSpacing")
	public DoubleProperty lineSpacingProperty() {
		return this.lineSpacing;
	}
	
	@Override
	@JsonProperty
	public boolean isTextWrappingEnabled() {
		return this.textWrapping.get();
	}
	
	@JsonProperty
	public void setTextWrappingEnabled(boolean enabled) {
		this.textWrapping.set(enabled);
	}
	
	@Override
	@Watchable(name = "textWrappingEnabled")
	public BooleanProperty textWrappingEnabledProperty() {
		return this.textWrapping;
	}
	@Override
	@JsonProperty
	public SlideShadow getTextShadow() {
		return this.textShadow.get();
	}
	
	@JsonProperty
	public void setTextShadow(SlideShadow shadow) {
		this.textShadow.set(shadow);
	}
	
	@Override
	@Watchable(name = "textShadow")
	public ObjectProperty<SlideShadow> textShadowProperty() {
		return this.textShadow;
	}
	
	@Override
	@JsonProperty
	public SlideShadow getTextGlow() {
		return this.textGlow.get();
	}
	
	@JsonProperty
	public void setTextGlow(SlideShadow glow) {
		this.textGlow.set(glow);
	}
	
	@Override
	@Watchable(name = "textGlow")
	public ObjectProperty<SlideShadow> textGlowProperty() {
		return this.textGlow;
	}
	
	@Override
	@JsonProperty
	public String getText() {
		return this.text.get();
	}
	
	@JsonProperty
	public void setText(String text) {
		this.text.set(text);
	}
	
	@Override
	@Watchable(name = "text")
	public StringProperty textProperty() {
		return this.text;
	}
}
