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
package org.praisenter.data.configuration;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents a resolution.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Resolution implements ReadonlyResolution, Copyable, Comparable<Resolution> {
	private final IntegerProperty width;
	private final IntegerProperty height;

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
	
	public Resolution() {
		this.width = new SimpleIntegerProperty();
		this.height = new SimpleIntegerProperty();
	}
	
	public Resolution(int width, int height) {
		this();
		this.width.set(width);
		this.height.set(height);
	}
	
	@Override
	public Resolution copy() {
		Resolution r = new Resolution(this.width.get(), this.height.get());
		return r;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Resolution) {
			Resolution r = (Resolution)obj;
			if (r.width.get() == this.width.get() && 
				r.height.get() == this.height.get()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 31;
		hash = hash * 39 + this.width.get();
		hash = hash * 39 + this.height.get();
		return hash;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.width.get())
		  .append("x")
		  .append(this.height.get());
		return sb.toString();
	}
	
	@Override
	public int compareTo(Resolution o) {
		if (o == null) return 1;
		// order by width first
		int diff = this.width.get() - o.width.get();
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			// order by height next
			diff = this.height.get() - o.height.get();
			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	@Override
	@JsonProperty
	public int getWidth() {
		return this.width.get();
	}
	
	@JsonProperty
	public void setWidth(int width) {
		this.width.set(width);
	}
	
	@Override
	public IntegerProperty widthProperty() {
		return this.width;
	}
	
	@Override
	@JsonProperty
	public int getHeight() {
		return this.height.get();
	}
	
	@JsonProperty
	public void setHeight(int height) {
		this.height.set(height);
	}
	
	@Override
	public IntegerProperty heightProperty() {
		return this.height;
	}
}
