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
package org.praisenter.data.slide.text;

import org.praisenter.data.Copyable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a font.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideFont implements ReadOnlySlideFont, Copyable {
	private final StringProperty family;
	private final ObjectProperty<SlideFontPosture> posture;
	private final ObjectProperty<SlideFontWeight> weight;
	private final DoubleProperty size;
	
	public SlideFont() {
		this.family = new SimpleStringProperty("SansSerif");
		this.weight = new SimpleObjectProperty<>(SlideFontWeight.NORMAL);
		this.posture = new SimpleObjectProperty<>(SlideFontPosture.REGULAR);
		this.size = new SimpleDoubleProperty(10);
	}
	
	@Override
	public SlideFont copy() {
		SlideFont font = new SlideFont();
		font.family.set(this.family.get());
		font.posture.set(this.posture.get());
		font.weight.set(this.weight.get());
		font.size.set(this.size.get());
		return font;
	}
	
	@Override
	@JsonProperty
	public String getFamily() {
		return this.family.get();
	}
	
	@JsonProperty
	public void setFamily(String family) {
		this.family.set(family);
	}
	
	@Override
	public StringProperty familyProperty() {
		return this.family;
	}
	
	@Override
	@JsonProperty
	public SlideFontPosture getPosture() {
		return this.posture.get();
	}
	
	@JsonProperty
	public void setPosture(SlideFontPosture posture) {
		this.posture.set(posture);
	}
	
	@Override
	public ObjectProperty<SlideFontPosture> postureProperty() {
		return this.posture;
	}
	
	@Override
	@JsonProperty
	public SlideFontWeight getWeight() {
		return this.weight.get();
	}
	
	@JsonProperty
	public void setWeight(SlideFontWeight weight) {
		this.weight.set(weight);
	}
	
	@Override
	public ObjectProperty<SlideFontWeight> weightProperty() {
		return this.weight;
	}
	
	@Override
	@JsonProperty
	public double getSize() {
		return this.size.get();
	}
	
	@JsonProperty
	public void setSize(double size) {
		this.size.set(size);
	}
	
	@Override
	public DoubleProperty sizeProperty() {
		return this.size;
	}
}
