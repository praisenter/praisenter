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
 * A stop in a gradient based on an offset between 0 and 1.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideGradientStop implements ReadOnlySlideGradientStop, Copyable {
	private final DoubleProperty offset;
	private final ObjectProperty<SlideColor> color;
	
	public SlideGradientStop() {
		this.offset = new SimpleDoubleProperty(0);
		this.color = new SimpleObjectProperty<>(new SlideColor());
	}
	
	public SlideGradientStop(double offset, SlideColor color) {
		this();
		this.offset.set(offset);
		this.color.set(color);
	}
	
	public SlideGradientStop(double offset, double red, double green, double blue, double alpha) {
		this();
		this.offset.set(offset);
		this.color.set(new SlideColor(red, green, blue, alpha));
	}
	
	@Override
	public SlideGradientStop copy() {
		SlideColor color = this.color.get();
		return new SlideGradientStop(
				this.offset.get(), 
				color != null ? color.copy() : null);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("STOP")
		  .append("[")
		  .append(this.offset.get()).append(", ")
		  .append(this.color.get())
		  .append("]");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.color.get(), this.offset.get());
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
			if (Objects.equals(this.color.get(), s.color.get()) &&
				this.offset.get() == s.offset.get()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public double getOffset() {
		return this.offset.get();
	}
	
	@JsonProperty
	public void setOffset(double offset) {
		this.offset.set(offset);
	}
	
	@Override
	public DoubleProperty offsetProperty() {
		return this.offset;
	}

	@Override
	@JsonProperty
	public SlideColor getColor() {
		return this.color.get();
	}
	
	@JsonProperty
	public void setColor(SlideColor color) {
		this.color.set(color);
	}
	
	@Override
	public ObjectProperty<SlideColor> colorProperty() {
		return this.color;
	}
}
