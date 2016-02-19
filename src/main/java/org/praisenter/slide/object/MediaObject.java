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
package org.praisenter.slide.object;

import java.util.Objects;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.media.MediaLibrary;
import org.praisenter.slide.MediaComponent;
import org.praisenter.slide.graphics.AbstractSlidePaint;
import org.praisenter.slide.graphics.ScaleType;
import org.praisenter.slide.graphics.SlidePaint;

/**
 * Represents a media object for audio, video, and images coming from the {@link MediaLibrary}.
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
@XmlRootElement(name = "media")
@XmlAccessorType(XmlAccessType.NONE)
public final class MediaObject extends AbstractSlidePaint implements SlidePaint {
	/** The media id */
	@XmlAttribute(name = "id", required = false)
	final UUID id;

	/** The media scaling type */
	@XmlAttribute(name = "scaling", required = false)
	final ScaleType scaling;
	
	/** True if the media should loop */
	@XmlAttribute(name = "loop", required = false)
	final boolean loop;
	
	/** True if the media should be muted */
	@XmlAttribute(name = "mute", required = false)
	final boolean mute;

	/**
	 * Constructor for JAXB.
	 */
	@SuppressWarnings("unused")
	private MediaObject() {
		this.id = null;
		this.scaling = ScaleType.UNIFORM;
		this.loop = false;
		this.mute = false;
	}
	
	/**
	 * Creates a new media object.
	 * @param id the referenced media id
	 * @param scaling the scaling type
	 * @param loop true if the media should loop
	 * @param mute true if the media should be muted
	 */
	public MediaObject(UUID id, ScaleType scaling, boolean loop, boolean mute) {
		if (id == null) throw new NullPointerException();
		this.id = id;
		this.scaling = scaling != null ? scaling : ScaleType.UNIFORM;
		this.loop = loop;
		this.mute = mute;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
		hash = 31 * hash + this.scaling.hashCode();
		hash = 31 * hash + (this.loop ? 1 : 0);
		hash = 31 * hash + (this.mute ? 1 : 0);
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MediaObject) {
			MediaObject m = (MediaObject)obj;
			return Objects.equals(this.id, m.id) &&
					this.scaling == m.scaling &&
					this.loop == m.loop &&
					this.mute == m.mute;
		}
		return false;
	}
	
	/**
	 * Returns the referenced media id.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}

	/**
	 * Returns the scaling type.
	 * @return {@link ScaleType}
	 */
	public ScaleType getScaling() {
		return this.scaling;
	}

	/**
	 * Returns true if this media will loop.
	 * @return boolean
	 */
	public boolean isLoop() {
		return this.loop;
	}

	/**
	 * Returns true if this media is muted.
	 * @return boolean
	 */
	public boolean isMute() {
		return this.mute;
	}
}
