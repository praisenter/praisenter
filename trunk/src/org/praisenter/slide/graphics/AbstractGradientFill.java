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
package org.praisenter.slide.graphics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a gradient {@link Fill}.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractGradientFill extends AbstractFill implements Fill {
	/** The offset to apply for gradients so that the middle stop can be all the way to either end */
	protected static final int OFFSET = 5;
	
	/** The default stops */
	protected static final Stop[] DEFAULT_STOPS = new Stop[] {
		new Stop(0, 0, 0, 0, 1.0f), 
		new Stop(0.5f, 0.5f, 0.5f, 0.5f, 1.0f), 
		new Stop(1.0f, 1.0f, 1.0f, 1.0f, 1.0f)
	};
	
	/** The list of stops */
	@XmlElement(name = "Stops")
	protected Stop[] stops;

	/**
	 * Default constructor.
	 */
	public AbstractGradientFill() {
		this(DEFAULT_STOPS);
	}
	
	/**
	 * Full constructor.
	 * @param stops the stops
	 */
	public AbstractGradientFill(Stop... stops) {
		if (stops == null) {
			stops = DEFAULT_STOPS;
		}
		this.stops = stops;
	}
	
	/**
	 * Returns the stops for this gradient.
	 * @return {@link Stop}[]
	 */
	public Stop[] getStops() {
		return this.stops;
	}
}
