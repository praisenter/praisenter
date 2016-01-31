package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

@XmlSeeAlso({
	BasicTextComponent.class,
	DateTimeComponent.class,
	SongTextComponent.class
})
public interface TextComponent extends SlideRegion, SlideComponent {
	public abstract SlidePaint getTextPaint();
	public abstract void setTextPaint(SlidePaint paint);
	public abstract SlideStroke getTextBorder();
	public abstract void setTextBorder(SlideStroke border);
	public abstract void setFontName(String name);
	public abstract String getFontName();
	public abstract void setFontSize(int size);
	public abstract int getFontSize();
	public abstract void setVerticalTextAlignment(VerticalTextAlignment alignment);
	public abstract VerticalTextAlignment getVerticalTextAlignment();
	public abstract void setHorizontalTextAlignment(HorizontalTextAlignment alignment);
	public abstract HorizontalTextAlignment getHorizontalTextAlignment();
	public abstract void setFontScaleType(FontScaleType type);
	public abstract FontScaleType getFontScaleType();
	public abstract void setPadding(int padding);
	public abstract int getPadding();
}
