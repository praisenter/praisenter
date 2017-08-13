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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.graphics.ShadowType;
import org.praisenter.slide.graphics.SlideColor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a colored shadow (or glow) that can be applied.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
@XmlRootElement(name = "shadow")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideShadow {
	/** The shadow type */
	@JsonProperty
	@XmlElement(name = "type", required = false)
	final ShadowType type;
	
	/** The color */
	@JsonProperty
	@XmlElement(name = "color", required = false)
	final SlideColor color;
	
	/** The offset from the left */
	@JsonProperty
	@XmlElement(name = "offsetX", required = false)
	final double offsetX;
	
	/** The offset from the top */
	@JsonProperty
	@XmlElement(name = "offsetY", required = false)
	final double offsetY;
	
	/** The radius of the blur */
	@JsonProperty
	@XmlElement(name = "radius", required = false)
	final double radius;

	/** The portion of the radius that has 100% of the color */
	@JsonProperty
	@XmlElement(name = "spread", required = false)
	final double spread;

	/**
	 * Creates a default shadow.
	 */
	public SlideShadow() {
		this(ShadowType.OUTER, new SlideColor(), 0.0, 0.0, 10.0, 0.0);
	}
	
	/**
	 * Full constructor.
	 * @param type the shadow type
	 * @param color the color
	 * @param offsetX the offset from the left
	 * @param offsetY the offset from the top
	 * @param radius the blur radius
	 * @param spread the spread of the shadow
	 */
	public SlideShadow(ShadowType type, SlideColor color, double offsetX, double offsetY, double radius, double spread) {
		this.type = type;
		this.color = color;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.radius = radius;
		this.spread = spread;
	}

	/**
	 * Returns the shadow type.
	 * @return {@link ShadowType}
	 */
	public ShadowType getType() {
		return this.type;
	}
	
	/**
	 * Returns the color.
	 * @return {@link SlideColor}
	 */
	public SlideColor getColor() {
		return this.color;
	}

	/**
	 * Returns the offset from the left.
	 * @return double
	 */
	public double getOffsetX() {
		return this.offsetX;
	}

	/**
	 * Returns the offset from the top.
	 * @return double
	 */
	public double getOffsetY() {
		return this.offsetY;
	}

	/**
	 * Returns the blur radius.
	 * @return double
	 */
	public double getRadius() {
		return this.radius;
	}

	/**
	 * Returns the spread of the shadow.
	 * @return double
	 */
	public double getSpread() {
		return this.spread;
	}
}
