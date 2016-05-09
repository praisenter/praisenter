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

/**
 * Represents a blinds animation.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlRootElement(name = "blinds")
@XmlAccessorType(XmlAccessType.NONE)
public final class Blinds extends SlideAnimation {
	/** The orientation of the blinds */
	@XmlElement(name = "orientation", required = false)
	Orientation orientation;
	
	/** The blind count */
	@XmlElement(name = "blindCount", required = false)
	int blindCount;
	
	/**
	 * Default constructor.
	 */
	public Blinds() {
		this.orientation = Orientation.HORIZONTAL;
		this.blindCount = 12;
	}
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.animation.SlideAnimation#copy(java.util.UUID)
	 */
	@Override
	public Blinds copy(UUID id) {
		Blinds animation = new Blinds();
		copy(animation, id);
		animation.orientation = this.orientation;
		animation.blindCount = this.blindCount;
		return animation;
	}
	
	/**
	 * Returns the orientation of the blinds.
	 * @return {@link Orientation}
	 */
	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * Sets the orientation of the blinds.
	 * @param orientation the orientation
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * Returns the number of blinds.
	 * @return int
	 */
	public int getBlindCount() {
		return this.blindCount;
	}

	/**
	 * Sets the number of blinds.
	 * @param blindCount the number of blinds
	 */
	public void setBlindCount(int blindCount) {
		this.blindCount = blindCount;
	}
}
