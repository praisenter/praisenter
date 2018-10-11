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

import java.util.Set;
import java.util.UUID;

import org.praisenter.data.Copyable;
import org.praisenter.data.Identifiable;
import org.praisenter.data.slide.ReadOnlySlideComponent;
import org.praisenter.data.slide.ReadOnlySlideRegion;
import org.praisenter.data.slide.SlideComponent;

import com.fasterxml.jackson.annotation.JsonProperty;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents a media object on a slide.
 * @author William Bittle
 * @version 3.0.0
 */
public final class MediaComponent extends SlideComponent implements ReadOnlyMediaComponent, ReadOnlySlideComponent, ReadOnlySlideRegion, Copyable, Identifiable {
	private final ObjectProperty<MediaObject> media;

	/**
	 * Default constructor.
	 */
	public MediaComponent() {
		this.media = new SimpleObjectProperty<>();
		
		// bind the name to the media name
		this.media.addListener((obs, ov, nv) -> {
			// whenever the media is changed, unbind and rebind if non-null
			this.name.unbind();
			if (nv != null) {
				this.name.bind(nv.mediaNameProperty());
			}
		});
	}
	
	@Override
	public MediaComponent copy() {
		MediaComponent component = new MediaComponent();
		this.copyTo(component);
		component.media.set(this.media.get());
		return component;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.AbstractSlideRegion#getReferencedMedia()
	 */
	@Override
	public Set<UUID> getReferencedMedia() {
		Set<UUID> media = super.getReferencedMedia();
		MediaObject mo = this.media.get();
		if (mo != null && mo.getMediaId() != null) {
			media.add(mo.getMediaId());
		}
		return media;
	}
	
	@Override
	@JsonProperty
	public MediaObject getMedia() {
		return this.media.get();
	}
	
	@JsonProperty
	public void setMedia(MediaObject media) {
		this.media.set(media);
	}

	@Override
	public ObjectProperty<MediaObject> mediaProperty() {
		return this.media;
	}
}
