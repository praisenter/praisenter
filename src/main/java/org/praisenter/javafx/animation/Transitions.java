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

public final class Transitions {
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final Map<Class<? extends SlideAnimation>, Class<? extends CustomTransition<?>>> MAPPING;
	private static final Set<AnimationOption> ANIMATION_OPTIONS = new TreeSet<>();
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
	
	public static final Set<AnimationOption> getAnimationOptions() {
		return Collections.unmodifiableSet(ANIMATION_OPTIONS);
	}
	
	public static final Set<AnimationOption> getEasingOptions() {
		return Collections.unmodifiableSet(EASING_OPTIONS);
	}
	
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
