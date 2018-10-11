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

import java.util.Objects;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a radial gradient.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideRadialGradient extends SlideGradient implements ReadOnlySlideRadialGradient, ReadOnlySlideGradient, SlidePaint, Copyable {
	private final DoubleProperty centerX;
	private final DoubleProperty centerY;
	private final DoubleProperty radius;
	
	public SlideRadialGradient() {
		super();
		this.centerX = new SimpleDoubleProperty(0);
		this.centerY = new SimpleDoubleProperty(0);
		this.radius = new SimpleDoubleProperty(1);
	}
	
	@Override
	public SlideRadialGradient copy() {
		SlideRadialGradient gradient = new SlideRadialGradient();
		gradient.centerX.set(this.centerX.get());
		gradient.centerY.set(this.centerY.get());
		gradient.radius.set(this.radius.get());
		gradient.cycleType.set(this.cycleType.get());
		gradient.stops.clear();
		for (SlideGradientStop stop : this.stops) {
			gradient.stops.add(stop.copy());
		}
		return gradient;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RADIAL_GRADIENT")
		  .append("[(")
		  .append(this.centerX.get()).append(", ")
		  .append(this.centerY.get()).append("), ")
		  .append(this.radius.get()).append(", ")
		  .append(this.cycleType.get()).append(", [");
		for (SlideGradientStop stop : this.stops) {
			sb.append(stop).append(", ");
		}
		sb.append("]]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.graphics.SlideGradient#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		hash = hash * 31 + super.hashCode();
		hash = hash * 31 + Objects.hash(
				this.centerX.get(),
				this.centerY.get(),
				this.radius.get());
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
			if (this.centerX.get() != g.centerX.get() ||
				this.centerY.get() != g.centerY.get() ||
				this.radius.get() != g.radius.get()) {
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public double getCenterX() {
		return this.centerX.get();
	}
	
	@JsonProperty
	public void setCenterX(double x) {
		this.centerX.set(x);
	}
	
	@Override
	public DoubleProperty centerXProperty() {
		return this.centerX;
	}

	@Override
	@JsonProperty
	public double getCenterY() {
		return this.centerY.get();
	}
	
	@JsonProperty
	public void setCenterY(double y) {
		this.centerY.set(y);
	}
	
	@Override
	public DoubleProperty centerYProperty() {
		return this.centerY;
	}
	
	@Override
	@JsonProperty
	public double getRadius() {
		return this.radius.get();
	}
	
	@JsonProperty
	public void setRadius(double x) {
		this.radius.set(x);
	}
	
	@Override
	public DoubleProperty radiusProperty() {
		return this.radius;
	}
}
