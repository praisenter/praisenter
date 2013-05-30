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
package org.praisenter.slide;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.resources.Messages;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextComponent;

/**
 * Abstract implementation of a {@link SlideComponent}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
// we must specify the implementing classes so that JAXB can unmarshall them
@XmlSeeAlso({
	GenericComponent.class,
	ImageMediaComponent.class,
	VideoMediaComponent.class,
	AudioMediaComponent.class,
	EmptyBackgroundComponent.class,
	TextComponent.class,
	DateTimeComponent.class
})
public abstract class AbstractSlideComponent implements SlideComponent, Serializable {
	/** The version id */
	private static final long serialVersionUID = -4160379089715478652L;
	
	/** The component name */
	@XmlElement(name = "Name")
	protected String name;

	/**
	 * Default constructor.
	 * <p>
	 * This should only be used by JAXB.
	 */
	protected AbstractSlideComponent() {
		this(Messages.getString("slide.component.unnamed"));
	}
	
	/**
	 * Minimal constructor.
	 * @param name the name of the component
	 */
	public AbstractSlideComponent(String name) {
		this.name = name;
	}
	
	/**
	 * Copy constructor.
	 * <p>
	 * This constructor performs a deep copy where necessary.
	 * @param component the component to copy
	 */
	public AbstractSlideComponent(AbstractSlideComponent component) {
		this.name = component.name;
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
}
