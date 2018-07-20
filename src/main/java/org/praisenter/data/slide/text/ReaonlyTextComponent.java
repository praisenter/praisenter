package org.praisenter.data.slide.text;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadonlySlideComponent;
import org.praisenter.data.slide.ReadonlySlideRegion;
import org.praisenter.data.slide.effects.SlideShadow;
import org.praisenter.data.slide.graphics.SlidePadding;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.graphics.SlideStroke;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

public interface ReaonlyTextComponent extends ReadonlySlideComponent, ReadonlySlideRegion, Copyable, Identifiable {
	public SlidePaint getTextPaint();
	public SlideStroke getTextBorder();
	public SlideShadow getTextShadow();
	public SlideShadow getTextGlow();
	public SlideFont getFont();
	public VerticalTextAlignment getVerticalTextAlignment();
	public HorizontalTextAlignment getHorizontalTextAlignment();
	public FontScaleType getFontScaleType();
	public SlidePadding getPadding();
	public double getLineSpacing();
	public boolean isTextWrappingEnabled();
	public String getText();
	
	public ReadOnlyObjectProperty<SlidePaint> textPaintProperty();
	public ReadOnlyObjectProperty<SlideStroke> textBorderProperty();
	public ReadOnlyObjectProperty<SlideShadow> textShadowProperty();
	public ReadOnlyObjectProperty<SlideShadow> textGlowProperty();
	public ReadOnlyObjectProperty<SlideFont> fontProperty();
	public ReadOnlyObjectProperty<VerticalTextAlignment> verticalTextAlignmentProperty();
	public ReadOnlyObjectProperty<HorizontalTextAlignment> horizontalTextAlignmentProperty();
	public ReadOnlyObjectProperty<FontScaleType> fontScaleTypeProperty();
	public ReadOnlyObjectProperty<SlidePadding> paddingProperty();
	public ReadOnlyDoubleProperty lineSpacingProperty();
	public ReadOnlyBooleanProperty textWrappingEnabledProperty();
	public ReadOnlyStringProperty textProperty();
}
