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
import javax.xml.bind.annotation.XmlRootElement;

import org.praisenter.slide.resources.Messages;

/**
 * Represents a template of a {@link BasicSlide}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlRootElement(name = "SlideTemplate")
@XmlAccessorType(XmlAccessType.NONE)
public class BasicSlideTemplate extends BasicSlide implements Slide, Template, Serializable {
	/** The version id */
	private static final long serialVersionUID = -3183054978115388166L;

	/**
	 * Default constructor.
	 * <p>
	 * This constructor should only be used by JAXB for
	 * marshalling and unmarshalling the objects.
	 */
	protected BasicSlideTemplate() {
		super(Messages.getString("slide.unnamed"), 400, 400);
	}
	
	/**
	 * Full constructor.
	 * @param name the name of the template
	 * @param width the width of the slide
	 * @param height the height of the slide
	 */
	public BasicSlideTemplate(String name, int width, int height) {
		super(name, width, height);
	}
	
	/**
	 * Minimal constructor.
	 * @param slide the slide to copy
	 */
	protected BasicSlideTemplate(BasicSlide slide) {
		super(slide);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.BasicSlide#copy()
	 */
	@Override
	public BasicSlideTemplate copy() {
		return new BasicSlideTemplate(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.Template#createSlide()
	 */
	public Slide createSlide() {
		return new BasicSlide(this);
	}
}
