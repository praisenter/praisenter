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
package org.praisenter.slide.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a font.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "font")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideFont {
	/** The font family */
	@JsonProperty
	@XmlElement(name = "family", required = false)
	final String family;
	
	/** The font weight (bold, extra bold, etc) */
	@JsonProperty
	@XmlElement(name = "weight", required = false)
	final SlideFontWeight weight;
	
	/** The font posture (italic, regular) */
	@JsonProperty
	@XmlElement(name = "posture", required = false)
	final SlideFontPosture posture;
	
	/** The font size */
	@JsonProperty
	@XmlElement(name = "size", required = false)
	final double size;
	
	/**
	 * Default constructor for JAXB.
	 */
	public SlideFont() {
		this.family = "SansSerif";
		this.weight = SlideFontWeight.NORMAL;
		this.posture = SlideFontPosture.REGULAR;
		this.size = 10;
	}
	
	/**
	 * Full constructor.
	 * @param family the font family
	 * @param weight the font weight (bold, extra bold, etc)
	 * @param posture the font posture (italic, regular)
	 * @param size the font size
	 */
	public SlideFont(String family, SlideFontWeight weight, SlideFontPosture posture, double size) {
		this.family = family;
		this.weight = weight;
		this.posture = posture;
		this.size = size;
	}
	
	/**
	 * Returns a new font exactly like this one, but with the given font size.
	 * @param size the new font size
	 * @return {@link SlideFont}
	 */
	public SlideFont size(double size) {
		return new SlideFont(this.family, this.weight, this.posture, size);
	}

	/**
	 * Returns the font family.
	 * @return String
	 */
	public String getFamily() {
		return this.family;
	}

	/**
	 * Returns the font weight;
	 * @return {@link SlideFontWeight}
	 */
	public SlideFontWeight getWeight() {
		return this.weight;
	}

	/**
	 * Returns the font posture.
	 * @return {@link SlideFontPosture}
	 */
	public SlideFontPosture getPosture() {
		return this.posture;
	}

	/**
	 * Returns the font size.
	 * @return double
	 */
	public double getSize() {
		return this.size;
	}
}
