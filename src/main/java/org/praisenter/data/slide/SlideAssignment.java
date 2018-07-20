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

import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents an assignment of a slide to a slide show.
 * @author William Bittle
 * @version 3.0.0
 */
public final class SlideAssignment implements ReadonlySlideAssignment, Copyable, Identifiable {
	private final ObjectProperty<UUID> id;
	private final ObjectProperty<UUID> slideId;
	
	public SlideAssignment() {
		this.id = new SimpleObjectProperty<>(UUID.randomUUID());
		this.slideId = new SimpleObjectProperty<>();
	}
	
	@Override
	public SlideAssignment copy() {
		SlideAssignment sa = new SlideAssignment();
		sa.id.set(this.id.get());
		sa.slideId.set(this.slideId.get());
		return sa;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.identityEquals(obj);
	}
	
	@Override
	public int hashCode() {
		return this.id.get().hashCode();
	}
	
	@Override
	public boolean identityEquals(Object other) {
		if (other == null) return false;
		if (other == this) return true;
		if (other instanceof SlideAssignment) {
			return this.id.get().equals(((SlideAssignment)other).id.get());
		}
		return false;
	}
	
	@Override
	@JsonProperty
	public UUID getId() {
		return this.id.get();
	}
	
	@JsonProperty
	public void setId(UUID id) {
		this.id.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> idProperty() {
		return this.id;
	}
	
	@Override
	@JsonProperty
	public UUID getSlideId() {
		return this.slideId.get();
	}
	
	@JsonProperty
	public void setSlideId(UUID id) {
		this.slideId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> slideIdProperty() {
		return this.slideId;
	}
}