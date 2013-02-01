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
package org.praisenter.slide;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.praisenter.slide.media.AudioMediaComponent;
import org.praisenter.slide.media.ImageMediaComponent;
import org.praisenter.slide.media.PlayableMediaComponent;
import org.praisenter.slide.media.VideoMediaComponent;
import org.praisenter.slide.text.DateTimeComponent;
import org.praisenter.slide.text.TextComponent;

/**
 * Represents a slide with graphics, text, etc.
 * @author William Bittle
 * @version 2.0.0
 * @since 2.0.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ 
	GenericComponent.class,
	ImageMediaComponent.class, 
	VideoMediaComponent.class,
	AudioMediaComponent.class,
	TextComponent.class,
	DateTimeComponent.class })
public interface Slide {
	/** The minimum size in pixels of a component or slide */
	public static final int MINIMUM_SIZE = 20;
	
	/**
	 * Returns a deep copy of this {@link Slide}.
	 * @return {@link Slide}
	 */
	public abstract Slide copy();
	
	/**
	 * Returns a template for this slide.
	 * @return {@link Template}
	 */
	public abstract Template createTemplate();
	
	// rendering
	
	/**
	 * Renders a preview of this slide.
	 * @param g the graphics object to render to
	 */
	public abstract void renderPreview(Graphics2D g);
	
	/**
	 * Renders the current state of this slide.
	 * @param g the graphics object to render to
	 */
	public abstract void render(Graphics2D g);
	
	// modification

	/**
	 * Returns the background component.
	 * <p>
	 * This can be any type of component, even a {@link RenderableComponent}. In this
	 * case the position should be 0,0. The width/height should also match the slide
	 * width/height.
	 * @return {@link SlideComponent}
	 */
	public abstract RenderableComponent getBackground();
	
	/**
	 * Sets the background to the given component.
	 * @param component the background component
	 */
	public abstract void setBackground(RenderableComponent component);
	
	/**
	 * Adds the given component.
	 * @param component the component to add
	 */
	public abstract void addComponent(SlideComponent component);
	
	/**
	 * Removes the given component.
	 * @param component the component to remove
	 * @return boolean true if the component was removed
	 */
	public abstract boolean removeComponent(SlideComponent component);
	
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
	public abstract void moveComponentUp(RenderableComponent component);
	
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
	public abstract void moveComponentDown(RenderableComponent component);
	
	/**
	 * Returns a list of the all the components of the given type.
	 * <p>
	 * This method will not return the background component even if it is
	 * of the given type.
	 * <p>
	 * This method will return the components in ascending order.
	 * @param clazz the class type
	 * @return List&lt;E&gt;
	 */
	public abstract <E extends SlideComponent> List<E> getComponents(Class<E> clazz);
	
	/**
	 * Returns a list of the all the components of the given type.
	 * <p>
	 * This method will return the background component only if true is passed in
	 * the includeBackground parameter.
	 * <p>
	 * This method will return the components in ascending order.
	 * @param clazz the class type
	 * @param includeBackground true if the background should be included in the list (if it matches the given type)
	 * @return List&lt;E&gt;
	 */
	public abstract <E extends SlideComponent> List<E> getComponents(Class<E> clazz, boolean includeBackground);
	
	/**
	 * Returns a list of the all the components of the given type that cannot
	 * be removed from the slide.
	 * @param clazz the class type
	 * @return List&lt;E&gt;
	 */
	public abstract <E extends SlideComponent> List<E> getStaticComponents(Class<E> clazz);
	
	/**
	 * Returns a list of the all the components of the given type that can
	 * be removed from the slide.
	 * <p>
	 * This method will not return the background component even if it is
	 * of the given type.
	 * <p>
	 * This method will return the components in ascending order.
	 * @param clazz the class type
	 * @return List&lt;E&gt;
	 */
	public abstract <E extends SlideComponent> List<E> getNonStaticComponents(Class<E> clazz);
	
	/**
	 * Returns all the {@link PlayableMediaComponent}s on this {@link Slide}.
	 * <p>
	 * This is useful for display of the slide to being/end media playback.
	 * <p>
	 * This method will not return the background component even if it is of type
	 * {@link PlayableMediaComponent}.
	 * @return List&lt;{@link PlayableMediaComponent}&gt;
	 */
	public abstract List<PlayableMediaComponent<?>> getPlayableMediaComponents();
	
	/**
	 * Returns true if the given component is on this slide and it is a static
	 * component (a component that cannot be removed).
	 * @param component the component
	 * @return boolean
	 */
	public abstract boolean isStaticComponent(SlideComponent component);
	
	/**
	 * Returns this slide/template's name.
	 * @return String
	 */
	public abstract String getName();
	
	/**
	 * Sets this slide/template's name.
	 * @param name the name
	 */
	public abstract void setName(String name);
	
	/**
	 * Returns the width of this slide in pixels.
	 * @return int
	 */
	public abstract int getWidth();
	
	/**
	 * Sets the width of this slide.
	 * <p>
	 * This method will also modify the width of the background component to
	 * match, if it's set.
	 * @param width the width in pixels
	 */
	public abstract void setWidth(int width);

	/**
	 * Returns the height of this slide in pixels.
	 * @return int
	 */
	public abstract int getHeight();
	
	/**
	 * Sets the height of this slide.
	 * <p>
	 * This method will also modify the height of the background component to
	 * match, if it's set.
	 * @param height the height in pixels
	 */
	public abstract void setHeight(int height);
	
	/**
	 * Adjusts the slide and all sub components to fit the given size.
	 * @param width the target width
	 * @param height the target height
	 */
	public abstract void adjustSize(int width, int height);
	
	/**
	 * Creates a new thumbnail for this slide using the given size.
	 * @param size the size of the thumbnail
	 * @return BufferedImage
	 */
	public abstract BufferedImage getThumbnail(Dimension size);
}
