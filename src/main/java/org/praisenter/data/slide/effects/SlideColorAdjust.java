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

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a color adjustment.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideColorAdjust implements ReadOnlySlideColorAdjust {
	private final DoubleProperty hue;
	private final DoubleProperty saturation;
	private final DoubleProperty brightness;
	private final DoubleProperty contrast;
	
	public SlideColorAdjust() {
		this.hue = new SimpleDoubleProperty(0);
		this.saturation = new SimpleDoubleProperty(0);
		this.brightness = new SimpleDoubleProperty(0);
		this.contrast = new SimpleDoubleProperty(0);
	}

	@Override
	public SlideColorAdjust copy() {
		SlideColorAdjust adjust = new SlideColorAdjust();
		adjust.hue.set(this.hue.get());
		adjust.saturation.set(this.saturation.get());
		adjust.brightness.set(this.brightness.get());
		adjust.contrast.set(this.contrast.get());
		return adjust;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("COLOR_ADJUST")
		  .append("[")
		  .append(this.hue.get()).append(", ")
		  .append(this.saturation.get()).append(", ")
		  .append(this.brightness.get()).append(", ")
		  .append(this.contrast.get())
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				this.hue.get(),
				this.saturation.get(),
				this.brightness.get(),
				this.contrast.get());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideColorAdjust) {
			SlideColorAdjust adjust = (SlideColorAdjust)obj;
			return adjust.hue.get() == this.hue.get() &&
				   adjust.saturation.get() == this.saturation.get() &&
				   adjust.brightness.get() == this.brightness.get() &&
				   adjust.contrast.get() == this.contrast.get();
		}
		return false;
	}

	@Override
	@JsonProperty
	public double getHue() {
		return this.hue.get();
	}
	
	@JsonProperty
	public void setHue(double hue) {
		this.hue.set(hue);
	}
	
	@Override
	public DoubleProperty hueProperty() {
		return this.hue;
	}

	@Override
	@JsonProperty
	public double getSaturation() {
		return this.saturation.get();
	}
	
	@JsonProperty
	public void setSaturation(double saturation) {
		this.saturation.set(saturation);
	}
	
	@Override
	public DoubleProperty saturationProperty() {
		return this.saturation;
	}
	
	@Override
	@JsonProperty
	public double getBrightness() {
		return this.brightness.get();
	}
	
	@JsonProperty
	public void setBrightness(double brightness) {
		this.brightness.set(brightness);
	}
	
	@Override
	public DoubleProperty brightnessProperty() {
		return this.brightness;
	}
	
	@Override
	@JsonProperty
	public double getContrast() {
		return this.contrast.get();
	}
	
	@JsonProperty
	public void setContrast(double contrast) {
		this.contrast.set(contrast);
	}
	
	@Override
	public DoubleProperty contrastProperty() {
		return this.contrast;
	}
}
