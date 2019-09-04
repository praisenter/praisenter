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

import org.praisenter.data.Copyable;
import org.praisenter.slide.easing.Easing;
import org.praisenter.slide.easing.Linear;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents an animation that can be applied to slides and components.
 * @author William Bittle
 * @version 3.0.0
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY)
//@JsonSubTypes({
//	@Type(value = Blinds.class, name = "blinds"),
//	@Type(value = Fade.class, name = "fade"),
//	@Type(value = Push.class, name = "push"),
//	@Type(value = Shaped.class, name = "shaped"),
//	@Type(value = Split.class, name = "split"),
//	@Type(value = Swap.class, name = "swap"),
//	@Type(value = Swipe.class, name = "swipe"),
//	@Type(value = Zoom.class, name = "zoom")
//})
public abstract class Animation implements ReadOnlyAnimation, Copyable {
	/** Value for a constantly repeating animation */
	public static final int INFINITE = -1;
	
	// defaults

	/** The default animation type */
	public static final AnimationType DEFAULT_ANIMATION_TYPE = AnimationType.IN;
	
	/** The default duration */
	public static final long DEFAULT_DURATION = 300;
	
	/** The default delay */
	public static final long DEFAULT_DELAY = 0;
	
	/** The default number of times to repeat */
	public static final int DEFAULT_REPEAT_COUNT = 1;
	
	/** The default value for auto-reverse */
	public static final boolean DEFAULT_AUTO_REVERSE = false;
	
	/** The default easing */
	public static final Easing DEFAULT_EASING = new Linear();
	
	// members
	
	private final ObjectProperty<AnimationType> animationType;
	private final LongProperty duration;
	private final LongProperty delay;
	private final IntegerProperty repeatCount;
	private final BooleanProperty autoReverseEnabled;
	private final ObjectProperty<Easing> easing;
	
	public Animation() {
		this.animationType = new SimpleObjectProperty<>(DEFAULT_ANIMATION_TYPE);
		this.duration = new SimpleLongProperty(DEFAULT_DURATION);
		this.delay = new SimpleLongProperty(DEFAULT_DELAY);
		this.repeatCount = new SimpleIntegerProperty(DEFAULT_REPEAT_COUNT);
		this.autoReverseEnabled = new SimpleBooleanProperty(DEFAULT_AUTO_REVERSE);
		this.easing = new SimpleObjectProperty<>(DEFAULT_EASING);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName())
		  .append("[")
		  .append(this.animationType.get()).append(", ")
		  .append(this.duration.get()).append(", ")
		  .append(this.delay.get()).append(", ")
		  .append(this.repeatCount.get()).append(", ")
		  .append(this.autoReverseEnabled.get()).append(", ")
		  .append(this.easing.get())
		  .append("]");
		return sb.toString();
	}
	
	public abstract Animation copy();
	
	protected void copyTo(Animation other) {
		other.animationType.set(this.animationType.get());
		other.autoReverseEnabled.set(this.autoReverseEnabled.get());
		other.delay.set(this.delay.get());
		other.duration.set(this.duration.get());
		other.easing.set(this.easing.get().copy());
		other.repeatCount.set(this.repeatCount.get());
	}

	@Override
	@JsonProperty
	public AnimationType getAnimationType() {
		return this.animationType.get();
	}
	
	@JsonProperty
	public void setAnimationType(AnimationType type) {
		this.animationType.set(type);
	}
	
	@Override
	public ObjectProperty<AnimationType> animationTypeProperty() {
		return this.animationType;
	}

	@Override
	@JsonProperty
	public long getDuration() {
		return this.duration.get();
	}
	
	@JsonProperty
	public void setDuration(long duration) {
		this.duration.set(duration);
	}
	
	@Override
	public LongProperty durationProperty() {
		return this.duration;
	}
	
	@Override
	@JsonProperty
	public long getDelay() {
		return this.delay.get();
	}
	
	@JsonProperty
	public void setDelay(long delay) {
		this.delay.set(delay);
	}
	
	@Override
	public LongProperty delayProperty() {
		return this.delay;
	}
	
	@Override
	@JsonProperty
	public int getRepeatCount() {
		return this.repeatCount.get();
	}
	
	@JsonProperty
	public void setRepeatCount(int count) {
		this.repeatCount.set(count);
	}
	
	@Override
	public IntegerProperty repeatCountProperty() {
		return this.repeatCount;
	}
	
	@Override
	@JsonProperty
	public boolean isAutoReverseEnabled() {
		return this.autoReverseEnabled.get();
	}
	
	@JsonProperty
	public void setAutoReverseEnabled(boolean enabled) {
		this.autoReverseEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty autoReverseEnabledProperty() {
		return this.autoReverseEnabled;
	}
	
	@Override
	@JsonProperty
	public Easing getEasing() {
		return this.easing.get();
	}
	
	@JsonProperty
	public void setEasing(Easing easing) {
		this.easing.set(easing);
	}
	
	@Override
	public ObjectProperty<Easing> easingProperty() {
		return this.easing;
	}
	
	public long getTotalTime() {
		return Math.max(0, this.delay.get()) + Math.max(0, this.duration.get()) * Math.max(1, this.repeatCount.get()) * (this.autoReverseEnabled.get() ? 2 : 1);
	}
}
