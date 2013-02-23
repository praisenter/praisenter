/*
 * Copyright (c) 2011-2013 William Bittle  http://www.praisenter.org/
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
package org.praisenter.slide.media;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.praisenter.media.AbstractAudioMedia;
import org.praisenter.media.MediaPlayerListener;
import org.praisenter.media.MediaTypeAdapter;
import org.praisenter.slide.SlideComponent;

/**
 * Represents a coponent that plays audio.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "AudioMediaComponent")
@XmlAccessorType(XmlAccessType.NONE)
public class AudioMediaComponent implements PlayableMediaComponent<AbstractAudioMedia>, MediaComponent<AbstractAudioMedia>, MediaPlayerListener, SlideComponent, Serializable {
	/** The version id */
	private static final long serialVersionUID = 4375436580503250539L;

	/** The name of the component */
	@XmlElement(name = "Name", required = true, nillable = false)
	protected String name;

	/** The media */
	@XmlElement(name = "Media", required = true, nillable = false)
	@XmlJavaTypeAdapter(MediaTypeAdapter.class)
	protected AbstractAudioMedia media;
	
	/** True if looping is enabled */
	@XmlAttribute(name = "LoopEnabled", required = true)
	protected boolean loopEnabled;
	
	/** True if the audio should be muted */
	@XmlAttribute(name = "AudioMuted", required = true)
	protected boolean audioMuted;
	
	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected AudioMediaComponent() {
		this(null, null);
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 * @param media the media
	 */
	public AudioMediaComponent(String name, AbstractAudioMedia media) {
		this.name = name;
		this.media = media;
	}
	
	/**
	 * Copy constructor.
	 * @param component the component to copy
	 */
	public AudioMediaComponent(AudioMediaComponent component) {
		this.name = component.name;
		this.media = component.media;
		this.loopEnabled = component.loopEnabled;
		this.audioMuted = component.audioMuted;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#copy()
	 */
	@Override
	public AudioMediaComponent copy() {
		return new AudioMediaComponent(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#getName()
	 */
	public String getName() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideComponent#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#getMedia()
	 */
	@Override
	public AbstractAudioMedia getMedia() {
		return this.media;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.MediaComponent#setMedia(org.praisenter.media.Media)
	 */
	@Override
	public void setMedia(AbstractAudioMedia media) {
		this.media = media;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#setLoopEnabled(boolean)
	 */
	@Override
	public void setLoopEnabled(boolean loopEnabled) {
		this.loopEnabled = loopEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#isLoopEnabled()
	 */
	@Override
	public boolean isLoopEnabled() {
		return this.loopEnabled;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#isAudioMuted()
	 */
	public boolean isAudioMuted() {
		return this.audioMuted;
	}

	/* (non-Javadoc)
	 * @see org.praisenter.slide.media.PlayableMediaComponent#setAudioMuted(boolean)
	 */
	public void setAudioMuted(boolean audioMuted) {
		this.audioMuted = audioMuted;
	}
}
