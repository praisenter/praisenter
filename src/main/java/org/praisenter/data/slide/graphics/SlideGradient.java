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

/**
 * Base class for gradient paints.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class SlideGradient implements ReadOnlySlideGradient, SlidePaint, Copyable {
	protected final ObjectProperty<SlideGradientCycleType> cycleType;
	protected final ObservableList<SlideGradientStop> stops;
	
	public SlideGradient() {
		this.cycleType = new SimpleObjectProperty<>(SlideGradientCycleType.NONE);
		this.stops = FXCollections.observableArrayList(
				new SlideGradientStop(0, 0, 0, 0, 1),
				new SlideGradientStop(1, 1, 1, 1, 1));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		SlideGradientCycleType cycleType = this.cycleType.get();
		
		int hash = 37;
		if (cycleType != null) hash = hash * 31 + cycleType.hashCode();
		for (int i = 0; i < this.stops.size(); i++) {
			SlideGradientStop stop = stops.get(i);
			hash = 31 * hash + stop.hashCode();
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
		if (obj instanceof SlideGradient) {
			SlideGradient g = (SlideGradient)obj;
			if (!Objects.equals(g.cycleType.get(), this.cycleType.get())) {
				return false;
			}
			if (g.stops.size() != this.stops.size()) {
				return false;
			}
			for (int i = 0; i < this.stops.size(); i++) {
				if (!g.stops.get(i).equals(this.stops.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public ObservableList<SlideGradientStop> getStopsUnmodifiable() {
		return FXCollections.unmodifiableObservableList(this.stops);
	}
	
	public ObservableList<SlideGradientStop> getStops() {
		return this.stops;
	}
	
	@JsonProperty
	public void setStops(List<SlideGradientStop> stops) {
		this.stops.setAll(stops);
	}
	
	@Override
	@JsonProperty
	public SlideGradientCycleType getCycleType() {
		return this.cycleType.get();
	}
	
	@JsonProperty
	public void setCycleType(SlideGradientCycleType cycleType) {
		this.cycleType.set(cycleType);
	}
	
	public ObjectProperty<SlideGradientCycleType> cycleTypeProperty() {
		return this.cycleType;
	}
}
