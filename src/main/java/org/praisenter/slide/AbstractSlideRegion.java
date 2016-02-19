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
package org.praisenter.slide;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStroke;

/**
 * Abstract implementation of the {@link SlideRegion} interface.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({
	SlideStroke.class,
	SlideColor.class,
	SlideLinearGradient.class,
	SlideRadialGradient.class
})
public abstract class AbstractSlideRegion implements SlideRegion {
	/** The x coordinate */
	@XmlAttribute(name = "x", required = false)
	int x;
	
	/** The y coordinate */
	@XmlAttribute(name = "y", required = false)
	int y;
	
	/** The width */
	@XmlAttribute(name = "width", required = false)
	int width;
	
	/** The height */
	@XmlAttribute(name = "height", required = false)
	int height;
	
	/** The border */
	@XmlElement(name = "border", required = false)
	SlideStroke border;
	
	/** The background */
	@XmlElement(name = "background", required = false)
	@XmlJavaTypeAdapter(value = SlidePaintXmlAdapter.class)
	SlidePaint background;
	
	/**
	 * Copies over the values of this region to the given region.
	 * @param to the region to copy to
	 */
	protected void copy(SlideRegion to) {
		// shouldn't need a deep copy of any of these
		to.setX(this.x);
		to.setY(this.y);
		to.setWidth(this.width);
		to.setHeight(this.height);
		to.setBorder(this.border);
		to.setBackground(this.background);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getX()
	 */
	@Override
	public int getX() {
		return this.x;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setX(int)
	 */
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getY()
	 */
	@Override
	public int getY() {
		return this.y;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setY(int)
	 */
	@Override
	public void setY(int y) {
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getWidth()
	 */
	@Override
	public int getWidth() {
		return this.width;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setWidth(int)
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getHeight()
	 */
	@Override
	public int getHeight() {
		return this.height;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setHeight(int)
	 */
	@Override
	public void setHeight(int height) {
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getBackground()
	 */
	@Override
	public SlidePaint getBackground() {
		return this.background;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setBackground(org.praisenter.slide.graphics.SlidePaint)
	 */
	@Override
	public void setBackground(SlidePaint background) {
		this.background = background;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getBorder()
	 */
	@Override
	public SlideStroke getBorder() {
		return this.border;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setBorder(org.praisenter.slide.graphics.SlideStroke)
	 */
	@Override
	public void setBorder(SlideStroke border) {
		this.border = border;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#adjust(double, double)
	 */
	@Override
	public void adjust(double pw, double ph) {
		// adjust width/height
		this.width = (int)Math.floor((double)this.width * pw);
		this.height = (int)Math.floor((double)this.height * ph);
		
		// adjust positioning
		this.x = (int)Math.ceil((double)this.x * pw);
		this.y = (int)Math.ceil((double)this.y * ph);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#resize(int, int)
	 */
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
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#translate(int, int)
	 */
	@Override
	public void translate(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#isBackgroundTransitionRequired(org.praisenter.slide.SlideRegion)
	 */
	@Override
	public boolean isBackgroundTransitionRequired(SlideRegion region) {
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
