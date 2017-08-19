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
package org.praisenter.slide.effects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a color adjustment.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideColorAdjust {
	/** The hue */
	@JsonProperty
	final double hue;
	
	/** The saturation */
	@JsonProperty
	final double saturation;
	
	/** The brightness */
	@JsonProperty
	final double brightness;

	/** The contrast */
	@JsonProperty
	final double contrast;
	
	/**
	 * Default constructor.
	 */
	public SlideColorAdjust() {
		this(0.0, 0.0, 0.0, 0.0);
	}

	/**
	 * Full constructor.
	 * @param hue the hue
	 * @param saturation the saturation
	 * @param brightness the brightness
	 * @param contrast the contrast
	 */
	public SlideColorAdjust(double hue, double saturation, double brightness, double contrast) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
		this.contrast = contrast;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 23;
		hash = 31 * hash + Double.hashCode(this.hue);
		hash = 31 * hash + Double.hashCode(this.saturation);
		hash = 31 * hash + Double.hashCode(this.brightness);
		hash = 31 * hash + Double.hashCode(this.contrast);
		return hash;
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
			return adjust.hue == this.hue &&
				   adjust.saturation == this.saturation &&
				   adjust.brightness == this.brightness &&
				   adjust.contrast == this.contrast;
		}
		return false;
	}

	/**
	 * Returns the hue.
	 * @return double
	 */
	public double getHue() {
		return this.hue;
	}

	/**
	 * Returns the saturation.
	 * @return double
	 */
	public double getSaturation() {
		return this.saturation;
	}

	/**
	 * Returns the brightness.
	 * @return double
	 */
	public double getBrightness() {
		return this.brightness;
	}

	/**
	 * Returns the contrast.
	 * @return double
	 */
	public double getContrast() {
		return this.contrast;
	}
}
