/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.slide.graphics;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a stop in a gradient fill operation.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "Stop")
@XmlAccessorType(XmlAccessType.NONE)
public class Stop {
	/** The stop location from 0.0 - 1.0 inclusive */
	@XmlElement(name = "Fraction")
	protected float fraction;
	
	/** The stop color */
	@XmlElement(name = "Color")
	protected ColorFill color;
	
	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected Stop() {
		this(0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	/**
	 * Full constructor.
	 * @param fraction the stop location from 0 to 1 inclusive
	 * @param color the stop color
	 */
	public Stop(float fraction, Color color) {
		this.fraction = fraction;
		this.color = new ColorFill(color);
	}
	
	/**
	 * Full constructor.
	 * @param fraction the stop location from 0 to 1 inclusive
	 * @param red the red color component
	 * @param green the green color component
	 * @param blue the blue color component
	 * @param alpha the alpha color component
	 */
	public Stop(float fraction, float red, float green, float blue, float alpha) {
		this.fraction = fraction;
		this.color = new ColorFill(red, green, blue, alpha);
	}
	
	/**
	 * Full constructor.
	 * @param fraction the stop location from 0 to 1 inclusive
	 * @param red the red color component
	 * @param green the green color component
	 * @param blue the blue color component
	 * @param alpha the alpha color component
	 */
	public Stop(float fraction, int red, int green, int blue, int alpha) {
		this(fraction, new Color(red, green, blue, alpha));
	}

	/**
	 * Returns the stop position.
	 * @return float
	 */
	public float getFraction() {
		return this.fraction;
	}

	/**
	 * Returns the stop color.
	 * @return Color
	 */
	public Color getColor() {
		return this.color.getColor();
	}
}
