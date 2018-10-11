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
 * Represents a linear gradient paint.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideLinearGradient extends SlideGradient implements ReadOnlySlideLinearGradient, ReadOnlySlideGradient, SlidePaint, Copyable {
	private final DoubleProperty startX;
	private final DoubleProperty startY;
	private final DoubleProperty endX;
	private final DoubleProperty endY;
	
	public SlideLinearGradient() {
		super();
		this.startX = new SimpleDoubleProperty(0);
		this.startY = new SimpleDoubleProperty(0);
		this.endX = new SimpleDoubleProperty(1);
		this.endY = new SimpleDoubleProperty(1);
	}
	
	@Override
	public SlideLinearGradient copy() {
		SlideLinearGradient gradient = new SlideLinearGradient();
		gradient.startX.set(this.startX.get());
		gradient.startY.set(this.startY.get());
		gradient.endX.set(this.endX.get());
		gradient.endY.set(this.endY.get());
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
		sb.append("LINEAR_GRADIENT")
		  .append("[(")
		  .append(this.startX.get()).append(", ")
		  .append(this.startY.get()).append("), (")
		  .append(this.endX.get()).append(", ")
		  .append(this.endY.get()).append("), ")
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
				this.startX.get(),
				this.startY.get(),
				this.endX.get(),
				this.endY.get());
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
			if (this.startX.get() != g.startX.get() ||
				this.startY.get() != g.startY.get() ||
				this.endX.get() != g.endX.get() ||
				this.endY.get() != g.endY.get()) {
				return false;
			}
			return super.equals(obj);
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public double getStartX() {
		return this.startX.get();
	}
	
	@JsonProperty
	public void setStartX(double x) {
		this.startX.set(x);
	}
	
	@Override
	public DoubleProperty startXProperty() {
		return this.startX;
	}

	@Override
	@JsonProperty
	public double getStartY() {
		return this.startY.get();
	}
	
	@JsonProperty
	public void setStartY(double y) {
		this.startY.set(y);
	}
	
	@Override
	public DoubleProperty startYProperty() {
		return this.startY;
	}
	
	@Override
	@JsonProperty
	public double getEndX() {
		return this.endX.get();
	}
	
	@JsonProperty
	public void setEndX(double x) {
		this.endX.set(x);
	}
	
	@Override
	public DoubleProperty endXProperty() {
		return this.endX;
	}
	
	@Override
	@JsonProperty
	public double getEndY() {
		return this.endY.get();
	}
	
	@JsonProperty
	public void setEndY(double y) {
		this.endY.set(y);
	}
	
	@Override
	public DoubleProperty endYProperty() {
		return this.endY;
	}
}
