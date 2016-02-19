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
package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the style of a stroke.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "strokeStyle")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideStrokeStyle {
	/** The stroke type */
	@XmlAttribute(name = "type", required = false)
	final SlideStrokeType type;
	
	/** The stroke join */
	@XmlAttribute(name = "join", required = false)
	final SlideStrokeJoin join;
	
	/** The stroke cap */
	@XmlAttribute(name = "cap", required = false)
	final SlideStrokeCap cap;
	
	/** The dash array */
	@XmlElement(name = "length", required = false)
	@XmlElementWrapper(name = "dashes", required = false)
	final Double[] dashes;
	
	/**
	 * Constructor for JAXB.
	 */
	@SuppressWarnings("unused")
	private SlideStrokeStyle() {
		// for jaxb
		this.type = SlideStrokeType.CENTERED;
		this.join = SlideStrokeJoin.MITER;
		this.cap = SlideStrokeCap.SQUARE;
		this.dashes = new Double[0];
	}
	
	/**
	 * Creates a new stroke style.
	 * @param type the stroke type
	 * @param join the stroke join
	 * @param cap the stroke cap
	 * @param dashes the stroke dashes
	 */
	public SlideStrokeStyle(SlideStrokeType type, SlideStrokeJoin join, SlideStrokeCap cap, Double... dashes) {
		this.type = type;
		this.join = join;
		this.cap = cap;
		if (dashes == null) {
			dashes = new Double[0];
		}
		this.dashes = dashes;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		hash = 31 * hash + this.type.hashCode();
		hash = 31 * hash + this.join.hashCode();
		hash = 31 * hash + this.cap.hashCode();
		for (int i = 0; i < this.dashes.length; i++) {
			long v = this.dashes[i].hashCode();
			hash = 31 * hash + (int)(v ^ (v >>> 32));
		}
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideStrokeStyle) {
			SlideStrokeStyle s = (SlideStrokeStyle)obj;
			if (this.type != s.type ||
				this.join != s.join ||
				this.cap != s.cap) {
				return false;
			}
			if (this.dashes.length != s.dashes.length) {
				return false;
			}
			for (int i = 0; i < this.dashes.length; i++) {
				if (this.dashes[i] == s.dashes[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns the stroke type.
	 * @return {@link SlideStrokeType}
	 */
	public SlideStrokeType getType() {
		return this.type;
	}

	/**
	 * Returns the stroke join.
	 * @return {@link SlideStrokeJoin}
	 */
	public SlideStrokeJoin getJoin() {
		return this.join;
	}

	/**
	 * Returns the stroke cap.
	 * @return {@link SlideStrokeCap}
	 */
	public SlideStrokeCap getCap() {
		return this.cap;
	}

	/**
	 * Returns the stroke dashes.
	 * <p>
	 * The array should not be modified.
	 * @return Double[]
	 */
	public Double[] getDashes() {
		return this.dashes;
	}
}
