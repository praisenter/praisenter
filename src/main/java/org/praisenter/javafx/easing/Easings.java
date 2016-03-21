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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class for easings.
 * @author William Bittle
 * @version 3.0.0
 */
public final class Easings {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** All easings by id */
	private static final Map<Integer, Class<?>> BY_ID;
	
	static {
		BY_ID = new HashMap<Integer, Class<?>>();
		BY_ID.put(Linear.ID, Linear.class);
		BY_ID.put(Quadratic.ID, Quadratic.class);
		BY_ID.put(Cubic.ID, Cubic.class);
		BY_ID.put(Quartic.ID, Quartic.class);
		BY_ID.put(Quintic.ID, Quintic.class);
		BY_ID.put(Circular.ID, Circular.class);
		BY_ID.put(Exponential.ID, Exponential.class);
		BY_ID.put(Sinusoidal.ID, Sinusoidal.class);
		BY_ID.put(Back.ID, Back.class);
		BY_ID.put(Bounce.ID, Bounce.class);
		BY_ID.put(Elastic.ID, Elastic.class);
	}
	
	/** Hidden constructor */
	private Easings() {}
	
	public static final Set<Integer> getEasingIds() {
		return Collections.unmodifiableSet(BY_ID.keySet());
	}
	
	/**
	 * Returns a new instance of the given easing class.
	 * @param clazz the easing class
	 * @param type the easing type
	 * @return {@link Easing}
	 */
	private static final Easing getEasing(Class<?> clazz, EasingType type) {
		if (clazz != null) {
			try {
				return (Easing)clazz.getConstructor(EasingType.class).newInstance(type);
			} catch (Exception e) {
				LOGGER.warn("Failed to instantiate class " + clazz.getName() + ".", e);
			}
		}
		return new Linear(type);
	}
	
	/**
	 * Returns a new easing for the given id and type.
	 * @param id the easing id 
	 * @param type the easing type
	 * @return {@link Easing}
	 */
	public static final Easing getEasing(int id, EasingType type) {
		return getEasing(BY_ID.get(id), type);
	}
	
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
}
