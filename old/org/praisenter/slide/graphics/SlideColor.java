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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a solid color paint.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideColor extends AbstractSlidePaint implements SlidePaint {
	/** The red component */
	@JsonProperty
	final double red;
	
	/** The green component */
	@JsonProperty
	final double green;
	
	/** The blue component */
	@JsonProperty
	final double blue;
	
	/** The alpha component */
	@JsonProperty
	final double alpha;
	
	/**
	 * Default constructor.
	 */
	public SlideColor() {
		// for jaxb
		this.red = 0;
		this.green = 0;
		this.blue = 0;
		this.alpha = 1;
	}

	/**
	 * Full constructor.
	 * @param red the red component
	 * @param green the green component
	 * @param blue the blue component
	 * @param alpha the alpha component
	 */
	public SlideColor(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("COLOR")
		  .append("[")
		  .append(this.red).append(", ")
		  .append(this.green).append(", ")
		  .append(this.blue).append(", ")
		  .append(this.alpha)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		// see http://stackoverflow.com/a/31220250
		long v = Double.doubleToLongBits(this.red);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.green);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.blue);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.alpha);
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
		if (obj instanceof SlideColor) {
			SlideColor c = (SlideColor)obj;
			if (c.red == this.red &&
				c.green == this.green &&
				c.blue == this.blue &&
				c.alpha == this.alpha) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the red component.
	 * @return double
	 */
	public double getRed() {
		return this.red;
	}

	/**
	 * Returns the green component.
	 * @return double
	 */
	public double getGreen() {
		return this.green;
	}

	/**
	 * Returns the blue component.
	 * @return double
	 */
	public double getBlue() {
		return this.blue;
	}

	/**
	 * Returns the alpha component.
	 * @return double
	 */
	public double getAlpha() {
		return this.alpha;
	}
}
