package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.AbstractSlideComponent;
import org.praisenter.slide.SlideComponent;
import org.praisenter.slide.SlidePaintXmlAdapter;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.SlideStrokeXmlAdapter;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideStroke;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractTextComponent extends AbstractSlideComponent implements SlideRegion, SlideComponent, TextComponent {
	@XmlElement(name = "textPaint", required = false)
	@XmlJavaTypeAdapter(value = SlidePaintXmlAdapter.class)
	SlidePaint textPaint;
	
	@XmlElement(name = "textBorder", required = false)
	@XmlJavaTypeAdapter(value = SlideStrokeXmlAdapter.class)
	SlideStroke textBorder;
	
	@XmlAttribute(name = "fontName", required = false)
	String fontName;
	
	@XmlAttribute(name = "fontSize", required = false)
	int fontSize;
	
	@XmlAttribute(name = "fontScaleType", required = false)
	FontScaleType fontScaleType;
	
	@XmlAttribute(name = "verticalAlignment", required = false)
	VerticalTextAlignment verticalTextAlignment;
	
	@XmlAttribute(name = "horizontalAlignment", required = false)
	HorizontalTextAlignment horizontalTextAlignment;
	
	@XmlAttribute(name = "padding", required = false)
	int padding;
	
	public AbstractTextComponent() {
		this.fontScaleType = FontScaleType.NONE;
		this.fontSize = 30;
		this.verticalTextAlignment = VerticalTextAlignment.TOP;
		this.horizontalTextAlignment = HorizontalTextAlignment.LEFT;
		this.padding = 0;
	}
	
	@Override
	public SlidePaint getTextPaint() {
		return this.textPaint;
	}

	@Override
	public void setTextPaint(SlidePaint paint) {
		this.textPaint = paint;
	}

	@Override
	public SlideStroke getTextBorder() {
		return this.textBorder;
	}

	@Override
	public void setTextBorder(SlideStroke border) {
		this.textBorder = border;
	}

	@Override
	public void setFontName(String name) {
		this.fontName = name;
	}

	@Override
	public String getFontName() {
		return this.fontName;
	}

	@Override
	public void setFontSize(int size) {
		this.fontSize = size;
	}

	@Override
	public int getFontSize() {
		return this.fontSize;
	}

	@Override
	public void setFontScaleType(FontScaleType type) {
		this.fontScaleType = type;
	}

	@Override
	public FontScaleType getFontScaleType() {
		return this.fontScaleType;
	}

	@Override
	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		this.verticalTextAlignment = alignment;
	}

	@Override
	public VerticalTextAlignment getVerticalTextAlignment() {
		return this.verticalTextAlignment;
	}

	@Override
	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		this.horizontalTextAlignment = alignment;
	}

	@Override
	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return this.horizontalTextAlignment;
	}

	@Override
	public void setPadding(int padding) {
		this.padding = padding;
	}

	@Override
	public int getPadding() {
		return this.padding;
	}
}
