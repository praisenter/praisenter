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
package org.praisenter.data.slide.animation;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a blinds animation.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Blinds extends Animation {
	/** The default orientation */
	public static final Orientation DEFAULT_ORIENTATION = Orientation.VERTICAL;
	
	/** The default blind count */
	public static final int DEFAULT_BLIND_COUNT = 12;
	
	private final ObjectProperty<Orientation> orientation;
	private final IntegerProperty blindCount;
	
	public Blinds() {
		this.orientation = new SimpleObjectProperty<>(DEFAULT_ORIENTATION);
		this.blindCount = new SimpleIntegerProperty(DEFAULT_BLIND_COUNT);
	}
	
	@Override
	public Blinds copy() {
		Blinds ani = new Blinds();
		this.copyTo(ani);
		ani.blindCount.set(this.blindCount.get());
		ani.orientation.set(this.orientation.get());
		return ani;
	}
	
	@JsonProperty
	public Orientation getOrientation() {
		return this.orientation.get();
	}
	
	@JsonProperty
	public void setOrientation(Orientation orientation) {
		this.orientation.set(orientation);
	}
	
	public ObjectProperty<Orientation> orientationProperty() {
		return this.orientation;
	}

	@JsonProperty
	public int getBlindCount() {
		return this.blindCount.get();
	}
	
	@JsonProperty
	public void setBlindCount(int count) {
		this.blindCount.set(count);
	}
	
	public IntegerProperty blindCountProperty() {
		return this.blindCount;
	}
}
