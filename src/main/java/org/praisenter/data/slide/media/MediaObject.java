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
package org.praisenter.data.slide.media;

import java.util.Objects;
import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.media.MediaType;
import org.praisenter.data.slide.graphics.ScaleType;
import org.praisenter.data.slide.graphics.SlidePaint;
import org.praisenter.data.slide.effects.SlideColorAdjust;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// FEATURE (L-L) Add a repeat property that will tile an image
// FEATURE (L-M) Evaluate supporting JavaFX http live streaming

/**
 * Represents a media object for audio, video, and images.
 * <p>
 * The usage of this class is limited to the following constraints (though not strictly enforced):
 * <ol>
 * <li>An audio paint is only compatible with a {@link MediaComponent} since it has no "visible" aspect.
 * <li>A video paint is only compatible with backgrounds and the {@link MediaComponent} (i.e. not borders, text, etc.).
 * <li>An image paint is compatible with everything backgrounds, borders, etc.
 * </ol>
 * In the case that {@link ScaleType#NONE} is chosen, the media will be positioned top-left.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaObject implements ReadOnlyMediaObject, SlidePaint, Copyable {
	private final ObjectProperty<UUID> mediaId;
	private final StringProperty mediaName;
	private final ObjectProperty<MediaType> mediaType;
	private final ObjectProperty<ScaleType> scaleType;
	private final BooleanProperty loopEnabled;
	private final BooleanProperty muted;
	private final ObjectProperty<SlideColorAdjust> colorAdjust;
	
	public MediaObject() {
		this.mediaId = new SimpleObjectProperty<>();
		this.mediaName = new SimpleStringProperty();
		this.mediaType = new SimpleObjectProperty<>();
		this.scaleType = new SimpleObjectProperty<>();
		this.loopEnabled = new SimpleBooleanProperty();
		this.muted = new SimpleBooleanProperty();
		this.colorAdjust = new SimpleObjectProperty<>();
	}
	
	@Override
	public MediaObject copy() {
		MediaObject mo = new MediaObject();
		mo.colorAdjust.set(this.colorAdjust.get());
		mo.loopEnabled.set(this.loopEnabled.get());
		mo.mediaId.set(this.mediaId.get());
		mo.mediaName.set(this.mediaName.get());
		mo.mediaType.set(this.mediaType.get());
		mo.muted.set(this.muted.get());
		mo.scaleType.set(this.scaleType.get());
		return mo;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(
				this.mediaId.get(),
				this.scaleType.get(),
				this.colorAdjust.get(),
				this.loopEnabled.get(),
				this.muted.get());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaObject) {
			MediaObject mo = (MediaObject)obj;
			return Objects.equals(this.mediaId.get(), mo.mediaId.get()) &&
					this.scaleType.get() == mo.scaleType.get() &&
					this.loopEnabled.get() == mo.loopEnabled.get() &&
					this.muted.get() == mo.muted.get() &&
					Objects.equals(this.colorAdjust.get(), mo.colorAdjust.get());
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MediaObject[")
			.append(this.mediaName.get()).append(", ")
			.append(this.mediaType.get()).append(", ")
			.append(this.scaleType.get()).append(", ")
			.append(this.muted.get()).append(", ")
			.append(this.loopEnabled.get())
		.append("]");
		return sb.toString();
	}
	
	@Override
	@JsonProperty
	public UUID getMediaId() {
		return this.mediaId.get();
	}
	
	@JsonProperty
	public void setMediaId(UUID id) {
		this.mediaId.set(id);
	}
	
	@Override
	public ObjectProperty<UUID> mediaIdProperty() {
		return this.mediaId;
	}
	
	@Override
	@JsonProperty
	public String getMediaName() {
		return this.mediaName.get();
	}
	
	@JsonProperty
	public void setMediaName(String name) {
		this.mediaName.set(name);
	}
	
	@Override
	public StringProperty mediaNameProperty() {
		return this.mediaName;
	}
	
	@Override
	@JsonProperty
	public MediaType getMediaType() {
		return this.mediaType.get();
	}
	
	@JsonProperty
	public void setMediaType(MediaType type) {
		this.mediaType.set(type);
	}
	
	@Override
	public ObjectProperty<MediaType> mediaTypeProperty() {
		return this.mediaType;
	}
	
	@Override
	@JsonProperty
	public ScaleType getScaleType() {
		return this.scaleType.get();
	}
	
	@JsonProperty
	public void setScaleType(ScaleType type) {
		this.scaleType.set(type);
	}
	
	@Override
	public ObjectProperty<ScaleType> scaleTypeProperty() {
		return this.scaleType;
	}
	
	@Override
	@JsonProperty
	public boolean isLoopEnabled() {
		return this.loopEnabled.get();
	}
	
	@JsonProperty
	public void setLoopEnabled(boolean enabled) {
		this.loopEnabled.set(enabled);
	}
	
	@Override
	public BooleanProperty loopEnabledProperty() {
		return this.loopEnabled;
	}
	
	@Override
	@JsonProperty
	public boolean isMuted() {
		return this.muted.get();
	}
	
	@JsonProperty
	public void setMuted(boolean muted) {
		this.muted.set(muted);
	}
	
	@Override
	public BooleanProperty mutedProperty() {
		return this.muted;
	}
	
	@Override
	@JsonProperty
	public SlideColorAdjust getColorAdjust() {
		return this.colorAdjust.get();
	}
	
	@JsonProperty
	public void setColorAdjust(SlideColorAdjust colorAdjust) {
		this.colorAdjust.set(colorAdjust);
	}
	
	@Override
	public ObjectProperty<SlideColorAdjust> colorAdjustProperty() {
		return this.colorAdjust;
	}
}
