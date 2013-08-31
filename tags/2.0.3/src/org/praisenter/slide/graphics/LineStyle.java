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

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a line style.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "LineStyle")
@XmlAccessorType(XmlAccessType.NONE)
public class LineStyle implements Serializable {
	/** The version id */
	private static final long serialVersionUID = 8619829633766048595L;

	/** The line width */
	@XmlElement(name = "Width")
	protected float width;
	
	/** The cap type */
	@XmlElement(name = "Cap")
	protected CapType cap;
	
	/** The join type */
	@XmlElement(name = "Join")
	protected JoinType join;
	
	/** The pattern */
	@XmlElement(name = "Pattern")
	protected DashPattern pattern;

	/**
	 * Default constructor.
	 */
	public LineStyle() {
		this(5.0f, CapType.SQUARE, JoinType.BEVEL, DashPattern.SOLID);
	}
	
	/**
	 * Full constructor.
	 * @param width the line width
	 * @param cap the cap type
	 * @param join the join type
	 * @param pattern the pattern
	 */
	public LineStyle(float width, CapType cap, JoinType join, DashPattern pattern) {
		this.width = width;
		this.cap = cap;
		this.join = join;
		this.pattern = pattern;
	}
	
	/**
	 * Returns the a new stroke for this {@link LineStyle}.
	 * @return Stroke
	 */
	public Stroke getStroke() {
		return new BasicStroke(
				this.width,
				this.cap.getStrokeValue(),
				this.join.getStrokeValue(),
				this.width,
				this.pattern.getDashLengths(this.width),
				0.0f);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof LineStyle) {
			LineStyle s = (LineStyle)obj;
			if (s.width == this.width
			 && s.cap == this.cap
			 && s.join == this.join
			 && s.pattern == this.pattern) {
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
		r = 37 * r + Float.floatToIntBits(this.width);
		r = 37 * r + this.cap.hashCode();
		r = 37 * r + this.join.hashCode();
		r = 37 * r + this.pattern.hashCode();
		return r;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LineStyle[Width=").append(this.width)
		  .append("|Cap=").append(this.cap)
		  .append("|Join=").append(this.join)
		  .append("|DashPattern=").append(this.pattern)
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Returns the line width.
	 * @return float
	 */
	public float getWidth() {
		return this.width;
	}

	/**
	 * Returns the cap type.
	 * @return {@link CapType}
	 */
	public CapType getCap() {
		return this.cap;
	}

	/**
	 * Returns the join type.
	 * @return {@link JoinType}
	 */
	public JoinType getJoin() {
		return this.join;
	}

	/**
	 * Returns the pattern.
	 * @return {@link DashPattern}
	 */
	public DashPattern getPattern() {
		return this.pattern;
	}
}
