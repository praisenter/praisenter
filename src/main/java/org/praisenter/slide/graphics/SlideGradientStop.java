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

/**
 * A stop in a gradient based on an offset between 0 and 1.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "stop")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideGradientStop {
	/** The stop location from 0.0 - 1.0 inclusive */
	@XmlElement(name = "offset", required = false)
	final double offset;
	
	/** The stop color */
	@XmlElement(name = "color", required = false)
	final SlideColor color;
	
	/**
	 * Default constructor.
	 */
	public SlideGradientStop() {
		// for jaxb
		this(0.0f, 0, 0, 0, 1.0);
	}
	
	/**
	 * Creates a new stop with the given offset and color.
	 * @param offset the offset
	 * @param color the color
	 */
	public SlideGradientStop(double offset, SlideColor color) {
		this.offset = offset;
		this.color = color;
	}
	
	/**
	 * Creates a new stop with the given offset and color.
	 * @param offset the offset
	 * @param red the color's red component
	 * @param green the color's green component
	 * @param blue the color's blue component
	 * @param alpha the color's alpha component
	 */
	public SlideGradientStop(double offset, int red, int green, int blue, double alpha) {
		this.offset = offset;
		this.color = new SlideColor(red / 255.0, green / 255.0, blue / 255.0, alpha);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		hash = 31 * hash + this.color.hashCode();
		long v = Double.doubleToLongBits(this.offset);
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
		if (obj instanceof SlideGradientStop) {
			SlideGradientStop s = (SlideGradientStop)obj;
			if (Objects.equals(this.color, s.color) &&
				this.offset == s.offset) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * The offset of this stop.
	 * @return double
	 */
	public double getOffset() {
		return this.offset;
	}

	/**
	 * The color for this stop.
	 * @return {@link SlideColor}
	 */
	public SlideColor getColor() {
		return this.color;
	}
}
