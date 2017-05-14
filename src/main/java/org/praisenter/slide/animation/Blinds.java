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

/**
 * Represents a blinds animation.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "blinds")
@XmlAccessorType(XmlAccessType.NONE)
public final class Blinds extends Animation {
	/** The default orientation */
	public static final Orientation DEFAULT_ORIENTATION = Orientation.VERTICAL;
	
	/** The default blind count */
	public static final int DEFAULT_BLIND_COUNT = 12;
	
	/** The orientation of the blinds */
	@XmlElement(name = "orientation", required = false)
	final Orientation orientation;
	
	/** The blind count */
	@XmlElement(name = "blindCount", required = false)
	final int blindCount;
	
	/**
	 * Default constructor for JAXB.
	 */
	Blinds() {
		super(AnimationType.IN);
		this.orientation = DEFAULT_ORIENTATION;
		this.blindCount = DEFAULT_BLIND_COUNT;
	}
	
	/**
	 * Full constructor.
	 * @param type the animation type
	 * @param duration the duration (in milliseconds)
	 * @param delay the delay (in milliseconds)
	 * @param repeatCount the repeat count; 1 or higher
	 * @param autoReverse true if auto-reverse should occur when repeat count is greater than 1
	 * @param easing the easing
	 * @param orientation the orientation of the blinds
	 * @param blindCount the number of blinds
	 */
	public Blinds(AnimationType type,
			long duration,
			long delay,
			int repeatCount,
			boolean autoReverse,
			Easing easing,
			Orientation orientation,
			int blindCount) {
		super(type, duration, delay, repeatCount, autoReverse, easing);
		this.orientation = orientation == null ? DEFAULT_ORIENTATION : orientation;
		this.blindCount = blindCount < 2 ? DEFAULT_BLIND_COUNT : blindCount;
	}
	
	/**
	 * Copy constructor.
	 * @param other the blinds animation to copy
	 */
	public Blinds(Blinds other) {
		this(other.type,
			 other.duration,
			 other.delay,
			 other.repeatCount,
			 other.autoReverse,
			 other.easing,
			 other.orientation,
			 other.blindCount);
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.animation.SlideAnimation#copy()
	 */
	@Override
	public Blinds copy() {
		return new Blinds(this);
	}
	
	/**
	 * Returns the orientation of the blinds.
	 * @return {@link Orientation}
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * Returns the number of blinds.
	 * @return int
	 */
	public int getBlindCount() {
		return this.blindCount;
	}
}
