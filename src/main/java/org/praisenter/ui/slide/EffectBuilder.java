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
package org.praisenter.ui.slide;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.effect.Effect;

/**
 * Helper class to build a single Effect from a list of ordered effects.
 * @author William Bittle
 * @version 3.0.0
 */
final class EffectBuilder {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The list of effects */
	private List<OrderedEffect> effects;
	
	/** hidden constructor */
	private EffectBuilder() {
		this.effects = new ArrayList<OrderedEffect>();
	}
	
	/**
	 * Creates a new effect builder.
	 * @return {@link EffectBuilder}
	 */
	public static final EffectBuilder create() {
		return new EffectBuilder();
	}
	
	/**
	 * Adds an effect to the builder.
	 * @param effect the effect
	 * @param order the order
	 * @return {@link EffectBuilder}
	 */
	public final EffectBuilder add(Effect effect, int order) {
		if (effect != null) {
			this.effects.add(new OrderedEffect(order, effect));
		}
		return this;
	}
	
	/**
	 * Builds the effects into a single effect.
	 * @return Effect
	 */
	public final Effect build() {
		if (this.effects.size() == 0) return null;
		
		Collections.sort(this.effects);
		
		Effect last = this.effects.get(0).effect;
		for (int i = 1; i < this.effects.size(); i++) {
			Effect next = this.effects.get(i).effect;
			try {
				Method method = next.getClass().getMethod("setInput", Effect.class);
				method.invoke(next, last);
				last = next;
				//method.invoke(obj, args);
			} catch (Exception ex) {
				// if an error occurs just skip this effect
				LOGGER.warn("Effect " + next.getClass() + " doesn't have a setInput method to chain the effect.  Skipping this effect.", ex);
			}
		}
		return last;
	}
}
