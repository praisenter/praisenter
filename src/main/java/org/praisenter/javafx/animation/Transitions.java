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
package org.praisenter.javafx.animation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.animation.Blinds;
import org.praisenter.slide.animation.Fade;
import org.praisenter.slide.animation.Push;
import org.praisenter.slide.animation.Shaped;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Split;
import org.praisenter.slide.animation.Swap;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.animation.Zoom;
import org.praisenter.slide.easing.Back;
import org.praisenter.slide.easing.Bounce;
import org.praisenter.slide.easing.Circular;
import org.praisenter.slide.easing.Cubic;
import org.praisenter.slide.easing.Elastic;
import org.praisenter.slide.easing.Exponential;
import org.praisenter.slide.easing.Linear;
import org.praisenter.slide.easing.Quadratic;
import org.praisenter.slide.easing.Quartic;
import org.praisenter.slide.easing.Quintic;
import org.praisenter.slide.easing.Sinusoidal;
import org.praisenter.utility.Formatter;

/**
 * Helper class for mapping animation configuration with Java FX animation classes.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class Transitions {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The mapping */
	private static final Map<Class<? extends SlideAnimation>, Class<? extends CustomTransition<?>>> MAPPING;
	
	/** The animation options */
	private static final Set<AnimationOption> ANIMATION_OPTIONS = new TreeSet<>();
	
	/** The easing options */
	private static final Set<AnimationOption> EASING_OPTIONS = new TreeSet<>();
	
	static {
		// setup the mapping
		MAPPING = new HashMap<>();
		MAPPING.put(Blinds.class, BlindsTransition.class);
		MAPPING.put(Fade.class, FadeTransition.class);
		MAPPING.put(Push.class, PushTransition.class);
		MAPPING.put(Shaped.class, ShapedTransition.class);
		MAPPING.put(Split.class, SplitTransition.class);
		MAPPING.put(Swap.class, SwapTransition.class);
		MAPPING.put(Swipe.class, SwipeTransition.class);
		MAPPING.put(Zoom.class, ZoomTransition.class);
		
		// setup the animation options
		int i = 0;
		ANIMATION_OPTIONS.add(new AnimationOption(Swap.class, i++));
		ANIMATION_OPTIONS.add(new AnimationOption(Fade.class, i++));
		ANIMATION_OPTIONS.add(new AnimationOption(Swipe.class, i++));
		ANIMATION_OPTIONS.add(new AnimationOption(Push.class, i++));
		ANIMATION_OPTIONS.add(new AnimationOption(Split.class, i++));
		ANIMATION_OPTIONS.add(new AnimationOption(Shaped.class, i++));
		ANIMATION_OPTIONS.add(new AnimationOption(Blinds.class, i++));
		ANIMATION_OPTIONS.add(new AnimationOption(Zoom.class, i++));
		
		// setup the easing options
		i = 0;
		EASING_OPTIONS.add(new AnimationOption(Linear.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Quadratic.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Cubic.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Quartic.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Quintic.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Exponential.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Sinusoidal.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Circular.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Back.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Bounce.class, i++));
		EASING_OPTIONS.add(new AnimationOption(Elastic.class, i++));
	}
	
	/**
	 * Returns the animation options.
	 * @return Set&lt;{@link AnimationOption}&gt;
	 */
	public static final Set<AnimationOption> getAnimationOptions() {
		return Collections.unmodifiableSet(ANIMATION_OPTIONS);
	}
	
	/**
	 * Returns the easing options.
	 * @return Set&lt;{@link AnimationOption}&gt;
	 */
	public static final Set<AnimationOption> getEasingOptions() {
		return Collections.unmodifiableSet(EASING_OPTIONS);
	}
	
	/**
	 * Returns a generated name based on the given animation.
	 * @param animation the animation
	 * @return String
	 */
	public static final String getName(SlideAnimation animation) {
		if (animation == null) return "";
		
		StringBuilder sb = new StringBuilder();
		
		// name
		sb.append(Translations.get(animation.getClass().getName())).append(" ");
		
		// TODO add class specific properties (CIRCLE/Collapse)
		
		// start-end
		long s = animation.getDelay();
		long d = animation.getDuration();
		sb.append(Formatter.getMillisecondsFormattedString(s))
		  .append("-")
		  .append(Formatter.getMillisecondsFormattedString(s + d));
		return sb.toString();
	}
	
	/**
	 * Creates a custom Java FX transition for the given animation.
	 * @param animation the animation
	 * @return {@link CustomTransition}
	 */
	public static final CustomTransition<?> createCustomTransition(SlideAnimation animation) {
		Class<? extends CustomTransition<?>> clazz = MAPPING.get(animation.getClass());
		try {
			Constructor<? extends CustomTransition<?>> constructor = clazz.getConstructor(animation.getClass());
			return constructor.newInstance(animation);
		} catch (NoSuchMethodException e) {
			LOGGER.error(clazz.getName() + " doesn't contain a constructor accepting one parameter of type " + animation.getClass().getName(), e);
		} catch (SecurityException e) {
			LOGGER.error("Failed to instantiate class due to a security exception.", e);
		} catch (InstantiationException e) {
			LOGGER.error("Failed to instatiate the object " + clazz.getName(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error("The constructor is not public.", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("The constructor was called with " + animation.getClass().getName() + ", but this type is not the right type.", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("Failed to call the constructor.", e);
		}
		return new SwapTransition(new Swap());
	}
}
