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
package org.praisenter.data.slide.graphics;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a solid color paint.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideColor implements ReadonlySlideColor, SlidePaint, Copyable {
	private final DoubleProperty red;
	private final DoubleProperty green;
	private final DoubleProperty blue;
	private final DoubleProperty alpha;
	
	public SlideColor() {
		this.red = new SimpleDoubleProperty(0);
		this.green = new SimpleDoubleProperty(0);
		this.blue = new SimpleDoubleProperty(0);
		this.alpha = new SimpleDoubleProperty(1);
	}

	public SlideColor(double red, double green, double blue, double alpha) {
		this();
		this.red.set(red);
		this.green.set(green);
		this.blue.set(blue);
		this.alpha.set(alpha);
	}
	
	@Override
	public SlideColor copy() {
		return new SlideColor(
				this.red.get(),
				this.green.get(),
				this.blue.get(),
				this.alpha.get());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("COLOR")
		  .append("[")
		  .append(this.red.get()).append(", ")
		  .append(this.green.get()).append(", ")
		  .append(this.blue.get()).append(", ")
		  .append(this.alpha.get())
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
		long v = Double.doubleToLongBits(this.red.get());
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.green.get());
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.blue.get());
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.alpha.get());
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
			if (c.red.get() == this.red.get() &&
				c.green.get() == this.green.get() &&
				c.blue.get() == this.blue.get() &&
				c.alpha.get() == this.alpha.get()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public double getRed() {
		return this.red.get();
	}
	
	@JsonProperty
	public void setRed(double red) {
		this.red.set(red);
	}
	
	@Override
	public DoubleProperty redProperty() {
		return this.red;
	}

	@Override
	@JsonProperty
	public double getGreen() {
		return this.green.get();
	}
	
	@JsonProperty
	public void setGreen(double green) {
		this.green.set(green);
	}
	
	@Override
	public DoubleProperty greenProperty() {
		return this.green;
	}

	@Override
	@JsonProperty
	public double getBlue() {
		return this.blue.get();
	}
	
	@JsonProperty
	public void setBlue(double blue) {
		this.blue.set(blue);
	}
	
	@Override
	public DoubleProperty blueProperty() {
		return this.blue;
	}

	@Override
	@JsonProperty
	public double getAlpha() {
		return this.alpha.get();
	}
	
	@JsonProperty
	public void setAlpha(double alpha) {
		this.alpha.set(alpha);
	}
	
	@Override
	public DoubleProperty alphaProperty() {
		return this.alpha;
	}
}
