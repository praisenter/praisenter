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
package org.praisenter.data.slide.effects;

import java.util.Objects;

import org.praisenter.data.Copyable;
import org.praisenter.data.slide.graphics.SlideColor;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a colored shadow (or glow) that can be applied.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class SlideShadow implements ReadOnlySlideShadow, Copyable {
	private final ObjectProperty<ShadowType> type;
	private final ObjectProperty<SlideColor> color;
	private final DoubleProperty offsetX;
	private final DoubleProperty offsetY;
	private final DoubleProperty radius;
	private final DoubleProperty spread;
	
	public SlideShadow() {
		this.type = new SimpleObjectProperty<>(ShadowType.OUTER);
		this.color = new SimpleObjectProperty<>(new SlideColor());
		this.offsetX = new SimpleDoubleProperty(0);
		this.offsetY = new SimpleDoubleProperty(0);
		this.radius = new SimpleDoubleProperty(10);
		this.spread = new SimpleDoubleProperty(0);
	}

	@Override
	public SlideShadow copy() {
		SlideShadow shadow = new SlideShadow();
		shadow.color.set(this.color.get());
		shadow.offsetX.set(this.offsetX.get());
		shadow.offsetY.set(this.offsetY.get());
		shadow.radius.set(this.radius.get());
		shadow.spread.set(this.spread.get());
		shadow.type.set(this.type.get());
		return shadow;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SHADOW_OR_GLOW")
		  .append("[")
		  .append(this.type.get()).append(", ")
		  .append(this.color.get()).append(", ")
		  .append(this.offsetX.get()).append(", ")
		  .append(this.offsetY.get()).append(", ")
		  .append(this.radius.get()).append(", ")
		  .append(this.spread.get())
		  .append("]");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideShadow) {
			SlideShadow shadow = (SlideShadow)obj;
			if (!Objects.equals(shadow.type.get(), this.type.get()) ||
				!Objects.equals(shadow.color.get(), this.color.get()) ||
				shadow.offsetX.get() != this.offsetX.get() ||
				shadow.offsetY.get() != this.offsetY.get() ||
				shadow.radius.get() != this.radius.get() ||
				shadow.spread.get() != this.spread.get()) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(
				this.type.get(),
				this.color.get(),
				this.offsetX.get(),
				this.offsetY.get(),
				this.radius.get(),
				this.spread.get());
	}

	@Override
	@JsonProperty
	public ShadowType getType() {
		return this.type.get();
	}
	
	@JsonProperty
	public void setType(ShadowType type) {
		this.type.set(type);
	}
	
	@Override
	public ObjectProperty<ShadowType> typeProperty() {
		return this.type;
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
	
	@Override
	@JsonProperty
	public double getOffsetX() {
		return this.offsetX.get();
	}
	
	@JsonProperty
	public void setOffsetX(double offset) {
		this.offsetX.set(offset);
	}
	
	@Override
	public DoubleProperty offsetXProperty() {
		return this.offsetX;
	}
	
	@Override
	@JsonProperty
	public double getOffsetY() {
		return this.offsetY.get();
	}
	
	@JsonProperty
	public void setOffsetY(double offset) {
		this.offsetY.set(offset);
	}
	
	@Override
	public DoubleProperty offsetYProperty() {
		return this.offsetY;
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
	
	@Override
	@JsonProperty
	public double getSpread() {
		return this.spread.get();
	}
	
	@JsonProperty
	public void setSpread(double spread) {
		this.spread.set(spread);
	}
	
	@Override
	public DoubleProperty spreadProperty() {
		return this.spread;
	}
}
