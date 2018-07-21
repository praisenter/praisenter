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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a stroke.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideStroke implements ReadonlySlideStroke, Copyable {
	private final ObjectProperty<SlidePaint> paint;
	private final ObjectProperty<SlideStrokeStyle> style;
	private final DoubleProperty width;
	private final DoubleProperty radius;
	
	public SlideStroke() {
		this.paint = new SimpleObjectProperty<>(new SlideColor(0, 0, 0, 1));
		this.style = new SimpleObjectProperty<>(new SlideStrokeStyle());
		this.width = new SimpleDoubleProperty(1);
		this.radius = new SimpleDoubleProperty(0);
	}
	
	public SlideStroke(SlidePaint paint, SlideStrokeStyle style, double width, double radius) {
		this();
		this.paint.set(paint);
		this.style.set(style);
		this.width.set(width);
		this.radius.set(radius);
	}
	
	@Override
	public SlideStroke copy() {
		SlidePaint paint = this.paint.get();
		SlideStrokeStyle style = this.style.get();
		return new SlideStroke(
				paint != null ? paint.copy() : null,
				style != null ? style.copy() : null,
				this.width.get(),
				this.radius.get());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("STROKE")
		  .append("[")
		  .append(this.paint.get()).append(", ")
		  .append(this.style.get()).append(", ")
		  .append(this.width.get()).append(", ")
		  .append(this.radius.get())
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				this.paint.get(),
				this.style.get(),
				this.width.get(),
				this.radius.get());
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
			if (Objects.equals(this.paint.get(), s.paint.get()) &&
				Objects.equals(this.style.get(), s.style.get()) &&
				this.radius.get() != s.radius.get() &&
				this.width.get() != s.width.get()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public SlidePaint getPaint() {
		return this.paint.get();
	}
	
	@JsonProperty
	public void setPaint(SlidePaint paint) {
		this.paint.set(paint);
	}
	
	@Override
	public ObjectProperty<SlidePaint> paintProperty() {
		return this.paint;
	}

	@Override
	@JsonProperty
	public SlideStrokeStyle getStyle() {
		return this.style.get();
	}
	
	@JsonProperty
	public void setStyle(SlideStrokeStyle style) {
		this.style.set(style);
	}
	
	@Override
	public ObjectProperty<SlideStrokeStyle> styleProperty() {
		return this.style;
	}
	
	@Override
	@JsonProperty
	public double getWidth() {
		return this.width.get();
	}
	
	@JsonProperty
	public void setWidth(double width) {
		this.width.set(width);
	}
	
	@Override
	public DoubleProperty widthProperty() {
		return this.width;
	}
	
	@Override
	@JsonProperty
	public double getRadius() {
		return this.radius.get();
	}
	
	@JsonProperty
	public void setRadius(double radius) {
		this.radius.set(radius);
	}
	
	@Override
	public DoubleProperty radiusProperty() {
		return this.radius;
	}
}
