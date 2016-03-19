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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

// FEATURE maybe have a setting to allow the animation to be played in reverse when the slide is transitioned out

/**
 * Represents an animation for a {@link SlideRegion}.
 * <p>
 * This class will support both slides and components by referencing the
 * UUID of the object.  Some fields may not be used by slides.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "animation")
@XmlAccessorType(XmlAccessType.NONE)
public final class SlideAnimation {
	/** The id of the object being animated */
	@XmlAttribute(name = "id", required = false)
	UUID id;
	
	/** The animation id */
	@XmlAttribute(name = "animationId", required = false)
	int animationId;
	
	/** The easing id */
	@XmlAttribute(name = "easingId", required = false)
	int easingId;
	
	/** The animation duration in milliseconds */
	@XmlAttribute(name = "duration", required = false)
	long duration;

	// ignored for slides
	
	/** The animation delay in milliseconds */
	@XmlAttribute(name = "delay", required = false)
	long delay;
	
	/**
	 * Copies this slide animation and assigns it the given id.
	 * @param id the new id
	 * @return {@link SlideAnimation}
	 */
	public SlideAnimation copy(UUID id) {
		SlideAnimation st = new SlideAnimation();
		st.id = id;
		st.animationId = animationId;
		st.easingId = easingId;
		st.delay = delay;
		st.duration = duration;
		return st;
	}
	
	/**
	 * Returns the id of the reference object.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}

	/**
	 * Sets the id of the reference object.
	 * @param id the id
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * Returns the animation id.
	 * @return int
	 */
	public int getAnimationId() {
		return this.animationId;
	}
	
	/**
	 * Sets the animation id.
	 * @param animationId the animation id
	 */
	public void setAnimationId(int animationId) {
		this.animationId = animationId;
	}
	
	/**
	 * Returns the easing id.
	 * @return int
	 */
	public int getEasingId() {
		return this.easingId;
	}
	
	/**
	 * Sets the easing id.
	 * @param easingId the easing id
	 */
	public void setEasingId(int easingId) {
		this.easingId = easingId;
	}
	
	/**
	 * Returns the animation delay in milliseconds.
	 * @return long
	 */
	public long getDelay() {
		return this.delay;
	}
	
	/**
	 * Sets the animation delay.
	 * @param delay the delay in milliseconds
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	/**
	 * Returns the animation duration in milliseconds.
	 * @return long
	 */
	public long getDuration() {
		return this.duration;
	}
	
	/**
	 * Sets the animation duration.
	 * @param duration the duration in milliseconds
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
}
