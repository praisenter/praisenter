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
package org.praisenter.slide.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Base class for gradient paints.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class SlideGradient extends AbstractSlidePaint implements SlidePaint {
	/** The default stops */
	static final SlideGradientStop[] DEFAULT_STOPS = new SlideGradientStop[] {
		new SlideGradientStop(0.0, 0.0, 0.0, 0.0, 1.0), 
		new SlideGradientStop(1.0, 1.0, 1.0, 1.0, 1.0)
	};
	
	/** The list of stops */
	@XmlElement(name = "stop", required = false)
	@XmlElementWrapper(name = "stops", required = false)
	final List<SlideGradientStop> stops;

	/**
	 * Constructor for JAXB.
	 */
	@SuppressWarnings("unused")
	private SlideGradient() {
		this(DEFAULT_STOPS);
	}
	
	/**
	 * Creates a new gradient with the following stops.
	 * @param stops the stops
	 */
	public SlideGradient(SlideGradientStop... stops) {
		if (stops == null) {
			stops = DEFAULT_STOPS;
		}
		this.stops = new ArrayList<SlideGradientStop>(Arrays.asList(stops));
	}
	
	/**
	 * Creates a new gradient with the following stops.
	 * @param stops the stops
	 */
	public SlideGradient(List<SlideGradientStop> stops) {
		if (stops == null) {
			stops = new ArrayList<SlideGradientStop>(Arrays.asList(DEFAULT_STOPS));
		}
		this.stops = stops;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int hash = 37;
		for (int i = 0; i < this.stops.size(); i++) {
			SlideGradientStop stop = stops.get(i);
			hash = 31 * hash + stop.hashCode();
		}
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof SlideGradient) {
			SlideGradient g = (SlideGradient)obj;
			if (g.stops.size() != this.stops.size()) {
				return false;
			}
			for (int i = 0; i < this.stops.size(); i++) {
				if (!g.stops.get(i).equals(this.stops.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the stops.
	 * @return List&lt;{@link SlideGradientStop}&gt;
	 */
	public List<SlideGradientStop> getStops() {
		return Collections.unmodifiableList(this.stops);
	}
}
