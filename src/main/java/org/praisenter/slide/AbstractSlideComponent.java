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
package org.praisenter.slide;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.effects.SlideShadow;
import org.praisenter.slide.media.MediaComponent;
import org.praisenter.slide.text.BasicTextComponent;
import org.praisenter.slide.text.CountdownComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextPlaceholderComponent;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Abstract implementation of the {@link SlideComponent} interface.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlSeeAlso({
	MediaComponent.class,
	BasicTextComponent.class,
	DateTimeComponent.class,
	TextPlaceholderComponent.class,
	CountdownComponent.class
})
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractSlideComponent extends AbstractSlideRegion implements SlideRegion, SlideComponent {

	/** The overall shadow */
	@JsonProperty
	@XmlElement(name = "shadow", required = false)
	SlideShadow shadow;

	/** The overall glow */
	@JsonProperty
	@XmlElement(name = "glow", required = false)
	SlideShadow glow;
	
	/**
	 * Default constructor.
	 */
	public AbstractSlideComponent() {
		this.shadow = null;
		this.glow = null;
	}
	
	/**
	 * Optional constructor.
	 * @param id the id
	 */
	public AbstractSlideComponent(UUID id) {
		super(id);
		this.shadow = null;
		this.glow = null;
	}
	
	/**
	 * Copy constructor.
	 * @param other the component to copy
	 * @param exact whether to copy the component exactly
	 */
	public AbstractSlideComponent(AbstractSlideComponent other, boolean exact) {
		super(other, exact);
		this.shadow = other.shadow;
		this.glow = other.glow;
	}
	

	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getShadow()
	 */
	@Override
	public SlideShadow getShadow() {
		return this.shadow;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setShadow(org.praisenter.slide.graphics.SlideShadow)
	 */
	@Override
	public void setShadow(SlideShadow shadow) {
		this.shadow = shadow;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#getGlow()
	 */
	@Override
	public SlideShadow getGlow() {
		return this.glow;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#setGlow(org.praisenter.slide.graphics.SlideShadow)
	 */
	@Override
	public void setGlow(SlideShadow glow) {
		this.glow = glow;
	}
}
