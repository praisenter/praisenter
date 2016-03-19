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
package org.praisenter.slide;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.Tag;
import org.praisenter.slide.text.TextPlaceholderComponent;

/**
 * Represents a slide.
 * <p>
 * A slide is a region of graphics that can include text, images, video
 * audio, etc.
 * @author William Bittle
 * @version 3.0.0
 */
@XmlSeeAlso({
	BasicSlide.class,
	SongSlide.class,
	BibleSlide.class
})
@XmlAccessorType(XmlAccessType.NONE)
public interface Slide extends SlideRegion {
	/** The version of the slide format */
	public static final String VERSION = "3.0.0";
	
	/** Value indicating a slide should show forever */
	public static final long TIME_FOREVER = -1;
	
	/** A default transition duration value */
	public static final long DEFAULT_TRANSITION_DURATION = 400;
	
	/** Value indicating the transition or easing id is not assigned */
	public static final int ID_NOT_SET = -1;
	
	// properties
	
	/**
	 * Returns the name of this slide.
	 * @return String
	 */
	public abstract String getName();
	
	/**
	 * Sets the name of this slide.
	 * @param name the slide name
	 */
	public abstract void setName(String name);
	
	/**
	 * Returns the path where this slide is saved.
	 * <p>
	 * Returns null if the slide has not been saved.
	 * @return Path
	 */
	public abstract Path getPath();
	
	/**
	 * Sets the path where this slide is saved.
	 * @param path the path
	 */
	public abstract void setPath(Path path);
	
	/**
	 * Returns the slide version this slide was created with.
	 * @return String
	 */
	public abstract String getVersion();
	
	// components
	
	/**
	 * Adds the given component to the components list.
	 * @param component the component to add
	 */
	public abstract void addComponent(SlideComponent component);
	
	/**
	 * Removes the given component from this slide.
	 * @param component the component to remove
	 * @return boolean true if the component was removed
	 */
	public abstract boolean removeComponent(SlideComponent component);
	
	/**
	 * Returns a read-only iterator for the components on this slide.
	 * @return Iterator&lt;{@link SlideComponent}&gt;
	 */
	public abstract Iterator<SlideComponent> getComponentIterator();
	
	/**
	 * Returns a list of the all the components of the given type.
	 * <p>
	 * This method will return the components in ascending order.
	 * @param clazz the class type
	 * @return List&lt;E&gt;
	 */
	public abstract <E extends SlideComponent> List<E> getComponents(Class<E> clazz);
	
	/**
	 * Returns true if this slide contains any {@link TextPlaceholderComponent}s.
	 * @return boolean
	 */
	public abstract boolean hasPlaceholders();
	
	// z-ordering
	
	/**
	 * Moves the given component up by one.
	 * <p>
	 * If the given component is not on this slide, this method does nothing.
	 * <p>
	 * If the given component is already the last component in this slide then
	 * the component is not modified.
	 * <p>
	 * Otherwise the given component is moved up by one and the next component is 
	 * moved back by one.
	 * @param component the component to move up
	 */
	public abstract void moveComponentUp(SlideComponent component);
	
	/**
	 * Moves the given component down by one.
	 * <p>
	 * If the given component is not on this slide, this method does nothing.
	 * <p>
	 * If the given component is already the first component in this slide then
	 * the component is not modified.
	 * <p>
	 * Otherwise the given component is moved down by one and the previous component is 
	 * moved up by one.
	 * @param component the component to move down
	 */
	public abstract void moveComponentDown(SlideComponent component);
	
	// sequencing
	
	/**
	 * Returns the time that this slide should show after the transition is complete.
	 * @return long
	 * @see #TIME_FOREVER
	 */
	public abstract long getTime();
	
	/**
	 * Sets the time that this slide should show after the transition is complete.
	 * @param time the time in milliseconds
	 */
	public abstract void setTime(long time);
	
	// component animations
	
	/**
	 * Returns the list of animations for the attached components.
	 * @return List&lt;{@link SlideAnimation}&gt;
	 */
	public abstract List<SlideAnimation> getAnimations();
	
	/**
	 * Returns the list of animations for the given id or null if one doesn't exist.
	 * @param id the id of the region
	 * @return {@link SlideAnimation}
	 */
	public abstract List<SlideAnimation> getAnimations(UUID id);
	
	// size
	
	/**
	 * Adjusts the slide and its component's sizes to fit the target width and height.
	 * @param width the target width
	 * @param height the target height
	 */
	public abstract void fit(int width, int height);
	
	// copy
	
	/* (non-Javadoc)
	 * @see org.praisenter.slide.SlideRegion#copy()
	 */
	public abstract Slide copy();
	
	/**
	 * Returns the tags for this slide.
	 * @return Set&lt;{@link Tag}&gt;
	 */
	public abstract Set<Tag> getTags();
	
	/**
	 * Sets the tags for this slide.
	 * @param tags the tags
	 */
	public abstract void setTags(Set<Tag> tags);
}
