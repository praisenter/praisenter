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
package org.praisenter.javafx.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a resolution.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "resolution")
@XmlAccessorType(XmlAccessType.NONE)
public final class Resolution implements Comparable<Resolution> {
	/** The width in pixels */
	@JsonProperty
	@XmlAttribute(name = "width")
	private final int width;
	
	/** The height in pixels */
	@JsonProperty
	@XmlAttribute(name = "height")
	private final int height;

	/** The list of common screen resolutions */
	public static final Resolution[] DEFAULT_RESOLUTIONS = new Resolution[] {
		new Resolution(800, 600),
		new Resolution(1024, 768),
		new Resolution(1280, 720),
		new Resolution(1280, 800),
		new Resolution(1280, 1024),
		new Resolution(1400, 1050),
		new Resolution(1600, 1200),
		new Resolution(1920, 1080),
		new Resolution(1920, 1200)
	};
	
	/**
	 * For JAXB.
	 */
	Resolution() {
		// for jaxb
		this(0, 0);
	}
	
	/**
	 * Creates a new resolution with the given height and width.
	 * @param width the width
	 * @param height the height
	 */
	public Resolution(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Resolution) {
			Resolution r = (Resolution)obj;
			if (r.width == this.width && r.height == this.height) {
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
		int hash = 31;
		hash = hash * 39 + this.width;
		hash = hash * 39 + this.height;
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.width)
		  .append("x")
		  .append(this.height);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Resolution o) {
		if (o == null) return 1;
		// order by width first
		int diff = this.width - o.width;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			// order by height next
			diff = this.height - o.height;
			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Returns the width of the resolution in pixels.
	 * @return int
	 */
	public int getWidth() {
		return this.width;
	}
	
	/**
	 * Returns the height of the resolution in pixels.
	 * @return int
	 */
	public int getHeight() {
		return this.height;
	}
}
