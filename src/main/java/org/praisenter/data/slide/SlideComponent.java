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
package org.praisenter.data.slide;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.effects.SlideShadow;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Abstract implementation of the {@link SlideComponent} interface.
 * @author William Bittle
 * @version 3.0.0
 */
public abstract class SlideComponent extends SlideRegion implements ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	private final ObjectProperty<SlideShadow> shadow;
	private final ObjectProperty<SlideShadow> glow;
	
	public SlideComponent() {
		super();
		this.shadow = new SimpleObjectProperty<>();
		this.glow = new SimpleObjectProperty<>();
	}
	
	public abstract SlideComponent copy();
	
	protected void copyTo(SlideComponent component) {
		this.copyTo(component);
		component.glow.set(this.glow.get().copy());
		component.shadow.set(this.shadow.get().copy());
	}
	
	@Override
	@JsonProperty
	public SlideShadow getShadow() {
		return this.shadow.get();
	}
	
	@JsonProperty
	public void setShadow(SlideShadow shadow) {
		this.shadow.set(shadow);
	}
	
	@Override
	public ObjectProperty<SlideShadow> shadowProperty() {
		return this.shadow;
	}
	
	@Override
	@JsonProperty
	public SlideShadow getGlow() {
		return this.glow.get();
	}
	
	@JsonProperty
	public void setGlow(SlideShadow glow) {
		this.glow.set(glow);
	}
	
	@Override
	public ObjectProperty<SlideShadow> glowProperty() {
		return this.glow;
	}
}
