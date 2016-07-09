package org.praisenter.javafx.slide;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.effect.Effect;

public final class EffectBuilder {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private List<OrderedEffect> effects;
	
	private EffectBuilder() {
		this.effects = new ArrayList<OrderedEffect>();
	}
	
	public static final EffectBuilder create() {
		return new EffectBuilder();
	}
	
	public final EffectBuilder add(Effect effect, int order) {
		if (effect != null) {
			this.effects.add(new OrderedEffect(order, effect));
		}
		return this;
	}
	
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
