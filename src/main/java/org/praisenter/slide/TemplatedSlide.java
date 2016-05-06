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

import org.praisenter.slide.text.TextPlaceholderComponent;

/**
 * Represents a slide, typically with {@link TextPlaceholderComponent}s, that was copied from
 * a {@link BasicSlide} for use in a specific setting.
 * <p>
 * A templated slide keeps track of the original {@link BasicSlide} it was generated from.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class TemplatedSlide extends BasicSlide implements Slide, SlideRegion  {
	/** The id of the slide this slide was based on */
	@XmlElement(name = "templateId", required = false)
	final UUID templateId;

	/**
	 * Minimal constructor.
	 * @param templateId the id of the slide being templated
	 */
	public TemplatedSlide(UUID templateId) {
		this.templateId = templateId;
	}
	
	/**
	 * Optional constructor.
	 * @param id this slide's id
	 * @param templateId the id of the slide being templated
	 */
	TemplatedSlide(UUID id, UUID templateId) {
		super(id);
		this.templateId = templateId;
	}
	
	/**
	 * Return the root slide id for the given slide.
	 * <p>
	 * A {@link TemplatedSlide} only retains the original slide id.  For example, if slide A
	 * was used to create slide B and then slide B was used to create slide C, slide C will have
	 * slide A's id as the template id.  Slide B's id is not retained.
	 * @param slide the slide
	 * @return UUID
	 */
	protected static final UUID getRootTemplateId(Slide slide) {
		if (slide == null) return null;
		if (slide instanceof TemplatedSlide) { 
			return ((TemplatedSlide)slide).getTemplateId();
		}
		return slide.getId();
	}

	/**
	 * Returns the template id for this slide.
	 * @return UUID or null
	 */
	public UUID getTemplateId() {
		return this.templateId;
	}
}
