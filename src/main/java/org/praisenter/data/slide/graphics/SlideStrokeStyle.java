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

import java.util.List;
import java.util.Objects;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// FEATURE (L-L) add a miter limit (right now we just use the width)
// FEATURE (L-L) add a stroke dash offset (this may only be needed if we support multiple borders)

/**
 * Represents the style of a stroke.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideStrokeStyle implements ReadOnlySlideStrokeStyle, Copyable {
	private final ObjectProperty<SlideStrokeType> type;
	private final ObjectProperty<SlideStrokeJoin> join;
	private final ObjectProperty<SlideStrokeCap> cap;
	private final ObservableList<Double> dashes;
	private final ObservableList<Double> dashesReadOnly;
	
	public SlideStrokeStyle() {
		this.type = new SimpleObjectProperty<>(SlideStrokeType.CENTERED);
		this.join = new SimpleObjectProperty<>(SlideStrokeJoin.MITER);
		this.cap = new SimpleObjectProperty<>(SlideStrokeCap.SQUARE);
		this.dashes = FXCollections.observableArrayList();
		this.dashesReadOnly = FXCollections.unmodifiableObservableList(this.dashes);
	}
	
	public SlideStrokeStyle(SlideStrokeType type, SlideStrokeJoin join, SlideStrokeCap cap, List<Double> dashes) {
		this();
		this.type.set(type);
		this.join.set(join);
		this.cap.set(cap);
		this.dashes.setAll(dashes);
	}
	
	@Override
	public SlideStrokeStyle copy() {
		return new SlideStrokeStyle(
				this.type.get(),
				this.join.get(),
				this.cap.get(),
				this.dashes);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("STYLE")
		  .append("[")
		  .append(this.type.get()).append(", ")
		  .append(this.join.get()).append(", ")
		  .append(this.cap.get()).append(", ")
		  .append(this.dashes)
		  .append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		List<Double> dashes = this.dashes;
		
		int hash = 37;
		hash = hash * 31 + Objects.hash(
				this.type.get(),
				this.join.get(),
				this.cap.get());
		if (dashes != null) {
			for (Double d : dashes) {
				if (d != null) {
					long v = d.hashCode();
					hash = 31 * hash + (int)(v ^ (v >>> 32));
				}
			}
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
			if (this.type.get() != s.type.get() ||
				this.join.get() != s.join.get() ||
				this.cap.get() != s.cap.get()) {
				return false;
			}
			List<Double> td = this.dashes;
			List<Double> sd = s.dashes;
			if (td == sd) return true;
			if (td != null && sd == null) return false;
			if (td == null && sd != null) return false;
			if (td.size() != sd.size()) {
				return false;
			}
			for (int i = 0; i < td.size(); i++) {
				if (td.get(i) != sd.get(i)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public SlideStrokeType getType() {
		return this.type.get();
	}
	
	@JsonProperty
	public void setType(SlideStrokeType type) {
		this.type.set(type);
	}
	
	@Override
	public ObjectProperty<SlideStrokeType> typeProperty() {
		return this.type;
	}
	
	@Override
	@JsonProperty
	public SlideStrokeJoin getJoin() {
		return this.join.get();
	}
	
	@JsonProperty
	public void setJoin(SlideStrokeJoin join) {
		this.join.set(join);
	}
	
	@Override
	public ObjectProperty<SlideStrokeJoin> joinProperty() {
		return this.join;
	}
	
	@Override
	@JsonProperty
	public SlideStrokeCap getCap() {
		return this.cap.get();
	}
	
	@JsonProperty
	public void setCap(SlideStrokeCap cap) {
		this.cap.set(cap);
	}
	
	@Override
	public ObjectProperty<SlideStrokeCap> capProperty() {
		return this.cap;
	}
	
	@Override
	@JsonProperty
	public ObservableList<Double> getDashes() {
		return this.dashes;
	}
	
	public ObservableList<Double> getDashesUnmodifiable() {
		return this.dashesReadOnly;
	}
	
	@JsonProperty
	public void setDashes(List<Double> dashes) {
		this.dashes.setAll(dashes);
	}
}
