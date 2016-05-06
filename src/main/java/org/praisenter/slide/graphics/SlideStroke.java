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
package org.praisenter.slide.graphics;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.slide.SlidePaintXmlAdapter;

/**
 * Represents a stroke.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "paintStroke")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideStroke {
	/** The stroke paint */
	@XmlElement(name = "paint", required = false)
	@XmlJavaTypeAdapter(value = SlidePaintXmlAdapter.class)
	final SlidePaint paint;
	
	/** The stroke style */
	@XmlElement(name = "style", required = false)
	final SlideStrokeStyle style;
	
	/** The stroke width */
	@XmlElement(name = "width", required = false)
	final double width;
	
	/** The stroke radius */
	@XmlElement(name = "radius", required = false)
	final double radius;
	
	/**
	 * Constructor for JAXB.
	 */
	@SuppressWarnings("unused")
	private SlideStroke() {
		// for jaxb
		this.paint = null;
		this.style = null;
		this.width = 0;
		this.radius = 0;
	}
	
	/**
	 * Creates a new stroke.
	 * @param paint the paint
	 * @param style the style
	 * @param width the width
	 * @param radius the radius
	 */
	public SlideStroke(SlidePaint paint, SlideStrokeStyle style, double width, double radius) {
		this.paint = paint;
		this.style = style;
		this.width = width;
		this.radius = radius;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		hash = 31 * hash + this.paint.hashCode();
		hash = 31 * hash + this.style.hashCode();
		long v = Double.doubleToLongBits(this.width);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.radius);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideStroke) {
			SlideStroke s = (SlideStroke)obj;
			if (Objects.equals(this.paint, s.paint) &&
				Objects.equals(this.style, s.style) &&
				this.radius != s.radius &&
				this.width != s.width) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the paint.
	 * @return {@link SlidePaint}
	 */
	public SlidePaint getPaint() {
		return this.paint;
	}

	/**
	 * Returns the style.
	 * @return {@link SlideStrokeStyle}
	 */
	public SlideStrokeStyle getStyle() {
		return this.style;
	}

	/**
	 * Returns the width.
	 * @return double
	 */
	public double getWidth() {
		return this.width;
	}

	/**
	 * Returns the radius.
	 * @return double
	 */
	public double getRadius() {
		return this.radius;
	}
}
