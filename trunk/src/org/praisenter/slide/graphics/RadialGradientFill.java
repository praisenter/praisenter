/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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

import java.awt.Color;
import java.awt.RadialGradientPaint;
import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a radial gradient {@link Fill} .
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "RadialGradientFill")
@XmlAccessorType(XmlAccessType.NONE)
public class RadialGradientFill extends AbstractGradientFill implements Fill, Serializable {
	/** The version id */
	private static final long serialVersionUID = -7948895575062286209L;
	
	/** The gradient direction */
	@XmlElement(name = "Direction")
	protected RadialGradientDirection direction;
	
	/**
	 * Default constructor.
	 */
	public RadialGradientFill() {
		super();
		this.direction = RadialGradientDirection.CENTER;
	}
	
	/**
	 * Full constructor.
	 * @param direction the center location
	 * @param stops the stops
	 */
	public RadialGradientFill(RadialGradientDirection direction, Stop... stops) {
		super(stops);
		this.direction = direction;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Fill#getPaint(int, int, int, int)
	 */
	@Override
	public RadialGradientPaint getPaint(int x, int y, int w, int h) {
		int size = this.stops.length;
		
		// get the fractions and colors
		float[] fractions = new float[size];
		Color[] colors = new Color[size];
		for (int i = 0; i < size; i++) {
			Stop stop = this.stops[i];
			fractions[i] = stop.fraction;
			colors[i] = stop.color.getPaint(x, y, w, h);
		}
		
		// get the start and end points
		float cx, cy, r = (float)Math.hypot(w, h);
		if (this.direction == RadialGradientDirection.BOTTOM_LEFT) {
			cx = x;
			cy = y + h;
		} else if (this.direction == RadialGradientDirection.BOTTOM_RIGHT) {
			cx = x + w;
			cy = y + h;
		} else if (this.direction == RadialGradientDirection.TOP_LEFT) {
			cx = x;
			cy = y;
		} else if (direction == RadialGradientDirection.TOP_RIGHT) {
			cx = x + w;
			cy = y;
		} else {
			cx = x + w / 2;
			cy = y + h / 2;
			r = r / 2;
		}
		r += OFFSET;
		
		return new RadialGradientPaint(cx, cy, r, fractions, colors);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof RadialGradientFill) {
			RadialGradientFill f = (RadialGradientFill)obj;
			if (f.direction == this.direction && Arrays.equals(this.stops, f.stops)) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int r = 37;
		r = 37 * r + this.direction.ordinal();
		for (Stop stop : this.stops) {
			r = 37 * r + stop.hashCode();
		}
		return r;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RadialGradientFill[Direction=").append(this.direction)
		  .append("|Stops={");
		for (Stop stop : this.stops) {
			sb.append(stop.toString());
		}
		sb.append("}]");
		return sb.toString();
	}
	
	/**
	 * Returns the center location.
	 * @return {@link RadialGradientDirection}
	 */
	public RadialGradientDirection getDirection() {
		return this.direction;
	}
}
