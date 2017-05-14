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
public abstract class Animation {
	/** Value for a constantly repeating animation */
	public static final int INFINITE = -1;
	
	// defaults

	/** The default animation type */
	public static final AnimationType DEFAULT_ANIMATION_TYPE = AnimationType.IN;
	
	/** The default duration */
	public static final long DEFAULT_DURATION = 300;
	
	/** The default delay */
	public static final long DEFAULT_DELAY = 0;
	
	/** The default number of times to repeat */
	public static final int DEFAULT_REPEAT_COUNT = 1;
	
	/** The default value for auto-reverse */
	public static final boolean DEFAULT_AUTO_REVERSE = false;
	
	/** The default easing */
	public static final Easing DEFAULT_EASING = new Linear();
	
	// members
	
	/** The animation type */
	@XmlElement(name = "type", required = false)
	final AnimationType type;
	
	/** The animation duration */
	@XmlElement(name = "duration", required = false)
	final long duration;
	
	/** The animation delay */
	@XmlElement(name = "delay", required = false)
	final long delay;
	
	/** The number of times to repeat the animation */
	@XmlElement(name = "repeatCount", required = false)
	final int repeatCount;
	
	/** Whether the animation should reverse or not */
	@XmlElement(name = "autoReverse", required = false)
	final boolean autoReverse;
	
	/** The easing function */
	@XmlElement(name = "easing", required = false)
	final Easing easing;

	/**
	 * Default constructor for JAXB.
	 */
	Animation() {
		this(AnimationType.IN);
	}
	
	/**
	 * Minimal constructor.
	 * @param type the animation type
	 */
	public Animation(AnimationType type) {
		this(type,
			 DEFAULT_DURATION,
			 DEFAULT_DELAY,
			 DEFAULT_REPEAT_COUNT,
			 DEFAULT_AUTO_REVERSE,
			 DEFAULT_EASING);
	}
	
	/**
	 * Full constructor.
	 * @param type the animation type
	 * @param duration the duration (in milliseconds)
	 * @param delay the delay (in milliseconds)
	 * @param repeatCount the repeat count; 1 or higher
	 * @param autoReverse true if auto-reverse should occur when repeat count is greater than 1
	 * @param easing the easing
	 */
	public Animation(AnimationType type,
			long duration,
			long delay,
			int repeatCount,
			boolean autoReverse,
			Easing easing) {
		this.type = type == null ? DEFAULT_ANIMATION_TYPE : type;
		this.duration = duration <= 0 ? DEFAULT_DURATION : duration;
		this.delay = delay < 0 ? DEFAULT_DELAY : delay;
		this.repeatCount = repeatCount < 1 ? DEFAULT_REPEAT_COUNT : repeatCount;
		this.autoReverse = autoReverse;
		this.easing = easing == null ? DEFAULT_EASING : easing;
	}
	
	/**
	 * Returns a copy of this animation.
	 * @return {@link Animation}
	 */
	public abstract Animation copy();
	
	/**
	 * Returns the animation type.
	 * @return {@link AnimationType}
	 */
	public AnimationType getType() {
		return this.type;
	}

	/**
	 * Returns the animation duration in milliseconds
	 * @return long
	 */
	public long getDuration() {
		return this.duration;
	}
	
	/**
	 * Returns the animation delay in milliseconds
	 * @return long
	 */
	public long getDelay() {
		return this.delay;
	}
	
	/**
	 * Returns the number of times the animation should repeat.
	 * @return int
	 * @see #INFINITE
	 */
	public int getRepeatCount() {
		return this.repeatCount;
	}
	
	/**
	 * Returns true if the animation should reverse before playing again.
	 * @return boolean
	 */
	public boolean isAutoReverse() {
		return this.autoReverse;
	}
	
	/**
	 * Returns the animation easing function.
	 * @return Easing
	 */
	public Easing getEasing() {
		return this.easing;
	}
}
