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

import org.praisenter.slide.easing.Easing;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a push animation.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "push")
@XmlAccessorType(XmlAccessType.NONE)
public final class Push extends Animation {
	/** The default direction */
	public static final Direction DEFAULT_DIRECTION = Direction.LEFT;
	
	/** The direction */
	@JsonProperty
	@XmlElement(name = "direction", required = false)
	final Direction direction;

	/**
	 * Default constructor for JAXB.
	 */
	Push() {
		super(AnimationType.IN);
		this.direction = DEFAULT_DIRECTION;
	}
	
	/**
	 * Full constructor.
	 * @param type the animation type
	 * @param duration the duration (in milliseconds)
	 * @param delay the delay (in milliseconds)
	 * @param repeatCount the repeat count; 1 or higher
	 * @param autoReverse true if auto-reverse should occur when repeat count is greater than 1
	 * @param easing the easing
	 * @param direction the direction of the push
	 */
	public Push(AnimationType type,
			long duration,
			long delay,
			int repeatCount,
			boolean autoReverse,
			Easing easing,
			Direction direction) {
		super(type, duration, delay, repeatCount, autoReverse, easing);
		this.direction = direction == null ? DEFAULT_DIRECTION : direction;
	}
	
	/**
	 * Copy constructor.
	 * @param other the animation to copy
	 */
	public Push(Push other) {
		this(other.type,
			 other.duration,
			 other.delay,
			 other.repeatCount,
			 other.autoReverse,
			 other.easing,
			 other.direction);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.animation.Animation#copy()
	 */
	@Override
	public Push copy() {
		return new Push(this);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.animation.Animation#copy(org.praisenter.slide.animation.AnimationType)
	 */
	@Override
	public Push copy(AnimationType type) {
		return new Push(
				type,
				this.duration,
				this.delay,
				this.repeatCount,
				this.autoReverse,
				this.easing,
				this.direction);
	}
	
	/**
	 * Returns the direction.
	 * @return {@link Direction}
	 */
	public Direction getDirection() {
		return this.direction;
	}
}
