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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a radial gradient.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "radialGradient")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideRadialGradient extends SlideGradient implements SlidePaint {
	/** The center x between 0 and 1 */
	@XmlAttribute(name = "cx", required = false)
	final double centerX;
	
	/** The center y between 0 and 1 */
	@XmlAttribute(name = "cy", required = false)
	final double centerY;
	
	/** The radius between 0 and 1 */
	@XmlAttribute(name = "r", required = false)
	final double radius;
	
	/** The cycle type */
	@XmlAttribute(name = "cycle", required = false)
	final SlideGradientCycleType cycleType;
	
	/**
	 * Constructor for JAXB.
	 */
	@SuppressWarnings("unused")
	private SlideRadialGradient() {
		this(0, 0, 0, SlideGradientCycleType.NONE, (SlideGradientStop[])null);
	}
	
	/**
	 * Creates a new radial gradient.
	 * @param centerX the center x between 0 and 1
	 * @param centerY the center y between 0 and 1
	 * @param radius the radius between 0 and 1
	 * @param cycleType the cycle type
	 * @param stops the stops
	 */
	public SlideRadialGradient(double centerX, double centerY, double radius, SlideGradientCycleType cycleType, List<SlideGradientStop> stops) {
		super(stops);
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.cycleType = cycleType;
	}
	
	/**
	 * Creates a new radial gradient.
	 * @param centerX the center x between 0 and 1
	 * @param centerY the center y between 0 and 1
	 * @param radius the radius between 0 and 1
	 * @param cycleType the cycle type
	 * @param stops the stops
	 */
	public SlideRadialGradient(double centerX, double centerY, double radius, SlideGradientCycleType cycleType, SlideGradientStop... stops) {
		super(stops);
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.cycleType = cycleType;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.graphics.SlideGradient#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		hash = hash * 31 + super.hashCode();
		hash = hash * 31 + this.cycleType.hashCode();
		long v = Double.doubleToLongBits(this.centerX);
		hash = hash * 31 + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.centerY);
		hash = hash * 31 + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.radius);
		hash = hash * 31 + (int)(v ^ (v >>> 32));
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.graphics.SlideGradient#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideRadialGradient) {
			SlideRadialGradient g = (SlideRadialGradient)obj;
			if (this.centerX != g.centerX ||
				this.centerY != g.centerY ||
				this.radius != g.radius ||
				this.cycleType != g.cycleType) {
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}
	
	/**
	 * Returns the center x between 0 and 1.
	 * @return double
	 */
	public double getCenterX() {
		return this.centerX;
	}

	/**
	 * Returns the center y between 0 and 1.
	 * @return double
	 */
	public double getCenterY() {
		return this.centerY;
	}

	/**
	 * Returns the radius between 0 and 1.
	 * @return double
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * Returns the cycle type.
	 * @return {@link SlideGradientCycleType}
	 */
	public SlideGradientCycleType getCycleType() {
		return this.cycleType;
	}
}
