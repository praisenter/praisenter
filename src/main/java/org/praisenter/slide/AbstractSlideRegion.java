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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.graphics.Rectangle;
import org.praisenter.slide.graphics.SlideColor;
import org.praisenter.slide.graphics.SlideLinearGradient;
import org.praisenter.slide.graphics.SlidePaint;
import org.praisenter.slide.graphics.SlideRadialGradient;
import org.praisenter.slide.graphics.SlideStroke;
import org.praisenter.slide.media.MediaObject;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	SlideRadialGradient.class,
	MediaObject.class
})
public abstract class AbstractSlideRegion implements SlideRegion {
	/** The id */
	@JsonProperty
	@XmlElement(name = "id", required = false)
	UUID id;
	
	/** The x coordinate */
	@JsonProperty
	@XmlElement(name = "x", required = false)
	double x;
	
	/** The y coordinate */
	@JsonProperty
	@XmlElement(name = "y", required = false)
	double y;
	
	/** The width */
	@JsonProperty
	@XmlElement(name = "width", required = false)
	double width;
	
	/** The height */
	@JsonProperty
	@XmlElement(name = "height", required = false)
	double height;
	
	/** The border */
	@JsonProperty
	@XmlElement(name = "border", required = false)
	SlideStroke border;
	
	/** The background */
	@JsonProperty
	@XmlElement(name = "background", required = false)
	@XmlJavaTypeAdapter(value = SlidePaintXmlAdapter.class)
	SlidePaint background;
	
	/** The component level opacity */
	@JsonProperty
	@XmlElement(name = "opacity", required = false)
	double opacity;
	
	/**
	 * Default constructor.
	 */
	public AbstractSlideRegion() {
		this(UUID.randomUUID());
	}
	
	/**
	 * Subclass constructor.
	 * @param id the id
	 */
	public AbstractSlideRegion(UUID id) {
		this.id = id;
		this.opacity = 1.0;
		this.x = 0;
		this.y = 0;
		this.width = 100;
		this.height = 100;
		this.border = null;
		this.background = null;
	}
	
	/**
	 * Copy constructor.
	 * @param other the component to copy
	 * @param exact whether to copy the component exactly
	 */
	public AbstractSlideRegion(AbstractSlideRegion other, boolean exact) {
		this.id = exact ? other.id : UUID.randomUUID();
		
		this.opacity = other.opacity;
		this.x = other.x;
		this.y = other.y;
		this.width = other.width;
		this.height = other.height;
		// NOTE: all of these are immutable so no deep copy needed
		this.border = other.border;
		this.background = other.background;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getId()
	 */
	@Override
	public UUID getId() {
		return this.id;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getX()
	 */
	@Override
	public double getX() {
		return this.x;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setX(int)
	 */
	@Override
	public void setX(double x) {
		this.x = x;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getY()
	 */
	@Override
	public double getY() {
		return this.y;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setY(int)
	 */
	@Override
	public void setY(double y) {
		this.y = y;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getWidth()
	 */
	@Override
	public double getWidth() {
		return this.width;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setWidth(int)
	 */
	@Override
	public void setWidth(double width) {
		this.width = width;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getHeight()
	 */
	@Override
	public double getHeight() {
		return this.height;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setHeight(int)
	 */
	@Override
	public void setHeight(double height) {
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		return new Rectangle(this.x, this.y, this.width, this.height);
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
	 * @see org.praisenter.slide.SlideRegion#setOpacity(double)
	 */
	@Override
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getOpacity()
	 */
	@Override
	public double getOpacity() {
		return this.opacity;
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
	public Rectangle resize(double dw, double dh) {
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
	public void translate(double dx, double dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#isMediaReferenced(java.util.UUID[])
	 */
	@Override
	public final boolean isMediaReferenced(UUID... ids) {
		Set<UUID> media = this.getReferencedMedia();
		for (UUID testId : ids) {
			if (media.contains(testId)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getReferencedMedia()
	 */
	@Override
	public Set<UUID> getReferencedMedia() {
		Set<UUID> ids = new HashSet<UUID>();
		// check the background
		if (this.background != null && this.background instanceof MediaObject) {
			MediaObject mo = (MediaObject)this.background;
			if (mo.getId() != null) {
				ids.add(mo.getId());
			}
		}
		// check the slide stroke
		if (this.border != null && this.border.getPaint() != null && this.border.getPaint() instanceof MediaObject) {
			MediaObject mo = (MediaObject)this.border.getPaint();
			if (mo.getId() != null) {
				ids.add(mo.getId());
			}
		}
		return ids;
	}
}
