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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Base class for gradient paints.
 * @author William Bittle
 * @version 3.0.0
 */
public class SlideGradient implements ReadOnlySlideGradient, SlidePaint, Copyable {
	private final ObjectProperty<SlideGradientType> type;
	private final DoubleProperty startX;
	private final DoubleProperty startY;
	private final DoubleProperty endX;
	private final DoubleProperty endY;
	private final ObjectProperty<SlideGradientCycleType> cycleType;
	private final ObservableList<SlideGradientStop> stops;
	private final ObservableList<SlideGradientStop> stopsReadOnly;
	
	public SlideGradient() {
		this.type = new SimpleObjectProperty<>(SlideGradientType.LINEAR);
		this.startX = new SimpleDoubleProperty(0);
		this.startY = new SimpleDoubleProperty(0);
		this.endX = new SimpleDoubleProperty(1);
		this.endY = new SimpleDoubleProperty(1);
		this.cycleType = new SimpleObjectProperty<>(SlideGradientCycleType.NONE);
		this.stops = FXCollections.observableArrayList(
				new SlideGradientStop(0, 0, 0, 0, 1),
				new SlideGradientStop(1, 1, 1, 1, 1));
		this.stopsReadOnly = FXCollections.unmodifiableObservableList(this.stops);
	}

	@Override
	public SlideGradient copy() {
		SlideGradient gradient = new SlideGradient();
		gradient.type.set(this.type.get());
		gradient.startX.set(this.startX.get());
		gradient.startY.set(this.startY.get());
		gradient.endX.set(this.endX.get());
		gradient.endY.set(this.endY.get());
		gradient.cycleType.set(this.cycleType.get());
		gradient.stops.clear();
		for (SlideGradientStop stop : this.stops) {
			gradient.stops.add(stop.copy());
		}
		return gradient;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GRADIENT")
		  .append("[(")
		  .append(this.type.get()).append(", ")
		  .append(this.startX.get()).append(", ")
		  .append(this.startY.get()).append("), (")
		  .append(this.endX.get()).append(", ")
		  .append(this.endY.get()).append("), ")
		  .append(this.cycleType.get()).append(", [");
		for (SlideGradientStop stop : this.stops) {
			sb.append(stop).append(", ");
		}
		sb.append("]]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		SlideGradientCycleType cycleType = this.cycleType.get();
		SlideGradientType type = this.type.get();
		
		int hash = 37;
		if (type != null) hash = hash * 31 + type.hashCode();
		if (cycleType != null) hash = hash * 31 + cycleType.hashCode();
		for (int i = 0; i < this.stops.size(); i++) {
			SlideGradientStop stop = stops.get(i);
			hash = 31 * hash + stop.hashCode();
		}
		hash = hash * 31 + super.hashCode();
		hash = hash * 31 + Objects.hash(
				this.startX.get(),
				this.startY.get(),
				this.endX.get(),
				this.endY.get());
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
			if (!Objects.equals(g.type.get(), this.type.get())) {
				return false;
			}
			if (!Objects.equals(g.cycleType.get(), this.cycleType.get())) {
				return false;
			}
			if (this.startX.get() != g.startX.get() ||
				this.startY.get() != g.startY.get() ||
				this.endX.get() != g.endX.get() ||
				this.endY.get() != g.endY.get()) {
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
	public SlideGradientType getType() {
		return this.type.get();
	}
	
	@JsonProperty
	public void setType(SlideGradientType type) {
		this.type.set(type);
	}
	
	public ObjectProperty<SlideGradientType> typeProperty() {
		return this.type;
	}
	
	@Override
	@JsonProperty
	public double getStartX() {
		return this.startX.get();
	}
	
	@JsonProperty
	public void setStartX(double x) {
		this.startX.set(x);
	}
	
	@Override
	public DoubleProperty startXProperty() {
		return this.startX;
	}

	@Override
	@JsonProperty
	public double getStartY() {
		return this.startY.get();
	}
	
	@JsonProperty
	public void setStartY(double y) {
		this.startY.set(y);
	}
	
	@Override
	public DoubleProperty startYProperty() {
		return this.startY;
	}
	
	@Override
	@JsonProperty
	public double getEndX() {
		return this.endX.get();
	}
	
	@JsonProperty
	public void setEndX(double x) {
		this.endX.set(x);
	}
	
	@Override
	public DoubleProperty endXProperty() {
		return this.endX;
	}
	
	@Override
	@JsonProperty
	public double getEndY() {
		return this.endY.get();
	}
	
	@JsonProperty
	public void setEndY(double y) {
		this.endY.set(y);
	}
	
	@Override
	public DoubleProperty endYProperty() {
		return this.endY;
	}
	
	@Override
	public ObservableList<SlideGradientStop> getStopsUnmodifiable() {
		return this.stopsReadOnly;
	}
	
	@JsonProperty
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
