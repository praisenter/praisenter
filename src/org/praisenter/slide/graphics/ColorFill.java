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
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a fill using a solid color.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "ColorFill")
@XmlAccessorType(XmlAccessType.NONE)
public class ColorFill extends AbstractFill implements Fill, Serializable {
	/** The version id */
	private static final long serialVersionUID = 5297424881541309870L;

	/** The red component */
	@XmlAttribute(name = "Red")
	protected int red;
	
	/** The green component */
	@XmlAttribute(name = "Green")
	protected int green;
	
	/** The blue component */
	@XmlAttribute(name = "Blue")
	protected int blue;
	
	/** The alpha component */
	@XmlAttribute(name = "Alpha")
	protected int alpha;
	
	/**
	 * Default constructor.
	 */
	public ColorFill() {
		this(Color.BLACK);
	}
	
	/**
	 * Uses the given color for this {@link ColorFill}.
	 * @param color the color
	 */
	public ColorFill(Color color) {
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.alpha = color.getAlpha();
	}

	/**
	 * Full constructor.
	 * @param red the red component from 0-1
	 * @param green the green component from 0-1
	 * @param blue the blue component from 0-1
	 * @param alpha the alpha component from 0-1
	 */
	public ColorFill(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	/**
	 * Returns the color for this {@link ColorFill}.
	 * @return Color
	 */
	public Color getColor() {
		return new Color(this.red, this.green, this.blue, this.alpha);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Fill#getPaint(int, int, int, int)
	 */
	@Override
	public Color getPaint(int x, int y, int w, int h) {
		// this fill doesn't need the params
		return new Color(this.red, this.green, this.blue, this.alpha);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof ColorFill) {
			ColorFill f = (ColorFill)obj;
			if (f.red == this.red
			 && f.green == this.green
			 && f.blue == this.blue
			 && f.alpha == this.alpha) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int r = 37;
		r = 37 * r + this.red;
		r = 37 * r + this.green;
		r = 37 * r + this.blue;
		r = 37 * r + this.alpha;
		return r;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ColorFill[Red=").append(this.red)
		  .append("|Green=").append(this.green)
		  .append("|Blue=").append(this.blue)
		  .append("|Alpha=").append(this.alpha)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the red component.
	 * @return float
	 */
	public float getRed() {
		return this.red;
	}

	/**
	 * Returns the green component.
	 * @return float
	 */
	public float getGreen() {
		return this.green;
	}

	/**
	 * Returns the blue component.
	 * @return float
	 */
	public float getBlue() {
		return this.blue;
	}

	/**
	 * Returns the alpha component.
	 * @return float
	 */
	public float getAlpha() {
		return this.alpha;
	}
}
