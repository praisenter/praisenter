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
package org.praisenter.javafx.slide.animation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.praisenter.javafx.slide.ObservableSlide;
import org.praisenter.javafx.slide.ObservableSlideComponent;
import org.praisenter.resources.translations.Translations;
import org.praisenter.slide.SlideRegion;
import org.praisenter.slide.animation.Animation;
import org.praisenter.slide.animation.AnimationType;
import org.praisenter.slide.animation.Blinds;
import org.praisenter.slide.animation.Fade;
import org.praisenter.slide.animation.Push;
import org.praisenter.slide.animation.Shaped;
import org.praisenter.slide.animation.SlideAnimation;
import org.praisenter.slide.animation.Split;
import org.praisenter.slide.animation.Swap;
import org.praisenter.slide.animation.Swipe;
import org.praisenter.slide.animation.Zoom;
import org.praisenter.utility.Formatter;

import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.scene.Node;

/**
 * Helper class for mapping animation configuration with Java FX animation classes.
 * @author William Bittle
 * @version 3.0.0
 * @since 3.0.0
 */
public final class Animations {
	/** The class-level logger */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** The mapping */
	private static final Map<Class<? extends Animation>, Class<? extends CustomTransition<?>>> MAPPING;
	
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

	}
	
	/**
	 * Returns a generated name based on the given animation.
	 * @param animation the animation
	 * @return String
	 */
	public static final String getName(Animation animation) {
		if (animation == null) return "";
		
		long s = animation.getDelay();
		long d = animation.getDuration();
		
		StringBuilder sb = new StringBuilder();
		sb.append(Formatter.getTimeMillisecondsFormattedString(s))
		  .append(" ")
		  .append(Translations.get(animation.getClass().getName()))
		  .append(" [")
		  .append(Formatter.getMillisecondsFormattedString(d))
		  .append("]");
		return sb.toString();
	}
	
	/**
	 * Creates a custom Java FX transition for the given animation.
	 * @param animation the animation
	 * @return {@link CustomTransition}
	 */
	public static final CustomTransition<?> createCustomTransition(Animation animation) {
		if (animation != null) {
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
		}
		return new SwapTransition();
	}
	
	/**
	 * Returns a Java FX Transition instance for the given out-going and in-coming slides.
	 * @param out the out-going slide; can be null
	 * @param in the in-coming slide; can be null
	 * @return Transition
	 */
	public static final Transition buildSlideTransition(ObservableSlide<?> out, ObservableSlide<?> in) {
		boolean transitionBackground = true;
		
		ParallelTransition inTransitions = new ParallelTransition();
		ParallelTransition outTransitions = new ParallelTransition();
		
		if (in != null) {
			Map<UUID, Node> inNodes = mapNodes(in);
			
			// check if we need to animate the slide itself
			transitionBackground = in.getRegion().isBackgroundTransitionRequired(out.getRegion());
			UUID slideId = in.getId();
			
			for (SlideAnimation animation : in.getAnimations()) {
				if (animation != null && 
					animation.getAnimation() != null && 
					animation.getId() != null && 
					inNodes.containsKey(animation.getId())) {
					// if we are not transitioning the background, then don't add any
					// animations that are associated with the background to the transition
					if (!transitionBackground && animation.getId().equals(slideId)) {
						continue;
					}
					// create the transition
					CustomTransition<?> transition = createCustomTransition(animation.getAnimation());
					// set the node
					transition.setNode(inNodes.get(animation.getId()));
					// add it to the parallel transition
					inTransitions.getChildren().add(transition);
				}
			}
		}
		
		if (out != null) {
			UUID slideId = out.getId();
			List<SlideAnimation> animations = out.getAnimations();
			if (in != null) {
				slideId = in.getId();
				animations = in.getAnimations();
			}
			
			for (SlideAnimation animation : animations) {
				if (animation != null && 
					animation.getAnimation() != null && 
					animation.getId() != null &&
					animation.getId().equals(slideId)) {
					
					// create the transition
					CustomTransition<?> transition = createCustomTransition(animation.getAnimation().copy(AnimationType.OUT));
					
					// if we are transitioning the background, then all we need to do is
					// use the slide animations to animate the slide's node, the components
					// will flow with it
					if (transitionBackground) {
						// set the node
						transition.setNode(out.getDisplayPane());
						// add it to the parallel transition
						outTransitions.getChildren().add(transition);
					// if we are not transitioning the background, then we need to transition
					// all the components of the out-going slide individually with all the animations
					// that are for the slide
					} else {
						Iterator<ObservableSlideComponent<?>> componentIterator = out.componentIterator();
						while (componentIterator.hasNext()) {
							ObservableSlideComponent<?> component = componentIterator.next();
							// set the node
							transition.setNode(component.getDisplayPane());
							// add it to the parallel transition
							outTransitions.getChildren().add(transition);
						}
					}
				}
			}
		}
		
		return new SequentialTransition(outTransitions, inTransitions);
	}
	
	/**
	 * Creates a map of {@link SlideRegion} id to Node.
	 * @param slide the slide
	 * @return Map&lt;UUID, Node&gt;
	 */
	private static final Map<UUID, Node> mapNodes(ObservableSlide<?> slide) {
		Map<UUID, Node> nodes = new HashMap<>();
		nodes.put(slide.getId(), slide.getDisplayPane());
		Iterator<ObservableSlideComponent<?>> componentIterator = slide.componentIterator();
		while (componentIterator.hasNext()) {
			ObservableSlideComponent<?> component = componentIterator.next();
			nodes.put(component.getId(), component.getDisplayPane());
		}
		return nodes;
	}
}
