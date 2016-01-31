package org.praisenter.slide;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlidePaintStroke;
import org.praisenter.slide.graphics.SlideStroke;

public abstract class AbstractSlideRegion implements SlideRegion {
	@XmlAttribute(name = "x", required = false)
	int x;
	
	@XmlAttribute(name = "y", required = false)
	int y;
	
	@XmlAttribute(name = "width", required = false)
	int width;
	
	@XmlAttribute(name = "height", required = false)
	int height;
	
	@XmlElement(name = "border", required = false)
	SlideStroke border;
	
	@XmlElement(name = "background", required = false)
	SlidePaint background;
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public int getY() {
		return this.y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public SlidePaint getBackground() {
		return this.background;
	}
	
	@Override
	public void setBackground(SlidePaint background) {
		this.background = background;
	}
	
	@Override
	public SlideStroke getBorder() {
		return this.border;
	}
	
	@Override
	public void setBorder(SlideStroke border) {
		this.border = border;
	}
	
	@Override
	public void adjust(double pw, double ph) {
		// adjust width/height
		this.width = (int)Math.floor((double)this.width * pw);
		this.height = (int)Math.floor((double)this.height * ph);
		
		// adjust positioning
		this.x = (int)Math.ceil((double)this.x * pw);
		this.y = (int)Math.ceil((double)this.y * ph);
	}
	
	@Override
	public Rectangle resize(int dw, int dh) {
		// update
		this.width += dw;
		this.height += dh;
		
		// make sure we dont go too small width/height
		if (this.width < Slide.MIN_SIZE) {
			this.width = Slide.MIN_SIZE;
		}
		if (this.height < Slide.MIN_SIZE) {
			this.height = Slide.MIN_SIZE;
		}
		
		return new Rectangle(this.x, this.y, this.width, this.height);
	}
	
	@Override
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#isTransitionRequired(org.praisenter.slide.SlideRegion)
	 */
	@Override
	public boolean isTransitionRequired(SlideRegion region) {
		if (region == null) return true;
		if (region == this) return false;
		
		// we need a transition if the position, size, background
		// or border are different
		if (this.x != region.getX() ||
			this.y != region.getY() ||
			this.width != region.getWidth() || 
			this.height != region.getHeight() ||
			!Objects.equals(this.background, region.getBackground()) ||
			!Objects.equals(this.border, region.getBorder())) {
			return true;
		}
		
		return false;
	}
}
