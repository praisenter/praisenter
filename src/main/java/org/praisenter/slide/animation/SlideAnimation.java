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
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.easing.Easing;
import org.praisenter.slide.easing.Linear;

/**
 * Represents an animation that can be applied to slides and components.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "animation")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso(value = {
	Blinds.class,
	Fade.class,
	Push.class,
	Shaped.class,
	Split.class,
	Swap.class,
	Swipe.class,
	Zoom.class
})
public abstract class SlideAnimation {
	/** The id of the object being animated */
	@XmlElement(name = "id", required = false)
	UUID id;
	
	/** The animation type */
	@XmlElement(name = "type", required = false)
	AnimationType type;
	
	/** The animation duration */
	@XmlElement(name = "duration", required = false)
	long duration;
	
	/** The animation delay */
	@XmlElement(name = "delay", required = false)
	long delay;
	
	/** The easing function */
	@XmlElement(name = "easing", required = false)
	Easing easing;
	
	/**
	 * Default constructor.
	 */
	protected SlideAnimation() {
		this.id = null;
		this.type = AnimationType.IN;
		this.duration = 300;
		this.delay = 0;
		this.easing = new Linear();
	}
	
	/**
	 * Copies over the default animation properties to the given other animation.
	 * @param other the new animation to copy to
	 * @param id the id that the new animation applies to
	 */
	void copy(SlideAnimation other, UUID id) {
		other.delay = this.delay;
		other.duration = this.duration;
		other.easing = this.easing.copy();
		other.id = id;
		other.type = this.type;
	}
	
	/**
	 * Returns a copy of this animation.
	 * @param id the new id for the animation; can be null
	 * @return {@link SlideAnimation}
	 */
	public abstract SlideAnimation copy(UUID id);
	
	/**
	 * Returns the id of the object to that this animation applies to.
	 * @return UUID
	 */
	public UUID getId() {
		return this.id;
	}
	
	/**
	 * Sets the id of the object to that this animation applies to.
	 * @param id the id
	 */
	public void setId(UUID id) {
		this.id = id;
	}
	
	/**
	 * Returns the animation type.
	 * @return {@link AnimationType}
	 */
	public AnimationType getType() {
		return this.type;
	}

	/**
	 * Sets the animation type.
	 * @param type the type
	 */
	public void setType(AnimationType type) {
		this.type = type;
	}
	
	/**
	 * Returns the animation duration in milliseconds
	 * @return long
	 */
	public long getDuration() {
		return this.duration;
	}
	
	/**
	 * Sets the duration.
	 * @param duration the duration in milliseconds
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	/**
	 * Returns the animation delay in milliseconds
	 * @return long
	 */
	public long getDelay() {
		return this.delay;
	}
	
	/**
	 * Sets the delay.
	 * @param delay the delay in milliseconds
	 */
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	/**
	 * Returns the animation easing function.
	 * @return Easing
	 */
	public Easing getEasing() {
		return this.easing;
	}
	
	/**
	 * Sets the animation easing function.
	 * @param easing the easing function
	 */
	public void setEasing(Easing easing) {
		this.easing = easing;
	}
}
