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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a rectangular padding.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class SlidePadding {
	/** The top padding */
	@JsonProperty
	final double top;
	
	/** The right padding */
	@JsonProperty
	final double right;
	
	/** The bottom padding */
	@JsonProperty
	final double bottom;
	
	/** The left padding */
	@JsonProperty
	final double left;
	
	/**
	 * Default constructor.
	 */
	public SlidePadding() {
		this.top = 0;
		this.right = 0;
		this.bottom = 0;
		this.left = 0;
	}
	
	/**
	 * Minimal constructor.
	 * @param padding the padding for all sides
	 */
	public SlidePadding(double padding) {
		this(padding, padding, padding, padding);
	}
	
	/**
	 * Full constructor.
	 * @param top the top padding
	 * @param right the right padding
	 * @param bottom the bottom padding
	 * @param left the left padding
	 */
	public SlidePadding(double top, double right, double bottom, double left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PADDING")
		  .append("[")
		  .append(this.top).append(", ")
		  .append(this.right).append(", ")
		  .append(this.bottom).append(", ")
		  .append(this.left)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		// see http://stackoverflow.com/a/31220250
		long v = Double.doubleToLongBits(this.top);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.right);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.bottom);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		v = Double.doubleToLongBits(this.left);
		hash = 31 * hash + (int)(v ^ (v >>> 32));
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlidePadding) {
			SlidePadding p = (SlidePadding)obj;
			if (p.top == this.top &&
				p.right == this.right &&
				p.bottom == this.bottom &&
				p.left == this.left) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the top padding.
	 * @return double
	 */
	public double getTop() {
		return this.top;
	}

	/**
	 * Returns the right padding.
	 * @return double
	 */
	public double getRight() {
		return this.right;
	}

	/**
	 * Returns the bottom padding.
	 * @return double
	 */
	public double getBottom() {
		return this.bottom;
	}

	/**
	 * Returns the left padding.
	 * @return double
	 */
	public double getLeft() {
		return this.left;
	}
}
