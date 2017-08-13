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
package org.praisenter.slide.animation;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a relationship between an animation and a component.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "componentAnimation")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideAnimation {
	/** The id of the component being animated */
	@JsonProperty
	@XmlElement(name = "id", required = false)
	final UUID id;
	
	/** The animation */
	@JsonProperty
	@XmlElement(name = "animation", required = false)
	final Animation animation;
	
	/**
	 * Default constructor for JAXB.
	 */
	SlideAnimation() {
		this.id = null;
		this.animation = null;
	}
	
	/**
	 * Minimal constructor.
	 * @param id the component id
	 * @param animation the animation
	 */
	public SlideAnimation(UUID id, Animation animation) {
		this.id = id;
		this.animation = animation;
	}
	
	/**
	 * Makes a copy of this slide animation.
	 * @return {@link SlideAnimation}
	 */
	public SlideAnimation copy() {
		return this.copy(this.id);
	}
	
	/**
	 * Makes a copy for the given id.
	 * @param id the id
	 * @return {@link SlideAnimation}
	 */
	public SlideAnimation copy(UUID id) {
		return new SlideAnimation(id, this.animation);
	}
	
	/**
	 * Returns the id.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Returns the animation.
	 * @return {@link Animation}
	 */
	public Animation getAnimation() {
		return this.animation;
	}
}
