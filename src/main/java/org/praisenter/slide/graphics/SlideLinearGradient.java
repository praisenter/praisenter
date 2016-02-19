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
 * Represents a linear gradient paint.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "linearGradient")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideLinearGradient extends SlideGradient implements SlidePaint {
	/** The start x between 0 and 1 */
	@XmlAttribute(name = "sx", required = false)
	final double startX;
	
	/** The start y between 0 and 1 */
	@XmlAttribute(name = "sy", required = false)
	final double startY;
	
	/** The end x between 0 and 1 */
	@XmlAttribute(name = "ex", required = false)
	final double endX;
	
	/** The end y between 0 and 1 */
	@XmlAttribute(name = "ey", required = false)
	final double endY;
	
	/** The cycle type */
	@XmlAttribute(name = "cycle", required = false)
	final SlideGradientCycleType cycleType;
	
	/**
	 * Constructor for JAXB.
	 */
	@SuppressWarnings("unused")
	private SlideLinearGradient() {
		this(0, 0, 0, 0, SlideGradientCycleType.NONE, (SlideGradientStop[])null);
	}
	
	/**
	 * Creates a new linear gradient.
	 * @param startX the start x between 0 and 1
	 * @param startY the start y between 0 and 1
	 * @param endX the end x between 0 and 1
	 * @param endY the end y between 0 and 1
	 * @param cycleType the cycle type
	 * @param stops the stops
	 */
	public SlideLinearGradient(double startX, double startY, double endX, double endY, SlideGradientCycleType cycleType, List<SlideGradientStop> stops) {
		super(stops);
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.cycleType = cycleType;
	}
	
	/**
	 * Creates a new linear gradient.
	 * @param startX the start x between 0 and 1
	 * @param startY the start y between 0 and 1
	 * @param endX the end x between 0 and 1
	 * @param endY the end y between 0 and 1
	 * @param cycleType the cycle type
	 * @param stops the stops
	 */
	public SlideLinearGradient(double startX, double startY, double endX, double endY, SlideGradientCycleType cycleType, SlideGradientStop... stops) {
		super(stops);
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
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
		long v = Double.doubleToLongBits(this.startX);
		hash = hash * 31 + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.startY);
		hash = hash * 31 + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.endX);
		hash = hash * 31 + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.endY);
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
		if (obj instanceof SlideLinearGradient) {
			SlideLinearGradient g = (SlideLinearGradient)obj;
			if (this.startX != g.startX ||
				this.startY != g.startY ||
				this.endX != g.endX ||
				this.endY != g.endY ||
				this.cycleType != g.cycleType) {
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}
	
	/**
	 * Returns the start x between 0 and 1.
	 * @return double
	 */
	public double getStartX() {
		return this.startX;
	}

	/**
	 * Returns the start y between 0 and 1.
	 * @return double
	 */
	public double getStartY() {
		return this.startY;
	}

	/**
	 * Returns the end x between 0 and 1.
	 * @return double
	 */
	public double getEndX() {
		return this.endX;
	}

	/**
	 * Returns the end y between 0 and 1.
	 * @return double
	 */
	public double getEndY() {
		return this.endY;
	}

	/**
	 * Returns cycle type.
	 * @return {@link SlideGradientCycleType}
	 */
	public SlideGradientCycleType getCycleType() {
		return this.cycleType;
	}
}
