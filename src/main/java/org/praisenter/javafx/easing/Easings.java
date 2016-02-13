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
package org.praisenter.javafx.easing;

/**
 * Factory class for easings.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Easings {
	/** Hidden constructor */
	private Easings() {}
	
	/**
	 * Returns a new linear easing.
	 * @return Easing
	 */
	public static final Easing getLinear() { 
		return new Linear(EasingType.IN); 
	}
	
	/**
	 * Returns a new quadratic easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getQuadratic(EasingType type) { 
		return new Quadratic(type); 
	}
	
	/**
	 * Returns a new cubic easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getCubic(EasingType type) { 
		return new Cubic(type); 
	}
	
	/**
	 * Returns a new quartic easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getQuartic(EasingType type) { 
		return new Quartic(type); 
	}
	
	/**
	 * Returns a new quintic easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getQuintic(EasingType type) { 
		return new Quintic(type); 
	}
	
	/**
	 * Returns a new circular easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getCircular(EasingType type) { 
		return new Circular(type); 
	}
	
	/**
	 * Returns a new exponential easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getExponential(EasingType type) { 
		return new Exponential(type); 
	}
	
	/**
	 * Returns a new sinusoidal easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getSinusoidal(EasingType type) { 
		return new Sinusoidal(type); 
	}
	
	/**
	 * Returns a new back easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getBack(EasingType type) { 
		return new Back(type); 
	}
	
	/**
	 * Returns a new bounce easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getBounce(EasingType type) { 
		return new Bounce(type); 
	}
	
	/**
	 * Returns a new elastic easing.
	 * @param type the easing type
	 * @return Easing
	 */
	public static final Easing getElastic(EasingType type) { 
		return new Elastic(type); 
	}
	
	/**
	 * Returns a new easing for the given id and type.
	 * @param id the easing id 
	 * @param type the easing type
	 * @return {@link Easing}
	 */
	public static final Easing get(int id, EasingType type) { 
		if (Quadratic.ID == id) {
			return getQuadratic(type);
		} else if (Cubic.ID == id) {
			return getCubic(type);
		} else if (Quartic.ID == id) {
			return getQuartic(type);
		} else if (Quintic.ID == id) {
			return getQuintic(type);
		} else if (Circular.ID == id) {
			return getCircular(type);
		} else if (Exponential.ID == id) {
			return getExponential(type);
		} else if (Sinusoidal.ID == id) {
			return getSinusoidal(type);
		} else if (Back.ID == id) {
			return getBack(type);
		} else if (Bounce.ID == id) {
			return getBounce(type);
		} else if (Elastic.ID == id) {
			return getElastic(type);
		}
		return getLinear();
	}
}
