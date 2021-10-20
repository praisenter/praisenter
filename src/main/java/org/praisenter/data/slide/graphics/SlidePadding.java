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
package org.praisenter.data.slide.graphics;

import java.util.Objects;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Represents a rectangular padding.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class SlidePadding implements ReadOnlySlidePadding, Copyable {
	private final DoubleProperty top;
	private final DoubleProperty right;
	private final DoubleProperty bottom;
	private final DoubleProperty left;
	
	public SlidePadding() {
		this.top = new SimpleDoubleProperty(0);
		this.right = new SimpleDoubleProperty(0);
		this.bottom = new SimpleDoubleProperty(0);
		this.left = new SimpleDoubleProperty(0);
	}
	
	public SlidePadding(double padding) {
		this(padding, padding, padding, padding);
	}
	
	public SlidePadding(double top, double right, double bottom, double left) {
		this();
		this.top.set(top);
		this.right.set(right);
		this.bottom.set(bottom);
		this.left.set(left);
	}

	@Override
	public SlidePadding copy() {
		return new SlidePadding(
				this.top.get(),
				this.right.get(),
				this.bottom.get(),
				this.left.get());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PADDING")
		  .append("[")
		  .append(this.top.get()).append(", ")
		  .append(this.right.get()).append(", ")
		  .append(this.bottom.get()).append(", ")
		  .append(this.left.get())
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				this.top.get(),
				this.right.get(),
				this.bottom.get(),
				this.left.get());
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
			if (p.top.get() == this.top.get() &&
				p.right.get() == this.right.get() &&
				p.bottom.get() == this.bottom.get() &&
				p.left.get() == this.left.get()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public double getTop() {
		return this.top.get();
	}
	
	@JsonProperty
	public void setTop(double top) {
		this.top.set(top);
	}
	
	@Override
	public DoubleProperty topProperty() {
		return this.top;
	}
	
	@Override
	@JsonProperty
	public double getRight() {
		return this.right.get();
	}
	
	@JsonProperty
	public void setRight(double right) {
		this.right.set(right);
	}
	
	@Override
	public DoubleProperty rightProperty() {
		return this.right;
	}
	
	@Override
	@JsonProperty
	public double getBottom() {
		return this.bottom.get();
	}
	
	@JsonProperty
	public void setBottom(double bottom) {
		this.bottom.set(bottom);
	}
	
	@Override
	public DoubleProperty bottomProperty() {
		return this.bottom;
	}
	
	@Override
	@JsonProperty
	public double getLeft() {
		return this.left.get();
	}
	
	@JsonProperty
	public void setLeft(double left) {
		this.left.set(left);
	}
	
	@Override
	public DoubleProperty leftProperty() {
		return this.left;
	}
}
