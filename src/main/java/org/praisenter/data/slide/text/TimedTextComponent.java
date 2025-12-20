/*
 * Copyright (c) 2015-2025 William Bittle  http://www.praisenter.org/
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

import java.time.Duration;
import java.time.LocalDateTime;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A component to show the current date and time based on a given format.
 * @author William Bittle
 * @version 3.1.8
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME, 
	include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({ 
	@Type(value = CountdownComponent.class, name = "countdownComponent"),
	@Type(value = DateTimeComponent.class, name = "dateTimeComponent")
})
public abstract class TimedTextComponent extends TextComponent implements ReadOnlyTimedTextComponent, ReadOnlyTextComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	protected final ObjectProperty<LocalDateTime> now;
	protected final ObjectProperty<LocalDateTime> startTime;
	protected final ObjectProperty<Duration> accumulatedTime;
	
	public TimedTextComponent() {
		this.now = new SimpleObjectProperty<>(LocalDateTime.now());
		this.startTime = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.now());
		this.accumulatedTime = new SimpleObjectProperty<>(Duration.ZERO);
		
		this.accumulatedTime.bind(Bindings.createObjectBinding(() -> {
			return Duration.between(this.startTime.get(), this.now.get());
		}, this.now, this.startTime));
	}
	
	// will only be bound/set when in use
	
	@Override
	public LocalDateTime getNow() {
		return this.now.get();
	}
	
	public void setNow(LocalDateTime datetime) {
		this.now.set(datetime);
	}

	@Override
	public ObjectProperty<LocalDateTime> nowProperty() {
		return this.now;
	}
	
	@Override
	public LocalDateTime getStartTime() {
		return this.startTime.get();
	}
	
	public void setStartTime(LocalDateTime startTime) {
		this.startTime.set(startTime);
	}
	
	@Override
	public ObjectProperty<LocalDateTime> startTimeProperty() {
		return this.startTime;
	}
	
	@Override
	public Duration getAccumulatedTime() {
		return this.accumulatedTime.get();
	}
	
	@Override
	public ReadOnlyObjectProperty<Duration> accumulatedTimeProperty() {
		return this.accumulatedTime;
	}
}
